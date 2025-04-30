package liedge.limatech.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LimaTechServerConfig
{
    private LimaTechServerConfig() {}

    // General server settings
    public static final ModConfigSpec.BooleanValue ENABLE_MOD_PVP;

    // Tools
    public static final ModConfigSpec.IntValue TOOLS_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue TOOLS_ENERGY_PER_ACTION;

    public static final ModConfigSpec.BooleanValue AXE_ALWAYS_CHOPS_LOGS;

    public static final ModConfigSpec SERVER_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ENABLE_MOD_PVP = builder.comment("Controls whether player-wielded non-melee weapons and offensive machines FROM THIS MOD ONLY can damage other players.",
                "This setting respects the base game's PVP rule. Use this if you want PVP but otherwise feel that this mod's weaponry is too unbalanced.")
                .define("enable_mod_pvp", false);

        TOOLS_ENERGY_CAPACITY = builder.comment("Base energy capacity of the LTX tools")
                .defineInRange("tools_energy_capacity", 80_000, 1, Integer.MAX_VALUE);
        TOOLS_ENERGY_PER_ACTION = builder.comment("Base energy cost per action (dig/attack/etc.) of the LTX tools")
                .defineInRange("tools_energy_per_action", 400, 1, Integer.MAX_VALUE);

        AXE_ALWAYS_CHOPS_LOGS = builder.comment("If set to true the LTX Axe will always vein-mine/chop down ALL log structures regardless of natural leaves presence.",
                "Off by default. Recommended to keep disabled to limit axe to natural trees only.")
                .define("axe_always_chops_logs", false);

        SERVER_CONFIG_SPEC = builder.build();
    }
}