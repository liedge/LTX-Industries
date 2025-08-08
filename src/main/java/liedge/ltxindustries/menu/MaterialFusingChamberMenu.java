package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuType;
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

        addHandlerSlotsGrid(menuContext.getInputInventory(), 0, 40, 27, 3, 1);
        addHandlerRecipeOutputSlot(menuContext.getOutputInventory(), 0, 134, 36, LTXIRecipeTypes.MATERIAL_FUSING);
        addFluidSlot(menuContext.getFluidHandler(), 0, 40, 45);

        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.getFluidHandler().syncAllTanks(collector);
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