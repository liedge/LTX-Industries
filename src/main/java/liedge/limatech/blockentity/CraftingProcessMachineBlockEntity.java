package liedge.limatech.blockentity;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CraftingProcessMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>>  extends TimedProcessMachineBlockEntity
{
    RecipeType<R> getRecipeType();

    boolean isInputSlot(int index);

    boolean isCrafting();

    void setCrafting(boolean crafting);

    default List<RecipeHolder<R>> getAllRecipes(Level level)
    {
        return level.getRecipeManager().getAllRecipesFor(getRecipeType());
    }

    default @Nullable RecipeHolder<R> getMatchingRecipe(Level level, I recipeInput, @Nullable RecipeHolder<R> lastRecipe)
    {
        return level.getRecipeManager().getRecipeFor(getRecipeType(), recipeInput, level, lastRecipe).orElse(null);
    }
}