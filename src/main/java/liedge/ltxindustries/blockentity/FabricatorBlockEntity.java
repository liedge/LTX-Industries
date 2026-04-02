package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.recipe.RecipeInputAccess;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class FabricatorBlockEntity extends BaseFabricatorBlockEntity
{
    public FabricatorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.FABRICATOR.get(), pos, state, 0);
    }

    public void startCrafting(ServerLevel level, RecipeHolder<FabricatingRecipe> holder, RecipeInputAccess inputAccess, boolean forceStart)
    {
        FabricatingRecipe recipe = holder.value();

        if (!isCrafting() && canInsertRecipeResults(level, recipe, inputAccess))
        {
            if (forceStart)
            {
                setCrafting(true);
                getRecipeCheck().setLastUsedRecipe(holder);
            }
            else if (recipe.matches(inputAccess, level))
            {
                recipe.consumeItemInputs(inputAccess, level.getRandom());
                setCrafting(true);
                getRecipeCheck().setLastUsedRecipe(holder);
            }
        }
    }

    private void stopCrafting(boolean insertResult, ServerLevel level)
    {
        getRecipeCheck().getLastUsedRecipe(level).ifPresent(holder ->
        {
            if (insertResult)
            {
                insertResourceResults(List.of(holder.value().generateItemResult(level)), getItems(BlockContentsType.OUTPUT));
            }

            energyCraftProgress = 0;
            setCrafting(false);
        });
    }

    @Override
    protected void tickServerFabricator(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Try progressing crafting recipe
        FabricatingRecipe recipe = getRecipeCheck().getLastUsedRecipe(level).map(RecipeHolder::value).orElse(null);
        if (isCrafting() && recipe != null)
        {
            if (energyCraftProgress < recipe.getEnergyRequired())
            {
                int toExtract = Math.min(getEnergyUsage(), recipe.getEnergyRequired() - energyCraftProgress);
                try (Transaction tx = Transaction.openRoot())
                {
                    energyCraftProgress += getEnergy().extract(toExtract, tx);
                    tx.commit();
                }
            }
            else
            {
                stopCrafting(true, level);
            }
        }
        else
        {
            stopCrafting(false, level);
        }
    }

    @Override
    public boolean isValidBlueprintItem(ItemResource resource)
    {
        return resource.is(LTXIItems.EMPTY_FABRICATION_BLUEPRINT.get());
    }
}