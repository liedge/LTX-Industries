package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.EnergyStorageArrayBlockEntity;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class EnergyStorageArrayMenu extends LimaItemHandlerMenu<EnergyStorageArrayBlockEntity>
{
    public EnergyStorageArrayMenu(LimaMenuType<EnergyStorageArrayBlockEntity, ?> type, int containerId, Inventory inventory, EnergyStorageArrayBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addContextSlot(0, 8, 62);
        addContextSlotsGrid(1, 56, 37, 4, 1);

        addPlayerInventory(DEFAULT_INV_X, DEFAULT_INV_Y);
        addPlayerHotbar(DEFAULT_INV_X, DEFAULT_HOTBAR_Y);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(menuContext.getEnergyStorage().createDataWatcher());
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

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}