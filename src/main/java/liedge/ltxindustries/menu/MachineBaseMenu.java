package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public abstract class MachineBaseMenu<CTX extends MachineBaseBlockEntity> extends BlockEntityMenu<CTX>
{
    protected MachineBaseMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    protected void initLayout(RecipeLayout layout)
    {
        for (LayoutSlot.Type slotType : LayoutSlot.Type.values())
        {
            BlockContentsType contentsType = slotType.getContentsType();
            if (contentsType == null) continue;

            List<LayoutSlot> layoutSlots = layout.getSlotsForType(slotType);
            for (int i = 0; i < layoutSlots.size(); i++)
            {
                LayoutSlot s = layoutSlots.get(i);

                switch (slotType)
                {
                    case ITEM_INPUT -> addSlot(contentsType, i, s.x(), s.y());
                    case ITEM_OUTPUT -> {
                        if (menuContext instanceof RecipeMachineBlockEntity<?,?> recipeMachine)
                            addRecipeOutputSlot(i, s.x(), s.y(), recipeMachine.getRecipeCheck().getRecipeType());
                        else
                            addSlot(contentsType, i, s.x(), s.y(), slot -> slot.allowPlacement(false));
                    }
                    case FLUID_INPUT -> addFluidSlot(contentsType, i, s.x(), s.y());
                    case FLUID_OUTPUT -> addFluidSlot(contentsType, i, s.x(), s.y(), slot -> slot.setAllowPlace(false));
                }
            }
        }
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(SharedMenuButtons.OPEN_UPGRADES,
                sender -> LimaMenuProvider.create(LTXIMenus.MACHINE_UPGRADES.get(), menuContext, null, false).openMenuScreen(sender));
        builder.handleAction(SharedMenuButtons.OPEN_IO_CONTROLS, LTXINetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }
}