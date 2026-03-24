package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class LTXIMachineMenu<CTX extends LTXIMachineBlockEntity> extends BlockEntityMenu<CTX>
{
    public static final int UPGRADES_BUTTON_ID = 0;
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected LTXIMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, boolean allowEnergySlotQuickTransfer)
    {
        super(type, containerId, inventory, menuContext);

        //addSlot(BlockContentsType.AUXILIARY, LTXIMachineBlockEntity.AUX_ENERGY_ITEM_SLOT, 8, 53, stack -> allowEnergySlotQuickTransfer && LimaItemUtil.hasEnergyCapability(stack));
        addSlot(BlockContentsType.AUXILIARY, LTXIMachineBlockEntity.AUX_ENERGY_ITEM_SLOT, 8, 53);
    }

    protected LTXIMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        this(type, containerId, inventory, menuContext, true);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(UPGRADES_BUTTON_ID,
                sender -> LimaMenuProvider.create(LTXIMenus.MACHINE_UPGRADES.get(), menuContext, null, false).openMenuScreen(sender));
        builder.handleAction(IO_CONTROLS_BUTTON_ID, LTXINetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}