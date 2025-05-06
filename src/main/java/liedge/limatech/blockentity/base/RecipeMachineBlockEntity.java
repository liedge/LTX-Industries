package liedge.limatech.blockentity.base;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.limacore.util.LimaItemUtil;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface RecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ItemHolderBlockEntity
{
    LimaRecipeCheck<I, R> getRecipeCheck();

    boolean isInputSlot(int index);

    int getOutputSlot();

    boolean isCrafting();

    void setCrafting(boolean crafting);

    // Helper methods
    default boolean canInsertRecipeResult(Level level, R recipe)
    {
        return LimaItemUtil.canMergeItemStacks(getItemHandler().getStackInSlot(getOutputSlot()), recipe.getResultItem(level.registryAccess()));
    }

    default boolean canInsertRecipeResult(Level level, RecipeHolder<R> recipeHolder)
    {
        return canInsertRecipeResult(level, recipeHolder.value());
    }
}