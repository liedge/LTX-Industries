package liedge.ltxindustries.blockentity;

import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CookingBlockEntity<R extends AbstractCookingRecipe> extends StateBlockRecipeMachineBlockEntity<SingleRecipeInput, R>
{
    protected CookingBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state)
    {
        super(type, recipeType, pos, state, 1, 1, 0, 0);
    }

    @Override
    protected SingleRecipeInput getRecipeInput(Level level)
    {
        return new SingleRecipeInput(getInputInventory().getStackInSlot(0));
    }

    @Override
    protected int getBaseRecipeCraftingTime(R recipe)
    {
        return recipe.getCookingTime();
    }

    @Override
    protected void consumeIngredients(SingleRecipeInput recipeInput, R recipe, Level level)
    {
        getInputInventory().extractItem(0, 1, false);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        return LimaItemUtil.canMergeItemStacks(getOutputInventory().getStackInSlot(0), recipe.getResultItem(level.registryAccess()));
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, SingleRecipeInput recipeInput)
    {
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
        getOutputInventory().insertItem(0, result, false);
    }
}