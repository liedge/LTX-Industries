package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.UnmanagedSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.SIDEBAR_BUTTON_FOCUSED;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.SIDEBAR_BUTTON_NOT_FOCUSED;

public abstract class LimaSidebarButton extends LimaRenderableButton
{
    protected LimaSidebarButton(int x, int y, Component message)
    {
        super(x, y, 18, 20, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        iconSprite().singleBlit(guiGraphics, getX(), getY() + 2, 1);
    }

    @Override
    protected UnmanagedSprite unfocusedSprite()
    {
        return SIDEBAR_BUTTON_NOT_FOCUSED;
    }

    @Override
    protected UnmanagedSprite focusedSprite()
    {
        return SIDEBAR_BUTTON_FOCUSED;
    }

    protected abstract UnmanagedSprite iconSprite();
}