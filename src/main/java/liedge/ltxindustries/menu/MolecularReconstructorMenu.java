package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.menu.slot.LimaHandlerSlot;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.blockentity.MolecularReconstructorBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class MolecularReconstructorMenu extends LTXIMachineMenu.EnergyMachineMenu<MolecularReconstructorBlockEntity>
{
    public MolecularReconstructorMenu(LimaMenuType<MolecularReconstructorBlockEntity, ?> type, int containerId, Inventory inventory, MolecularReconstructorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(new LimaHandlerSlot(menuContext.getInputInventory(), 0, 56, 34, true, stack -> stack.isDamaged() && !stack.is(LTXITags.Items.REPAIR_BLACKLIST)));
        addSlot(new LimaHandlerSlot(menuContext.getOutputInventory(), 0, 104, 34, false, LimaItemUtil.ALWAYS_FALSE));

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
    }
}