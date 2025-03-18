package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenuSlot;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RocketTurretMenu extends SidedUpgradableMachineMenu<RocketTurretBlockEntity>
{
    public RocketTurretMenu(LimaMenuType<RocketTurretBlockEntity, ?> type, int containerId, Inventory inventory, RocketTurretBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(0, 8, 62);
        addSlotsGrid(menuContainer(), 1, 80, 23, 5, 4, (container, index, x, y) -> new LimaItemHandlerMenuSlot(container, index, x, y, false));

        addPlayerInventoryAndHotbar(8, 106);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index >= 0 && index < 21)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (LimaItemUtil.hasEnergyCapability(stack))
        {
            return quickMoveToContainerSlot(stack, 0);
        }

        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
    }
}