package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.*;
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
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidWithCountRenderer.INSTANCE);

        for (FluidStack stack : sizedIngredient.getCachedValues())
        {
            slot.addFluidStack(stack.getFluid(), stack.getAmount(), stack.getComponentsPatch());
        }

        if (sizedIngredient.isDeterministic()) deterministicOverlay(slot, sizedIngredient);
    }

    protected void itemResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        ItemResult result = recipe.getItemResult(resultIndex);
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(result.getDisplayStack());

        List<Component> tooltipLines = new ObjectArrayList<>();
        addPriorityTooltip(result, tooltipLines);
        IDrawable chanceOverlay = resultChanceOverlay(result, tooltipLines);
        IDrawable countOverlay = resultCountOverlay(result.getCount(), tooltipLines, Component::literal);

        slot.addRichTooltipCallback((view, lines) -> lines.addAll(tooltipLines));
        if (chanceOverlay != null || countOverlay != null) slot.setOverlay(new ResultOverlay(chanceOverlay, countOverlay), 0, 0);
    }

    protected void fluidResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        FluidResult result = recipe.getFluidResult(resultIndex);
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addFluidStack(result.getFluid());

        List<Component> tooltipLines = new ObjectArrayList<>();
        addPriorityTooltip(result, tooltipLines);
        IDrawable chanceOverlay = resultChanceOverlay(result, tooltipLines);
        IDrawable countOverlay = resultCountOverlay(result.getCount(), tooltipLines, s -> Component.translatable("jei.tooltip.liquid.amount", s));
        IIngredientRenderer<FluidStack> renderer = result.getCount().isConstant() ? FluidWithCountRenderer.INSTANCE : FluidWithoutCountRenderer.INSTANCE;

        slot.setCustomRenderer(NeoForgeTypes.FLUID_STACK, renderer);
        slot.addRichTooltipCallback((view, lines) -> lines.addAll(tooltipLines));
        if (chanceOverlay != null || countOverlay != null) slot.setOverlay(new ResultOverlay(chanceOverlay, countOverlay), 0, 0);
    }

    private @Nullable IDrawable resultChanceOverlay(StackBaseResult<?, ?> result, List<Component> lines)
    {
        if (result.getChance() == 1f) return null;

        String formattedChance = LimaTextUtil.format1PlacePercentage(result.getChance());
        lines.add(LTXILangKeys.OUTPUT_CHANCE_TOOLTIP.translate().append(formattedChance).withStyle(ChatFormatting.YELLOW));
        return ScaledFontDrawable.withStyle(formattedChance, ChatFormatting.YELLOW, 0.5f);
    }

    private @Nullable IDrawable resultCountOverlay(ResultCount count, List<Component> lines, Function<String, Component> amountTooltip)
    {
        if (count.isConstant()) return null;

        String formattedAmount = String.format("%s-%s", LimaTextUtil.formatWholeNumber(count.min()), LimaTextUtil.formatWholeNumber(count.max()));
        lines.add(LTXILangKeys.OUTPUT_VARIABLE_COUNT_TOOLTIP.translate().append(amountTooltip.apply(formattedAmount)));
        return ScaledFontDrawable.plainText("VAR", 0.75f);
    }

    private void addPriorityTooltip(StackBaseResult<?, ?> result, List<Component> lines)
    {
        MutableComponent component = result.getPriority().translate();
        if (result.getPriority() == ResultPriority.PRIMARY)
        {
            lines.add(component.withStyle(ChatFormatting.GREEN));
        }
        else
        {
            lines.add(component.withStyle(LTXIConstants.OUTPUT_ORANGE.chatStyle()));
            lines.add(LTXILangKeys.OUTPUT_NON_PRIMARY_TOOLTIP.translate());
        }
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

    private record ResultOverlay(@Nullable IDrawable chance, @Nullable IDrawable count) implements IDrawable
    {
        @Override
        public int getWidth()
        {
            return 16;
        }

        @Override
        public int getHeight()
        {
            return 16;
        }

        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset)
        {
            if (chance != null) chance.draw(graphics, xOffset + 1, yOffset + 1);
            if (count != null) count.draw(graphics, xOffset + 16 - count.getWidth(), yOffset + 17 - count.getHeight());
        }
    }
}