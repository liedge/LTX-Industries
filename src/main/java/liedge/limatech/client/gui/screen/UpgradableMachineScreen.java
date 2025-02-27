package liedge.limatech.client.gui.screen;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.gui.widget.MachineUpgradesButton;
import liedge.limatech.menu.UpgradableMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class UpgradableMachineScreen<M extends UpgradableMachineMenu<?>> extends LimaMenuScreen<M>
{
    protected UpgradableMachineScreen(M menu, Inventory inventory, Component title, int primaryWidth, int height)
    {
        super(menu, inventory, title, primaryWidth, height, LimaTechConstants.LIME_GREEN.packedRGB());
        this.rightPadding = 18;
    }

    protected void addSidebarWidgets()
    {
        addRenderableWidget(new MachineUpgradesButton(rightPos, topPos + 3, this));
    }

    @Override
    protected void positionLabels()
    {
        super.positionLabels();
        if (alignInventoryLabelRight) this.inventoryLabelX = primaryWidth - 6 - font.width(playerInventoryTitle);
    }
}