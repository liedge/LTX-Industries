package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.itemhandler.BlockInventoryType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class AutoFabricatorBlockEntity extends BaseFabricatorBlockEntity
{
    private boolean shouldCheckRecipe;
    private boolean shouldCheckBlueprint;

    public AutoFabricatorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.AUTO_FABRICATOR.get(), pos, state, 16);
    }

    private void updateBlueprint(Level level)
    {
        shouldCheckBlueprint = false;

        ItemStack bpItem = getAuxInventory().getStackInSlot(AUX_BLUEPRINT_SLOT);
        ResourceLocation bpId = bpItem.get(LTXIDataComponents.BLUEPRINT_RECIPE);
        RecipeHolder<FabricatingRecipe> holder = null;

        if (bpId != null) holder = LimaRecipesUtil.getRecipeById(level, bpId, LTXIRecipeTypes.FABRICATING).orElse(null);

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
            boolean check = false;

            if (optional.isPresent())
            {
                FabricatingRecipe recipe = optional.get().value();

                LimaRecipeInput input = LimaRecipeInput.create(getInputInventory());
                if (canInsertRecipeResults(level, recipe) && recipe.matches(input, level)) // Preliminary check
                {
                    recipe.consumeIngredientsLenientSlots(input, false);
                    check = true; // We consume ingredients here and start crafting. Last used recipe will persist until crafting completes.
                }
            }

            setCrafting(check);
        }

        // Tick recipe progress
        FabricatingRecipe recipe = getRecipeCheck().getLastUsedRecipe(level).map(RecipeHolder::value).orElse(null);
        if (isCrafting() && recipe != null && canInsertRecipeResults(level, recipe))
        {
            // Accumulate energy for recipe
            if (energyCraftProgress < recipe.getEnergyRequired())
            {
                int toExtract = Math.min(getEnergyUsage(), recipe.getEnergyRequired() - energyCraftProgress);
                energyCraftProgress += getEnergyStorage().extractEnergy(toExtract, false);
            }
            else // When done crafting
            {
                getOutputInventory().insertItem(0, recipe.generateFabricatingResult(level.random), false);

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
    public Item getValidBlueprintItem()
    {
        return LTXIItems.FABRICATION_BLUEPRINT.get();
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.AUTO_FABRICATOR.get();
    }

    @Override
    public void onItemSlotChanged(BlockInventoryType inventoryType, int slot)
    {
        super.onItemSlotChanged(inventoryType, slot);
        if (inventoryType == BlockInventoryType.INPUT || inventoryType == BlockInventoryType.OUTPUT)
        {
            shouldCheckRecipe = true;
        }
        else if (inventoryType == BlockInventoryType.AUXILIARY && slot == AUX_BLUEPRINT_SLOT)
        {
            shouldCheckRecipe = true;
            shouldCheckBlueprint = true;
        }
    }
}