package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.ItemResultType;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class LTXIJeiCategory<R extends LimaCustomRecipe<?>> implements IRecipeCategory<RecipeHolder<R>>
{
    protected final IGuiHelper helper;
    private final int width;
    private final int height;
    private final Component title;

    // Commonly used drawables
    protected final IDrawableStatic machineProgressBackground;
    protected final IDrawableAnimated machineProgress;

    protected LTXIJeiCategory(IGuiHelper helper, LimaRecipeType<R> gameRecipeType, int width, int height)
    {
        this.helper = helper;
        this.width = width;
        this.height = height;
        this.title = gameRecipeType.translate();

        this.machineProgressBackground = guiSpriteDrawable(MachineProgressWidget.BACKGROUND_SPRITE, MachineProgressWidget.BACKGROUND_WIDTH, MachineProgressWidget.BACKGROUND_HEIGHT).build();
        this.machineProgress = guiSpriteDrawable(MachineProgressWidget.FILL_SPRITE, MachineProgressWidget.FILL_WIDTH, MachineProgressWidget.FILL_HEIGHT).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    protected LTXIJeiCategory(IGuiHelper helper, Supplier<? extends LimaRecipeType<R>> typeSupplier, int width, int height)
    {
        this(helper, typeSupplier.get(), width, height);
    }

    protected abstract void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries);

    protected abstract void drawRecipe(RecipeHolder<R> recipeHolder, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY);

    protected final RegistryAccess localRegistryAccess()
    {
        return Objects.requireNonNull(Minecraft.getInstance().level, "Minecraft local level is null").registryAccess();
    }

    protected ResourceLocation unwrapGuiSpriteTexture(ResourceLocation spriteLocation)
    {
        return spriteLocation.withPath(path -> String.format("textures/gui/sprites/%s.png", path));
    }

    protected IDrawableBuilder guiSpriteDrawable(ResourceLocation spriteLocation, int width, int height)
    {
        return helper.drawableBuilder(unwrapGuiSpriteTexture(spriteLocation), 0, 0, width, height).setTextureSize(width, height);
    }

    private void deterministicOverlay(IRecipeSlotBuilder slot, LimaSizedIngredient<?, ?> ingredient)
    {
        if (ingredient.getConsumeChance() == 0)
        {
            slot.setOverlay(ScaledFontDrawable.withStyle("NC", ChatFormatting.GREEN, 0.5f), 1, 1)
                    .addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.INPUT_NOT_CONSUMED_TOOLTIP.translate().withStyle(ChatFormatting.GREEN)));
        }
        else
        {
            String formattedChance = LimaTextUtil.format1PlacePercentage(ingredient.getConsumeChance());
            slot.setOverlay(ScaledFontDrawable.withStyle(formattedChance, ChatFormatting.YELLOW, 0.5f), 1, 1)
                    .addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.INPUT_CONSUME_CHANCE_TOOLTIP.translateArgs(formattedChance).withStyle(ChatFormatting.YELLOW)));
        }
    }

    protected void sizedIngredientsSlot(IRecipeLayoutBuilder builder, R recipe, int ingredientIndex, int x, int y)
    {
        LimaSizedItemIngredient sizedIngredient = recipe.getItemIngredient(ingredientIndex);
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                .addItemStacks(List.of(sizedIngredient.getCachedValues()));

        if (sizedIngredient.isDeterministic()) deterministicOverlay(slot, sizedIngredient);
    }

    protected void sizedIngredientSlotsGrid(IRecipeLayoutBuilder builder, R recipe, int x, int y, int columns)
    {
        List<?> ingredients = recipe.getItemIngredients();
        for (int i = 0; i < ingredients.size(); i++)
        {
            int sx = x + (i % columns) * 18;
            int sy = y + (i / columns) * 18;
            sizedIngredientsSlot(builder, recipe, i, sx, sy);
        }
    }

    protected void fluidIngredientSlot(IRecipeLayoutBuilder builder, R recipe, int ingredientIndex, int x, int y)
    {
        LimaSizedFluidIngredient sizedIngredient = recipe.getFluidIngredient(ingredientIndex);
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidIngredientRenderer.INSTANCE);

        for (FluidStack stack : sizedIngredient.getCachedValues())
        {
            slot.addFluidStack(stack.getFluid(), stack.getAmount(), stack.getComponentsPatch());
        }

        if (sizedIngredient.isDeterministic()) deterministicOverlay(slot, sizedIngredient);
    }

    protected void itemResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        ItemResult result = recipe.getItemResult(resultIndex);
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(result.getGuiPreviewResult());

        // Append optional output tooltips
        if (!result.requiredOutput())
        {
            slot.addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.OUTPUT_OPTIONAL_TOOLTIP.translate().withStyle(LTXIConstants.OUTPUT_ORANGE.chatStyle())));
        }

        // Append non-standard overlays and tooltips
        ItemResultType type = result.getType();
        if (type == ItemResultType.RANDOM_CHANCE)
        {
            String formattedChance = LimaTextUtil.format1PlacePercentage(result.resultChance());
            slot.setOverlay(ScaledFontDrawable.withStyle(formattedChance, ChatFormatting.YELLOW, 0.5f), 1, 1)
                    .addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.OUTPUT_CHANCE_TOOLTIP.translate().append(formattedChance).withStyle(ChatFormatting.YELLOW)));
        }
        else if (type == ItemResultType.VARIABLE_COUNT)
        {
            String formattedCount = result.minimumCount() + "-" + result.maximumCount();

            int countWidth = Minecraft.getInstance().font.width(formattedCount);
            float scale = countWidth > 15 ? 15f / countWidth : 1;
            IDrawable count = ScaledFontDrawable.plainText(formattedCount, scale);

            slot.setOverlay(count, 16 - count.getWidth(), 17 - count.getHeight())
                    .addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.OUTPUT_VARIABLE_COUNT_TOOLTIP.translate().append(formattedCount)));
        }
    }

    protected void fluidResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        FluidStack result = recipe.getFluidResult(resultIndex);
        builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidIngredientRenderer.INSTANCE).addFluidStack(result.getFluid(), result.getAmount());
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public final void draw(RecipeHolder<R> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        guiGraphics.fill(0, 0, width, height, 0xff2e2e2e);
        drawRecipe(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public final void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> recipe, IFocusGroup focuses)
    {
        setRecipe(builder, recipe, recipe.value(), focuses, localRegistryAccess());
    }

    @Override
    public Component getTitle()
    {
        return title;
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return null;
    }
}