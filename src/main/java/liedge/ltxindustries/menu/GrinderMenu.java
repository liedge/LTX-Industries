package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.GrinderBlockEntity;
import liedge.ltxindustries.blockentity.SidedItemEnergyMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GrinderMenu extends SidedUpgradableMachineMenu<GrinderBlockEntity>
{
    public GrinderMenu(LimaMenuType<GrinderBlockEntity, ?> type, int containerId, Inventory inventory, GrinderBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(1, 44, 36);
        addRecipeResultSlotsGrid(2, 102, 36, 3, 1, LTXIRecipeTypes.GRINDING);
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
        if (index == SidedItemEnergyMachineBlockEntity.ENERGY_ITEM_SLOT || menuContext.isInputSlot(index))
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (menuContext.isOutputSlot(index))
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
                return quickMoveToContainerSlot(stack, 1);
            }
        }
    }
}