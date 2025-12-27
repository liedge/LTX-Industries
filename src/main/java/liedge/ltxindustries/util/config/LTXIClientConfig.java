package liedge.ltxindustries.util.config;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.math.LimaCoreMath;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIClientConfig
{
    public static final ModConfigSpec.BooleanValue SOLID_COLOR_CROSSHAIR;
    private static final ModConfigSpec.ConfigValue<String> WEAPON_CROSSHAIR_COLOR;

    public static final ModConfigSpec.BooleanValue ALWAYS_SHOW_UPGRADE_ICONS;
    public static final ModConfigSpec.BooleanValue INVERT_MODE_SWITCH_SCROLL;

    // Equipment HUD
    private static final HorizontalAlignment DEFAULT_EQUIPMENT_HUD_X_ALIGN = HorizontalAlignment.LEFT;
    private static final VerticalAlignment DEFAULT_EQUIPMENT_HUD_Y_ALIGN = VerticalAlignment.CENTER;
    private static final ModConfigSpec.ConfigValue<String> EQUIPMENT_HUD_HORIZONTAL_ALIGN;
    public static final ModConfigSpec.IntValue EQUIPMENT_HUD_X_OFFSET;
    private static final ModConfigSpec.ConfigValue<String> EQUIPMENT_HUD_VERTICAL_ALIGN;
    public static final ModConfigSpec.IntValue EQUIPMENT_HUD_Y_OFFSET;

    // Shield health HUD
    private static final HorizontalAlignment DEFAULT_SHIELD_HUD_X_ALIGN = HorizontalAlignment.CENTER;
    private static final VerticalAlignment DEFAULT_SHIELD_HUD_Y_ALIGN = VerticalAlignment.BOTTOM;
    private static final ModConfigSpec.ConfigValue<String> SHIELD_HUD_HORIZONTAL_ALIGN;
    public static final ModConfigSpec.IntValue SHIELD_HUD_X_OFFSET;
    private static final ModConfigSpec.ConfigValue<String> SHIELD_HUD_VERTICAL_ALIGN;
    public static final ModConfigSpec.IntValue SHIELD_HUD_Y_OFFSET;

    public static final ModConfigSpec CLIENT_CONFIG_SPEC;

    // Cached values
    private static LimaColor crosshairColor = LimaColor.WHITE;
    private static HorizontalAlignment equipmentHUDXAlign = DEFAULT_EQUIPMENT_HUD_X_ALIGN;
    private static VerticalAlignment equipmentHUDYAlign = DEFAULT_EQUIPMENT_HUD_Y_ALIGN;
    private static HorizontalAlignment shieldHorizontalAlign = DEFAULT_SHIELD_HUD_X_ALIGN;
    private static VerticalAlignment shieldVerticalAlign = DEFAULT_SHIELD_HUD_Y_ALIGN;

    public static void reCacheConfigValues(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == CLIENT_CONFIG_SPEC)
        {
            crosshairColor = SOLID_COLOR_CROSSHAIR.get() ? LimaCoreMath.tryParseHexadecimal(WEAPON_CROSSHAIR_COLOR.get()).map(LimaColor::createOpaque).orElse(LimaColor.WHITE) : LimaColor.WHITE;
            equipmentHUDXAlign = HorizontalAlignment.CODEC.byNameOrElse(EQUIPMENT_HUD_HORIZONTAL_ALIGN.get(), DEFAULT_EQUIPMENT_HUD_X_ALIGN);
            equipmentHUDYAlign = VerticalAlignment.CODEC.byNameOrElse(EQUIPMENT_HUD_VERTICAL_ALIGN.get(), DEFAULT_EQUIPMENT_HUD_Y_ALIGN);
            shieldHorizontalAlign = HorizontalAlignment.CODEC.byNameOrElse(SHIELD_HUD_HORIZONTAL_ALIGN.get(), DEFAULT_SHIELD_HUD_X_ALIGN);
            shieldVerticalAlign = VerticalAlignment.CODEC.byNameOrElse(SHIELD_HUD_VERTICAL_ALIGN.get(), DEFAULT_SHIELD_HUD_Y_ALIGN);
        }
    }

    public static LimaColor getCrosshairColor()
    {
        return crosshairColor;
    }

    public static HorizontalAlignment getEquipmentHUDXAlign()
    {
        return equipmentHUDXAlign;
    }

    public static VerticalAlignment getEquipmentHUDYAlign()
    {
        return equipmentHUDYAlign;
    }

    public static HorizontalAlignment getShieldHorizontalAlign()
    {
        return shieldHorizontalAlign;
    }

    public static VerticalAlignment getShieldVerticalAlign()
    {
        return shieldVerticalAlign;
    }

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        SOLID_COLOR_CROSSHAIR = builder.comment("Controls whether the weapon crosshair/reticle is a custom color. Otherwise, uses the minecraft style background aware transparent color. Defaults to false")
                .define("solid_color_weapon_crosshair", false);
        WEAPON_CROSSHAIR_COLOR = builder.comment("The color of the weapon crosshair if the solid color crosshair option is enabled. Use a hexadecimal color format prefixed with #.")
                .define("weapon_crosshair_color", "#ffffff");

        ALWAYS_SHOW_UPGRADE_ICONS = builder.comment("Whether upgrade module icons are always shown instead of needing to hold down SHIFT. (Defaults to false)")
                .define("always_show_upgrade_icons", false);

        INVERT_MODE_SWITCH_SCROLL = builder.comment("Inverts the scroll direction for switching between equipment modes. By default, scroll down cycles forward.")
                .define("invert_mode_switch_scroll", false);

        builder.push("equipment_hud").comment("HUD element positioning for equipment items (weapon ammo, tool speed, etc.)");
        EQUIPMENT_HUD_HORIZONTAL_ALIGN = builder.comment("The horizontal alignment of equipment items' HUD overlay.")
                .comment("Valid: left, center, right")
                .define("horizontal_align", HorizontalAlignment.LEFT.getSerializedName());
        EQUIPMENT_HUD_X_OFFSET = builder.comment("The x offset from the horizontal alignment point.")
                .defineInRange("x_offset", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
        EQUIPMENT_HUD_VERTICAL_ALIGN = builder.comment("The vertical alignment of the equipment items' HUD overlay.")
                .comment("Valid: top, center, bottom")
                .define("vertical_align", VerticalAlignment.CENTER.getSerializedName());
        EQUIPMENT_HUD_Y_OFFSET = builder.comment("The y offset from the vertical alignment point.")
                .defineInRange("y_offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        builder.pop();

        builder.push("shield_hud");
        SHIELD_HUD_HORIZONTAL_ALIGN = builder.comment("The horizontal alignment of the shield health HUD overlay.")
                .comment("Valid: left, center, right")
                .define("horizontal_align", HorizontalAlignment.CENTER.getSerializedName());
        SHIELD_HUD_X_OFFSET = builder.comment("The x offset from the horizontal alignment point.")
                .defineInRange("x_offset", -73, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SHIELD_HUD_VERTICAL_ALIGN = builder.comment("The vertical alignment of the shield health HUD overlay.")
                .comment("Valid: top, center, bottom")
                .define("vertical_align", VerticalAlignment.BOTTOM.getSerializedName());
        SHIELD_HUD_Y_OFFSET = builder.comment("The y offset from the vertical alignment point.")
                .defineInRange("y_offset", 49, Integer.MIN_VALUE, Integer.MAX_VALUE);
        builder.pop();

        CLIENT_CONFIG_SPEC = builder.build();
    }
}