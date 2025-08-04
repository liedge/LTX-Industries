package liedge.ltxindustries.menu;

import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.MaterialFusingChamberBlockEntity;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MaterialFusingChamberMenu extends LTXIMachineMenu.EnergyMachineMenu<MaterialFusingChamberBlockEntity>
{
    public MaterialFusingChamberMenu(LimaMenuType<MaterialFusingChamberBlockEntity, ?> type, int containerId, Inventory inventory, MaterialFusingChamberBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        LimaBlockEntityItemHandler inputInv = menuContext.getInputInventory();
        addHandlerSlot(inputInv, 0, 42, 27);
        addHandlerSlot(inputInv, 1, 60, 27);
        addHandlerSlot(inputInv, 2, 51, 45);
        addHandlerRecipeOutputSlot(menuContext.getOutputInventory(), 0, 112, 36, LTXIRecipeTypes.MATERIAL_FUSING);

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
        if (index < 4)
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
                return quickMoveToContainerSlots(stack, 1, 4, false);
            }
        }
    }
}