package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenuSlot;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.BaseTurretBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class TurretMenu<BE extends BaseTurretBlockEntity> extends LTXIMachineMenu.EnergyMachineMenu<BE>
{
    public TurretMenu(LimaMenuType<BE, ?> type, int containerId, Inventory inventory, BE menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlotsGrid(menuContext.getOutputInventory(), 0, 80, 23, 5, 4, (ctr, slot, sx, sy) -> new LimaItemHandlerMenuSlot(ctr, slot, sx, sy, false));
        addPlayerInventoryAndHotbar(8, 106);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < inventoryStart)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (LimaItemUtil.hasEnergyCapability(stack))
        {
            return quickMoveToContainerSlot(stack, 0);
        }
        else
        {
            return false;
        }
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
    }
}