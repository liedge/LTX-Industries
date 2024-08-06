package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaRenderable;
import liedge.limacore.client.gui.UnmanagedSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.Mth;

public class ScrollbarWidget implements LimaRenderable, NarratableEntry, GuiEventListener
{
    private final int x;
    private final int y;
    private final int height;
    private final int scrollRange;
    private final ScrollableGUIElement element;

    private boolean scrolling;
    private boolean focused;
    private int scrollPosition = 0;

    public ScrollbarWidget(int x, int y, int height, ScrollableGUIElement element)
    {
        this.x = x;
        this.y = y;
        this.height = height;
        this.scrollRange = height - 13;
        this.element = element;
    }

    private void setPositionFromMouse(double mouseY)
    {
        int relativeMouseY = ((int) mouseY - y) - (13 /2);
        setScrollPosition(relativeMouseY);
    }

    private void setScrollPosition(int scrollPosition)
    {
        int adjusted = Mth.clamp(scrollPosition, 0, scrollRange);
        if (this.scrollPosition != adjusted)
        {
            this.scrollPosition = adjusted;
            element.scrollUpdated(adjusted);
        }
    }

    public void reset()
    {
        setScrollPosition(0);
        setFocused(false);
    }

    public void moveScrollbar(int delta)
    {
        if (!scrolling && element.canScroll())
        {
            setScrollPosition(scrollPosition + delta);
        }
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return 8;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (element.canScroll())
        {
            UnmanagedSprite sprite = isFocused() ? ScreenWidgetSprites.SCROLLBAR_FOCUSED : ScreenWidgetSprites.SCROLLBAR_NOT_FOCUSED;
            sprite.singleBlit(graphics, x, y + scrollPosition);
        }
        else
        {
            ScreenWidgetSprites.SCROLLBAR_DISABLED.singleBlit(graphics, x, y);
        }
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

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return LimaRenderable.super.isMouseOver(mouseX, mouseY);
    }
}