package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.UpgradeStationBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class UpgradeStationMenu extends BlockEntityMenu<UpgradeStationBlockEntity>
{
    public UpgradeStationMenu(LimaMenuType<UpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, UpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addSlot(BlockContentsType.GENERAL, UpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT, 17, 22);
        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 66);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) { }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(0, menuContext::openUpgradesSubMenu);
        builder.handleUnitAction(1, menuContext::openColorConfigMenu);
    }
}