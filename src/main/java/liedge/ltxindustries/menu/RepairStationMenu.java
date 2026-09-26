package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.blockentity.RepairStationBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class RepairStationMenu extends LTXIMachineMenu<RepairStationBlockEntity>
{
    public RepairStationMenu(MenuType<?> type, int containerId, Inventory inventory, RepairStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, true);

        addSlot(BlockContentsType.INPUT, 0, 56, 34);
        addOutputSlot(0, 104, 34);

        addDefaultPlayerInventoryAndHotbar();

        menuContext.getEnergy().syncAllProperties(this);
        menuContext.keepEnergyConsumerPropertiesSynced(this);
        menuContext.keepTimedProcessSynced(this);
    }
}