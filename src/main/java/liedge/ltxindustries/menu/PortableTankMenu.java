package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.PortableTankBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class PortableTankMenu extends MachineBaseMenu<PortableTankBlockEntity>
{
    public PortableTankMenu(MenuType<?> type, int containerId, Inventory inventory, PortableTankBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addFluidSlot(BlockContentsType.GENERAL, 0, 80, 36);
        addDefaultPlayerInventoryAndHotbar();

        getTank().syncAllProperties(this);
    }

    private LimaBlockEntityFluids getTank()
    {
        return menuContext.getFluidsOrThrow(BlockContentsType.GENERAL);
    }
}