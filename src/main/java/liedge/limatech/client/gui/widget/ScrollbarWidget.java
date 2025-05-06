package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaRenderable;
import liedge.limatech.LimaTech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScrollbarWidget implements LimaRenderable, NarratableEntry, GuiEventListener
{
    private static final ResourceLocation SCROLLER_SPRITE = LimaTech.RESOURCES.location("scroller");
    private static final ResourceLocation FOCUSED_SCROLLER_SPRITE = LimaTech.RESOURCES.location("scroller_focus");
    private static final ResourceLocation DISABLED_SCROLLER_SPRITE = LimaTech.RESOURCES.location("scroller_disabled");

    private final int x;
    private final int y;
    private final int height;
    private final int scrollRange;
    private final ScrollableGUIElement element;

    private final TextureAtlasSprite scrollerSprite;
    private final TextureAtlasSprite focusScrollerSprite;
    private final TextureAtlasSprite disabledScrollerSprite;

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

        this.scrollerSprite = LimaWidgetSprites.sprite(SCROLLER_SPRITE);
        this.focusScrollerSprite = LimaWidgetSprites.sprite(FOCUSED_SCROLLER_SPRITE);
        this.disabledScrollerSprite = LimaWidgetSprites.sprite(DISABLED_SCROLLER_SPRITE);
    }

    private void setPositionFromMouse(double mouseY)
    {
        int relativeMouseY = ((int) mouseY - y) - (13 /2);
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

    public void moveScrollbar(int delta)
    {
        if (!scrolling && element.canScroll())
        {
            setScrollPosition(scrollPosition + delta, false);
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
            TextureAtlasSprite sprite = isFocused() ? focusScrollerSprite : scrollerSprite;
            graphics.blit(x, y + scrollPosition, 0, 8, 13, sprite);
        }
        else
        {
            graphics.blit(x, y, 0, 8, 13, disabledScrollerSprite);
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