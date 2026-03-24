package liedge.ltxindustries.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class LimaSidebarButton extends LimaRenderableButton
{
    public static final int SIDEBAR_BUTTON_WIDTH = 18;
    public static final int SIDEBAR_BUTTON_HEIGHT = 20;
    private static final Identifier RIGHT_SPRITE = RESOURCES.id("widget/right_sidebar_button");
    private static final Identifier RIGHT_FOCUSED_SPRITE = RESOURCES.id("widget/right_sidebar_button_focus");
    private static final Identifier LEFT_SPRITE = RESOURCES.id("widget/left_sidebar_button");
    private static final Identifier LEFT_FOCUSED_SPRITE = RESOURCES.id("widget/left_sidebar_button_focus");

    private LimaSidebarButton(int x, int y, Component message)
    {
        super(x, y, SIDEBAR_BUTTON_WIDTH, SIDEBAR_BUTTON_HEIGHT, message);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        int xOffset = isLeftSided() ? 2 : 0;
        renderInnerContents(guiGraphics, getX() + xOffset, getY() + 2);
    }

    @Override
    protected boolean useFocusedSprite()
    {
        return isHovered();
    }

    @Override
    protected abstract Identifier focusedSprite();

    protected abstract void renderInnerContents(GuiGraphics graphics, int guiX, int guiY);

    protected abstract boolean isLeftSided();

    protected void renderSprite(GuiGraphics graphics, Identifier sprite, int x, int y)
    {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 16, 16);
    }

    public static abstract class LeftSided extends LimaSidebarButton
    {
        public LeftSided(int x, int y, Component message)
        {
            super(x, y, message);
        }

        @Override
        protected Identifier unfocusedSprite()
        {
            return LEFT_SPRITE;
        }

        @Override
        protected Identifier focusedSprite()
        {
            return LEFT_FOCUSED_SPRITE;
        }

        @Override
        protected boolean isLeftSided()
        {
            return true;
        }
    }

    public static abstract class RightSided extends LimaSidebarButton
    {
        public RightSided(int x, int y, Component message)
        {
            super(x, y, message);
        }

        @Override
        protected Identifier unfocusedSprite()
        {
            return RIGHT_SPRITE;
        }

        @Override
        protected Identifier focusedSprite()
        {
            return RIGHT_FOCUSED_SPRITE;
        }

        @Override
        protected boolean isLeftSided()
        {
            return false;
        }
    }
}