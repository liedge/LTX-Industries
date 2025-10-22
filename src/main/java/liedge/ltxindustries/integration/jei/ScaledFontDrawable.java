package liedge.ltxindustries.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.LimaColor;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.Objects;

public final class ScaledFontDrawable implements IDrawable
{
    static ScaledFontDrawable plainText(String text, float scale)
    {
        return new ScaledFontDrawable(text, -1, scale);
    }

    static ScaledFontDrawable withColor(String text, LimaColor color, float scale)
    {
        return new ScaledFontDrawable(text, color.argb32(), scale);
    }

    static ScaledFontDrawable withStyle(String text, ChatFormatting formatting, float scale)
    {
        return new ScaledFontDrawable(text, Objects.requireNonNullElse(formatting.getColor(), -1), scale);
    }

    private final Font font;

    private final String text;
    private final int argb32;
    private final float scale;
    private final int width;
    private final int height;

    public ScaledFontDrawable(String text, int argb32, float scale)
    {
        this.font = Minecraft.getInstance().font;
        this.text = text;
        this.argb32 = argb32;
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
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset)
    {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(xOffset, yOffset, 0);
        poseStack.scale(scale, scale, 1f);

        guiGraphics.drawString(font, text, 0, 0, argb32, true);

        poseStack.popPose();
    }
}