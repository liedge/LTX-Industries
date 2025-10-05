package liedge.ltxindustries.util.config;

import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.math.LimaCoreMath;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIClientConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SOLID_COLOR_CROSSHAIR = BUILDER.comment(
                    "Controls whether the weapon crosshair/reticle is a custom color. Otherwise, uses the minecraft style background aware transparent color. Defaults to false")
            .define("solid_color_weapon_crosshair", false);

    private static final ModConfigSpec.ConfigValue<String> WEAPON_CROSSHAIR_COLOR = BUILDER.comment(
                    "The color of the weapon crosshair if the solid color crosshair option is enabled. Use a hexadecimal color format prefixed with #.")
            .define("weapon_crosshair_color", LimaColor.WHITE.toString());

    private static final ModConfigSpec.BooleanValue ALWAYS_SHOW_UPGRADE_ICONS = BUILDER.comment(
            "Whether upgrade module icons are always shown instead of needing to hold down SHIFT. (Defaults to false)")
            .define("always_show_upgrade_icons", false);

    public static final ModConfigSpec CLIENT_CONFIG_SPEC = BUILDER.build();

    // Cached values
    private static boolean solidCrosshairColor;
    private static LimaColor crosshairColor = LimaColor.WHITE;

    public static void reCacheConfigValues(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == CLIENT_CONFIG_SPEC)
        {
            solidCrosshairColor = SOLID_COLOR_CROSSHAIR.get();
            crosshairColor = solidCrosshairColor ? LimaCoreMath.tryParseHexadecimal(WEAPON_CROSSHAIR_COLOR.get()).map(LimaColor::createOpaque).orElse(LimaColor.WHITE) : LimaColor.WHITE;
        }
    }

    public static boolean alwaysShowUpgradeIcons()
    {
        return ALWAYS_SHOW_UPGRADE_ICONS.getAsBoolean();
    }

    public static boolean isSolidCrosshairColor()
    {
        return solidCrosshairColor;
    }

    public static LimaColor getCrosshairColor()
    {
        return crosshairColor;
    }
}