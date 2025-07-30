package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class LimaRecipeMachineBlockEntity<I extends LimaRecipeInput, R extends LimaCustomRecipe<I>> extends StateBlockRecipeMachineBlockEntity<I, R>
{
    protected LimaRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inventorySize)
    {
        super(type, recipeType, pos, state, inventorySize);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        List<ItemStack> results = recipe.getPossibleItemResults();

        final int start = outputSlotsStart();
        final int end = start + outputSlotsCount();

        for (ItemStack stack : results)
        {
            if (!LimaItemHandlerUtil.insertItemIntoSlots(getItemHandler(), stack, start, end, true).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    protected void insertRecipeResults(Level level, LimaItemHandlerBase machineInventory, R recipe, I recipeInput)
    {
        List<ItemStack> results = recipe.generateItemResults(recipeInput, level.registryAccess(), level.random);

        final int start = outputSlotsStart();
        final int end = start + outputSlotsCount();

        for (ItemStack stack : results)
        {
            LimaItemHandlerUtil.insertItemIntoSlots(machineInventory, stack, start, end, false);
        }
    }
}