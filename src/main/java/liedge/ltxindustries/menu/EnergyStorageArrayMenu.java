package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.BaseESABlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class EnergyStorageArrayMenu extends LTXIMachineMenu.EnergyMachineMenu<BaseESABlockEntity>
{
    public EnergyStorageArrayMenu(LimaMenuType<BaseESABlockEntity, ?> type, int containerId, Inventory inventory, BaseESABlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, false);

        addSlotsGrid(BlockContentsType.GENERAL, 0, 56, 37, 4, 1, LimaItemUtil::hasEnergyCapability);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
    }
}