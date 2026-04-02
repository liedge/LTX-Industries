package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.screen.UpgradesConfigScreen;
import liedge.ltxindustries.menu.LTXIMachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;

public class MachineUpgradesButton extends LTXISidebarButton.RightSided
{
    private final LimaMenuScreen<?> parent;

    public MachineUpgradesButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LTXILangKeys.MACHINE_UPGRADES_SIDEBAR_TOOLTIP.translate());
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    protected void extractInnerContents(GuiGraphicsExtractor graphics, int guiX, int guiY)
    {
        renderSprite(graphics, UpgradesConfigScreen.MODULE_SPRITE, guiX, guiY);
    }

    @Override
    protected void onPress()
    {
        parent.sendUnitButtonData(LTXIMachineMenu.UPGRADES_BUTTON_ID);
    }
}