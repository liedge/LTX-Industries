package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.BaseESABlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class EnergyStorageArrayMenu extends LTXIMachineMenu.EnergyMachineMenu<BaseESABlockEntity>
{
    public EnergyStorageArrayMenu(LimaMenuType<BaseESABlockEntity, ?> type, int containerId, Inventory inventory, BaseESABlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addHandlerSlotsGrid(menuContext().getChargingInventory(), 0, 56, 37, 4, 1);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < 5)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (LimaItemUtil.hasEnergyCapability(stack))
        {
            return quickMoveToContainerSlots(stack, 1, 5, false);
        }

        return false;
    }
}