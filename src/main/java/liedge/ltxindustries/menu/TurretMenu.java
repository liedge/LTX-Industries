package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.turret.TurretBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class TurretMenu<BE extends TurretBlockEntity> extends LTXIMachineMenu<BE>
{
    public TurretMenu(LimaMenuType<BE, ?> type, int containerId, Inventory inventory, BE menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlotsGrid(BlockContentsType.OUTPUT, 0, 80, 23, 5, 4, slot -> slot.allowPlacement(false));
        addPlayerInventoryAndHotbar(8, 106);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
    }
}