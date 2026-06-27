package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.PortableGeneratorBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class PortableGeneratorMenu extends MachineBaseMenu<PortableGeneratorBlockEntity>
{
    public PortableGeneratorMenu(LimaMenuType<PortableGeneratorBlockEntity, ?> type, int containerId, Inventory inventory, PortableGeneratorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BlockContentsType.INPUT, 0, 80, 26);
        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 66);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().syncAllProperties(collector);
        collector.register(menuContext.syncEnergyGeneration());
        collector.register(menuContext.syncFuelUnits());
    }
}