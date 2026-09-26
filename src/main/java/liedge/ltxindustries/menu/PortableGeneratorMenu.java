package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.blockentity.PortableGeneratorBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class PortableGeneratorMenu extends MachineBaseMenu<PortableGeneratorBlockEntity>
{
    public PortableGeneratorMenu(MenuType<?> type, int containerId, Inventory inventory, PortableGeneratorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BlockContentsType.INPUT, 0, 80, 26);
        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 66);

        menuContext.getEnergy().syncAllProperties(this);
        addDataWatcher(menuContext.syncEnergyGeneration());
        addDataWatcher(menuContext.syncFuelUnits());
    }
}