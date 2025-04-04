package liedge.limatech.menu;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.UpgradableMachineBlockEntity;
import liedge.limatech.blockentity.base.SidedAccessBlockEntity;
import liedge.limatech.registry.game.LimaTechNetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class SidedUpgradableMachineMenu<CTX extends ItemHolderBlockEntity & SidedAccessBlockEntity & UpgradableMachineBlockEntity> extends UpgradableMachineMenu<CTX>
{
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected SidedUpgradableMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleAction(IO_CONTROLS_BUTTON_ID, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}