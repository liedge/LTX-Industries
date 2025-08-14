package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.EnergyMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public abstract class LTXIMachineMenu<CTX extends LTXIMachineBlockEntity> extends BlockEntityMenu<CTX>
{
    public static final int UPGRADES_BUTTON_ID = 0;
    public static final int IO_CONTROLS_BUTTON_ID = 1;

    protected LTXIMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
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

    public static abstract class EnergyMachineMenu<CTX extends EnergyMachineBlockEntity> extends LTXIMachineMenu<CTX>
    {
        protected EnergyMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, boolean allowEnergySlotQuickTransfer)
        {
            super(type, containerId, inventory, menuContext);

            // Energy slot always in the same place, for now.
            addSlot(BlockContentsType.AUXILIARY, LTXIMachineBlockEntity.AUX_ENERGY_ITEM_SLOT, 8, 53, stack -> allowEnergySlotQuickTransfer && LimaItemUtil.hasEnergyCapability(stack));
        }

        protected EnergyMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
        {
            this(type, containerId, inventory, menuContext, true);
        }
    }

    public static abstract class RecipeEnergyMachineMenu<CTX extends EnergyMachineBlockEntity & RecipeMachineBlockEntity<?, ?>> extends EnergyMachineMenu<CTX>
    {
        protected RecipeEnergyMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
        {
            super(type, containerId, inventory, menuContext);
        }

        protected void addRecipeOutputSlot(int handlerIndex, int x, int y)
        {
            addRecipeOutputSlot(handlerIndex, x, y, menuContext.getRecipeCheck().getRecipeType());
        }

        protected void addRecipeOutputSlotsGrid(int indexStart, int x, int y, int columns, int rows)
        {
            addRecipeOutputSlotsGrid(indexStart, x, y, columns, rows, menuContext.getRecipeCheck().getRecipeType());
        }
    }
}