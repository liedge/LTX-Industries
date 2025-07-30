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
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class LTXIJeiCategory<R extends LimaCustomRecipe<?>> implements IRecipeCategory<RecipeHolder<R>>
{
    private final IDrawable background;
    private final Component title;

    // Commonly used drawables
    protected final IDrawableStatic machineProgressBackground;
    protected final IDrawableAnimated machineProgress;

    protected LTXIJeiCategory(IGuiHelper helper, LimaRecipeType<R> gameRecipeType, int width, int height)
    {
        this.background = new SolidColorDrawable(width, height, FastColor.ARGB32.opaque(0x2e2e2e));
        this.title = gameRecipeType.translate();

        this.machineProgressBackground = guiSpriteDrawable(helper, MachineProgressWidget.BACKGROUND_SPRITE, MachineProgressWidget.BACKGROUND_WIDTH, MachineProgressWidget.BACKGROUND_HEIGHT).build();
        this.machineProgress = guiSpriteDrawable(helper, MachineProgressWidget.FILL_SPRITE, MachineProgressWidget.FILL_WIDTH, MachineProgressWidget.FILL_HEIGHT).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    protected LTXIJeiCategory(IGuiHelper helper, Supplier<? extends LimaRecipeType<R>> typeSupplier, int width, int height)
    {
        this(helper, typeSupplier.get(), width, height);
    }

    protected abstract void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries);

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

    protected void itemResultSlot(IRecipeLayoutBuilder builder, R recipe, int resultIndex, int x, int y)
    {
        ItemResult result = recipe.getItemResult(resultIndex);
        IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                .addItemStack(result.item());
        if (result.chance() != 1f)
        {
            String formattedChance = LimaTextUtil.format1PlacePercentage(result.chance());
            slotBuilder.setOverlay(new SmallFontDrawable(formattedChance, ChatFormatting.YELLOW), 1, 1).addTooltipCallback(outputChanceTooltip(formattedChance));
        }
    }

    private IRecipeSlotTooltipCallback outputChanceTooltip(String formattedChance)
    {
        Component tooltip = LTXILangKeys.OUTPUT_CHANCE_TOOLTIP.translate().append(formattedChance).withStyle(ChatFormatting.YELLOW);
        return (view, lines) -> lines.add(tooltip);
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
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return null;
    }
}