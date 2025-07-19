package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaRenderable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

public abstract class LimaRenderableButton extends AbstractButton implements LimaRenderable
{
    protected LimaRenderableButton(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }

    protected LimaRenderableButton(int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty());
    }

    @Deprecated
    @Override
    public final void onPress()
    {
        onPress(0);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    @Deprecated
    public final void onClick(double mouseX, double mouseY) {}

    @Override
    public void onClick(double mouseX, double mouseY, int button)
    {
        onPress(button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (acceptsKeyboardInput())
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        TextureAtlasSprite sprite = isHoveredOrFocused() ? focusedSprite() : unfocusedSprite();
        guiGraphics.blit(getX(), getY(), 0, width, height, sprite);
    }

    public abstract void onPress(int button);

    protected abstract TextureAtlasSprite unfocusedSprite();

    protected TextureAtlasSprite focusedSprite()
    {
        return unfocusedSprite();
    }

    protected boolean acceptsKeyboardInput()
    {
        return true;
    }
}