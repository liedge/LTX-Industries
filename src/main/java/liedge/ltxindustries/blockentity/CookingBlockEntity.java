package liedge.ltxindustries.blockentity;

import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.block.MachineState;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.template.BaseRecipeMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public abstract class CookingBlockEntity<R extends AbstractCookingRecipe> extends BaseRecipeMachineBlockEntity<SingleRecipeInput, R>
{
    protected CookingBlockEntity(ConfigurableIOBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state)
    {
        super(type, recipeType, pos, state, 1, 1, 0, 0);
    }

    @Override
    protected SingleRecipeInput getRecipeInput(Level level)
    {
        LimaBlockEntityItems inputs = getInputInventory();

        int amount = inputs.getAmountAsInt(0);
        ItemStack current = inputs.getResource(0).toStack(amount);

        return new SingleRecipeInput(current);
    }

    @Override
    protected int getBaseRecipeCraftingTime(R recipe)
    {
        return recipe.cookingTime();
    }

    @Override
    protected void consumeIngredients(SingleRecipeInput recipeInput, R recipe, Level level)
    {
        try (Transaction tx = Transaction.openRoot())
        {
            LimaBlockEntityItems input = getInputInventory();
            ItemResource stored = input.getResource(0);

            int extracted = input.extract(0, stored, 1, tx);

            if (extracted > 0) tx.commit();
        }
    }

    @Override
    public boolean canInsertRecipeResults(ServerLevel level, R recipe, SingleRecipeInput input)
    {
        ItemStack output = recipe.assemble(input, level.registryAccess());

        int inserted;

        try (Transaction tx = Transaction.openRoot())
        {
            inserted = getOutputInventory().insert(0, ItemResource.of(output), output.getCount(), tx);
        }

        return inserted >= output.getCount();
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, SingleRecipeInput recipeInput)
    {
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());

        try (Transaction tx = Transaction.openRoot())
        {
            int inserted = getOutputInventory().insert(0, ItemResource.of(result), result.getCount(), tx);
            if (inserted > 0) tx.commit();
        }
    }

    @Override
    protected void craftRecipe(ServerLevel level, R recipe, int maxOperations)
    {
        for (int i = 0; i < maxOperations; i++)
        {
            // Cannot re-use vanilla inputs for cooking recipes
            SingleRecipeInput input = getRecipeInput(level);
            if (i > 0)
            {
                boolean canContinue = recipe.matches(input, level) && canInsertRecipeResults(level, recipe, input);

                if (!canContinue) break;
            }

            insertRecipeResults(level, recipe, input);
            consumeIngredients(input, recipe, level);
        }
    }

    @Override
    protected void onCraftingStateChanged(boolean newCraftingState)
    {
        BlockState newState = getBlockState().setValue(LTXIBlockProperties.BINARY_MACHINE_STATE, MachineState.of(newCraftingState));
        nonNullLevel().setBlockAndUpdate(getBlockPos(), newState);
    }
}