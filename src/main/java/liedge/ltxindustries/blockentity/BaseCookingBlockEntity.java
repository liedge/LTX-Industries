package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseCookingBlockEntity<R extends AbstractCookingRecipe> extends StateBlockRecipeMachineBlockEntity<SingleRecipeInput, R>
{
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

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
    public int inputSlotsStart()
    {
        return INPUT_SLOT;
    }

    @Override
    public int inputSlotsCount()
    {
        return 1;
    }

    @Override
    public int outputSlotsStart()
    {
        return OUTPUT_SLOT;
    }

    @Override
    public int outputSlotsCount()
    {
        return 1;
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        return LimaItemUtil.canMergeItemStacks(getItemHandler().getStackInSlot(OUTPUT_SLOT), recipe.getResultItem(level.registryAccess()));
    }

    @Override
    protected void insertRecipeResults(Level level, LimaItemHandlerBase machineInventory, R recipe, SingleRecipeInput recipeInput)
    {
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
        machineInventory.insertItem(OUTPUT_SLOT, result, false);
    }
}