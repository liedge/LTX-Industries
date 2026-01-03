package liedge.ltxindustries.util.config;

import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIServerConfig
{
    private LTXIServerConfig() {}

    // General server settings
    public static final ModConfigSpec.BooleanValue ENABLE_MOD_PVP;

    // Tools
    public static final ModConfigSpec.IntValue TOOLS_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue TOOLS_ENERGY_PER_ACTION;

    // Armor/shield
    public static final ModConfigSpec.IntValue ARMOR_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ARMOR_ENERGY_PER_ACTION;
    public static final ModConfigSpec.DoubleValue SHIELD_EFFECT_BLOCK_COST;

    public static final ModConfigSpec SERVER_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ENABLE_MOD_PVP = builder.comment("Controls whether player-wielded non-melee weapons and offensive machines FROM THIS MOD ONLY can damage other players.",
                "This setting respects the base game's PVP rule. Use this if you want PVP but otherwise feel that this mod's weaponry is too unbalanced.")
                .define("enable_mod_pvp", false);

        TOOLS_ENERGY_CAPACITY = builder.comment("Base energy capacity of the ε-Series tools")
                .defineInRange("tools_energy_capacity", 80_000, 1, Integer.MAX_VALUE);
        TOOLS_ENERGY_PER_ACTION = builder.comment("Base energy cost per action (dig/attack/etc.) of the ε-Series tools")
                .defineInRange("tools_energy_per_action", 400, 1, Integer.MAX_VALUE);

        ARMOR_ENERGY_CAPACITY = builder.comment("Base energy capacity of the WNDR-L armor pieces")
                .defineInRange("armor_energy_capacity", 125_000, 1, Integer.MAX_VALUE);
        ARMOR_ENERGY_PER_ACTION = builder.comment("Base energy post per action (reduce damage/stop falls/etc.) of the WNDR-L armor pieces")
                .defineInRange("armor_energy_per_action", 500, 1, Integer.MAX_VALUE);

        SHIELD_EFFECT_BLOCK_COST = builder.comment("How much shield health it costs to block a mob effect. Affects both entity and player shields")
                .defineInRange("shield_effect_block_cost", 2d, 0d, EntityBubbleShield.GLOBAL_MAX_SHIELD);

        SERVER_CONFIG_SPEC = builder.build();
    }
}