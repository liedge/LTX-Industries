package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class SubMenuBackButton extends LimaSidebarButton.LeftSided
{
    private static final ResourceLocation SPRITE = LTXIndustries.RESOURCES.location("widget/back");

    private final LimaMenuScreen<?> parent;
    private final int buttonId;

    public SubMenuBackButton(int x, int y, LimaMenuScreen<?> parent, int buttonId)
    {
        super(x, y, LTXILangKeys.BACK_BUTTON_LABEL.translate());
        this.parent = parent;
        this.buttonId = buttonId;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int guiX, int guiY)
    {
        renderSprite(graphics, SPRITE, guiX, guiY);
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(buttonId);
    }
}