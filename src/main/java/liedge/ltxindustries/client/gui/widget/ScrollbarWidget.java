package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.BaseLimaRenderable;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScrollbarWidget extends BaseLimaRenderable implements NarratableEntry, GuiEventListener
{
    private static final int SCROLLER_WIDTH = 8;
    static final int SCROLLER_HEIGHT = 13;
    private static final ResourceLocation SCROLLER_SPRITE = LTXIndustries.RESOURCES.location("widget/scroller");

    private final int scrollRange;
    private final ScrollableGUIElement element;

    private boolean scrolling;
    private boolean focused;
    private int scrollPosition = 0;

    public ScrollbarWidget(int x, int y, int height, ScrollableGUIElement element)
    {
        super(x, y, SCROLLER_WIDTH, height);
        this.scrollRange = height - SCROLLER_HEIGHT;
        this.element = element;
    }

    private void setPositionFromMouse(double mouseY)
    {
        int relativeMouseY = ((int) mouseY - getY()) - (SCROLLER_HEIGHT / 2);
        setScrollPosition(relativeMouseY, false);
    }

    private void setScrollPosition(int scrollPosition, boolean forceUpdate)
    {
        scrollPosition = Mth.clamp(scrollPosition, 0, scrollRange);
        if (this.scrollPosition != scrollPosition || forceUpdate)
        {
            this.scrollPosition = scrollPosition;
            element.scrollUpdated(scrollPosition);
        }
    }

    public void reset()
    {
        setScrollPosition(0, true);
        setFocused(false);
    }

    @Deprecated(forRemoval = true)
    public void moveScrollbar(int delta)
    {
        if (!scrolling && element.canScroll())
        {
            setScrollPosition(scrollPosition + delta, false);
        }
    }

    public boolean moveScrollBar(double scrollWheelY)
    {
        if (!scrolling && element.canScroll())
        {
            int delta = element.getScrollDelta() * (int) -Math.signum(scrollWheelY);
            setScrollPosition(scrollPosition + delta, false);
            return true;
        }

        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.blitSprite(SCROLLER_SPRITE, getX(), getY() + scrollPosition, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    @Override
    public void setFocused(boolean focused)
    {
        this.focused = focused;
    }

    @Override
    public boolean isFocused()
    {
        return focused;
    }

    @Override
    public NarrationPriority narrationPriority()
    {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isMouseOver(mouseX, mouseY) && button == 0 && element.canScroll())
        {
            setPositionFromMouse(mouseY);
            this.scrolling = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (button == 0) this.scrolling = false;

        return GuiEventListener.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (scrolling)
        {
            setPositionFromMouse(mouseY);
            return true;
        }

        return false;
    }
}