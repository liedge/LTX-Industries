package liedge.ltxindustries.blockentity.base;

import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.limacore.recipe.result.ResourceResult;
import liedge.limacore.recipe.result.ResultPriority;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface RecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ItemHolderBlockEntity
{
    LimaRecipeCheck<I, R> getRecipeCheck();

    boolean isCrafting();

    void setCrafting(boolean crafting);

    boolean canInsertRecipeResults(ServerLevel level, R recipe, I input);

    default <RES extends Resource> void insertResourceResults(List<ResourceStack<RES>> stacks, @Nullable ResourceHandler<RES> inventory)
    {
        try (Transaction tx = Transaction.openRoot())
        {
            for (ResourceStack<RES> stack : stacks)
            {
                ResourceHandlerUtil.insertStacking(inventory, stack.resource(), stack.amount(), tx);
            }

            tx.commit();
        }
    }

    default <RES extends Resource> boolean canInsertResourceResults(Collection<? extends ResourceResult<RES>> results, @Nullable ResourceHandler<RES> inventory)
    {
        if (results.isEmpty()) return true;

        try (Transaction tx = Transaction.openRoot())
        {
            for (ResourceResult<RES> result : results)
            {
                if (result.getPriority() != ResultPriority.PRIMARY) continue;

                int required = result.getCount().max();
                int inserted = ResourceHandlerUtil.insertStacking(inventory, result.getResource(), required, tx);

                if (inserted < required) return false;
            }
        } // Do not commit tx

        return true;
    }
}