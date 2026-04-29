package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.lib.math.CompareOperation;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.limacore.world.loot.condition.CompareValuesCondition;
import liedge.limacore.world.loot.condition.EntityHostilityCondition;
import liedge.limacore.world.loot.number.EntityAttributeValue;
import liedge.limacore.world.loot.number.ValueMathOperation;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.effect.*;
import liedge.ltxindustries.lib.upgrades.effect.entity.ApplyMobEffect;
import liedge.ltxindustries.lib.upgrades.tooltip.*;
import liedge.ltxindustries.lib.upgrades.value.*;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

import static liedge.ltxindustries.LTXIConstants.*;
import static liedge.ltxindustries.LTXIConstants.BUBBLE_SHIELD_BLUE;
import static liedge.ltxindustries.LTXIConstants.CREATIVE_PINK;
import static liedge.ltxindustries.LTXIIdentifiers.*;
import static liedge.ltxindustries.LTXIIdentifiers.ID_AURORA;
import static liedge.ltxindustries.LTXIIdentifiers.ID_NOVA;
import static liedge.ltxindustries.LTXIIdentifiers.ID_SERENITY;
import static liedge.ltxindustries.LTXIIdentifiers.ID_STARGAZER;
import static liedge.ltxindustries.LTXIIdentifiers.ID_WONDERLAND_BODY;
import static liedge.ltxindustries.LTXIIdentifiers.ID_WONDERLAND_FEET;
import static liedge.ltxindustries.LTXIIdentifiers.ID_WONDERLAND_HEAD;
import static liedge.ltxindustries.LTXIIdentifiers.ID_WONDERLAND_LEGS;
import static liedge.ltxindustries.LTXITags.Upgrades.*;
import static liedge.ltxindustries.LTXITags.Upgrades.MINING_DROPS_MODIFIERS;
import static liedge.ltxindustries.data.generation.LTXIBootstrapUtil.*;
import static liedge.ltxindustries.data.generation.LTXIBootstrapUtil.luckOverlay;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.itemIcon;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.sprite;
import static liedge.ltxindustries.registry.bootstrap.LTXIEnchantments.AMMO_SCAVENGER;
import static liedge.ltxindustries.registry.bootstrap.LTXIEnchantments.RAZOR;
import static liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents.*;
import static liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents.GRENADE_UNLOCK;

public final class LTXIUpgrades
{
    private LTXIUpgrades() {}

