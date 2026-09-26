package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.blockentity.BaseECABlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class EnergyCellArrayMenu extends LTXIMachineMenu<BaseECABlockEntity>
{
    public EnergyCellArrayMenu(MenuType<?> type, int containerId, Inventory inventory, BaseECABlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, false);

        addSlotsGrid(BlockContentsType.GENERAL, 0, 56, 37, 4, 1);

        addDefaultPlayerInventoryAndHotbar();

        menuContext.getEnergy().syncAllProperties(this);
    }
}