package liedge.ltxindustries.integration.jei;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.LimaColor;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public record SolidColorDrawable(int width, int height, int backgroundColor) implements IDrawable
{
    public SolidColorDrawable
    {
        Preconditions.checkArgument(width > 0 && height > 0, "Dimensions must be positive.");
    }

    public SolidColorDrawable(int width, int height, LimaColor color)
    {
        this(width, height, color.argb32());
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
        guiGraphics.fill(xOffset, yOffset, xOffset + width, yOffset + height, backgroundColor);
    }
}