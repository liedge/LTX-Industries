package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;

abstract class LTXIRecipeHolderCategory<R extends LimaCustomRecipe<?>> extends LTXIBaseCategory<RecipeHolder<R>>
{
    protected LTXIRecipeHolderCategory(IGuiHelper helper, LimaRecipeType<R> gameRecipeType, int width, int height)
    {
        super(helper, gameRecipeType.translate(), width, height);
    }

    protected abstract void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries);

    @Override
    public abstract IRecipeHolderType<R> getRecipeType();

    @Override
    public void draw(RecipeHolder<R> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        graphics.fill(0, 0, getWidth(), getHeight(), BG_COLOR);
    }

    @Override
    public final void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> recipe, IFocusGroup focuses)
    {
        setRecipe(builder, recipe, recipe.value(), focuses, localRegistryAccess());
    }
}