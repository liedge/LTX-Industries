package liedge.ltxindustries.menu;

import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class MachineBaseMenu<CTX extends MachineBaseBlockEntity> extends BlockEntityMenu<CTX>
{
    public static final int UPGRADES_BUTTON_ID = 0;
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected MachineBaseMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(UPGRADES_BUTTON_ID,
                sender -> LimaMenuProvider.create(LTXIMenus.MACHINE_UPGRADES.get(), menuContext, null, false).openMenuScreen(sender));
        builder.handleAction(IO_CONTROLS_BUTTON_ID, LTXINetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}