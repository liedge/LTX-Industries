package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.AutoFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.entity.player.Inventory;

public class AutoFabricatorMenu extends LTXIMachineMenu<AutoFabricatorBlockEntity>
{
    public AutoFabricatorMenu(LimaMenuType<AutoFabricatorBlockEntity, ?> type, int containerId, Inventory inventory, AutoFabricatorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlotsGrid(BlockContentsType.INPUT, 0, 33, 30, 8, 2);
        addSlot(BlockContentsType.AUXILIARY, BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 120, 73);
        addRecipeOutputSlot(0, 152, 73, LTXIRecipeTypes.FABRICATING);
        addPlayerInventoryAndHotbar(15, 98);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        collector.register(menuContext.keepProgressSynced());
        collector.register(menuContext.getRecipeCheck().keepLastUsedSynced());
    }
}