package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.SharedMenuButtons;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.Identifier;

public class SubMenuBackButton extends LTXISidebarButton.LeftSided
{
    private static final Identifier SPRITE = LTXIndustries.RESOURCES.id("widget/back");

    private final LimaMenuScreen<?> parent;

    public SubMenuBackButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LTXILangKeys.BACK_BUTTON_LABEL.translate());
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    protected void extractInnerContents(GuiGraphicsExtractor graphics, int guiX, int guiY)
    {
        renderSprite(graphics, SPRITE, guiX, guiY);
    }

    @Override
    protected void onPress()
    {
        parent.sendUnitButtonData(SharedMenuButtons.EXIT_SUB_MENU);
    }
}