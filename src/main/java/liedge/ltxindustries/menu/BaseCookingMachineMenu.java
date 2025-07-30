package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.BaseCookingBlockEntity;
import liedge.ltxindustries.blockentity.SidedItemEnergyMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BaseCookingMachineMenu<CTX extends BaseCookingBlockEntity<?>> extends SidedUpgradableMachineMenu<CTX>
{
    public BaseCookingMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BaseCookingBlockEntity.INPUT_SLOT, 54, 34);
        addRecipeResultSlot(BaseCookingBlockEntity.OUTPUT_SLOT, 106, 34, menuContext.getRecipeCheck().getRecipeType());
        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < BaseCookingBlockEntity.OUTPUT_SLOT)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (index == BaseCookingBlockEntity.OUTPUT_SLOT)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
            {
                return quickMoveToContainerSlot(stack, SidedItemEnergyMachineBlockEntity.ENERGY_ITEM_SLOT);
            }
            else
            {
                return quickMoveToContainerSlot(stack, BaseCookingBlockEntity.INPUT_SLOT);
            }
        }
    }
}