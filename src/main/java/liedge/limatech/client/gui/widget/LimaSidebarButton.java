package liedge.limatech.client.gui.widget;

import liedge.limatech.LimaTech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class LimaSidebarButton extends LimaRenderableButton
{
    public static final int SIDEBAR_BUTTON_WIDTH = 18;
    public static final int SIDEBAR_BUTTON_HEIGHT = 20;
    private static final ResourceLocation SPRITE = LimaTech.RESOURCES.location("sidebar_button");
    private static final ResourceLocation FOCUS_SPRITE = LimaTech.RESOURCES.location("sidebar_button_focus");

    private final TextureAtlasSprite sprite;
    private final TextureAtlasSprite focusSprite;
    private final TextureAtlasSprite iconSprite;

    protected LimaSidebarButton(int x, int y, Component message, ResourceLocation iconSpriteLocation)
    {
        super(x, y, SIDEBAR_BUTTON_WIDTH, SIDEBAR_BUTTON_HEIGHT, message);
        this.sprite = LimaWidgetSprites.sprite(SPRITE);
        this.focusSprite = LimaWidgetSprites.sprite(FOCUS_SPRITE);
        this.iconSprite = LimaWidgetSprites.sprite(iconSpriteLocation);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(getX(), getY() + 2, 0, 16, 16, iconSprite);
    }

    @Override
    protected TextureAtlasSprite unfocusedSprite()
    {
        return sprite;
    }

    @Override
    protected TextureAtlasSprite focusedSprite()
    {
        return focusSprite;
    }
}