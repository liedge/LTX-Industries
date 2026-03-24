package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.resources.Identifier;

public class SubMenuBackButton extends LimaSidebarButton.LeftSided
{
    private static final Identifier SPRITE = LTXIndustries.RESOURCES.id("widget/back");

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
    protected void renderInnerContents(GuiGraphics graphics, int guiX, int guiY)
    {
        renderSprite(graphics, SPRITE, guiX, guiY);
    }

    @Override
    public void onPress(InputWithModifiers input)
    {
        if (input.isLeft()) parent.sendUnitButtonData(buttonId);
    }
}