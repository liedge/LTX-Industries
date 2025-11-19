package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.advancement.ComparableBounds;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.lib.damage.DamageReductionType;
import liedge.limacore.lib.math.CompareOperation;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.limacore.world.loot.condition.EntityHostilityLootCondition;
import liedge.limacore.world.loot.condition.NumberComparisonLootCondition;
import liedge.limacore.world.loot.number.EntityAttributeValueProvider;
import liedge.limacore.world.loot.number.MathOpsNumberProvider;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.*;
import liedge.ltxindustries.lib.upgrades.effect.entity.BubbleShieldUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.DynamicDamageTagUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.MobEffectUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.value.*;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.tooltip.*;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

import java.util.Optional;

import static liedge.ltxindustries.LTXIConstants.*;
import static liedge.ltxindustries.LTXITags.EquipmentUpgrades.*;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.data.generation.LTXIBootstrapUtil.*;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.itemIcon;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.sprite;
import static liedge.ltxindustries.registry.bootstrap.LTXIEnchantments.AMMO_SCAVENGER;
import static liedge.ltxindustries.registry.bootstrap.LTXIEnchantments.RAZOR;
import static liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents.*;

public final class LTXIEquipmentUpgrades
{
    private LTXIEquipmentUpgrades() {}

    // Built-in upgrades
    public static final ResourceKey<EquipmentUpgrade> LTX_SHOVEL_DEFAULT = key("default/ltx_shovel");
    public static final ResourceKey<EquipmentUpgrade> LTX_WRENCH_DEFAULT = key("default/ltx_wrench");
    public static final ResourceKey<EquipmentUpgrade> LTX_MELEE_DEFAULT = key("default/ltx_melee");
    public static final ResourceKey<EquipmentUpgrade> GLOWSTICK_LAUNCHER_DEFAULT = key("default/glowstick_launcher");
    public static final ResourceKey<EquipmentUpgrade> SUBMACHINE_GUN_DEFAULT = key("default/submachine_gun");
    public static final ResourceKey<EquipmentUpgrade> SHOTGUN_DEFAULT = key("default/shotgun");
    public static final ResourceKey<EquipmentUpgrade> LFR_DEFAULT = key("default/linear_fusion_rifle");

    // Tool upgrades
    public static final ResourceKey<EquipmentUpgrade> TOOL_ENERGY_UPGRADE = key("tool_energy_upgrade");
    public static final ResourceKey<EquipmentUpgrade> EPSILON_FISHING_LURE = key("epsilon_fishing_lure");
    public static final ResourceKey<EquipmentUpgrade> TOOL_NETHERITE_LEVEL = key("tool_netherite_level");
    public static final ResourceKey<EquipmentUpgrade> EPSILON_OMNI_DRILL = key("epsilon_omni_drill");
    public static final ResourceKey<EquipmentUpgrade> TOOL_VIBRATION_CANCEL = key("tool_vibration_cancel");
    public static final ResourceKey<EquipmentUpgrade> TOOL_DIRECT_DROPS = key("tool_direct_drops");

