package liedge.ltxindustries.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class LimaSidebarButton extends LimaRenderableButton
{
    public static final int SIDEBAR_BUTTON_WIDTH = 18;
    public static final int SIDEBAR_BUTTON_HEIGHT = 20;
    private static final ResourceLocation RIGHT_SPRITE = RESOURCES.location("widget/right_sidebar_button");
    private static final ResourceLocation RIGHT_FOCUSED_SPRITE = RESOURCES.location("widget/right_sidebar_button_focus");
    private static final ResourceLocation LEFT_SPRITE = RESOURCES.location("widget/left_sidebar_button");
    private static final ResourceLocation LEFT_FOCUSED_SPRITE = RESOURCES.location("widget/left_sidebar_button_focus");

    private LimaSidebarButton(int x, int y, Component message)
    {
        super(x, y, SIDEBAR_BUTTON_WIDTH, SIDEBAR_BUTTON_HEIGHT, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        int xOffset = isLeftSided() ? 2 : 0;
        renderContents(guiGraphics, getX() + xOffset, getY() + 2);
    }

    @Override
    protected boolean useFocusedSprite()
    {
        return isHovered();
    }

    @Override
    protected abstract ResourceLocation focusedSprite();

    protected abstract void renderContents(GuiGraphics graphics, int guiX, int guiY);

    protected abstract boolean isLeftSided();

    protected void renderSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y)
    {
        graphics.blitSprite(sprite, x, y, 16, 16);
    }

    public static abstract class LeftSided extends LimaSidebarButton
    {
        public LeftSided(int x, int y, Component message)
        {
            super(x, y, message);
        }

        @Override
        protected ResourceLocation unfocusedSprite()
        {
            return LEFT_SPRITE;
        }

        @Override
        protected ResourceLocation focusedSprite()
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
        protected ResourceLocation unfocusedSprite()
        {
            return RIGHT_SPRITE;
        }

        @Override
        protected ResourceLocation focusedSprite()
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