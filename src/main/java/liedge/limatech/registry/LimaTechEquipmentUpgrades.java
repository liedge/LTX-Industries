package liedge.limatech.registry;

import com.mojang.serialization.MapCodec;
import liedge.limatech.LimaTech;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import liedge.limatech.upgradesystem.effect.*;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechEquipmentUpgrades
{
    private static final DeferredRegister<UpgradeEffectType<?>> EFFECT_TYPES = LimaTech.RESOURCES.deferredRegister(LimaTechRegistries.UPGRADE_EFFECT_TYPE_KEY);

    private LimaTechEquipmentUpgrades() {}

    public static void initRegister(IEventBus bus)
    {
        EFFECT_TYPES.register(bus);
    }

    // Upgrade effect types
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<ItemAttributeModifiersUpgradeEffect>> ITEM_ATTRIBUTE_MODIFIERS = registerType("attribute_modifiers", ItemAttributeModifiersUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<WeaponAttributesUpgradeEffect>> WEAPON_ATTRIBUTE_MODIFIER = registerType("weapon_attribute_modifier", WeaponAttributesUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<NoAngerUpgradeEffect>> NO_ANGER = registerType("no_anger", NoAngerUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<NoVibrationUpgradeEffect>> NO_VIBRATION = registerType("no_vibration", NoVibrationUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<BypassArmorUpgradeEffect>> BYPASS_ARMOR = registerType("bypass_armor", BypassArmorUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<KnockbackModifierUpgradeEffect>> KNOCKBACK_MODIFIER = registerType("knockback_modifier", KnockbackModifierUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<BubbleShieldUpgradeEffect>> BUBBLE_SHIELD_RESTORE = registerType("bubble_shield_restore", BubbleShieldUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<EnchantmentLevelUpgradeEffect>> ENCHANTMENT_LEVEL = registerType("enchantment_level", EnchantmentLevelUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<GrenadeTypeSelectionUpgradeEffect>> GRENADE_TYPE_SELECTION = registerType("grenade_type_selection", GrenadeTypeSelectionUpgradeEffect.CODEC);
    public static final DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<SetAmmoSourceUpgradeEffect>> SET_AMMO_SOURCE = registerType("set_ammo_source", SetAmmoSourceUpgradeEffect.CODEC);

    //#region Built in upgrade resource keys

    // Weapon-specific upgrades
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

    //#endregion

    private static ResourceKey<EquipmentUpgrade> key(String name)
    {
        return LimaTech.RESOURCES.resourceKey(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, name);
    }

    private static <T extends EquipmentUpgradeEffect> DeferredHolder<UpgradeEffectType<?>, UpgradeEffectType<T>> registerType(String name, MapCodec<T> codec)
    {
        return EFFECT_TYPES.register(name, () -> new UpgradeEffectType<>(codec));
    }
}