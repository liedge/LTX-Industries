package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.screen.UpgradesConfigScreen;
import liedge.ltxindustries.menu.LTXIMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;

public class MachineUpgradesButton extends LimaSidebarButton.RightSided
{
    private final LimaMenuScreen<?> parent;

    public MachineUpgradesButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LTXILangKeys.MACHINE_UPGRADES_SIDEBAR_TOOLTIP.translate());
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    protected void renderInnerContents(GuiGraphics graphics, int guiX, int guiY)
    {
        renderSprite(graphics, UpgradesConfigScreen.MACHINE_MODULE_SPRITE, guiX, guiY);
    }

    @Override
    public void onPress(InputWithModifiers input)
    {
        if (input.isLeft()) parent.sendUnitButtonData(LTXIMachineMenu.UPGRADES_BUTTON_ID);
    }
}