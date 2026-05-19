package liedge.ltxindustries.integration.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

public final class ScaledFontDrawable implements IDrawable
{
    private final Component text;
    private final float scale;
    private final int width;
    private final int height;

    public ScaledFontDrawable(Component text, float scale)
    {
        Font font = Minecraft.getInstance().font;

        this.text = text;
        this.scale = scale;
        this.width = Mth.ceil(font.width(text) * scale);
        this.height = Mth.ceil(font.lineHeight * scale);
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int xOffset, int yOffset)
    {
        graphics.nextStratum();

        Matrix3x2fStack matrixStack = graphics.pose();

        matrixStack.pushMatrix();

        matrixStack.translate(xOffset, yOffset);
        matrixStack.scale(scale);

        graphics.text(Minecraft.getInstance().font, text, 0, 0, -1, true);

        matrixStack.popMatrix();
    }
}