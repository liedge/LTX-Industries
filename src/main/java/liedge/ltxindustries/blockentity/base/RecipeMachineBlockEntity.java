package liedge.ltxindustries.blockentity.base;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.recipe.LimaRecipeCheck;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface RecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ItemHolderBlockEntity
{
    LimaRecipeCheck<I, R> getRecipeCheck();

    boolean isCrafting();

    void setCrafting(boolean crafting);

    boolean canInsertRecipeResults(Level level, R recipe);

    default boolean canInsertRecipeResults(Level level, RecipeHolder<R> recipeHolder)
    {
        return canInsertRecipeResults(level, recipeHolder.value());
    }
}