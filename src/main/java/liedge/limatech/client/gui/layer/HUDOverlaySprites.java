package liedge.limatech.client.gui.layer;

import liedge.limacore.client.gui.UnmanagedSprite;
import net.minecraft.resources.ResourceLocation;

import static liedge.limatech.LimaTech.RESOURCES;

public final class HUDOverlaySprites
{
    private HUDOverlaySprites() {}

    private static final ResourceLocation TEXTURE_SHEET = RESOURCES.textureLocation("gui", "hud_overlay");

    private static UnmanagedSprite sprite(int u, int v, int width, int height)
    {
        return new UnmanagedSprite(TEXTURE_SHEET, u, v, width, height);
    }

    public static final UnmanagedSprite AUTO_CROSSHAIR_1 = sprite(26, 0, 6, 1);
    public static final UnmanagedSprite AUTO_CROSSHAIR_2 = sprite(33, 0, 1, 6);

    public static final UnmanagedSprite SHOTGUN_CROSSHAIR_LEFT = sprite(0, 0, 6, 13);
    public static final UnmanagedSprite SHOTGUN_CROSSHAIR_RIGHT = sprite(6, 0, 6, 13);

    public static final UnmanagedSprite LAUNCHER_CROSSHAIR_CENTER = sprite(0, 14, 5, 5);
    public static final UnmanagedSprite LAUNCHER_CROSSHAIR_UP = sprite(6, 14, 7, 2);
    public static final UnmanagedSprite LAUNCHER_CROSSHAIR_DOWN = sprite(6, 17, 7, 2);
    public static final UnmanagedSprite LAUNCHER_CROSSHAIR_LEFT = sprite(0, 20, 2, 7);
    public static final UnmanagedSprite LAUNCHER_CROSSHAIR_RIGHT = sprite(3, 20, 2, 7);
    public static final UnmanagedSprite LAUNCHER_CROSSHAIR_DOWN_DROP = sprite(13, 7, 7, 7);

    public static final UnmanagedSprite MAGNUM_CROSSHAIR_1 = sprite(13, 0, 6, 6);
    public static final UnmanagedSprite MAGNUM_CROSSHAIR_2 = sprite(19, 0, 6, 6);

    public static final UnmanagedSprite NORMAL_AMMO_INDICATOR = sprite(46, 0, 49, 13);
    public static final UnmanagedSprite ENERGY_AMMO_INDICATOR = sprite(187, 0, 49, 23);
    public static final UnmanagedSprite INFINITE_AMMO_INDICATOR = sprite(46, 14, 47, 13);
    public static final UnmanagedSprite RELOADING_INDICATOR = sprite(134, 0, 35, 15);
    public static final UnmanagedSprite RELOADING_INDICATOR_INNER = sprite(170, 0, 16, 3);

    public static final UnmanagedSprite BUBBLE_SHIELD_INDICATOR = sprite(96, 0, 37, 13);
}