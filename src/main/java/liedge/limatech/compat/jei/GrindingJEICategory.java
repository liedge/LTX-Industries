package liedge.limatech.compat.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.client.gui.screen.GrinderScreen;
import liedge.limatech.client.gui.widget.ScreenWidgetSprites;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.registry.LimaTechBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ItemLike;

public class GrindingJEICategory extends LimaTechJEICategory<GrindingRecipe>
{
    private final IDrawableStatic progressBackground;
    private final IDrawableAnimated progressForeground;

    GrindingJEICategory(IGuiHelper helper, LimaRecipeType<GrindingRecipe> limaRecipeType)
    {
        super(helper, limaRecipeType, GrinderScreen.SCREEN_TEXTURE, 51, 29, 76, 26);
        this.progressBackground = unmanagedSpriteDrawable(helper, ScreenWidgetSprites.MACHINE_PROGRESS_BACKGROUND).build();
        this.progressForeground = unmanagedSpriteDrawable(helper, ScreenWidgetSprites.MACHINE_PROGRESS_FOREGROUND).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected ItemLike categoryIconItem()
    {
        return LimaTechBlocks.GRINDER;
    }

    @Override
    public RecipeType<GrindingRecipe> getRecipeType()
    {
        return LimaTechJEIPlugin.GRINDING_JEI;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrindingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 5).addIngredients(recipe.getIngredient(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 5).addItemStack(recipe.getResultItem(localRegistryAccess()));
    }

    @Override
    public void draw(GrindingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        progressBackground.draw(guiGraphics, 24, 10);
        progressForeground.draw(guiGraphics, 25, 11);
    }
}