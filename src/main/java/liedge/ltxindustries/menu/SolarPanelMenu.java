package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.SolarPanelBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelMenu extends MachineBaseMenu<SolarPanelBlockEntity>
{
    public SolarPanelMenu(LimaMenuType<SolarPanelBlockEntity, ?> type, int containerId, Inventory inventory, SolarPanelBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 66);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().syncAllProperties(collector);
        collector.register(menuContext.syncEnergyGeneration());
    }
}