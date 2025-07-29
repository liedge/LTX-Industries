package liedge.ltxindustries.menu;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.SidedItemEnergyMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.UpgradableMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntity;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class SidedUpgradableMachineMenu<CTX extends ItemHolderBlockEntity & SidedAccessBlockEntity & UpgradableMachineBlockEntity> extends UpgradableMachineMenu<CTX>
{
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected SidedUpgradableMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, int energySlotX, int energySlotY)
    {
        super(type, containerId, inventory, menuContext);
        addSlot(SidedItemEnergyMachineBlockEntity.ENERGY_ITEM_SLOT, energySlotX, energySlotY);
    }

    protected SidedUpgradableMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        this(type, containerId, inventory, menuContext, 8, 53);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleAction(IO_CONTROLS_BUTTON_ID, LTXINetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}