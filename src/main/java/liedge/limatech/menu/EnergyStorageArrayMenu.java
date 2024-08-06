package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.EnergyStorageArrayBlockEntity;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import static liedge.limatech.blockentity.EnergyStorageArrayBlockEntity.INPUT_ENERGY_ITEM_SLOT;
import static liedge.limatech.blockentity.EnergyStorageArrayBlockEntity.OUTPUT_ENERGY_ITEM_SLOT;

public class EnergyStorageArrayMenu extends LimaItemHandlerMenu<EnergyStorageArrayBlockEntity>
{
    public EnergyStorageArrayMenu(LimaMenuType<EnergyStorageArrayBlockEntity, ?> type, int containerId, Inventory inventory, EnergyStorageArrayBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addContextSlot(INPUT_ENERGY_ITEM_SLOT, 62, 41);
        addContextSlot(OUTPUT_ENERGY_ITEM_SLOT, 98, 41);

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
        return false;
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}