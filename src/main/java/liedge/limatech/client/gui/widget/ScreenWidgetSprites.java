package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limatech.LimaTech;
import net.minecraft.resources.ResourceLocation;

public final class ScreenWidgetSprites
{
    private ScreenWidgetSprites() {}

    private static final ResourceLocation TEXTURE_SHEET = LimaTech.RESOURCES.textureLocation("gui", "screen_widgets");

    private static UnmanagedSprite sprite(int u, int v, int width, int height)
    {
        return new UnmanagedSprite(TEXTURE_SHEET, u, v, width, height);
    }

    static final UnmanagedSprite ENERGY_GAUGE_BACKGROUND = sprite(0, 0, 10, 48);
    static final UnmanagedSprite ENERGY_GAUGE_FOREGROUND = sprite(10, 0, 8, 46);
    static final UnmanagedSprite MACHINE_PROGRESS_BACKGROUND = sprite(18, 0, 24, 6);
    static final UnmanagedSprite MACHINE_PROGRESS_FOREGROUND = sprite(18, 6, 22, 4);
}