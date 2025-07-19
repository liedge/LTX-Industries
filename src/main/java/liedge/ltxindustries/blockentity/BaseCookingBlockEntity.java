package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseCookingBlockEntity<R extends AbstractCookingRecipe> extends SimpleRecipeMachineBlockEntity<SingleRecipeInput, R>
{
    protected BaseCookingBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state)
    {
        super(type, recipeType, pos, state, 3);
    }

    @Override
    protected SingleRecipeInput getRecipeInput(Level level)
    {
        return new SingleRecipeInput(getItemHandler().getStackInSlot(1));
    }

    @Override
    protected void consumeIngredients(SingleRecipeInput recipeInput, R recipe, Level level)
    {
        getItemHandler().extractItem(1, 1, false);
    }

    @Override
    public boolean isInputSlot(int index)
    {
        return index == 1;
    }

    @Override
    public int getOutputSlot()
    {
        return 2;
    }
}