    private static ResourceKey<Upgrade> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(LTXIRegistries.Keys.UPGRADES, name);
    }

    private static ResourceKey<Upgrade> defaultKey(String name)
    {
        return key("default/" + name);
    }

    //#region Universal upgrades

    // Targeting
    public static final ResourceKey<Upgrade> ALL_ENTITIES_TARGETING = key("targeting/all");
    public static final ResourceKey<Upgrade> NEUTRAL_ENEMY_TARGETING = key("targeting/neutral_enemy");
    public static final ResourceKey<Upgrade> HOSTILE_TARGETING = key("targeting/hostile_only");

    // Misc
    public static final ResourceKey<Upgrade> NO_ANGER_ATTACKS = key("no_anger_attacks");
    public static final ResourceKey<Upgrade> MOB_DROPS_CAPTURE = key("mob_drops_capture");

    //#endregion

    //#region Equipment exclusive upgrades

    // Default equipment upgrades
    public static final ResourceKey<Upgrade> EPSILON_SHOVEL_DEFAULT = defaultKey(ID_EPSILON_SHOVEL);
    public static final ResourceKey<Upgrade> EPSILON_WRENCH_DEFAULT = defaultKey(ID_EPSILON_WRENCH);
    public static final ResourceKey<Upgrade> EPSILON_MELEE_DEFAULT = defaultKey("epsilon_melee");
    public static final ResourceKey<Upgrade> WAYFINDER_DEFAULT = defaultKey(ID_WAYFINDER);
    public static final ResourceKey<Upgrade> SERENITY_DEFAULT = defaultKey(ID_SERENITY);
    public static final ResourceKey<Upgrade> MIRAGE_DEFAULT = defaultKey(ID_MIRAGE);
    public static final ResourceKey<Upgrade> AURORA_DEFAULT = defaultKey(ID_AURORA);
    public static final ResourceKey<Upgrade> STARGAZER_DEFAULT = defaultKey(ID_STARGAZER);
    public static final ResourceKey<Upgrade> NOVA_DEFAULT = defaultKey(ID_NOVA);
    public static final ResourceKey<Upgrade> HEAD_DEFAULT = defaultKey(ID_WONDERLAND_HEAD);
    public static final ResourceKey<Upgrade> BODY_DEFAULT = defaultKey(ID_WONDERLAND_BODY);
    public static final ResourceKey<Upgrade> LEGS_DEFAULT = defaultKey(ID_WONDERLAND_LEGS);
    public static final ResourceKey<Upgrade> FEET_DEFAULT = defaultKey(ID_WONDERLAND_FEET);

    // General equipment upgrades
    public static final ResourceKey<Upgrade> EQUIPMENT_ENERGY_UPGRADE = key("equipment_energy_upgrade");
    public static final ResourceKey<Upgrade> EQUIPMENT_BLOCK_DROPS_CAPTURE = key("equipment_block_drops_capture");

    // Tool upgrades
    public static final ResourceKey<Upgrade> EPSILON_FISHING_LURE = key("epsilon_fishing_lure");
    public static final ResourceKey<Upgrade> TOOL_NETHERITE_LEVEL = key("tool_netherite_level");
    public static final ResourceKey<Upgrade> EPSILON_OMNI_DRILL = key("epsilon_omni_drill");
    public static final ResourceKey<Upgrade> TREE_VEIN_MINE = key("tree_vein_mine");
    public static final ResourceKey<Upgrade> TOOL_VIBRATION_CANCEL = key("tool_vibration_cancel");

    // Weapon upgrades
    public static final ResourceKey<Upgrade> WEAPON_VIBRATION_CANCEL = key("weapon_vibration_cancel");
    public static final ResourceKey<Upgrade> LIGHTWEIGHT_ENERGY_ADAPTER = key("lightweight_energy_adapter");
    public static final ResourceKey<Upgrade> SPECIALIST_ENERGY_ADAPTER = key("specialist_energy_adapter");
    public static final ResourceKey<Upgrade> EXPLOSIVES_ENERGY_ADAPTER = key("explosives_energy_adapter");
    public static final ResourceKey<Upgrade> HEAVY_ENERGY_ADAPTER = key("heavy_energy_adapter");
    public static final ResourceKey<Upgrade> INFINITE_AMMO = key("infinite_ammo");
    public static final ResourceKey<Upgrade> NOVA_GOD_ROUNDS = key("nova_god_rounds");
    public static final ResourceKey<Upgrade> HANABI_SPEED_BOOST = key("hanabi_speed_boost");

    // Armor upgrades
    public static final ResourceKey<Upgrade> PASSIVE_NIGHT_VISION = key("night_vision");
    public static final ResourceKey<Upgrade> ARMOR_PASSIVE_SHIELD = key("armor_passive_shield");
    public static final ResourceKey<Upgrade> ARMOR_DEFENSE = key("armor_defense");
    public static final ResourceKey<Upgrade> BREATHING_UNIT = key("breathing_unit");
    public static final ResourceKey<Upgrade> PASSIVE_SATURATION = key("saturation");
    public static final ResourceKey<Upgrade> CREATIVE_FLIGHT = key("creative_flight");

    // Enchantments
    public static final ResourceKey<Upgrade> EFFICIENCY_ENCHANTMENT = key("enchantment/efficiency");
    public static final ResourceKey<Upgrade> SILK_TOUCH_ENCHANTMENT = key("enchantment/silk_touch");
    public static final ResourceKey<Upgrade> FORTUNE_ENCHANTMENT = key("enchantment/fortune");
    public static final ResourceKey<Upgrade> LOOTING_ENCHANTMENT = key("enchantment/looting");
    public static final ResourceKey<Upgrade> AMMO_SCAVENGER_ENCHANTMENT = key("enchantment/ammo_scavenger");
    public static final ResourceKey<Upgrade> RAZOR_ENCHANTMENT = key("enchantment/razor");

    // Hanabi grenade cores
    public static final ResourceKey<Upgrade> FLAME_GRENADE_CORE = key("flame_grenade_core");
    public static final ResourceKey<Upgrade> CRYO_GRENADE_CORE = key("cryo_grenade_core");
    public static final ResourceKey<Upgrade> ELECTRIC_GRENADE_CORE = key("electric_grenade_core");
    public static final ResourceKey<Upgrade> ACID_GRENADE_CORE = key("acid_grenade_core");
    public static final ResourceKey<Upgrade> NEURO_GRENADE_CORE = key("neuro_grenade_core");
    public static final ResourceKey<Upgrade> OMNI_GRENADE_CORE = key("omni_grenade_core");

    //#endregion

    //#region Machine exclusive upgrades

    public static final ResourceKey<Upgrade> ECA_CAPACITY_UPGRADE = key("eca_capacity");
    public static final ResourceKey<Upgrade> STANDARD_MACHINE_SYSTEMS = key("standard_machine_systems");
    public static final ResourceKey<Upgrade> ULTIMATE_MACHINE_SYSTEMS = key("ultimate_machine_systems");
    public static final ResourceKey<Upgrade> GPM_PARALLEL = key("gpm_parallel");
    public static final ResourceKey<Upgrade> FABRICATOR_UPGRADE = key("fabricator_upgrade");

    public static final ResourceKey<Upgrade> GEO_SYNTHESIZER_PARALLEL = key("geo_synthesizer_parallel");

    public static final ResourceKey<Upgrade> TURRET_LOOTING = key("turret_enchantment/looting");
    public static final ResourceKey<Upgrade> TURRET_RAZOR = key("turret_enchantment/razor");

    //#endregion

    public static void bootstrap(BootstrapContext<Upgrade> context)
    {
        HolderGetter<Upgrade> holders = context.lookup(LTXIRegistries.Keys.UPGRADES);
        universalUpgrades(context, holders);
        equipmentUpgrades(context, holders);
        machineUpgrades(context, holders);
    }

    private static void universalUpgrades(BootstrapContext<Upgrade> context, HolderGetter<Upgrade> holders)
    {
        //  Holder getters
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<BlockEntityType<?>> blockEntities = context.lookup(Registries.BLOCK_ENTITY_TYPE);

        // Common holder sets
        HolderSet<Item> allWeapons = items.getOrThrow(LTXITags.Items.WEAPON_EQUIPMENT);
        HolderSet<BlockEntityType<?>> turrets = blockEntities.getOrThrow(LTXITags.BlockEntities.TURRETS);

        Upgrade.builder(ALL_ENTITIES_TARGETING)
                .createDefaultTitle(LTXIConstants.HOSTILE_ORANGE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .exclusiveWith(holders, TARGET_PREDICATES)
                .targetRestriction(AllOfCondition.allOf())
                .effectIcon(sprite("all_targets"))
                .category("target_predicates")
                .register(context);
        Upgrade.builder(NEUTRAL_ENEMY_TARGETING)
                .createDefaultTitle(LTXIConstants.HOSTILE_ORANGE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .exclusiveWith(holders, TARGET_PREDICATES)
                .targetRestriction(EntityHostilityCondition.attackerIs(MinMaxRange.atLeast(MobHostility.NEUTRAL_ENEMY)))
                .effectIcon(sprite("neutral_enemy_targets"))
                .category("target_predicates")
                .register(context);
        Upgrade.builder(HOSTILE_TARGETING)
                .createDefaultTitle(LTXIConstants.HOSTILE_ORANGE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .exclusiveWith(holders, TARGET_PREDICATES)
                .targetRestriction(EntityHostilityCondition.attackerIs(MinMaxRange.atLeast(MobHostility.HOSTILE)))
                .effectIcon(sprite("hostile_targets"))
                .category("target_predicates")
                .register(context);

        Upgrade.builder(NO_ANGER_ATTACKS)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_ANGER)
                .staticTooltip(0)
                .effectIcon(sprite("stealth_damage"))
                .register(context);
        Upgrade.builder(MOB_DROPS_CAPTURE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .withSpecialEffect(CAPTURE_MOB_DROPS, CaptureMobDrops.INSTANCE)
                .effectIcon(sprite("magnet"))
                .register(context);
    }

    private static void equipmentUpgrades(BootstrapContext<Upgrade> context, HolderGetter<Upgrade> holders)
    {
        // Holder getters
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<GameEvent> gameEvents = context.lookup(Registries.GAME_EVENT);
        HolderGetter<MobEffect> mobEffects = context.lookup(Registries.MOB_EFFECT);

        // AnyHolderSets
        HolderSet<Block> anyBlockHolderSet = new AnyHolderSet<>(BuiltInRegistries.BLOCK);
        HolderSet<Item> anyItemHolderSet = new AnyHolderSet<>(BuiltInRegistries.ITEM);

        // Common holder sets
        HolderSet<Item> miningTools = items.getOrThrow(LTXITags.Items.MINING_TOOLS);
        HolderSet<Item> modularMiningTools = items.getOrThrow(LTXITags.Items.MODULAR_MINING_TOOLS);
        HolderSet<Item> meleeWeapons = items.getOrThrow(LTXITags.Items.MELEE_WEAPONS);
        HolderSet<Item> projectileWeapons = items.getOrThrow(LTXITags.Items.ENERGY_PROJECTILE_WEAPONS);
        HolderSet<Item> allWeapons = items.getOrThrow(LTXITags.Items.WEAPON_EQUIPMENT);
        HolderSet<Item> wonderlandArmor = items.getOrThrow(LTXITags.Items.WONDERLAND_ARMOR);

        // Built in upgrades
        final Component defaultToolTitle = LTXILangKeys.TOOL_DEFAULT_UPGRADE_TITLE.translate().withStyle(LIME_GREEN.chatStyle());
        Upgrade.builder(EPSILON_SHOVEL_DEFAULT)
                .setTitle(defaultToolTitle)
                .forEquipment(LTXIItems.EPSILON_SHOVEL)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(defaultModuleIcon(LTXIItems.EPSILON_SHOVEL))
                .category("default/tool")
                .register(context);
        Upgrade.builder(EPSILON_WRENCH_DEFAULT)
                .setTitle(defaultToolTitle)
                .forEquipment(LTXIItems.EPSILON_WRENCH)
                .withEffect(CAPTURE_BLOCK_DROPS, CaptureBlockDrops.captureItems(items.getOrThrow(LTXITags.Items.WRENCH_BREAKABLE)))
                .effectIcon(defaultModuleIcon(LTXIItems.EPSILON_WRENCH))
                .category("default/tool")
                .register(context);
        Upgrade.builder(EPSILON_MELEE_DEFAULT)
                .setTitle(defaultToolTitle)
                .forEquipment(meleeWeapons)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(RAZOR), 1, 5))
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(Enchantments.LOOTING), 1, 5))
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(ConstantDouble.of(0.2d), MathOperation.ADD_PERCENT_OF_TOTAL), CompareValuesCondition.comparingValues(
                        EntityAttributeValue.totalValue(LootContext.EntityTarget.THIS, Attributes.ARMOR),
                        EntityAttributeValue.baseValue(LootContext.EntityTarget.THIS, Attributes.ARMOR),
                        CompareOperation.LESS_THAN_OR_EQUALS))
                .tooltip(0, key -> TranslatableTooltip.create(key, ValueComponent.of(ConstantDouble.of(0.2d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE)))
                .effectIcon(defaultOverlay(sprite("razor")))
                .category("default/tool")
                .register(context);

        ContextlessValue gslEnergyCap = ConstantDouble.of(50_000);
        ContextlessValue gslEnergyUse = ConstantDouble.of(5000);
        Upgrade.builder(WAYFINDER_DEFAULT)
                .forEquipment(LTXIItems.WAYFINDER)
                .exclusiveWith(holders, RELOAD_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(gslEnergyCap, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(gslEnergyUse, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(gslEnergyCap, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(gslEnergyUse, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(defaultModuleIcon(LTXIItems.WAYFINDER))
                .category("default/weapon")
                .register(context);
        Upgrade.builder(SERENITY_DEFAULT)
                .forEquipment(LTXIItems.SERENITY)
                .withEffect(SUPPRESS_VIBRATIONS, SuppressVibrations.mainHand(gameEvents.getOrThrow(LTXITags.GameEvents.WEAPON_VIBRATIONS)))
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_ANGER)
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_KNOCKBACK)
                .staticTooltip(0)
                .effectIcon(defaultModuleIcon(LTXIItems.SERENITY))
                .category("default/weapon")
                .register(context);
        Upgrade.builder(MIRAGE_DEFAULT)
                .forEquipment(LTXIItems.MIRAGE)
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_KNOCKBACK)
                .staticTooltip(0)
                .effectIcon(defaultModuleIcon(LTXIItems.MIRAGE))
                .category("default/weapon")
                .register(context);
        Upgrade.builder(AURORA_DEFAULT)
                .forEquipment(LTXIItems.AURORA)
                .itemAttributes(Attributes.MOVEMENT_SPEED, "speed", LevelBasedValue.constant(0.25f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.MAINHAND)
                .itemAttributes(Attributes.STEP_HEIGHT, "step_height", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND)
                .itemAttributes(Attributes.SAFE_FALL_DISTANCE, "safe_fall_dist", LevelBasedValue.constant(3), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND)
                .damageAttributes(Attributes.KNOCKBACK_RESISTANCE, "knockback_resist", LevelBasedValue.constant(-0.5f), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .damageAttributes(LimaCoreAttributes.KNOCKBACK_MULTIPLIER, "knockback", LevelBasedValue.constant(0.5f), AttributeModifier.Operation.ADD_VALUE)
                .effectIcon(defaultModuleIcon(LTXIItems.AURORA))
                .category("default/weapon")
                .register(context);
        Upgrade.builder(STARGAZER_DEFAULT)
                .forEquipment(LTXIItems.STARGAZER)
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(TargetDistanceCurve.of(ConstantDouble.of(30), ConstantDouble.of(50), ConstantDouble.of(50)), MathOperation.ADD))
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(ConstantDouble.of(0.15d), MathOperation.ADD_PERCENT_OF_TOTAL),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                .moving(MovementPredicate.speed(MinMaxBounds.Doubles.atMost(1e-3d)))
                                .flags(EntityFlagsPredicate.Builder.flags().setCrouching(true))))
                .tooltip(0, key -> TranslatableTooltip.create(key,
                        ValueComponent.of(ConstantDouble.of(1), ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE),
                        ValueComponent.of(ConstantDouble.of(30), ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL),
                        ValueComponent.of(ConstantDouble.of(50), ValueFormat.FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .tooltip(1, key -> TranslatableTooltip.create(key, ValueComponent.of(ConstantDouble.of(0.15d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE)))
                .effectIcon(defaultModuleIcon(LTXIItems.STARGAZER))
                .category("default/weapon")
                .register(context);
        Upgrade.builder(NOVA_DEFAULT)
                .forEquipment(LTXIItems.NOVA)
                .damageAttributes(Attributes.KNOCKBACK_RESISTANCE, "knockback_resist", LevelBasedValue.constant(-1), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .damageAttributes(LimaCoreAttributes.KNOCKBACK_MULTIPLIER, "knockback", LevelBasedValue.constant(2f), AttributeModifier.Operation.ADD_VALUE)
                .effectIcon(defaultModuleIcon(LTXIItems.NOVA))
                .category("default/weapon")
                .register(context);

        ContextlessValue headMECost = ConstantDouble.of(1);
        Upgrade.builder(HEAD_DEFAULT)
                .forEquipment(LTXIItems.WONDERLAND_HEAD)
                .withEffect(MOB_EFFECT_IMMUNITY, MobEffectImmunity.immuneTo(mobEffects, LTXITags.MobEffects.VISION_DEBUFF, headMECost))
                .tooltip(0, key -> TranslatableTooltip.create(key, energyActionsTooltip(headMECost)))
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_HEAD))
                .category("default/armor/1")
                .register(context);
        Upgrade.builder(BODY_DEFAULT)
                .forEquipment(LTXIItems.WONDERLAND_BODY)
                .itemAttributes(Attributes.BLOCK_INTERACTION_RANGE, "block_reach", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST)
                .itemAttributes(Attributes.ENTITY_INTERACTION_RANGE, "entity_reach", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST)
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_BODY))
                .category("default/armor/2")
                .register(context);
        ContextlessValue legsMECost = ConstantDouble.of(1);
        Upgrade.builder(LEGS_DEFAULT)
                .forEquipment(LTXIItems.WONDERLAND_LEGS)
                .itemAttributes(Attributes.MOVEMENT_SPEED, "speed_boost", LevelBasedValue.constant(0.1f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.LEGS)
                .itemAttributes(Attributes.WATER_MOVEMENT_EFFICIENCY, "water_move", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS)
                .withEffect(MOB_EFFECT_IMMUNITY, MobEffectImmunity.immuneTo(mobEffects, LTXITags.MobEffects.MOVEMENT_DEBUFF, legsMECost))
                .tooltip(0, key -> TranslatableTooltip.create(key, energyActionsTooltip(legsMECost)))
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_LEGS))
                .category("default/armor/3")
                .register(context);
        Upgrade.builder(FEET_DEFAULT)
                .forEquipment(LTXIItems.WONDERLAND_FEET)
                .withSpecialEffect(CANCEL_FALLS, CancelFall.cancelFalls(ConstantDouble.of(1)))
                .itemAttributes(Attributes.STEP_HEIGHT, "step_boost", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET)
                .itemAttributes(Attributes.SAFE_FALL_DISTANCE, "safe_fall", LevelBasedValue.constant(5), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET)
                .withConditionUnit(DAMAGE_IMMUNITY, DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))))
                .staticTooltip(0)
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_FEET))
                .category("default/armor/4")
                .register(context);

        // General equipment upgrades
        ContextlessValue equipmentEnergyAmt = ExponentialDouble.of(2, LinearDouble.oneIncrement(2));
        Upgrade.builder(EQUIPMENT_ENERGY_UPGRADE)
                .createDefaultTitle(REM_BLUE)
                .forEquipment(items, LTXITags.Items.ENERGY_UPGRADABLE_EQUIPMENT)
                .setMaxRank(4)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(equipmentEnergyAmt, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(equipmentEnergyAmt, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .effectIcon(sprite("extra_energy"))
                .category("equipment")
                .register(context);

        // Tool upgrades
        Upgrade.builder(EPSILON_FISHING_LURE)
                .createDefaultTitle(LIME_GREEN)
                .forEquipment(LTXIItems.EPSILON_FISHING_ROD)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LURE)))
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LUCK_OF_THE_SEA)))
                .effectIcon(plusOverlay(itemIcon(LTXIItems.EPSILON_FISHING_ROD)))
                .category("tools")
                .register(context);
        Upgrade.builder(TOOL_NETHERITE_LEVEL)
                .forEquipment(modularMiningTools)
                .exclusiveWith(holders, MINING_LEVELS)
                .withEffect(MODULAR_TOOL, ModularTool.limitedTo(blocks.getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)))
                .effectIcon(greenArrowOverlay(itemIcon(Items.NETHERITE_PICKAXE)))
                .category("tools")
                .register(context);
        Upgrade.builder(EPSILON_OMNI_DRILL)
                .createDefaultTitle(LIME_GREEN)
                .forEquipment(modularMiningTools)
                .withEffect(MODULAR_TOOL, ModularTool.effectiveOn(anyBlockHolderSet))
                .effectIcon(sprite("purple_drill_head"))
                .category("tools/drill")
                .register(context);
        Upgrade.builder(TREE_VEIN_MINE)
                .forEquipment(LTXIItems.EPSILON_AXE)
                .withEffect(VEIN_MINE, VeinMine.create(BlockPredicate.Builder.block().of(blocks, BlockTags.LOGS), BlockPredicate.Builder.block().of(blocks, BlockTags.LEAVES), VeinMine.MAX_BLOCK_LIMIT, false))
                .staticTooltip(0)
                .effectIcon(veinMineOverlay(itemIcon(Items.OAK_LOG)))
                .category("tools")
                .register(context);
        Upgrade.builder(TOOL_VIBRATION_CANCEL)
                .forEquipment(items.getOrThrow(LTXITags.Items.TOOL_EQUIPMENT))
                .withEffect(SUPPRESS_VIBRATIONS, SuppressVibrations.forSlot(EquipmentSlotGroup.HAND, gameEvents.getOrThrow(LTXITags.GameEvents.HANDHELD_EQUIPMENT)))
                .effectIcon(redXOverlay(itemIcon(Items.SCULK_SENSOR)))
                .category("tools")
                .register(context);
        Upgrade.builder(EQUIPMENT_BLOCK_DROPS_CAPTURE)
                .forEquipment(miningTools)
                .withEffect(CAPTURE_BLOCK_DROPS, CaptureBlockDrops.captureItems(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .category("tools")
                .register(context);

        // Weapon-specific upgrades
        Upgrade.builder(NOVA_GOD_ROUNDS)
                .forEquipment(LTXIItems.NOVA)
                .withEffect(EXTRA_DAMAGE_TAGS, LTXITags.DamageTypes.BYPASS_SURVIVAL_DEFENSES)
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(ValueMathOperation.of(EntityAttributeValue.totalValue(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH), ConstantValue.exactly(0.25f), MathOperation.MULTIPLY), MathOperation.ADD))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.ATTRIBUTE_SCALED_DAMAGE_UPGRADE,
                        ValueComponent.of(ConstantDouble.of(0.25d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE),
                        StaticTooltip.of(Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId()).withStyle(ChatFormatting.DARK_RED))))
                .effectIcon(plusOverlay(itemIcon(LTXIItems.NOVA)))
                .register(context);
        Upgrade.builder(HANABI_SPEED_BOOST)
                .forEquipment(LTXIItems.HANABI)
                .setMaxRank(2)
                .withEffect(WEAPON_RANGE, ValueOperation.of(LinearDouble.linearIncrement(0.5d), MathOperation.ADD))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.PROJECTILE_SPEED_UPGRADE, ValueComponent.of(LinearDouble.linearIncrement(0.5d), ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .effectIcon(yellowArrowOverlay(sprite("orb_grenade")))
                .register(context);
        Upgrade.builder(WEAPON_VIBRATION_CANCEL)
                .forEquipment(projectileWeapons)
                .withEffect(SUPPRESS_VIBRATIONS, SuppressVibrations.mainHand(gameEvents.getOrThrow(LTXITags.GameEvents.WEAPON_VIBRATIONS)))
                .effectIcon(redXOverlay(itemIcon(Items.SCULK_SENSOR)))
                .register(context);

        ContextlessValue lightweightEnergyCapacity = ConstantDouble.of(100_000);
        ContextlessValue lightweightEnergyUsage = ConstantDouble.of(10_000);
        Upgrade.builder(LIGHTWEIGHT_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .forEquipment(items, LTXITags.Items.LIGHTWEIGHT_WEAPONS)
                .exclusiveWith(holders, RELOAD_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(lightweightEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(lightweightEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(lightweightEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(lightweightEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(LIGHTWEIGHT_ENERGY_ADAPTER.identifier().getPath()))
                .category("weapon/ammo")
                .register(context);

        ContextlessValue specialistEnergyCapacity = ConstantDouble.of(5_000_000);
        ContextlessValue specialistEnergyUsage = ConstantDouble.of(1_000_000);
        Upgrade.builder(SPECIALIST_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .forEquipment(items, LTXITags.Items.SPECIALIST_WEAPONS)
                .exclusiveWith(holders, RELOAD_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(specialistEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(specialistEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(specialistEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(specialistEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(SPECIALIST_ENERGY_ADAPTER.identifier().getPath()))
                .category("weapon/ammo")
                .register(context);

        ContextlessValue explosivesEnergyCapacity = ConstantDouble.of(20_000_000);
        ContextlessValue explosivesEnergyUsage = ConstantDouble.of(10_000_000);
        Upgrade.builder(EXPLOSIVES_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .forEquipment(items, LTXITags.Items.EXPLOSIVE_WEAPONS)
                .exclusiveWith(holders, RELOAD_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(explosivesEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(explosivesEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(explosivesEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(explosivesEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(EXPLOSIVES_ENERGY_ADAPTER.identifier().getPath()))
                .category("weapon/ammo")
                .register(context);

        ContextlessValue heavyEnergyCapacity = ConstantDouble.of(50_000_000);
        ContextlessValue heavyEnergyUsage = ConstantDouble.of(25_000_000);
        Upgrade.builder(HEAVY_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .forEquipment(items, LTXITags.Items.HEAVY_WEAPONS)
                .exclusiveWith(holders, RELOAD_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(heavyEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(heavyEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(heavyEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(heavyEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(HEAVY_ENERGY_ADAPTER.identifier().getPath()))
                .category("weapon/ammo")
                .register(context);

        Upgrade.builder(INFINITE_AMMO)
                .createDefaultTitle(CREATIVE_PINK)
                .forEquipment(projectileWeapons)
                .exclusiveWith(holders, RELOAD_SOURCE_MODIFIERS)
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.infiniteAmmo())
                .effectIcon(sprite("infinite_ammo"))
                .category("weapon/ammo")
                .register(context);

        // Armor upgrades
        Upgrade.builder(PASSIVE_NIGHT_VISION)
                .forEquipment(LTXIItems.WONDERLAND_HEAD)
                .withConditionalEffect(EQUIPMENT_TICK, ApplyMobEffect.applyPassiveEffect(MobEffects.NIGHT_VISION, ConstantDouble.of(400), ConstantDouble.of(0)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().periodicTick(60)))
                .effectIcon(sprite("blue_visor"))
                .category("armor")
                .register(context);
        Upgrade.builder(ARMOR_PASSIVE_SHIELD)
                .createDefaultTitle(BUBBLE_SHIELD_BLUE)
                .forEquipment(wonderlandArmor)
                .setMaxRank(4)
                .itemAttributes(LTXIAttributes.SHIELD_CAPACITY, "shield", LevelBasedValue.perLevel(5), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.ARMOR)
                .effectIcon(sprite("bubble_shield"))
                .category("armor")
                .register(context);
        ContextlessValue defenseAmt = LinearDouble.linearIncrement(0.0625d);
        ContextlessValue defenseEnergy = LinearDouble.oneIncrement(1);
        Upgrade.builder(ARMOR_DEFENSE)
                .forEquipment(wonderlandArmor)
                .setMaxRank(4)
                .itemAttributes(Attributes.KNOCKBACK_RESISTANCE, "steady", LevelBasedValue.perLevel(0.0625f), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.ARMOR)
                .withConditionalEffect(DAMAGE_REDUCTION, DamageReduction.blockDamage(defenseAmt, defenseEnergy), DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                .tooltip(0, key -> TranslatableTooltip.create(key, ValueComponent.of(defenseAmt, ValueFormat.PERCENTAGE, ValueSentiment.POSITIVE), energyActionsTooltip(defenseEnergy)))
                .effectIcon(sprite("defense"))
                .category("armor")
                .register(context);
        Upgrade.builder(BREATHING_UNIT)
                .forEquipment(LTXIItems.WONDERLAND_HEAD)
                .withConditionalEffect(EQUIPMENT_TICK, ApplyMobEffect.applyPassiveEffect(MobEffects.WATER_BREATHING, ConstantDouble.of(400), ConstantDouble.of(0)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().periodicTick(60)))
                .effectIcon(sprite("respirator"))
                .category("armor")
                .register(context);
        Upgrade.builder(PASSIVE_SATURATION)
                .forEquipment(LTXIItems.WONDERLAND_BODY)
                .withConditionalEffect(EQUIPMENT_TICK, ApplyMobEffect.applyPassiveEffect(MobEffects.SATURATION, ConstantDouble.of(200), ConstantDouble.of(0)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().periodicTick(60)))
                .effectIcon(greenArrowOverlay(itemIcon(Items.COOKED_BEEF)))
                .category("armor")
                .register(context);
        Upgrade.builder(CREATIVE_FLIGHT)
                .createDefaultTitle(CREATIVE_PINK)
                .forEquipment(LTXIItems.WONDERLAND_BODY)
                .itemAttributes(NeoForgeMod.CREATIVE_FLIGHT, "fly", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST)
                .effectIcon(sprite("flight"))
                .category("armor")
                .register(context);

        // Enchantments
        Upgrade.builder(EFFICIENCY_ENCHANTMENT)
                .forEquipment(miningTools)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.EFFICIENCY)))
                .effectIcon(yellowArrowOverlay(itemIcon(LTXIItems.EPSILON_DRILL)))
                .category("enchants")
                .register(context);
        Upgrade.builder(SILK_TOUCH_ENCHANTMENT)
                .forEquipment(miningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(bottomRightOverlay(itemIcon(LTXIItems.EPSILON_DRILL), "silk_overlay", 8))
                .category("enchants")
                .register(context);
        Upgrade.builder(FORTUNE_ENCHANTMENT)
                .forEquipment(miningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.FORTUNE)))
                .effectIcon(luckOverlay(LTXIItems.EPSILON_DRILL))
                .category("enchants")
                .register(context);
        Upgrade.builder(LOOTING_ENCHANTMENT)
                .forEquipment(allWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(luckOverlay(LTXIItems.EPSILON_SWORD))
                .category("enchants")
                .register(context);
        Upgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .forEquipment(allWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .category("enchants")
                .register(context);
        Upgrade.builder(RAZOR_ENCHANTMENT)
                .forEquipment(allWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor"))
                .category("enchants")
                .register(context);

        // Hanabi grenade cores
        Upgrade.builder(FLAME_GRENADE_CORE)
                .forEquipment(LTXIItems.HANABI)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .effectIcon(sprite("flame_grenade_core"))
                .category("grenade_cores")
                .register(context);
        Upgrade.builder(CRYO_GRENADE_CORE)
                .forEquipment(LTXIItems.HANABI)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .effectIcon(sprite("cryo_grenade_core"))
                .category("grenade_cores")
                .register(context);
        Upgrade.builder(ELECTRIC_GRENADE_CORE)
                .forEquipment(LTXIItems.HANABI)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .effectIcon(sprite("electric_grenade_core"))
                .category("grenade_cores")
                .register(context);
        Upgrade.builder(ACID_GRENADE_CORE)
                .forEquipment(LTXIItems.HANABI)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .effectIcon(sprite("acid_grenade_core"))
                .category("grenade_cores")
                .register(context);
        Upgrade.builder(NEURO_GRENADE_CORE)
                .forEquipment(LTXIItems.HANABI)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("neuro_grenade_core"))
                .category("grenade_cores")
                .register(context);
        Upgrade.builder(OMNI_GRENADE_CORE)
                .forEquipment(LTXIItems.HANABI)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("omni_grenade_core"))
                .category("grenade_cores")
                .register(context);
    }

    private static void machineUpgrades(BootstrapContext<Upgrade> context, HolderGetter<Upgrade> holders)
    {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<BlockEntityType<?>> blockEntities = context.lookup(Registries.BLOCK_ENTITY_TYPE);

        ContextlessValue ecaScaling = ExponentialDouble.of(2, LinearDouble.oneIncrement(3));
        Upgrade.builder(ECA_CAPACITY_UPGRADE)
                .createDefaultTitle(REM_BLUE)
                .forMachines(LTXIBlockEntities.ENERGY_CELL_ARRAY)
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, ValueOperation.of(ecaScaling, MathOperation.MULTIPLY))
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueOperation.of(ecaScaling, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(ecaScaling, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(ecaScaling, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .setMaxRank(5)
                .effectIcon(sprite("extra_energy"))
                .register(context);

        ContextlessValue smsEnergyStorage = LinearDouble.linearIncrement(0.5d);
        Upgrade.builder(STANDARD_MACHINE_SYSTEMS)
                .forMachines(blockEntities, LTXITags.BlockEntities.STANDARD_UPGRADABLE_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(smsEnergyStorage, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, ValueOperation.of(smsEnergyStorage, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(TICKS_PER_OPERATION, ValueOperation.of(ExponentialDouble.linearExponent(0.725d), MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueOperation.of(ExponentialDouble.linearExponent(1.5d), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, MinimumMachineSpeed.atLeast(5))
                .tooltip(energyCapacityTooltip(smsEnergyStorage, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(smsEnergyStorage, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(ExponentialDouble.linearExponent(1.5d), ValueFormat.MULTIPLICATIVE, ValueSentiment.NEUTRAL))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.MACHINE_SPEED_UPGRADE, ValueComponent.of(ExponentialDouble.negativeLinearExponent(0.725d), ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE)))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.ENERGY_PER_RECIPE_UPGRADE, ValueComponent.of(ExponentialDouble.linearExponent(1.0875d), ValueFormat.MULTIPLICATIVE, ValueSentiment.NEGATIVE)))
                .setMaxRank(6)
                .effectIcon(greenArrowOverlay(sprite("titanium_gear")))
                .category("gpm")
                .register(context);

        ContextlessValue umsEnergyStorage = ConstantDouble.of(16);
        ContextlessValue umsEnergyUsage = ConstantDouble.of(512);
        Upgrade.builder(ULTIMATE_MACHINE_SYSTEMS)
                .createDefaultTitle(LTXIConstants.LIME_GREEN)
                .forMachines(blockEntities, LTXITags.BlockEntities.ULTIMATE_UPGRADABLE_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(umsEnergyStorage, MathOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, ValueOperation.of(umsEnergyStorage, MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueOperation.of(umsEnergyUsage, MathOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, ValueOperation.of(ConstantDouble.of(0), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, MinimumMachineSpeed.atLeast(0))
                .tooltip(energyCapacityTooltip(umsEnergyStorage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(umsEnergyStorage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(umsEnergyUsage, ValueFormat.MULTIPLICATIVE, ValueSentiment.NEGATIVE))
                .staticTooltip(LTXILangKeys.INSTANT_PROCESSING_UPGRADE.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()))
                .effectIcon(sprite("ultimate_gear"))
                .category("gpm")
                .register(context);

        ContextlessValue gpmParallelOps = LinearDouble.linearIncrement(2);
        Upgrade.builder(GPM_PARALLEL)
                .createDefaultTitle(o -> o.withStyle(ChatFormatting.LIGHT_PURPLE))
                .forMachines(blockEntities, LTXITags.BlockEntities.GENERAL_PROCESSING_MACHINES)
                .exclusiveWith(holders, PARALLEL_OPS_UPGRADES)
                .withEffect(PARALLEL_OPERATIONS, ValueOperation.of(gpmParallelOps, MathOperation.REPLACE))
                .tooltip(parallelOpsTooltip(gpmParallelOps, ValueFormat.FLAT_NUMBER, ValueSentiment.POSITIVE))
                .setMaxRank(4)
                .effectIcon(plusOverlay(sprite("titanium_gear")))
                .category("gpm")
                .register(context);

        ContextlessValue fabCapacity = LinearDouble.linearIncrement(2);
        ContextlessValue fabTransfer = LinearDouble.linearIncrement(3);
        ContextlessValue fabUsage = ExponentialDouble.of(2, LinearDouble.oneIncrement(2));
        Upgrade.builder(FABRICATOR_UPGRADE)
                .forMachines(LTXIBlockEntities.FABRICATOR, LTXIBlockEntities.AUTO_FABRICATOR)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(fabCapacity, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, ValueOperation.of(fabTransfer, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(fabUsage, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(fabCapacity, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(fabTransfer, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(fabUsage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .register(context);

        ContextlessValue geoSynthParallel = ExponentialDouble.of(2, LinearDouble.oneIncrement(4));
        Upgrade.builder(GEO_SYNTHESIZER_PARALLEL)
                .createDefaultTitle(o -> o.withStyle(ChatFormatting.AQUA))
                .forMachines(LTXIBlockEntities.GEO_SYNTHESIZER)
                .exclusiveWith(holders, PARALLEL_OPS_UPGRADES)
                .withEffect(PARALLEL_OPERATIONS, ValueOperation.of(geoSynthParallel, MathOperation.REPLACE))
                .tooltip(parallelOpsTooltip(geoSynthParallel, ValueFormat.FLAT_NUMBER, ValueSentiment.POSITIVE))
                .setMaxRank(3)
                .effectIcon(plusOverlay(itemIcon(LTXIBlocks.GEO_SYNTHESIZER)))
                .category("machine_unique")
                .register(context);

        Upgrade.builder(TURRET_LOOTING)
                .forMachines(blockEntities, LTXITags.BlockEntities.TURRETS)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LOOTING)))
                .setMaxRank(3)
                .effectIcon(luckOverlay(LTXIItems.EPSILON_SWORD))
                .category("turret")
                .register(context);

        Upgrade.builder(TURRET_RAZOR)
                .forMachines(blockEntities, LTXITags.BlockEntities.TURRETS)
                .setMaxRank(2)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(LTXIEnchantments.RAZOR)))
                .effectIcon(sprite("razor"))
                .category("turret")
                .register(context);
    }
}