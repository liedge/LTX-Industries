package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.client.gui.widget.OpenIOControlButton;
import liedge.ltxindustries.menu.SidedUpgradableMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static liedge.ltxindustries.menu.SidedUpgradableMachineMenu.IO_CONTROLS_BUTTON_ID;

public abstract class SidedUpgradableMachineScreen<M extends SidedUpgradableMachineMenu<?>> extends UpgradableMachineScreen<M>
{
    protected SidedUpgradableMachineScreen(M menu, Inventory inventory, Component title, int primaryWidth, int height)
    {
        super(menu, inventory, title, primaryWidth, height);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        for (BlockEntityInputType type : menu.menuContext().getType().getValidInputTypes())
        {
            int yOffset = 23 + (type.ordinal() * 20);
            addRenderableWidget(new OpenIOControlButton(rightPos, topPos + yOffset, this, IO_CONTROLS_BUTTON_ID, type));
        }
    }
}