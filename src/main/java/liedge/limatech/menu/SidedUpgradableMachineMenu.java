package liedge.limatech.menu;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.UpgradableMachineBlockEntity;
import liedge.limatech.blockentity.io.SidedMachineIOHolder;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class SidedUpgradableMachineMenu<CTX extends ItemHolderBlockEntity & SidedMachineIOHolder & UpgradableMachineBlockEntity> extends LimaItemHandlerMenu<CTX>
{
    public static final int UPGRADES_BUTTON_ID = 0;
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected SidedUpgradableMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(UPGRADES_BUTTON_ID, sender -> LimaMenuProvider.openStandaloneMenu(sender, LimaTechMenus.MACHINE_UPGRADES.get(), menuContext));
        builder.handleAction(IO_CONTROLS_BUTTON_ID, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}