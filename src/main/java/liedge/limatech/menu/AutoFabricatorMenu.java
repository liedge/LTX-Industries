package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.AutoFabricatorBlockEntity;
import liedge.limatech.blockentity.BaseFabricatorBlockEntity;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AutoFabricatorMenu extends SidedUpgradableMachineMenu<AutoFabricatorBlockEntity>
{
    public AutoFabricatorMenu(LimaMenuType<AutoFabricatorBlockEntity, ?> type, int containerId, Inventory inventory, AutoFabricatorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(BaseFabricatorBlockEntity.ENERGY_ITEM_SLOT, 8, 62);
        addRecipeResultSlot(BaseFabricatorBlockEntity.OUTPUT_SLOT, 153, 73, LimaTechRecipeTypes.FABRICATING);
        addSlot(BaseFabricatorBlockEntity.BLUEPRINT_ITEM_SLOT, 120, 73);
        addSlotsGrid(3, 33, 30, 8, 2);
        addPlayerInventoryAndHotbar(15, 98);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index == BaseFabricatorBlockEntity.OUTPUT_SLOT)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else if (index < 19)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
                return quickMoveToContainerSlot(stack, BaseFabricatorBlockEntity.ENERGY_ITEM_SLOT);
            else if (stack.is(LimaTechItems.FABRICATION_BLUEPRINT) && stack.get(LimaTechDataComponents.BLUEPRINT_RECIPE) != null)
                return quickMoveToContainerSlot(stack, BaseFabricatorBlockEntity.BLUEPRINT_ITEM_SLOT);
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