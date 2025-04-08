package liedge.limatech.registry.bootstrap;

import liedge.limacore.lib.damage.DamageReductionType;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.limacore.world.loot.number.MathOpsNumberProvider;
import liedge.limacore.world.loot.number.TargetedAttributeValueProvider;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.LimaTechTags;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.effect.equipment.*;
import liedge.limatech.lib.upgrades.effect.value.AttributeAmountTooltip;
import liedge.limatech.lib.upgrades.effect.value.DoubleLevelBasedValue;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.game.LimaTechItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS;
import static liedge.limatech.LimaTechTags.EquipmentUpgrades.MINING_DROPS_MODIFIERS;
import static liedge.limatech.lib.upgrades.UpgradeIcon.*;
import static liedge.limatech.registry.bootstrap.LimaTechEnchantments.AMMO_SCAVENGER;
import static liedge.limatech.registry.bootstrap.LimaTechEnchantments.RAZOR;
import static liedge.limatech.registry.game.LimaTechUpgradeEffectComponents.*;

public final class LimaTechEquipmentUpgrades
{
    private LimaTechEquipmentUpgrades() {}

    // Built-in upgrades
    public static final ResourceKey<EquipmentUpgrade> LTX_SHOVEL_DEFAULT = key("ltx_shovel_default");
    public static final ResourceKey<EquipmentUpgrade> LTX_AXE_DEFAULT = key("ltx_axe_default");
    public static final ResourceKey<EquipmentUpgrade> LTX_WRENCH_DEFAULT = key("ltx_wrench_default");
    public static final ResourceKey<EquipmentUpgrade> SUBMACHINE_GUN_DEFAULT = key("submachine_gun_default");
    public static final ResourceKey<EquipmentUpgrade> SHOTGUN_DEFAULT = key("shotgun_default");

    // Tool upgrades
    public static final ResourceKey<EquipmentUpgrade> TOOL_OMNI_MINER = key("tool_omni_miner");
    public static final ResourceKey<EquipmentUpgrade> TOOL_VIBRATION_CANCEL = key("tool_vibration_cancel");
    public static final ResourceKey<EquipmentUpgrade> TOOL_DIRECT_DROPS = key("tool_direct_drops");

    // Weapon upgrades
    public static final ResourceKey<EquipmentUpgrade> WEAPON_VIBRATION_CANCEL = key("weapon_vibration_cancel");
    public static final ResourceKey<EquipmentUpgrade> WEAPON_DIRECT_DROPS = key("weapon_direct_drops");
    public static final ResourceKey<EquipmentUpgrade> WEAPON_ARMOR_PIERCE = key("weapon_armor_pierce");
    public static final ResourceKey<EquipmentUpgrade> HIGH_IMPACT_ROUNDS = key("high_impact_rounds");
    public static final ResourceKey<EquipmentUpgrade> MAGNUM_SCALING_ROUNDS = key("magnum_scaling_rounds");
    public static final ResourceKey<EquipmentUpgrade> GRENADE_LAUNCHER_PROJECTILE_SPEED = key("grenade_launcher_projectile_speed");

    // Enchantments
    public static final ResourceKey<EquipmentUpgrade> SILK_TOUCH_ENCHANT = key("silk_touch_enchantment");
    public static final ResourceKey<EquipmentUpgrade> FORTUNE_ENCHANTMENT = key("fortune_enchantment");
    public static final ResourceKey<EquipmentUpgrade> LOOTING_ENCHANTMENT = key("looting_enchantment");
    public static final ResourceKey<EquipmentUpgrade> AMMO_SCAVENGER_ENCHANTMENT = key("ammo_scavenger_enchantment");
    public static final ResourceKey<EquipmentUpgrade> RAZOR_ENCHANTMENT = key("razor_enchantment");

    // Universal upgrades
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_STEALTH_DAMAGE = key("universal_stealth_damage");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_ENERGY_AMMO = key("universal_energy_ammo");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_INFINITE_AMMO = key("universal_infinite_ammo");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_SHIELD_REGEN = key("universal_shield_regen");

