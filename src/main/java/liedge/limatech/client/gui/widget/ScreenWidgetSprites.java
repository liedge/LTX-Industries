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

    public static final UnmanagedSprite ENERGY_GAUGE_BACKGROUND = sprite(0, 0, 10, 48);
    public static final UnmanagedSprite ENERGY_GAUGE_FOREGROUND = sprite(10, 0, 8, 46);
    public static final UnmanagedSprite MACHINE_PROGRESS_BACKGROUND = sprite(18, 0, 24, 6);
    public static final UnmanagedSprite MACHINE_PROGRESS_FOREGROUND = sprite(18, 6, 22, 4);
    public static final UnmanagedSprite SCROLLBAR_DISABLED = sprite(18, 10, 8, 13);
    public static final UnmanagedSprite SCROLLBAR_NOT_FOCUSED = sprite(26, 10, 8, 13);
    public static final UnmanagedSprite SCROLLBAR_FOCUSED = sprite(34, 10, 8, 13);
    public static final UnmanagedSprite BACK_BUTTON_NOT_FOCUSED = sprite(42, 0, 12, 12);
    public static final UnmanagedSprite BACK_BUTTON_FOCUSED = sprite(54, 0, 12, 12);
    public static final UnmanagedSprite ITEM_IO_BUTTON = sprite(66, 0, 15, 16);
    public static final UnmanagedSprite ENERGY_IO_BUTTON = sprite(81, 0, 15, 16);
    public static final UnmanagedSprite FLUID_IO_BUTTON = sprite(96, 0, 15, 16);
}