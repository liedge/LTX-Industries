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

        menuContext.getEnergy().syncAllProperties(this);
        addDataWatcher(menuContext.syncEnergyGeneration());
        addDataWatcher(menuContext.syncFuelUnits());
    }
}