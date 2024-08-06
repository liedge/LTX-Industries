package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.MaterialFusingChamberBlockEntity;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MaterialFusingChamberMenu extends LimaItemHandlerMenu<MaterialFusingChamberBlockEntity>
{
    public MaterialFusingChamberMenu(LimaMenuType<MaterialFusingChamberBlockEntity, ?> type, int containerId, Inventory inventory, MaterialFusingChamberBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addContextSlot(0, 8, 62);
        addContextSlot(1, 42, 27);
        addContextSlot(2, 60, 27);
        addContextSlot(3, 51, 45);
        addContextRecipeResultSlot(4, 112, 36, LimaTechRecipeTypes.MATERIAL_FUSING);

        addPlayerInventory(DEFAULT_INV_X, DEFAULT_INV_Y);
        addPlayerHotbar(DEFAULT_INV_X, DEFAULT_HOTBAR_Y);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(menuContext.getEnergyStorage().createDataWatcher());
        collector.register(menuContext.keepProcessSynced());
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

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}