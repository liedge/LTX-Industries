package liedge.ltxindustries.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIWeaponsConfig
{
    private LTXIWeaponsConfig() {}

    // Serenity
    public static final ModConfigSpec.DoubleValue SERENITY_BASE_DAMAGE;

    // Mirage
    public static final ModConfigSpec.DoubleValue MIRAGE_BASE_DAMAGE;

    // Aurora
    public static final ModConfigSpec.DoubleValue AURORA_BASE_DAMAGE;

    // Hanabi
    public static final ModConfigSpec.DoubleValue EXPLOSIVE_SHELL_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_SHELL_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_SHELL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CRYO_SHELL_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue CRYO_SHELL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ELECTRIC_SHELL_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue ELECTRIC_SHELL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ACID_SHELL_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue GLOOM_GAS_SHELL_BASE_DAMAGE;

    // Stargazer
    public static final ModConfigSpec.DoubleValue STARGAZER_BASE_DAMAGE;

    // Daybreak
    public static final ModConfigSpec.DoubleValue DAYBREAK_BASE_IMPACT_DAMAGE;
    public static final ModConfigSpec.DoubleValue DAYBREAK_BASE_SPLASH_DAMAGE;

    // Nova
    public static final ModConfigSpec.DoubleValue NOVA_BASE_DAMAGE;

    public static final ModConfigSpec WEAPONS_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("serenity");
        SERENITY_BASE_DAMAGE = ConfigUtil.baseDamage(builder, null, 3d);
        builder.pop();

        builder.push("mirage");
        MIRAGE_BASE_DAMAGE = ConfigUtil.baseDamage(builder, null, 4.5d);
        builder.pop();

        builder.push("aurora");
        AURORA_BASE_DAMAGE = ConfigUtil.baseDamage(builder, "Base damage PER PELLET. Aurora fires 7 pellets.", 9d);
        builder.pop();

        builder.push("hanabi");
        EXPLOSIVE_SHELL_BASE_DAMAGE = ConfigUtil.damageSpec(builder, "explosive_shell_base_damage", null, 30d);
        FLAME_SHELL_BASE_DAMAGE = ConfigUtil.damageSpec(builder, "flame_shell_base_damage", null, 10d);
        FLAME_SHELL_DAMAGE_MULTIPLIER = builder.comment("Flame shells' damage multiplier to mobs that are weak to fire damage").defineInRange("flame_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        CRYO_SHELL_BASE_DAMAGE = ConfigUtil.damageSpec(builder, "cryo_shell_base_damage", null, 4d);
        CRYO_SHELL_DAMAGE_MULTIPLIER = builder.comment("Cryo shell damage multiplier to mobs that are weak to freeze damage").defineInRange("cryo_multiplier", 8.0d, 0d, Double.MAX_VALUE);
        ELECTRIC_SHELL_BASE_DAMAGE = ConfigUtil.damageSpec(builder, "electric_shell_base_damage", null, 30d);
        ELECTRIC_SHELL_DAMAGE_MULTIPLIER = builder.comment("Electric shell damage multiplier to mobs in rain, water, or who are weak to electricity").defineInRange("electric_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        ACID_SHELL_BASE_DAMAGE = ConfigUtil.damageSpec(builder, "acid_shell_base_damage", null, 50d);
        GLOOM_GAS_SHELL_BASE_DAMAGE = ConfigUtil.damageSpec(builder, "gloom_gas_shell_base_damage", null, 4d);
        builder.pop();

        builder.push("stargazer");
        STARGAZER_BASE_DAMAGE = ConfigUtil.baseDamage(builder, null, 25d);
        builder.pop();

        builder.push("daybreak");
        DAYBREAK_BASE_IMPACT_DAMAGE = ConfigUtil.damageSpec(builder, "base_impact_damage", "Direct hit base damage", 100d);
        DAYBREAK_BASE_SPLASH_DAMAGE = ConfigUtil.damageSpec(builder, "base_splash_damage", "Base splash/AOE/explosion damage", 40d);
        builder.pop();

        builder.push("nova");
        NOVA_BASE_DAMAGE = ConfigUtil.baseDamage(builder, null, 75d);
        builder.pop();

        WEAPONS_CONFIG_SPEC = builder.build();
    }
}