    // Hanabi grenade cores
    public static final ResourceKey<EquipmentUpgrade> FLAME_GRENADE_CORE = key("flame_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> CRYO_GRENADE_CORE = key("cryo_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> ELECTRIC_GRENADE_CORE = key("electric_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> ACID_GRENADE_CORE = key("acid_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> NEURO_GRENADE_CORE = key("neuro_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> OMNI_GRENADE_CORE = key("omni_grenade_core");

    private static ResourceKey<EquipmentUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, name);
    }

    public static void bootstrap(BootstrapContext<EquipmentUpgrade> context)
    {
        // Holder getters
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<GameEvent> gameEvents = context.lookup(Registries.GAME_EVENT);
        HolderGetter<EquipmentUpgrade> holders = context.lookup(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);

        // AnyHolderSets
        HolderSet<Block> anyBlockHolderSet = new AnyHolderSet<>(BuiltInRegistries.BLOCK.asLookup());
        HolderSet<Item> anyItemHolderSet = new AnyHolderSet<>(BuiltInRegistries.ITEM.asLookup());

        // Common holder sets
        HolderSet<Item> ltxMiningTools = items.getOrThrow(LimaTechTags.Items.LTX_MINING_TOOLS);
        HolderSet<Item> ltxProjectileWeapons = items.getOrThrow(LimaTechTags.Items.LTX_PROJECTILE_WEAPONS);
        HolderSet<Item> ltxAllWeapons = items.getOrThrow(LimaTechTags.Items.LTX_ALL_WEAPONS);

        // Built in upgrades
        final Component defaultToolTitle = LimaTechLang.TOOL_DEFAULT_UPGRADE_TITLE.translate().withStyle(LimaTechConstants.LIME_GREEN.chatStyle());
        EquipmentUpgrade.builder(LTX_SHOVEL_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LimaTechItems.LTX_SHOVEL)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.constantLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(bottomRightComposite(itemIcon(LimaTechItems.LTX_SHOVEL), sprite("default")))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(LTX_AXE_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LimaTechItems.LTX_AXE)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.constantLevel(enchantments.getOrThrow(RAZOR), 1))
                .effectIcon(bottomRightComposite(itemIcon(LimaTechItems.LTX_AXE), sprite("default")))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(LTX_WRENCH_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LimaTechItems.LTX_WRENCH)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.blocksOnly(items.getOrThrow(LimaTechTags.Items.WRENCH_BREAKABLE)))
                .effectIcon(bottomRightComposite(itemIcon(LimaTechItems.LTX_WRENCH), sprite("default")))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(SUBMACHINE_GUN_DEFAULT)
                .supports(LimaTechItems.SUBMACHINE_GUN)
                .withEffect(PREVENT_VIBRATION, PreventVibrationUpgradeEffect.suppressMainHand(gameEvents.getOrThrow(LimaTechTags.GameEvents.WEAPON_VIBRATIONS)))
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DynamicDamageTagUpgradeEffect.of(DamageTypeTags.NO_ANGER, DamageTypeTags.NO_KNOCKBACK))
                .effectIcon(bottomLeftComposite(itemIcon(LimaTechItems.SUBMACHINE_GUN), sprite("default")))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(SHOTGUN_DEFAULT)
                .supports(LimaTechItems.SHOTGUN)
                .withEffect(ITEM_ATTRIBUTE_MODIFIERS, AttributeModifierUpgradeEffect.constantMainHand(Attributes.MOVEMENT_SPEED, SHOTGUN_DEFAULT.location().withSuffix("shotgun_speed_boost"), 0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(ITEM_ATTRIBUTE_MODIFIERS, AttributeModifierUpgradeEffect.constantMainHand(Attributes.STEP_HEIGHT, SHOTGUN_DEFAULT.location().withSuffix("shotgun_step_height_boost"), 1, AttributeModifier.Operation.ADD_VALUE))
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, new ModifyReductionsUpgradeEffect(DamageReductionType.ARMOR, LevelBasedValue.constant(0.9f)))
                .effectIcon(bottomLeftComposite(itemIcon(LimaTechItems.SHOTGUN), sprite("default")))
                .category("default/weapon")
                .register(context);

        // Tool upgrades
        EquipmentUpgrade.builder(TOOL_OMNI_MINER)
                .createDefaultTitle(LimaTechConstants.LIME_GREEN)
                .supports(ltxMiningTools)
                .withSpecialEffect(TOOL_PROFILE, ToolProfileUpgradeEffect.alwaysMinesAndDrops(anyBlockHolderSet, 13f))
                .effectIcon(sprite("omni_miner"))
                .register(context);
        EquipmentUpgrade.builder(TOOL_VIBRATION_CANCEL)
                .supports(items.getOrThrow(LimaTechTags.Items.LTX_ALL_TOOLS))
                .withEffect(PREVENT_VIBRATION, PreventVibrationUpgradeEffect.suppressHands(gameEvents.getOrThrow(LimaTechTags.GameEvents.HANDHELD_EQUIPMENT)))
                .effectIcon(sprite("earmuffs"))
                .register(context);
        EquipmentUpgrade.builder(TOOL_DIRECT_DROPS)
                .supports(ltxMiningTools)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.blocksOnly(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .register(context);

        // Weapon-specific upgrades
        EquipmentUpgrade.builder(HIGH_IMPACT_ROUNDS)
                .supports(LimaTechItems.SHOTGUN, LimaTechItems.MAGNUM)
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DamageAttributesUpgradeEffect.of(Attributes.KNOCKBACK_RESISTANCE, HIGH_IMPACT_ROUNDS.location().withSuffix(".knockback_resist"), LevelBasedValue.constant(-1f), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DamageAttributesUpgradeEffect.of(LimaCoreAttributes.KNOCKBACK_MULTIPLIER, HIGH_IMPACT_ROUNDS.location().withSuffix(".knockback"), LevelBasedValue.perLevel(2f), AttributeModifier.Operation.ADD_VALUE))
                .effectIcon(sprite("powerful_lightfrag"))
                .register(context);
        EquipmentUpgrade.builder(MAGNUM_SCALING_ROUNDS)
                .supports(LimaTechItems.MAGNUM)
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DynamicDamageTagUpgradeEffect.of(LimaTechTags.DamageTypes.BYPASS_SURVIVAL_DEFENSES))
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueUpgradeEffect.create(MathOpsNumberProvider.of(TargetedAttributeValueProvider.of(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH), ConstantValue.exactly(0.25f), MathOperation.MULTIPLY), CompoundValueOperation.FLAT_ADDITION,
                        new AttributeAmountTooltip(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH, LevelBasedValue.constant(0.25f))))
                .effectIcon(bottomLeftComposite(itemIcon(LimaTechItems.MAGNUM), sprite("powerful_lightfrag")))
                .register(context);
        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withEffect(WEAPON_PROJECTILE_SPEED, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.FLAT_ADDITION))
                .effectIcon(sprite("grenade_speed_boost"))
                .register(context);

        // Universal upgrades
        EquipmentUpgrade.builder(WEAPON_VIBRATION_CANCEL)
                .supports(ltxProjectileWeapons)
                .withEffect(PREVENT_VIBRATION, PreventVibrationUpgradeEffect.suppressMainHand(gameEvents.getOrThrow(LimaTechTags.GameEvents.WEAPON_VIBRATIONS)))
                .effectIcon(sprite("earmuffs"))
                .register(context);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supports(ltxProjectileWeapons)
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DynamicDamageTagUpgradeEffect.of(DamageTypeTags.NO_ANGER))
                .effectIcon(sprite("stealth_damage"))
                .register(context);
        EquipmentUpgrade.builder(UNIVERSAL_ENERGY_AMMO)
                .createDefaultTitle(LimaTechConstants.REM_BLUE)
                .supports(ltxProjectileWeapons)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(AMMO_SOURCE, WeaponAmmoSource.COMMON_ENERGY_UNIT)
                .effectIcon(sprite("energy_ammo"))
                .register(context);
        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .createDefaultTitle(LimaTechConstants.CREATIVE_PINK)
                .supports(ltxProjectileWeapons)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(AMMO_SOURCE, WeaponAmmoSource.INFINITE)
                .effectIcon(sprite("infinite_ammo"))
                .register(context);
        EquipmentUpgrade.builder(WEAPON_ARMOR_PIERCE)
                .supports(ltxProjectileWeapons)
                .setMaxRank(3)
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, new ModifyReductionsUpgradeEffect(DamageReductionType.ARMOR, LevelBasedValue.perLevel(0.75f, -0.25f)))
                .effectIcon(sprite("broken_armor"))
                .register(context);
        EquipmentUpgrade.builder(UNIVERSAL_SHIELD_REGEN)
                .supports(ltxProjectileWeapons)
                .setMaxRank(3)
                .withTargetedEffect(EQUIPMENT_KILL, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER, new BubbleShieldUpgradeEffect(LevelBasedValue.constant(4), LevelBasedValue.perLevel(10)))
                .withTargetedEffect(EQUIPMENT_KILL, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER, MobEffectUpgradeEffect.create(MobEffects.REGENERATION, LevelBasedValue.constant(60)))
                .effectIcon(sprite("bubble_shield"))
                .register(context);
        EquipmentUpgrade.builder(WEAPON_DIRECT_DROPS)
                .supports(ltxAllWeapons)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.entityDrops(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .register(context);

        // Enchantments
        EquipmentUpgrade.builder(SILK_TOUCH_ENCHANT)
                .supports(ltxMiningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.constantLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(bottomLeftComposite(sprite("pickaxe_head"), sprite("silk_touch")))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(FORTUNE_ENCHANTMENT)
                .supports(ltxMiningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.FORTUNE)))
                .withEffect(ENERGY_USAGE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE, true))
                .effectIcon(bottomLeftComposite(sprite("pickaxe_head"), sprite("clover")))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supports(ltxAllWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(bottomRightComposite(itemIcon(LimaTechItems.LTX_SWORD), sprite("clover")))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supports(ltxAllWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supports(ltxAllWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor_enchant"))
                .category("enchants")
                .register(context);

        // Hanabi grenade cores
        EquipmentUpgrade.builder(FLAME_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .effectIcon(sprite("flame_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(CRYO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .effectIcon(sprite("cryo_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(ELECTRIC_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .effectIcon(sprite("electric_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(ACID_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .effectIcon(sprite("acid_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(NEURO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("neuro_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(OMNI_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("omni_grenade_core"))
                .category("grenade_cores")
                .register(context);
    }
}