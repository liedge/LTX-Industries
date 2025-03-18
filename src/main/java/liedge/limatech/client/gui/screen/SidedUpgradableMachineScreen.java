package liedge.limatech.client.gui.screen;

import liedge.limatech.blockentity.base.BlockEntityInputType;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import liedge.limatech.menu.SidedUpgradableMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static liedge.limatech.menu.SidedUpgradableMachineMenu.IO_CONTROLS_BUTTON_ID;

public abstract class SidedUpgradableMachineScreen<M extends SidedUpgradableMachineMenu<?>> extends UpgradableMachineScreen<M>
{
    protected SidedUpgradableMachineScreen(M menu, Inventory inventory, Component title, int primaryWidth, int height)
    {
        super(menu, inventory, title, primaryWidth, height);
    }

    @Override
    protected void addSidebarWidgets()
    {
        super.addSidebarWidgets();

        for (BlockEntityInputType type : menu.menuContext().getType().getValidInputTypes())
        {
            int yOffset = 23 + (type.ordinal() * 20);
            addRenderableWidget(new OpenIOControlButton(rightPos, topPos + yOffset, this, IO_CONTROLS_BUTTON_ID, type));
        }

        //addRenderableWidget(new OpenIOControlButton(rightPos, topPos + 23, this, IO_CONTROLS_BUTTON_ID, BlockEntityInputType.ITEMS));
        //addRenderableWidget(new OpenIOControlButton(rightPos, topPos + 43, this, IO_CONTROLS_BUTTON_ID, BlockEntityInputType.ENERGY));
    }
}