package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.blockentity.MolecularReconstructorBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MolecularReconstructorMenu extends LTXIMachineMenu.EnergyMachineMenu<MolecularReconstructorBlockEntity>
{
    public MolecularReconstructorMenu(LimaMenuType<MolecularReconstructorBlockEntity, ?> type, int containerId, Inventory inventory, MolecularReconstructorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addHandlerSlot(menuContext.getInputInventory(), 0, 56, 34);
        addHandlerSlot(menuContext.getOutputInventory(), 0, 104, 34, false);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < inventoryStart)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
            {
                return quickMoveToContainerSlot(stack, ENERGY_SLOT_INDEX);
            }
            else if (stack.isDamaged() && !stack.is(LTXITags.Items.REPAIR_BLACKLIST))
            {
                return quickMoveToContainerSlot(stack, 1);
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