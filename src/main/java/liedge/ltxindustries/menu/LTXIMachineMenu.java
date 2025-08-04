package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.template.EnergyMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class LTXIMachineMenu<CTX extends LTXIMachineBlockEntity> extends LimaItemHandlerMenu<CTX>
{
    public static final int UPGRADES_BUTTON_ID = 0;
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected static final int ENERGY_SLOT_INDEX = 0;

    protected LTXIMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(UPGRADES_BUTTON_ID, sender -> LimaMenuProvider.openStandaloneMenu(sender, LTXIMenus.MACHINE_UPGRADES.get(), menuContext));
        builder.handleAction(IO_CONTROLS_BUTTON_ID, LTXINetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }

    public static abstract class EnergyMachineMenu<CTX extends EnergyMachineBlockEntity> extends LTXIMachineMenu<CTX>
    {
        protected EnergyMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
        {
            super(type, containerId, inventory, menuContext);

            // Energy slot always in the same place, for now.
            addHandlerSlot(menuContext.getAuxInventory(), LTXIMachineBlockEntity.AUX_ENERGY_ITEM_SLOT, 8, 53);
        }
    }
}