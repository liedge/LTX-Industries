package liedge.limatech.registry;

import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import net.minecraft.resources.ResourceKey;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechEquipmentUpgrades
{
    private LimaTechEquipmentUpgrades() {}

    // Weapon-specific upgrades
    public static final ResourceKey<EquipmentUpgrade> LIGHTFRAG_BASE_ARMOR_BYPASS = key("lightfrag_armor_bypass");
    public static final ResourceKey<EquipmentUpgrade> SMG_BUILT_IN = key("smg_built_in");
    public static final ResourceKey<EquipmentUpgrade> SHOTGUN_BUILT_IN = key("shotgun_built_in");
    public static final ResourceKey<EquipmentUpgrade> HIGH_IMPACT_ROUNDS = key("high_impact_rounds");
    public static final ResourceKey<EquipmentUpgrade> MAGNUM_SCALING_ROUNDS = key("magnum_scaling_rounds");
    public static final ResourceKey<EquipmentUpgrade> GRENADE_LAUNCHER_PROJECTILE_SPEED = key("grenade_launcher_projectile_speed");

    // Universal upgrades
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_ANTI_VIBRATION = key("universal_anti_vibration");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_STEALTH_DAMAGE = key("universal_stealth_damage");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_ENERGY_AMMO = key("universal_energy_ammo");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_INFINITE_AMMO = key("universal_infinite_ammo");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_ARMOR_PIERCE = key("universal_armor_pierce");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_SHIELD_REGEN = key("universal_shield_regen");

    public static final ResourceKey<EquipmentUpgrade> LOOTING_ENCHANTMENT = key("looting_enchantment");
    public static final ResourceKey<EquipmentUpgrade> AMMO_SCAVENGER_ENCHANTMENT = key("ammo_scavenger_enchantment");
    public static final ResourceKey<EquipmentUpgrade> RAZOR_ENCHANTMENT = key("razor_enchantment");

    // Hanabi grenade cores
    public static final ResourceKey<EquipmentUpgrade> FLAME_GRENADE_CORE = key("flame_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> FREEZE_GRENADE_CORE = key("freeze_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> ELECTRIC_GRENADE_CORE = key("electric_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> ACID_GRENADE_CORE = key("acid_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> NEURO_GRENADE_CORE = key("neuro_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> OMNI_GRENADE_CORE = key("omni_grenade_core");

    private static ResourceKey<EquipmentUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, name);
    }
}