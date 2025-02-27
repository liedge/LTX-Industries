package liedge.limatech.client.gui.screen;

import liedge.limatech.blockentity.io.MachineInputType;
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
        addRenderableWidget(new OpenIOControlButton(rightPos, topPos + 23, this, IO_CONTROLS_BUTTON_ID, MachineInputType.ITEMS));
        addRenderableWidget(new OpenIOControlButton(rightPos, topPos + 43, this, IO_CONTROLS_BUTTON_ID, MachineInputType.ENERGY));
    }
}