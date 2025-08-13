package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.menu.slot.LimaHandlerSlot;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.BaseTurretBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class TurretMenu<BE extends BaseTurretBlockEntity> extends LTXIMachineMenu.EnergyMachineMenu<BE>
{
    public TurretMenu(LimaMenuType<BE, ?> type, int containerId, Inventory inventory, BE menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlotsGrid(menuContext.getOutputInventory(), 0, 80, 23, 5, 4, (ctr, slot, sx, sy) -> new LimaHandlerSlot(ctr, slot, sx, sy, false, LimaItemUtil.ALWAYS_FALSE));
        addPlayerInventoryAndHotbar(8, 106);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
    }
}