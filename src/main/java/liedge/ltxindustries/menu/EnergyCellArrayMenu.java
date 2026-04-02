package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.BaseECABlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class EnergyCellArrayMenu extends LTXIMachineMenu<BaseECABlockEntity>
{
    public EnergyCellArrayMenu(LimaMenuType<BaseECABlockEntity, ?> type, int containerId, Inventory inventory, BaseECABlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, false);

        addSlotsGrid(BlockContentsType.GENERAL, 0, 56, 37, 4, 1);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
    }
}