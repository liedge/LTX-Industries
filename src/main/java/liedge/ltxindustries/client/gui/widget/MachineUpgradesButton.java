package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.LTXIMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class MachineUpgradesButton extends LimaSidebarButton.RightSided
{
    private static final ResourceLocation ICON_SPRITE = LTXIndustries.RESOURCES.location("machine_upgrade_module");

    private final LimaMenuScreen<?> parent;

    public MachineUpgradesButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LTXILangKeys.MACHINE_UPGRADES_SIDEBAR_TOOLTIP.translate());
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(LTXIMachineMenu.UPGRADES_BUTTON_ID);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int guiX, int guiY)
    {
        renderSprite(graphics, ICON_SPRITE, guiX, guiY);
    }
}