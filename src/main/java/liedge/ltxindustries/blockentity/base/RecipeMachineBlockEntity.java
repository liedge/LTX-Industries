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

    int inputSlotsStart();

    int inputSlotsCount();

    default boolean isInputSlot(int index)
    {
        int start = inputSlotsStart();
        return index >= start && index < (start + inputSlotsCount());
    }

    int outputSlotsStart();

    int outputSlotsCount();

    default boolean isOutputSlot(int index)
    {
        int start = outputSlotsStart();
        return index >= start && index < (start + outputSlotsCount());
    }

    boolean isCrafting();

    void setCrafting(boolean crafting);

    boolean canInsertRecipeResults(Level level, R recipe);

    default boolean canInsertRecipeResults(Level level, RecipeHolder<R> recipeHolder)
    {
        return canInsertRecipeResults(level, recipeHolder.value());
    }
}