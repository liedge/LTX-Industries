package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.MolecularReconstructorBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class MolecularReconstructorMenu extends LTXIMachineMenu<MolecularReconstructorBlockEntity>
{
    public MolecularReconstructorMenu(LimaMenuType<MolecularReconstructorBlockEntity, ?> type, int containerId, Inventory inventory, MolecularReconstructorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BlockContentsType.INPUT, 0, 56, 34);
        addOutputSlot(0, 104, 34);

        //addSlot(new LimaHandlerSlot(menuContext.getInputInventory(), 0, 56, 34, true, stack -> stack.isDamaged() && !stack.is(LTXITags.Items.REPAIR_BLACKLIST)));
        //addSlot(new LimaHandlerSlot(menuContext.getOutputInventory(), 0, 104, 34, false, LimaItemUtil.ALWAYS_FALSE));

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.keepTimedProcessSynced(collector);
    }
}