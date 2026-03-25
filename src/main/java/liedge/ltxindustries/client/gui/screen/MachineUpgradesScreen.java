package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.menu.MachineUpgradeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MachineUpgradesScreen extends UpgradesConfigScreen<MachineUpgrade, MachineUpgradeMenu>
{
    private static final Identifier SLOT_SPRITE = LTXIndustries.RESOURCES.id("slot/machine_module");

    public MachineUpgradesScreen(MachineUpgradeMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 18);
    }

    @Override
    protected void blitSlotSprites(GuiGraphicsExtractor graphics)
    {
        blitSlotSprite(graphics, SLOT_SPRITE, 23, 86);
    }

    @Override
    protected Identifier fallbackModuleSprite()
    {
        return UpgradesConfigScreen.MACHINE_MODULE_SPRITE;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, MachineUpgradeMenu.BACK_BUTTON_ID));
    }
}