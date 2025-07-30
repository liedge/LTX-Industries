package liedge.ltxindustries.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.LimaColor;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.Objects;

public final class SmallFontDrawable implements IDrawable
{
    private final String text;
    private final int argb32;
    private final int width;

    public SmallFontDrawable(String text, int argb32)
    {
        this.text = text;
        this.argb32 = argb32;
        this.width = Mth.ceil(Minecraft.getInstance().font.width(text) / 2d);
    }

    public SmallFontDrawable(String text, LimaColor color)
    {
        this(text, color.argb32());
    }

    public SmallFontDrawable(String text, ChatFormatting formatting)
    {
        this(text, Objects.requireNonNullElse(formatting.getColor(), -1));
    }

    public SmallFontDrawable(String text)
    {
        this(text, LimaColor.WHITE);
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return 5;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset)
    {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset, 0);
        poseStack.scale(0.5f, 0.5f, 1f);

        guiGraphics.drawString(Minecraft.getInstance().font, text, 0, 0, argb32, true);

        poseStack.popPose();
    }
}