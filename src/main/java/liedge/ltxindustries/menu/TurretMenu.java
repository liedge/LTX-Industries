package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.ltxindustries.blockentity.turret.TurretBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class TurretMenu<BE extends TurretBlockEntity> extends LTXIMachineMenu<BE>
{
    public TurretMenu(LimaMenuType<BE, ?> type, int containerId, Inventory inventory, BE menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlotsGrid(menuContext.getOutputInventory(), 0, 80 ,23, 5, 4, (ctr, slot, sx, sy) -> new LimaItemSlot(ctr, slot, sx, sy, false));
        addPlayerInventoryAndHotbar(8, 106);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
    }
}