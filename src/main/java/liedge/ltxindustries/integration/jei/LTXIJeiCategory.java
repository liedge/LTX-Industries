package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
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
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class LTXIJeiCategory<R extends LimaCustomRecipe<?>> implements IRecipeCategory<RecipeHolder<R>>
{
    private final int width;
    private final int height;
    private final Component title;

    // Commonly used drawables
    protected final IDrawableStatic machineProgressBackground;
    protected final IDrawableAnimated machineProgress;

    protected LTXIJeiCategory(IGuiHelper helper, LimaRecipeType<R> gameRecipeType, int width, int height)
    {
        this.width = width;
        this.height = height;
        this.title = gameRecipeType.translate();

        this.machineProgressBackground = guiSpriteDrawable(helper, MachineProgressWidget.BACKGROUND_SPRITE, MachineProgressWidget.BACKGROUND_WIDTH, MachineProgressWidget.BACKGROUND_HEIGHT).build();
        this.machineProgress = guiSpriteDrawable(helper, MachineProgressWidget.FILL_SPRITE, MachineProgressWidget.FILL_WIDTH, MachineProgressWidget.FILL_HEIGHT).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
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

    protected IDrawableBuilder guiSpriteDrawable(IGuiHelper helper, ResourceLocation spriteLocation, int width, int height)
    {
        return helper.drawableBuilder(unwrapGuiSpriteTexture(spriteLocation), 0, 0, width, height).setTextureSize(width, height);
    }

    protected void sizedIngredientsSlot(IRecipeLayoutBuilder builder, R recipe, int ingredientIndex, int x, int y)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(List.of(recipe.getItemIngredient(ingredientIndex).getItems()));
    }

    protected void sizedIngredientSlotsGrid(IRecipeLayoutBuilder builder, R recipe, int x, int y, int columns)
    {
        List<SizedIngredient> ingredients = recipe.getItemIngredients();
        for (int i = 0; i < ingredients.size(); i++)
        {
            int sx = x + (i % columns) * 18;
            int sy = y + (i / columns) * 18;
            sizedIngredientsSlot(builder, recipe, i, sx, sy);
        }
    }

    protected void fluidIngredientSlot(IRecipeLayoutBuilder builder, R recipe, int ingredientIndex, int x, int y)
    {
        IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidIngredientRenderer.INSTANCE);
        SizedFluidIngredient ingredient = recipe.getFluidIngredient(ingredientIndex);

        FluidStack[] fluids = ingredient.getFluids();
        for (FluidStack stack : fluids)
        {
            slotBuilder.addFluidStack(stack.getFluid(), stack.getAmount());
        }
    }

    protected void itemResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        ItemResult result = recipe.getItemResult(resultIndex);
        IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                .addItemStack(result.item());
        if (result.chance() != 1f)
        {
            String formattedChance = LimaTextUtil.format1PlacePercentage(result.chance());
            slotBuilder.setOverlay(new SmallFontDrawable(formattedChance, ChatFormatting.YELLOW), 1, 1).addRichTooltipCallback(outputChanceTooltip(formattedChance));
        }
    }

    protected void itemResultSlotsGrid(IRecipeLayoutBuilder builder, R recipe, int x, int y, int columns)
    {
        for (int i = 0; i < recipe.getItemResults().size(); i++)
        {
            int sx = x + (i % columns) * 18;
            int sy = y + (i / columns) * 18;
            itemResultSlot(builder, recipe, i, sx, sy);
        }
    }

    protected void fluidResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        FluidStack result = recipe.getFluidResult(resultIndex);
        builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidIngredientRenderer.INSTANCE).addFluidStack(result.getFluid(), result.getAmount());
    }

    protected void fluidResultSlotsGrid(IRecipeLayoutBuilder builder, R recipe, int x, int y, int columns)
    {
        for (int i = 0; i < recipe.getFluidResults().size(); i++)
        {
            int sx = x + (i % columns) * 18;
            int sy = y + (i / columns) * 18;
            fluidResultSlot(builder, recipe, i, sx, sy);
        }
    }

    private IRecipeSlotRichTooltipCallback outputChanceTooltip(String formattedChance)
    {
        Component tooltip = LTXILangKeys.OUTPUT_CHANCE_TOOLTIP.translate().append(formattedChance).withStyle(ChatFormatting.YELLOW);
        return (view, builder) -> builder.add(tooltip);
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