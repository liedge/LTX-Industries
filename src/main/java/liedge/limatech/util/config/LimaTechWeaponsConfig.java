package liedge.limatech.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LimaTechWeaponsConfig
{
    private LimaTechWeaponsConfig() {}

    // General weapon settings
    public static final ModConfigSpec.BooleanValue WEAPONS_HURT_OWNED_ENTITIES;

    // Submachine gun
    public static final ModConfigSpec.DoubleValue SMG_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue SMG_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue SMG_ENERGY_AMMO_COST;

    // Shotgun
    public static final ModConfigSpec.DoubleValue SHOTGUN_BASE_PELLET_DAMAGE;
    public static final ModConfigSpec.IntValue SHOTGUN_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue SHOTGUN_ENERGY_AMMO_COST;

    // Grenade Launcher
    public static final ModConfigSpec.IntValue GRENADE_LAUNCHER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue GRENADE_LAUNCHER_ENERGY_AMMO_COST;
    public static final ModConfigSpec.DoubleValue EXPLOSIVE_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue FREEZE_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue FREEZE_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ELECTRIC_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue ELECTRIC_GRENADE_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ACID_GRENADE_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue NEURO_GRENADE_BASE_DAMAGE;

    // Rocket Launcher
    public static final ModConfigSpec.DoubleValue ROCKET_LAUNCHER_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue ROCKET_LAUNCHER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ROCKET_LAUNCHER_ENERGY_AMMO_COST;

    // Magnum
    public static final ModConfigSpec.DoubleValue MAGNUM_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGNUM_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue MAGNUM_ENERGY_AMMO_COST;

    public static final ModConfigSpec WEAPONS_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        WEAPONS_HURT_OWNED_ENTITIES = builder.comment(
                "Whether this mod's weapons and their projectiles hurt entities owned/tamed by the weapon user.",
                "Does not apply to entities with no owner or entities owned by someone else. Disabled by default.")
                .define("weapons_hurt_owned_entities", false);

        // Common messages
        final String baseDamageMsg = "Weapon base damage (each full number is half a heart).";
        final String ceuCapacityDamageMsg = "Common Energy Unit capacity when using applicable upgrades.";
        final String ceuAmmoCostMsg = "Ammo synthesis cost (in Common Energy Units) when using applicable upgrades.";

        // SMG
        builder.push("submachine_gun");
        SMG_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 3.0, 0.1d, Double.MAX_VALUE);
        SMG_ENERGY_CAPACITY = builder.comment(ceuCapacityDamageMsg).defineInRange("energy_capacity", 1_000_000, 1, Integer.MAX_VALUE);
        SMG_ENERGY_AMMO_COST = builder.comment(ceuAmmoCostMsg).defineInRange("energy_ammo_cost", 80_000, 1, Integer.MAX_VALUE);
        builder.pop();

        // Shotgun
        builder.push("shotgun");
        SHOTGUN_BASE_PELLET_DAMAGE = builder.comment("Base damage PER PELLET of the shotgun. The shotgun fires 7 pellets.").defineInRange("base_damage", 9.0d, 0.1d, Double.MAX_VALUE);
        SHOTGUN_ENERGY_CAPACITY = builder.comment(ceuCapacityDamageMsg).defineInRange("energy_capacity", 5_000_000, 1, Integer.MAX_VALUE);
        SHOTGUN_ENERGY_AMMO_COST = builder.comment(ceuAmmoCostMsg).defineInRange("energy_ammo_cost", 200_000, 1, Integer.MAX_VALUE);
        builder.pop();

        // Grenade Launcher
        builder.push("grenade_launcher");
        GRENADE_LAUNCHER_ENERGY_CAPACITY = builder.comment(ceuCapacityDamageMsg).defineInRange("energy_capacity", 15_000_000, 1, Integer.MAX_VALUE);
        GRENADE_LAUNCHER_ENERGY_AMMO_COST = builder.comment(ceuAmmoCostMsg).defineInRange("energy_ammo_cost", 800_000, 1, Integer.MAX_VALUE);
        EXPLOSIVE_GRENADE_BASE_DAMAGE = builder.comment("Explosive grenades base damage").defineInRange("explosive_base_damage", 36.0d, 0.1d, Double.MAX_VALUE);
        FLAME_GRENADE_BASE_DAMAGE = builder.comment("Flame grenades base damage").defineInRange("flame_base_damage", 10.0d, 0.1d, Double.MAX_VALUE);
        FLAME_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Flame grenades damage multiplier to mobs that have the dataTag limatech:weak_to_flame").defineInRange("flame_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        FREEZE_GRENADE_BASE_DAMAGE = builder.comment("Freeze grenades base damage").defineInRange("freeze_base_damage", 6.0d, 0.1d, Double.MAX_VALUE);
        FREEZE_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Freeze grenades damage multiplier to mobs that have the dataTag limatech:weak_to_freeze").defineInRange("freeze_multiplier", 8.0d, 0d, Double.MAX_VALUE);
        ELECTRIC_GRENADE_BASE_DAMAGE = builder.comment("Electric grenades base damage").defineInRange("electric_base_damage", 28.0d, 0.1d, Double.MAX_VALUE);
        ELECTRIC_GRENADE_DAMAGE_MULTIPLIER = builder.comment("Electric grenades damage multiplier to mobs in rain/water or that have the dataTag limatech:weak_to_electric").defineInRange("electric_multiplier", 3.0d, 0d, Double.MAX_VALUE);
        ACID_GRENADE_BASE_DAMAGE = builder.comment("Acid grenades base damage").defineInRange("acid_base_damage", 10.0d, 0.1d, Double.MAX_VALUE);
        NEURO_GRENADE_BASE_DAMAGE = builder.comment("Neuro grenades base damage").defineInRange("neuro_base_damage", 4.0d, 0.1d, Double.MAX_VALUE);
        builder.pop();

        // Rocket Launcher
        builder.push("rocket_launcher");
        ROCKET_LAUNCHER_BASE_DAMAGE = builder.comment(baseDamageMsg).defineInRange("base_damage", 80.0d, 0.1d, Double.MAX_VALUE);
        ROCKET_LAUNCHER_ENERGY_CAPACITY = builder.comment(ceuCapacityDamageMsg).defineInRange("energy_capacity", 20_000_00, 1, Integer.MAX_VALUE);
        ROCKET_LAUNCHER_ENERGY_AMMO_COST = builder.comment(ceuAmmoCostMsg).defineInRange("energy_ammo_cost", 1_000_000, 1, Integer.MAX_VALUE);
        builder.pop();

        // Magnum
        builder.push("magnum");
        MAGNUM_BASE_DAMAGE = builder.comment(baseDamageMsg, "Used when targeting non-living entities or when it is the higher value over the scaling damage.").defineInRange("base_damage", 60d, 0.1d, Double.MAX_VALUE);
        MAGNUM_ENERGY_CAPACITY = builder.comment(ceuCapacityDamageMsg).defineInRange("energy_capacity", 20_000_000, 1, Integer.MAX_VALUE);
        MAGNUM_ENERGY_AMMO_COST = builder.comment(ceuAmmoCostMsg).defineInRange("energy_ammo_cost", 20_000_000, 1, Integer.MAX_VALUE);
        builder.pop();

        WEAPONS_CONFIG_SPEC = builder.build();
    }
}