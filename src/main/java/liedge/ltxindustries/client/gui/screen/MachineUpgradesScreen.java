package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.menu.MachineUpgradeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MachineUpgradesScreen extends UpgradesConfigScreen<MachineUpgradeMenu>
{
    public MachineUpgradesScreen(MachineUpgradeMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 18);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, MachineUpgradeMenu.BACK_BUTTON_ID));
    }
}