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

    public static int getArmorEnergyCapacity()
    {
        return ConfigUtil.getIntValueOrZero(SERVER_CONFIG_SPEC, ARMOR_ENERGY_CAPACITY);
    }

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ENABLE_MOD_PVP = builder.comment("Controls whether player-wielded non-melee weapons and offensive machines FROM THIS MOD ONLY can damage other players.",
                "This setting respects the base game's PVP rule. Use this if you want PVP but otherwise feel that this mod's weaponry is too unbalanced.")
                .define("enable_mod_pvp", false);

        builder.comment("ε-Series tools").push("epsilon_tools");
        TOOLS_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 80_000);
        TOOLS_ENERGY_PER_ACTION = ConfigUtil.customEnergyUsage(builder, "Base energy usage per action (dig/attack/etc.)", 400);
        builder.pop();

        builder.comment("AL/1C 'Wonderland' armor set").push("wonderland_armor");
        ARMOR_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 125_000);
        ARMOR_ENERGY_PER_ACTION = ConfigUtil.customEnergyUsage(builder, "Base energy usage per action (reduce damage/stop falls/etc.)", 500);
        builder.pop();

        SHIELD_EFFECT_BLOCK_COST = builder.comment("How much shield health it costs to block a mob effect. Affects both entity and player shields")
                .defineInRange("shield_effect_block_cost", 2d, 0d, EntityBubbleShield.GLOBAL_MAX_SHIELD);

        SERVER_CONFIG_SPEC = builder.build();
    }
}