package liedge.limatech.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LimaTechServerConfig
{
    private LimaTechServerConfig() {}

    public static final ModConfigSpec.BooleanValue GENERATE_ALL_ENCHANTED_BOOK_LEVELS;
    public static final ModConfigSpec.BooleanValue GENERATE_ALL_UPGRADE_RANKS;
    public static final ModConfigSpec.BooleanValue ENABLE_MOD_PVP;

    public static final ModConfigSpec SERVER_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        GENERATE_ALL_ENCHANTED_BOOK_LEVELS = builder.comment("If set to true, all levels of enchanted books are added to the main creative tab.",
                "If false, only the highest level will be added.").define("generate_all_enchanted_book_levels", true);

        GENERATE_ALL_UPGRADE_RANKS = builder.comment("If set to true, all possible ranks of equipment and machine upgrades are generated in their respective creative tabs.",
                        "If false, only the highest rank will be generated")
                .define("generate_all_upgrade_ranks", true);

        ENABLE_MOD_PVP = builder.comment("Controls whether player-wielded non-melee weapons and offensive machines FROM THIS MOD ONLY can damage other players.",
                "This setting respects the base game's PVP rule. Use this if you want PVP but otherwise feel that this mod's weaponry is too unbalanced.")
                .define("enable_mod_pvp", false);

        SERVER_CONFIG_SPEC = builder.build();
    }
}