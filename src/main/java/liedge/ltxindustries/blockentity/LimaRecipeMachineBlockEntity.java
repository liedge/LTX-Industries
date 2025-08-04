package liedge.ltxindustries.blockentity;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

public abstract class LimaRecipeMachineBlockEntity<R extends LimaCustomRecipe<LimaRecipeInput>> extends StateBlockRecipeMachineBlockEntity<LimaRecipeInput, R>
{
    protected LimaRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots)
    {
        super(type, recipeType, pos, state, inputSlots, outputSlots);
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return LimaRecipeInput.create(getInputInventory());
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, R recipe, Level level)
    {
        recipe.consumeIngredientsLenientSlots(recipeInput, false);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        List<ItemStack> results = recipe.getPossibleItemResults();

        for (ItemStack stack : results)
        {
            if (!ItemHandlerHelper.insertItem(getOutputInventory(), stack, true).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, LimaRecipeInput recipeInput)
    {
        List<ItemStack> results = recipe.generateItemResults(recipeInput, level.registryAccess(), level.random);

        for (ItemStack stack : results)
        {
            ItemHandlerHelper.insertItem(getOutputInventory(), stack, false);
        }
    }
}