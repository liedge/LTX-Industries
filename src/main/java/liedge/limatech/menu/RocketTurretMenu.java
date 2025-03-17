package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RocketTurretMenu extends UpgradableMachineMenu<RocketTurretBlockEntity>
{
    public RocketTurretMenu(LimaMenuType<RocketTurretBlockEntity, ?> type, int containerId, Inventory inventory, RocketTurretBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(0, 8, 62);
        addSlotsGrid(1, 80, 23, 5, 4);

        addPlayerInventoryAndHotbar(8, 106);

        //addSlotsGrid(menuContainer(), 1, 80, 23, 5, 4, (container, index, x, y) -> new LimaItemHandlerMenuSlot(container, index, x, y, false));
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
    }
}