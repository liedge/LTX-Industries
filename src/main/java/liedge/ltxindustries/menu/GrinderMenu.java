package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.GrinderBlockEntity;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GrinderMenu extends LTXIMachineMenu.EnergyMachineMenu<GrinderBlockEntity>
{
    public GrinderMenu(LimaMenuType<GrinderBlockEntity, ?> type, int containerId, Inventory inventory, GrinderBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addHandlerSlot(menuContext.getInputInventory(), 0, 44, 36);
        addHandlerRecipeOutputSlotGrid(menuContext.getOutputInventory(), 0, 102, 36, 3, 1, LTXIRecipeTypes.GRINDING);
        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < menuContext.getInputInventory().getSlots() + 1)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (index < inventoryStart)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
            {
                return quickMoveToContainerSlot(stack, ENERGY_SLOT_INDEX);
            }
            else
            {
                return quickMoveToContainerSlot(stack, 1);
            }
        }
    }
}