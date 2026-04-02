package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.recipe.RecipeInputAccess;
import liedge.limacore.recipe.SimpleResourceAccess;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public class AutoFabricatorBlockEntity extends BaseFabricatorBlockEntity
{
    private boolean shouldCheckRecipe;
    private boolean shouldCheckBlueprint;

    public AutoFabricatorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.AUTO_FABRICATOR.get(), pos, state, 16);
    }

    private void updateBlueprint(ServerLevel level)
    {
        shouldCheckBlueprint = false;

        ItemResource bpItem = auxInventory.getResource(AUX_BLUEPRINT_SLOT);
        ResourceKey<Recipe<?>> recipeKey = bpItem.get(LTXIDataComponents.BLUEPRINT_RECIPE);
        RecipeHolder<FabricatingRecipe> holder = null;

        if (recipeKey != null) holder = LimaRegistryUtil.getRecipeByKey(level, recipeKey, LTXIRecipeTypes.FABRICATING).orElse(null);

        getRecipeCheck().setLastUsedRecipe(holder);
    }

    @Override
    protected void tickServerFabricator(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Update blueprint status (if necessary)
        if (shouldCheckBlueprint && !isCrafting()) updateBlueprint(level);

        // Check and update crafting status
        if (shouldCheckRecipe && !isCrafting())
        {
            shouldCheckRecipe = false;

            Optional<RecipeHolder<FabricatingRecipe>> optional = getRecipeCheck().getLastUsedRecipe(level);
            boolean willCraft = false;

            if (optional.isPresent())
            {
                FabricatingRecipe recipe = optional.get().value();

                RecipeInputAccess inputAccess = new SimpleResourceAccess(getItems(BlockContentsType.INPUT), null);
                if (canInsertRecipeResults(level, recipe, inputAccess) && recipe.matches(inputAccess, level))
                {
                    recipe.consumeItemInputs(inputAccess, level.getRandom());
                    willCraft = true; // Consume ingredients here and start crafting. Last used recipe will persist until crafting completes.
                }
            }

            setCrafting(willCraft);
        }

        // Tick recipe progress
        FabricatingRecipe recipe = getRecipeCheck().getLastUsedRecipe(level).map(RecipeHolder::value).orElse(null);
        if (isCrafting() && recipe != null && canInsertRecipeResults(level, recipe, new SimpleResourceAccess(getItems(BlockContentsType.INPUT), null)))
        {
            // Accumulate energy for recipe
            if (energyCraftProgress < recipe.getEnergyRequired())
            {
                int toExtract = Math.min(getEnergyUsage(), recipe.getEnergyRequired() - energyCraftProgress);
                try (Transaction tx = Transaction.openRoot())
                {
                    energyCraftProgress += getEnergy().extract(toExtract, tx);
                    tx.commit();
                }
            }
            else // When done crafting
            {
                insertResourceResults(List.of(recipe.generateItemResult(level)), getItems(BlockContentsType.OUTPUT));

                // Reset state & check recipe after every craft
                energyCraftProgress = 0;
                setCrafting(false);
                shouldCheckRecipe = true;
            }
        }
        else
        {
            // Reset energy just in case crafting was interrupted, but this shouldn't ever happen.
            energyCraftProgress = 0;
        }
    }

    @Override
    public boolean isValidBlueprintItem(ItemResource resource)
    {
        return resource.is(LTXIItems.FABRICATION_BLUEPRINT.get()) && resource.has(LTXIDataComponents.BLUEPRINT_RECIPE);
    }

    @Override
    public void onItemChanged(BlockContentsType contentsType, int index, ItemStack previousContents)
    {
        super.onItemChanged(contentsType, index, previousContents);

        if (contentsType == BlockContentsType.INPUT || contentsType == BlockContentsType.OUTPUT)
        {
            shouldCheckRecipe = true;
        }
        else if (contentsType == BlockContentsType.AUXILIARY && index == AUX_BLUEPRINT_SLOT)
        {
            shouldCheckRecipe = true;
            shouldCheckBlueprint = true;
        }
    }
}