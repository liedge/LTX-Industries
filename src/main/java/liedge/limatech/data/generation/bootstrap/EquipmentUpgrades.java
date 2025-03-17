package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.LimaCoreTags;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.world.loot.number.MathOpsNumberProvider;
import liedge.limacore.world.loot.number.TargetedAttributeValueProvider;
import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.effect.AmmoSourceUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.AttributeModifierUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.EnchantmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.equipment.BubbleShieldUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.equipment.DynamicDamageTagUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.equipment.KnockbackStrengthUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.ComplexValueTooltip;
import liedge.limatech.lib.upgrades.effect.value.ComplexValueUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.DoubleLevelBasedValue;
import liedge.limatech.lib.upgrades.effect.value.SimpleValueUpgradeEffect;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.registry.LimaTechEnchantments.AMMO_SCAVENGER;
import static liedge.limatech.registry.LimaTechEnchantments.RAZOR;
import static liedge.limatech.registry.LimaTechEquipmentUpgrades.*;
import static liedge.limatech.registry.LimaTechUpgradeEffectComponents.*;

class EquipmentUpgrades implements UpgradesBootstrap<EquipmentUpgrade>
{
    @Override
    public void run(BootstrapContext<EquipmentUpgrade> context)
    {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<EquipmentUpgrade> holders = context.lookup(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);

        // Weapon-specific upgrades
        EquipmentUpgrade.builder(LIGHTFRAG_BASE_ARMOR_BYPASS)
                .supports(HolderSet.direct(LimaTechItems.SUBMACHINE_GUN, LimaTechItems.SHOTGUN,  LimaTechItems.MAGNUM))
                .withConditionalEffect(ARMOR_BYPASS, ComplexValueUpgradeEffect.simpleConstant(-4f, CompoundValueOperation.FLAT_ADDITION))
                .effectIcon(sprite("lightfrags"))
                .buildAndRegister(context);

        EquipmentUpgrade.builder(SMG_BUILT_IN)
                .supports(HolderSet.direct(LimaTechItems.SUBMACHINE_GUN))
                .withUnitEffect(PREVENT_SCULK_VIBRATION)
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_ANGER))
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_KNOCKBACK))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.SUBMACHINE_GUN))
                .buildAndRegister(context);

        EquipmentUpgrade.builder(SHOTGUN_BUILT_IN)
                .supports(LimaTechItems.SHOTGUN)
                .withEffect(ITEM_ATTRIBUTE_MODIFIERS, AttributeModifierUpgradeEffect.constantMainHand(Attributes.MOVEMENT_SPEED, RESOURCES.location("shotgun_speed_boost"), 0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(ITEM_ATTRIBUTE_MODIFIERS, AttributeModifierUpgradeEffect.constantMainHand(Attributes.STEP_HEIGHT, RESOURCES.location("shotgun_step_height_boost"), 1, AttributeModifier.Operation.ADD_VALUE))
                .withConditionalEffect(ARMOR_BYPASS, ComplexValueUpgradeEffect.simpleConstant(-0.15f, CompoundValueOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.SHOTGUN))
                .buildAndRegister(context);

        EquipmentUpgrade.builder(HIGH_IMPACT_ROUNDS)
                .supports(LimaTechItems.SHOTGUN, LimaTechItems.MAGNUM)
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(LimaCoreTags.DamageTypes.IGNORES_KNOCKBACK_RESISTANCE))
                .withConditionalEffect(WEAPON_PRE_ATTACK, new KnockbackStrengthUpgradeEffect(LevelBasedValue.perLevel(1.5f)))
                .effectIcon(sprite("powerful_lightfrag"))
                .buildAndRegister(context);

        EquipmentUpgrade.builder(MAGNUM_SCALING_ROUNDS)
                .supports(LimaTechItems.MAGNUM)
                .withConditionalEffect(WEAPON_DAMAGE, ComplexValueUpgradeEffect.of(MathOpsNumberProvider.of(TargetedAttributeValueProvider.of(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH), ConstantValue.exactly(0.25f), MathOperation.MULTIPLICATION), CompoundValueOperation.FLAT_ADDITION,
                        ComplexValueTooltip.attributeValueTooltip(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH, LevelBasedValue.constant(0.25f))))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.MAGNUM))
                .effectIcon(itemWithSpriteOverlay(LimaTechItems.MAGNUM, "powerful_lightfrag", 10, 10, 0, 6))
                .buildAndRegister(context);

        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withEffect(WEAPON_PROJECTILE_SPEED, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.FLAT_ADDITION))
                .effectIcon(sprite("grenade_speed_boost"))
                .buildAndRegister(context);

        // Universal upgrades
        EquipmentUpgrade.builder(UNIVERSAL_ANTI_VIBRATION)
                .supportsLTXWeapons(items)
                .withUnitEffect(PREVENT_SCULK_VIBRATION)
                .effectIcon(sprite("no_vibration"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supportsLTXWeapons(items)
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_ANGER))
                .effectIcon(sprite("stealth_damage"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(UNIVERSAL_ENERGY_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(AMMO_SOURCE, new AmmoSourceUpgradeEffect(WeaponAmmoSource.COMMON_ENERGY_UNIT))
                .effectIcon(sprite("energy_ammo"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(AMMO_SOURCE, new AmmoSourceUpgradeEffect(WeaponAmmoSource.INFINITE))
                .effectIcon(sprite("infinite_ammo"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(UNIVERSAL_ARMOR_PIERCE)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withConditionalEffect(ARMOR_BYPASS, ComplexValueUpgradeEffect.simpleRankBased(LevelBasedValue.perLevel(-0.1f), CompoundValueOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(sprite("armor_bypass"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(UNIVERSAL_SHIELD_REGEN)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withConditionalEffect(WEAPON_KILL, new BubbleShieldUpgradeEffect(LevelBasedValue.constant(4), LevelBasedValue.perLevel(10)))
                .effectIcon(sprite("bubble_shield"))
                .buildAndRegister(context);

        // Enchantments
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(sprite("looting"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor_enchant"))
                .buildAndRegister(context);

        // Hanabi grenade cores
        EquipmentUpgrade.builder(FLAME_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .effectIcon(hanabiCoreIcon(GrenadeType.FLAME))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(CRYO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .effectIcon(hanabiCoreIcon(GrenadeType.CRYO))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(ELECTRIC_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .effectIcon(hanabiCoreIcon(GrenadeType.ELECTRIC))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(ACID_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .effectIcon(hanabiCoreIcon(GrenadeType.ACID))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(NEURO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(hanabiCoreIcon(GrenadeType.NEURO))
                .buildAndRegister(context);
        EquipmentUpgrade.builder(OMNI_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("omni_grenade_core"))
                .buildAndRegister(context);
    }
}