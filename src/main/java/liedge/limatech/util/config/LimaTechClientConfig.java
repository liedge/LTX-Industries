package liedge.limatech.util.config;

import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class LimaTechClientConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<Integer> BUBBLE_SHIELD_QUALITY = BUILDER.comment(
                    "Controls how many groups of polygons the bubble shield is 'split' into to be animated. 32 allows every face to be individually animated.",
                    "Higher numbers may have a negative performance impact. Changes require a client restart to properly take effect.",
                    "Default value: 8, allowed values: 1, 2, 4, 8, 16, 32")
            .gameRestart()
            .defineInList("bubble_shield_quality", 8, IntList.of(1, 2, 4, 8, 16, 32));

    private static final ModConfigSpec.BooleanValue SOLID_COLOR_CROSSHAIR = BUILDER.comment(
                    "Controls whether the weapon crosshair/reticle is a custom color. Otherwise, uses the minecraft style background aware transparent color. Defaults to false")
            .define("solid_color_weapon_crosshair", false);

    private static final ModConfigSpec.ConfigValue<String> WEAPON_CROSSHAIR_COLOR = BUILDER.comment(
                    "The color of the weapon crosshair if the solid color crosshair option is enabled. Use a hexadecimal color format prefixed with #.")
            .define("weapon_crosshair_color", LimaColor.WHITE.toString());

    public static final ModConfigSpec CLIENT_CONFIG_SPEC = BUILDER.build();

    // Cached values
    private static boolean solidCrosshairColor;
    private static LimaColor crosshairColor = LimaColor.WHITE;

    public static void onConfigLoaded(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == CLIENT_CONFIG_SPEC)
        {
            solidCrosshairColor = SOLID_COLOR_CROSSHAIR.get();
            crosshairColor = solidCrosshairColor ? LimaMathUtil.tryParseHexadecimal(WEAPON_CROSSHAIR_COLOR.get()).map(LimaColor::createOpaque).orElse(LimaColor.WHITE) : LimaColor.WHITE;
        }
    }

    public static int getBubbleShieldQuality()
    {
        return BUBBLE_SHIELD_QUALITY.get();
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