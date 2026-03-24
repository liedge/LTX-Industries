package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaRenderable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        Identifier sprite = useFocusedSprite() ? focusedSprite() : unfocusedSprite();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);
    }

    protected abstract Identifier unfocusedSprite();

    protected Identifier focusedSprite()
    {
        return unfocusedSprite();
    }

    protected boolean useFocusedSprite()
    {
        return isHoveredOrFocused();
    }
}