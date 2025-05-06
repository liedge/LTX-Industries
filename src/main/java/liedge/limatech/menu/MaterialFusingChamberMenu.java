package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.MaterialFusingChamberBlockEntity;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MaterialFusingChamberMenu extends SidedUpgradableMachineMenu<MaterialFusingChamberBlockEntity>
{
    public MaterialFusingChamberMenu(LimaMenuType<MaterialFusingChamberBlockEntity, ?> type, int containerId, Inventory inventory, MaterialFusingChamberBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addSlot(0, 8, 62);
        addSlot(1, 42, 27);
        addSlot(2, 60, 27);
        addSlot(3, 51, 45);
        addRecipeResultSlot(4, 112, 36, LimaTechRecipeTypes.MATERIAL_FUSING);

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
        if (index >= 0 && index < 4)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (index == 4)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
            {
                return quickMoveToContainerSlot(stack, 0);
            }
            else
            {
                return quickMoveToContainerSlots(stack, 1, 4, false);
            }
        }
    }
}