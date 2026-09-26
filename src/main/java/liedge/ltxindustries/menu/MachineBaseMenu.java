package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.BlockEntityMenuProvider;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.List;

public abstract class MachineBaseMenu<CTX extends MachineBaseBlockEntity> extends BlockEntityMenu<CTX>
{
    protected MachineBaseMenu(MenuType<?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);

        handleUnitButton(SharedMenuButtons.OPEN_UPGRADES, sender -> {
            MenuProvider provider = new BlockEntityMenuProvider(LTXIMenus.MACHINE_UPGRADES, getMenuContext(), LTXILangKeys.GUI_UPGRADES.translate(), false);
            sender.openMenu(provider);
        });
        handleButton(SharedMenuButtons.OPEN_IO_CONTROLS, LTXINetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlsSubMenu);
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
}