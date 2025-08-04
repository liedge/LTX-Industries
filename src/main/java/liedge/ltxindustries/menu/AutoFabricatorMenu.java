package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.AutoFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AutoFabricatorMenu extends LTXIMachineMenu.EnergyMachineMenu<AutoFabricatorBlockEntity>
{
    public AutoFabricatorMenu(LimaMenuType<AutoFabricatorBlockEntity, ?> type, int containerId, Inventory inventory, AutoFabricatorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addHandlerRecipeOutputSlot(menuContext.getOutputInventory(), 0, 152, 73, LTXIRecipeTypes.FABRICATING);
        addHandlerSlot(menuContext.getAuxInventory(), BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 120, 73);
        addHandlerSlotsGrid(menuContext.getInputInventory(), 0, 33, 30, 8, 2);
        addPlayerInventoryAndHotbar(15, 98);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < inventoryStart)
        {
            return quickMoveToAllInventory(stack, index == 1);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
                return quickMoveToContainerSlot(stack, ENERGY_SLOT_INDEX);
            else if (stack.is(menuContext.getValidBlueprintItem()) && stack.get(LTXIDataComponents.BLUEPRINT_RECIPE) != null)
                return quickMoveToContainerSlot(stack, 2);
            else
                return quickMoveToContainerSlots(stack, 3, 19, false);
        }
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        collector.register(menuContext.keepProgressSynced());
        collector.register(menuContext.getRecipeCheck().createDataWatcher());
    }
}