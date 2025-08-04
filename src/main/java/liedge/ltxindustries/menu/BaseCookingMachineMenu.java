package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.BaseCookingBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BaseCookingMachineMenu<CTX extends BaseCookingBlockEntity<?>> extends LTXIMachineMenu.EnergyMachineMenu<CTX>
{
    public BaseCookingMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addHandlerSlot(menuContext.getInputInventory(), 0, 54, 34);
        addHandlerRecipeOutputSlot(menuContext.getOutputInventory(), 0, 106, 34, menuContext.getRecipeCheck().getRecipeType());

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
        if (index < 2)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (index == 2)
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