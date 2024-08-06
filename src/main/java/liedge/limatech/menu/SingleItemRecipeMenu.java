package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.SimpleRecipeMachineBlockEntity;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public abstract class SingleItemRecipeMenu<CTX extends SimpleRecipeMachineBlockEntity<?, ?>> extends LimaItemHandlerMenu<CTX>
{
    protected SingleItemRecipeMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addContextSlot(0, 8, 62);
        addContextSlot(1, 54, 34);
        addContextRecipeResultSlot(2, 106, 34, menuContext.machineRecipeType());

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
                return quickMoveToContainerSlot(stack, 0);
            }
            else
            {
                return quickMoveToContainerSlot(stack, 1);
            }
        }
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}