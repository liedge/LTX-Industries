package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.ltxindustries.blockentity.UpgradeStationBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class UpgradeStationMenu extends BlockEntityMenu<UpgradeStationBlockEntity>
{
    public UpgradeStationMenu(MenuType<?> type, int containerId, Inventory inventory, UpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addSlot(BlockContentsType.GENERAL, UpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT, 17, 22);
        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 66);

        handleUnitButton(0, menuContext::openUpgradesSubMenu);
        handleUnitButton(1, menuContext::openColorConfigMenu);
    }
}