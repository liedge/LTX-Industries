package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.GrinderBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class GrinderMenu extends LTXIMachineMenu.RecipeEnergyMachineMenu<GrinderBlockEntity>
{
    public GrinderMenu(LimaMenuType<GrinderBlockEntity, ?> type, int containerId, Inventory inventory, GrinderBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BlockContentsType.INPUT, 0, 44, 36);
        addRecipeOutputSlotsGrid(0, 102, 36, 3, 1);
        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
    }
}