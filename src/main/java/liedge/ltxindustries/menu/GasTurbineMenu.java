package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.blockentity.GasTurbineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class GasTurbineMenu extends MachineBaseMenu<GasTurbineBlockEntity>
{
    public GasTurbineMenu(MenuType<?> type, int containerId, Inventory inventory, GasTurbineBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addFluidSlot(BlockContentsType.INPUT, 0, 80, 26);
        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 66);

        menuContext.getEnergy().syncAllProperties(this);
        menuContext.syncEnergyGeneration(this);
        menuContext.getFluidsOrThrow(BlockContentsType.INPUT).syncAllProperties(this);
    }
}