package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.CookingBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class CookingMachineMenu<CTX extends CookingBlockEntity<?>> extends LTXIMachineMenu.RecipeEnergyMachineMenu<CTX>
{
    public CookingMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BlockContentsType.INPUT, 0, 50, 34);
        addRecipeOutputSlot(0, 108, 34);

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