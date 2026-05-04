package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.RepairStationBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class RepairStationMenu extends LTXIMachineMenu<RepairStationBlockEntity>
{
    public RepairStationMenu(LimaMenuType<RepairStationBlockEntity, ?> type, int containerId, Inventory inventory, RepairStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, true);

        addSlot(BlockContentsType.INPUT, 0, 56, 34);
        addOutputSlot(0, 104, 34);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.keepTimedProcessSynced(collector);
    }
}