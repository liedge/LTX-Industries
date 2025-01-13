package liedge.limatech.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LimaTechServerConfig
{
    private LimaTechServerConfig() {}

    public static final ModConfigSpec.BooleanValue GENERATE_ALL_UPGRADE_RANKS;

    public static final ModConfigSpec SERVER_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        GENERATE_ALL_UPGRADE_RANKS = builder.comment("If set to true, all possible ranks of equipment and machine upgrades are generated in their respective creative tabs.",
                        "If false, only the highest rank will be generated")
                .define("generate_all_upgrade_ranks", true);

        SERVER_CONFIG_SPEC = builder.build();
    }
}