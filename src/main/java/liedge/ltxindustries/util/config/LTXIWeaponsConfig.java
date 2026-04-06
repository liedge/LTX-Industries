package liedge.ltxindustries.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIWeaponsConfig
{
    private LTXIWeaponsConfig() {}

    public static final float MIN_BASE_DAMAGE = 0.5f;
    public static final float MAX_BASE_DAMAGE = 4096f;

    // Serenity
    public static final ModConfigSpec.DoubleValue SERENITY_BASE_DAMAGE;

    // Shotgun
    public static final ModConfigSpec.DoubleValue AURORA_BASE_DAMAGE;

    // Hanabi
    public static final ModConfigSpec.DoubleValue EXPLOSIVE_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CRYO_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue CRYO_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ELECTRIC_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue ELECTRIC_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ACID_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue NEURO_GRENADE_BASE_DAMAGE;

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

        // Common messages
        final String baseDamageMsg = "Weapon base damage (each full number is half a heart).";

        builder.push("serenity");
        SERENITY_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 3.0, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        builder.push("aurora");
        AURORA_BASE_DAMAGE = builder.comment("Base damage PER PELLET of the Aurora. It fires 7 pellets by default.").defineInRange("base_damage", 9.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        builder.push("hanabi");
        EXPLOSIVE_GRENADE_BASE_DAMAGE = builder.comment("Explosive shell base damage").defineInRange("explosive_base_damage", 30.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        FLAME_GRENADE_BASE_DAMAGE = builder.comment("Flame shell base damage").defineInRange("flame_base_damage", 10.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        FLAME_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Flame shells' damage multiplier to mobs that are weak to fire damage").defineInRange("flame_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        CRYO_GRENADE_BASE_DAMAGE = builder.comment("Cryo shell base damage").defineInRange("cryo_base_damage", 4.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        CRYO_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Cryo shell damage multiplier to mobs that are weak to freeze damage").defineInRange("cryo_multiplier", 8.0d, 0d, Double.MAX_VALUE);
        ELECTRIC_GRENADE_BASE_DAMAGE = builder.comment("Electric shell base damage").defineInRange("electric_base_damage", 30.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        ELECTRIC_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Electric shell damage multiplier to mobs in rain, water, or who are weak to electricity").defineInRange("electric_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        ACID_GRENADE_BASE_DAMAGE = builder.comment("Acid shell base damage").defineInRange("acid_base_damage", 50.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        NEURO_GRENADE_BASE_DAMAGE = builder.comment("Neuro shell base damage").defineInRange("neuro_base_damage", 4.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        builder.push("stargazer");
        STARGAZER_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 25d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        builder.push("daybreak");
        DAYBREAK_BASE_IMPACT_DAMAGE = builder.comment("Base direct hit damage of Daybreak's rockets").defineInRange("base_impact_damage", 100.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        DAYBREAK_BASE_SPLASH_DAMAGE = builder.comment("Base splash (AOE) damage of Daybreak's rockets").defineInRange("base_splash_damage", 40.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        builder.push("nova");
        NOVA_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 75d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        WEAPONS_CONFIG_SPEC = builder.build();
    }
}