    // Weapon upgrades
    public static final ResourceKey<EquipmentUpgrade> WEAPON_VIBRATION_CANCEL = key("weapon_vibration_cancel");
    public static final ResourceKey<EquipmentUpgrade> WEAPON_DIRECT_DROPS = key("weapon_direct_drops");
    public static final ResourceKey<EquipmentUpgrade> WEAPON_ARMOR_PIERCE = key("weapon_armor_pierce");
    public static final ResourceKey<EquipmentUpgrade> LIGHTWEIGHT_ENERGY_ADAPTER = key("lightweight_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> SPECIALIST_ENERGY_ADAPTER = key("specialist_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> EXPLOSIVES_ENERGY_ADAPTER = key("explosives_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> HEAVY_ENERGY_ADAPTER = key("heavy_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_INFINITE_AMMO = key("universal_infinite_ammo");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_STEALTH_DAMAGE = key("universal_stealth_damage");
    public static final ResourceKey<EquipmentUpgrade> NEUTRAL_ENEMY_TARGET_FILTER = key("target_filter/neutral_enemy");
    public static final ResourceKey<EquipmentUpgrade> HOSTILE_TARGET_FILTER = key("target_filter/hostile");
    public static final ResourceKey<EquipmentUpgrade> WEAPON_SHIELD_REGEN = key("weapon_shield_regen");
    public static final ResourceKey<EquipmentUpgrade> HIGH_IMPACT_ROUNDS = key("high_impact_rounds");
    public static final ResourceKey<EquipmentUpgrade> HEAVY_PISTOL_GOD_ROUNDS = key("heavy_pistol_god_rounds");
    public static final ResourceKey<EquipmentUpgrade> GRENADE_LAUNCHER_PROJECTILE_SPEED = key("grenade_launcher_projectile_speed");

    // Enchantments
    public static final ResourceKey<EquipmentUpgrade> EFFICIENCY_ENCHANTMENT = key("enchantment/efficiency");
    public static final ResourceKey<EquipmentUpgrade> SILK_TOUCH_ENCHANTMENT = key("enchantment/silk_touch");
    public static final ResourceKey<EquipmentUpgrade> FORTUNE_ENCHANTMENT = key("enchantment/fortune");
    public static final ResourceKey<EquipmentUpgrade> LOOTING_ENCHANTMENT = key("enchantment/looting");
    public static final ResourceKey<EquipmentUpgrade> AMMO_SCAVENGER_ENCHANTMENT = key("enchantment/ammo_scavenger");
    public static final ResourceKey<EquipmentUpgrade> RAZOR_ENCHANTMENT = key("enchantment/razor");

    // Hanabi grenade cores
    public static final ResourceKey<EquipmentUpgrade> FLAME_GRENADE_CORE = key("flame_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> CRYO_GRENADE_CORE = key("cryo_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> ELECTRIC_GRENADE_CORE = key("electric_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> ACID_GRENADE_CORE = key("acid_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> NEURO_GRENADE_CORE = key("neuro_grenade_core");
    public static final ResourceKey<EquipmentUpgrade> OMNI_GRENADE_CORE = key("omni_grenade_core");

    private static ResourceKey<EquipmentUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LTXIRegistries.Keys.EQUIPMENT_UPGRADES, name);
    }

    public static void bootstrap(BootstrapContext<EquipmentUpgrade> context)
    {
        // Holder getters
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<GameEvent> gameEvents = context.lookup(Registries.GAME_EVENT);
        HolderGetter<EquipmentUpgrade> holders = context.lookup(LTXIRegistries.Keys.EQUIPMENT_UPGRADES);

        // AnyHolderSets
        HolderSet<Block> anyBlockHolderSet = new AnyHolderSet<>(BuiltInRegistries.BLOCK.asLookup());
        HolderSet<Item> anyItemHolderSet = new AnyHolderSet<>(BuiltInRegistries.ITEM.asLookup());

        // Common holder sets
        HolderSet<Item> ltxAllTools = items.getOrThrow(LTXITags.Items.ALL_TOOLS);
        HolderSet<Item> ltxMiningTools = items.getOrThrow(LTXITags.Items.MINING_TOOLS);
        HolderSet<Item> ltxProjectileWeapons = items.getOrThrow(LTXITags.Items.ENERGY_PROJECTILE_WEAPONS);
        HolderSet<Item> ltxAllWeapons = items.getOrThrow(LTXITags.Items.ALL_WEAPONS);

        // Built in upgrades
        final Component defaultToolTitle = LTXILangKeys.TOOL_DEFAULT_UPGRADE_TITLE.translate().withStyle(LIME_GREEN.chatStyle());
        EquipmentUpgrade.builder(LTX_SHOVEL_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LTXIItems.LTX_SHOVEL)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.fixed(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(defaultModuleIcon(LTXIItems.LTX_SHOVEL))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(LTX_WRENCH_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LTXIItems.LTX_WRENCH)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.blocksOnly(items.getOrThrow(LTXITags.Items.WRENCH_BREAKABLE)))
                .effectIcon(defaultModuleIcon(LTXIItems.LTX_WRENCH))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(LTX_MELEE_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(items.getOrThrow(LTXITags.Items.MELEE_WEAPONS))
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.fixed(enchantments.getOrThrow(RAZOR), 1, 5))
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.fixed(enchantments.getOrThrow(Enchantments.LOOTING), 1, 5))
                .withConditionalEffect(EQUIPMENT_DAMAGE, SimpleValueEffect.of(ConstantDouble.of(0.2d), MathOperation.ADD_PERCENT_OF_TOTAL), NumberComparisonLootCondition.comparingValues(
                        EntityAttributeValueProvider.totalValue(LootContext.EntityTarget.THIS, Attributes.ARMOR),
                        EntityAttributeValueProvider.baseValue(LootContext.EntityTarget.THIS, Attributes.ARMOR),
                        CompareOperation.LESS_THAN_OR_EQUALS))
                .tooltip(0, key -> TranslatableTooltip.create(key, ValueComponent.of(ConstantDouble.of(0.2d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE)))
                .effectIcon(bottomRightComposite(sprite("razor"), sprite("default_overlay"), 7))
                .category("default/tool")
                .register(context);

        UpgradeDoubleValue gslEnergyCap = ConstantDouble.of(50_000);
        UpgradeDoubleValue gslEnergyUse = ConstantDouble.of(5000);
        EquipmentUpgrade.builder(GLOWSTICK_LAUNCHER_DEFAULT)
                .supports(LTXIItems.GLOWSTICK_LAUNCHER)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(gslEnergyCap, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(gslEnergyUse, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(gslEnergyCap, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(gslEnergyUse, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(defaultModuleIcon(LTXIItems.GLOWSTICK_LAUNCHER))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(SUBMACHINE_GUN_DEFAULT)
                .supports(LTXIItems.SUBMACHINE_GUN)
                .withEffect(PREVENT_VIBRATION, PreventVibrationUpgradeEffect.suppressMainHand(gameEvents.getOrThrow(LTXITags.GameEvents.WEAPON_VIBRATIONS)))
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DynamicDamageTagUpgradeEffect.of(DamageTypeTags.NO_ANGER, DamageTypeTags.NO_KNOCKBACK))
                .effectIcon(defaultModuleIcon(LTXIItems.SUBMACHINE_GUN))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(SHOTGUN_DEFAULT)
                .supports(LTXIItems.SHOTGUN)
                .itemAttributes(Attributes.MOVEMENT_SPEED, "speed", LevelBasedValue.constant(0.25f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.MAINHAND)
                .itemAttributes(Attributes.STEP_HEIGHT, "step_height", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND)
                .itemAttributes(Attributes.SAFE_FALL_DISTANCE, "safe_fall_dist", LevelBasedValue.constant(3), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND)
                .withEffect(DAMAGE_REDUCTION_MODIFIER, new ModifyReductionsUpgradeEffect(DamageReductionType.ARMOR, LevelBasedValue.constant(-0.1f)))
                .effectIcon(defaultModuleIcon(LTXIItems.SHOTGUN))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(LFR_DEFAULT)
                .supports(LTXIItems.LINEAR_FUSION_RIFLE)
                .withConditionalEffect(EQUIPMENT_DAMAGE, SimpleValueEffect.of(ConstantDouble.of(25), MathOperation.ADD),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                .distance(DistancePredicate.absolute(MinMaxBounds.Doubles.atLeast(40.0d)))))
                .withConditionalEffect(EQUIPMENT_DAMAGE, SimpleValueEffect.of(ConstantDouble.of(0.25d), MathOperation.ADD_PERCENT_OF_TOTAL),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                .moving(MovementPredicate.speed(MinMaxBounds.Doubles.atMost(1e-3d)))
                                .flags(EntityFlagsPredicate.Builder.flags().setCrouching(true))))
                .tooltip(0, key -> TranslatableTooltip.create(key, ValueComponent.of(ConstantDouble.of(25), ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .tooltip(1, key -> TranslatableTooltip.create(key, ValueComponent.of(ConstantDouble.of(0.25d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE)))
                .effectIcon(defaultModuleIcon(LTXIItems.LINEAR_FUSION_RIFLE))
                .category("default/weapon")
                .register(context);

        // Tool upgrades
        UpgradeDoubleValue toolEnergy = ExponentialDouble.of(2, LinearDouble.oneIncrement(2));
        EquipmentUpgrade.builder(TOOL_ENERGY_UPGRADE)
                .createDefaultTitle(REM_BLUE)
                .supports(ltxAllTools)
                .setMaxRank(4)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(toolEnergy, MathOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueEffect.of(toolEnergy, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(toolEnergy, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(toolEnergy, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .effectIcon(sprite("extra_energy"))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(EPSILON_FISHING_LURE)
                .createDefaultTitle(LIME_GREEN)
                .supports(LTXIItems.LTX_FISHING_ROD)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(Enchantments.LURE)))
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(Enchantments.LUCK_OF_THE_SEA)))
                .effectIcon(plusOverlay(itemIcon(LTXIItems.LTX_FISHING_ROD)))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(TOOL_NETHERITE_LEVEL)
                .supports(ltxMiningTools)
                .exclusiveWith(holders, MINING_LEVEL_UPGRADES)
                .withEffect(MINING_RULES, MiningRuleUpgradeEffect.miningLevelAndSpeed(blocks.getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL), 11f, 2))
                .effectIcon(greenArrowOverlay(itemIcon(Items.NETHERITE_PICKAXE)))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(EPSILON_OMNI_DRILL)
                .createDefaultTitle(LIME_GREEN)
                .supports(LTXIItems.LTX_DRILL)
                .withEffect(MINING_RULES, new MiningRuleUpgradeEffect(Optional.of(anyBlockHolderSet), Optional.empty(), Optional.empty(), 3))
                .effectIcon(sprite("purple_drill_head"))
                .category("tools/drill")
                .register(context);
        EquipmentUpgrade.builder(TOOL_VIBRATION_CANCEL)
                .supports(items.getOrThrow(LTXITags.Items.ALL_TOOLS))
                .withEffect(PREVENT_VIBRATION, PreventVibrationUpgradeEffect.suppressHands(gameEvents.getOrThrow(LTXITags.GameEvents.HANDHELD_EQUIPMENT)))
                .effectIcon(sprite("earmuffs"))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(TOOL_DIRECT_DROPS)
                .supports(ltxMiningTools)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.blocksOnly(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .category("tools")
                .register(context);

        // Weapon-specific upgrades
        EquipmentUpgrade.builder(HIGH_IMPACT_ROUNDS)
                .supports(LTXIItems.SHOTGUN, LTXIItems.HEAVY_PISTOL)
                .damageAttributes(Attributes.KNOCKBACK_RESISTANCE, "knockback_resistance", LevelBasedValue.constant(-1f), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .damageAttributes(LimaCoreAttributes.KNOCKBACK_MULTIPLIER, "knockback", LevelBasedValue.constant(2f), AttributeModifier.Operation.ADD_VALUE)
                .effectIcon(sprite("powerful_lightfrag"))
                .register(context);
        EquipmentUpgrade.builder(HEAVY_PISTOL_GOD_ROUNDS)
                .supports(LTXIItems.HEAVY_PISTOL)
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DynamicDamageTagUpgradeEffect.of(LTXITags.DamageTypes.BYPASS_SURVIVAL_DEFENSES))
                .withConditionalEffect(EQUIPMENT_DAMAGE, ContextValueEffect.of(MathOpsNumberProvider.of(EntityAttributeValueProvider.totalValue(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH), ConstantValue.exactly(0.25f), MathOperation.MULTIPLY), MathOperation.ADD))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.ATTRIBUTE_SCALED_DAMAGE_UPGRADE,
                        ValueComponent.of(ConstantDouble.of(0.25d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE),
                        StaticTooltip.of(Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId()).withStyle(ChatFormatting.DARK_RED))))
                .effectIcon(plusOverlay(itemIcon(LTXIItems.HEAVY_PISTOL)))
                .register(context);
        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withEffect(WEAPON_RANGE, SimpleValueEffect.of(LinearDouble.linearIncrement(0.5d), MathOperation.ADD))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.PROJECTILE_SPEED_UPGRADE, ValueComponent.of(LinearDouble.linearIncrement(0.5d), ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .effectIcon(sprite("grenade_speed_boost"))
                .register(context);

        // Universal upgrades
        EquipmentUpgrade.builder(WEAPON_VIBRATION_CANCEL)
                .supports(ltxProjectileWeapons)
                .withEffect(PREVENT_VIBRATION, PreventVibrationUpgradeEffect.suppressMainHand(gameEvents.getOrThrow(LTXITags.GameEvents.WEAPON_VIBRATIONS)))
                .effectIcon(sprite("earmuffs"))
                .register(context);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supports(ltxProjectileWeapons)
                .withTargetedEffect(EQUIPMENT_PRE_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, DynamicDamageTagUpgradeEffect.of(DamageTypeTags.NO_ANGER))
                .effectIcon(sprite("stealth_damage"))
                .register(context);
        EquipmentUpgrade.builder(WEAPON_DIRECT_DROPS)
                .supports(ltxAllWeapons)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.entityDrops(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .register(context);
        EquipmentUpgrade.builder(WEAPON_ARMOR_PIERCE)
                .supports(ltxProjectileWeapons)
                .setMaxRank(3)
                .withEffect(DAMAGE_REDUCTION_MODIFIER, new ModifyReductionsUpgradeEffect(DamageReductionType.ARMOR, LevelBasedValue.perLevel(-0.2f)))
                .effectIcon(sprite("broken_armor"))
                .register(context);

        UpgradeDoubleValue lightweightEnergyCapacity = ConstantDouble.of(100_000);
        UpgradeDoubleValue lightweightEnergyUsage = ConstantDouble.of(10_000);
        EquipmentUpgrade.builder(LIGHTWEIGHT_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.LIGHTWEIGHT_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(lightweightEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(lightweightEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(lightweightEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(lightweightEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(LIGHTWEIGHT_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        UpgradeDoubleValue specialistEnergyCapacity = ConstantDouble.of(5_000_000);
        UpgradeDoubleValue specialistEnergyUsage = ConstantDouble.of(1_000_000);
        EquipmentUpgrade.builder(SPECIALIST_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.SPECIALIST_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(specialistEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(specialistEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(specialistEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(specialistEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(SPECIALIST_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        UpgradeDoubleValue explosivesEnergyCapacity = ConstantDouble.of(20_000_000);
        UpgradeDoubleValue explosivesEnergyUsage = ConstantDouble.of(10_000_000);
        EquipmentUpgrade.builder(EXPLOSIVES_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.EXPLOSIVE_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(explosivesEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(explosivesEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(explosivesEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(explosivesEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(EXPLOSIVES_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        UpgradeDoubleValue heavyEnergyCapacity = ConstantDouble.of(50_000_000);
        UpgradeDoubleValue heavyEnergyUsage = ConstantDouble.of(25_000_000);
        EquipmentUpgrade.builder(HEAVY_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.HEAVY_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(heavyEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(heavyEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(heavyEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(heavyEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(HEAVY_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .createDefaultTitle(LTXIConstants.CREATIVE_PINK)
                .supports(ltxProjectileWeapons)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.infiniteAmmo())
                .effectIcon(sprite("infinite_ammo"))
                .category("weapon/ammo")
                .register(context);
        EquipmentUpgrade.builder(NEUTRAL_ENEMY_TARGET_FILTER)
                .createDefaultTitle(HOSTILE_ORANGE)
                .supports(ltxAllWeapons)
                .withEffect(TARGET_CONDITIONS, EntityHostilityLootCondition.create(ComparableBounds.atLeast(MobHostility.NEUTRAL_ENEMY)).build())
                .effectIcon(sprite("neutral_enemy_targets"))
                .category("target_filters")
                .register(context);
        EquipmentUpgrade.builder(HOSTILE_TARGET_FILTER)
                .createDefaultTitle(HOSTILE_ORANGE)
                .supports(ltxAllWeapons)
                .withEffect(TARGET_CONDITIONS, EntityHostilityLootCondition.create(ComparableBounds.atLeast(MobHostility.HOSTILE)).build())
                .effectIcon(sprite("hostile_targets"))
                .category("target_filters")
                .register(context);
        EquipmentUpgrade.builder(WEAPON_SHIELD_REGEN)
                .supports(ltxProjectileWeapons)
                .setMaxRank(3)
                .withTargetedEffect(EQUIPMENT_KILL, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER, new BubbleShieldUpgradeEffect(LevelBasedValue.constant(4), LevelBasedValue.perLevel(10)))
                .withTargetedEffect(EQUIPMENT_KILL, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER, MobEffectUpgradeEffect.create(MobEffects.REGENERATION, LevelBasedValue.constant(60)))
                .effectIcon(sprite("bubble_shield"))
                .register(context);

        // Enchantments
        EquipmentUpgrade.builder(EFFICIENCY_ENCHANTMENT)
                .supports(ltxMiningTools)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(Enchantments.EFFICIENCY)))
                .effectIcon(bottomRightComposite(itemIcon(LTXIItems.LTX_DRILL), sprite("yellow_arrow_overlay"), 9))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(SILK_TOUCH_ENCHANTMENT)
                .supports(ltxMiningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.fixed(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(bottomRightComposite(itemIcon(LTXIItems.LTX_DRILL), sprite("silk_overlay"), 8))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(FORTUNE_ENCHANTMENT)
                .supports(ltxMiningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(Enchantments.FORTUNE)))
                .effectIcon(luckOverlayIcon(LTXIItems.LTX_DRILL))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supports(ltxAllWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(luckOverlayIcon(LTXIItems.LTX_SWORD))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supports(ltxAllWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supports(ltxAllWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor"))
                .category("enchants")
                .register(context);

        // Hanabi grenade cores
        EquipmentUpgrade.builder(FLAME_GRENADE_CORE)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .effectIcon(sprite("flame_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(CRYO_GRENADE_CORE)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .effectIcon(sprite("cryo_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(ELECTRIC_GRENADE_CORE)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .effectIcon(sprite("electric_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(ACID_GRENADE_CORE)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .effectIcon(sprite("acid_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(NEURO_GRENADE_CORE)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("neuro_grenade_core"))
                .category("grenade_cores")
                .register(context);
        EquipmentUpgrade.builder(OMNI_GRENADE_CORE)
                .supports(LTXIItems.GRENADE_LAUNCHER)
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