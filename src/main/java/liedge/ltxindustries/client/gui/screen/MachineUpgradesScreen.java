package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.menu.MachineUpgradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineUpgradesScreen extends UpgradesConfigScreen<MachineUpgrade, MachineUpgradeMenu>
{
    private static final ResourceLocation SLOT_SPRITE = LTXIndustries.RESOURCES.location("slot/machine_module");

    public MachineUpgradesScreen(MachineUpgradeMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.leftPadding = 18;
    }

    @Override
    protected void blitSlotSprites(GuiGraphics graphics)
    {
        blitSlotSprite(graphics, SLOT_SPRITE, 23, 86);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, MachineUpgradeMenu.BACK_BUTTON_ID));
    }
}