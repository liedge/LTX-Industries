package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.PortableTankBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class PortableTankMenu extends MachineBaseMenu<PortableTankBlockEntity>
{
    public PortableTankMenu(LimaMenuType<PortableTankBlockEntity, ?> type, int containerId, Inventory inventory, PortableTankBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addFluidSlot(BlockContentsType.GENERAL, 0, 80, 36);
        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        getTank().syncAllProperties(collector);
    }

    private LimaBlockEntityFluids getTank()
    {
        return menuContext.getFluidsOrThrow(BlockContentsType.GENERAL);
    }
}