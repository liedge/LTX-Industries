package liedge.ltxindustries.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIWeaponsConfig
{
    private LTXIWeaponsConfig() {}

    public static final float MIN_BASE_DAMAGE = 0.5f;
    public static final float MAX_BASE_DAMAGE = 4096f;

    // Submachine gun
    public static final ModConfigSpec.DoubleValue SMG_BASE_DAMAGE;

    // Shotgun
    public static final ModConfigSpec.DoubleValue SHOTGUN_BASE_PELLET_DAMAGE;

    // Grenade Launcher
    public static final ModConfigSpec.DoubleValue EXPLOSIVE_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CRYO_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue CRYO_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ELECTRIC_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue ELECTRIC_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ACID_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue NEURO_GRENADE_BASE_DAMAGE;

    // LFR
    public static final ModConfigSpec.DoubleValue LFR_BASE_DAMAGE;

    // Rocket Launcher
    public static final ModConfigSpec.DoubleValue ROCKET_LAUNCHER_BASE_IMPACT_DAMAGE;
    public static final ModConfigSpec.DoubleValue ROCKET_LAUNCHER_BASE_SPLASH_DAMAGE;

    // Magnum
    public static final ModConfigSpec.DoubleValue HEAVY_PISTOL_BASE_DAMAGE;

    public static final ModConfigSpec WEAPONS_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // Common messages
        final String baseDamageMsg = "Weapon base damage (each full number is half a heart).";

        // SMG
        builder.push("submachine_gun");
        SMG_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 3.0, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        // Shotgun
        builder.push("shotgun");
        SHOTGUN_BASE_PELLET_DAMAGE = builder.comment("Base damage PER PELLET of the shotgun. The shotgun fires 7 pellets.").defineInRange("base_damage", 9.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        // Grenade Launcher
        builder.push("grenade_launcher");
        EXPLOSIVE_GRENADE_BASE_DAMAGE = builder.comment("Explosive grenades base damage").defineInRange("explosive_base_damage", 30.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        FLAME_GRENADE_BASE_DAMAGE = builder.comment("Flame grenades base damage").defineInRange("flame_base_damage", 10.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        FLAME_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Flame grenades damage multiplier to mobs that are weak to fire damage").defineInRange("flame_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        CRYO_GRENADE_BASE_DAMAGE = builder.comment("Cryo grenades base damage").defineInRange("cryo_base_damage", 4.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        CRYO_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Cryo grenades damage multiplier to mobs that are weak to freeze damage").defineInRange("cryo_multiplier", 8.0d, 0d, Double.MAX_VALUE);
        ELECTRIC_GRENADE_BASE_DAMAGE = builder.comment("Electric grenades base damage").defineInRange("electric_base_damage", 30.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        ELECTRIC_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Electric grenades damage multiplier to mobs in rain, water, or who are weak to electricity").defineInRange("electric_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        ACID_GRENADE_BASE_DAMAGE = builder.comment("Acid grenades base damage").defineInRange("acid_base_damage", 50.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        NEURO_GRENADE_BASE_DAMAGE = builder.comment("Neuro grenades base damage").defineInRange("neuro_base_damage", 4.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        builder.push("linear_fusion_rifle");
        LFR_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 25d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        // Rocket Launcher
        builder.push("rocket_launcher");
        ROCKET_LAUNCHER_BASE_IMPACT_DAMAGE = builder.comment("Base direct hit damage of rockets").defineInRange("base_impact_damage", 100.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        ROCKET_LAUNCHER_BASE_SPLASH_DAMAGE = builder.comment("Base splash (AOE) damage of rockets").defineInRange("base_splash_damage", 40.0d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        // Magnum
        builder.push("heavy_pistol");
        HEAVY_PISTOL_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 75d, MIN_BASE_DAMAGE, MAX_BASE_DAMAGE);
        builder.pop();

        WEAPONS_CONFIG_SPEC = builder.build();
    }
}