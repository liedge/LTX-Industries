package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.MaterialFusingChamberBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class MaterialFusingChamberMenu extends LTXIMachineMenu.StandardRecipeMachineMenu<MaterialFusingChamberBlockEntity>
{
    public MaterialFusingChamberMenu(LimaMenuType<MaterialFusingChamberBlockEntity, ?> type, int containerId, Inventory inventory, MaterialFusingChamberBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlotsGrid(BlockContentsType.INPUT, 0, 40, 27, 3, 1);
        addRecipeOutputSlot(0, 134, 36);
        addFluidSlot(menuContext.getFluidHandlerOrThrow(BlockContentsType.INPUT), 0, 40, 45);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.getFluidHandlerOrThrow(BlockContentsType.INPUT).syncAllTanks(collector);
    }
}