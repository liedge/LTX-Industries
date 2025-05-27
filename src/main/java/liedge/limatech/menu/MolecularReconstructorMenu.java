package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.blockentity.MolecularReconstructorBlockEntity;
import liedge.limatech.blockentity.SidedItemEnergyMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MolecularReconstructorMenu extends SidedUpgradableMachineMenu<MolecularReconstructorBlockEntity>
{
    public MolecularReconstructorMenu(LimaMenuType<MolecularReconstructorBlockEntity, ?> type, int containerId, Inventory inventory, MolecularReconstructorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(SidedItemEnergyMachineBlockEntity.ENERGY_ITEM_SLOT, 8, 62);
        addSlot(MolecularReconstructorBlockEntity.REPAIR_INPUT_SLOT, 59, 34);
        addSlot(MolecularReconstructorBlockEntity.REPAIR_OUTPUT_SLOT, 98, 34, false);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index <= MolecularReconstructorBlockEntity.REPAIR_OUTPUT_SLOT)
        {
            return quickMoveToAllInventory(stack, index == MolecularReconstructorBlockEntity.REPAIR_OUTPUT_SLOT);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
            {
                return quickMoveToContainerSlot(stack, SidedItemEnergyMachineBlockEntity.ENERGY_ITEM_SLOT);
            }
            else if (stack.isDamaged() && !stack.is(LimaTechTags.Items.REPAIR_BLACKLIST))
            {
                return quickMoveToContainerSlot(stack, MolecularReconstructorBlockEntity.REPAIR_INPUT_SLOT);
            }
        }

        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
    }
}