package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.advancement.ComparableBounds;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.lib.math.CompareOperation;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.limacore.world.loot.condition.EntityHostilityLootCondition;
import liedge.limacore.world.loot.condition.NumberComparisonLootCondition;
import liedge.limacore.world.loot.number.EntityAttributeValueProvider;
import liedge.limacore.world.loot.number.MathOpsNumberProvider;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.*;
import liedge.ltxindustries.lib.upgrades.effect.entity.ApplyMobEffect;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.tooltip.*;
import liedge.ltxindustries.lib.upgrades.value.*;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIAttributes;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

import static liedge.ltxindustries.LTXIConstants.*;
import static liedge.ltxindustries.LTXIIdentifiers.*;
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
    public static final ResourceKey<EquipmentUpgrade> LTX_SHOVEL_DEFAULT = defaultKey(ID_LTX_SHOVEL);
    public static final ResourceKey<EquipmentUpgrade> LTX_WRENCH_DEFAULT = defaultKey(ID_LTX_WRENCH);
    public static final ResourceKey<EquipmentUpgrade> LTX_MELEE_DEFAULT = defaultKey("ltx_melee");
    public static final ResourceKey<EquipmentUpgrade> GLOWSTICK_LAUNCHER_DEFAULT = defaultKey(ID_GLOWSTICK_LAUNCHER);
    public static final ResourceKey<EquipmentUpgrade> SUBMACHINE_GUN_DEFAULT = defaultKey(ID_SUBMACHINE_GUN);
    public static final ResourceKey<EquipmentUpgrade> SHOTGUN_DEFAULT = defaultKey(ID_SHOTGUN);
    public static final ResourceKey<EquipmentUpgrade> LFR_DEFAULT = defaultKey(ID_LINEAR_FUSION_RIFLE);
    public static final ResourceKey<EquipmentUpgrade> HEAVY_PISTOL_DEFAULT = defaultKey(ID_HEAVY_PISTOL);
    public static final ResourceKey<EquipmentUpgrade> HEAD_DEFAULT = defaultKey(ID_WONDERLAND_HEAD);
    public static final ResourceKey<EquipmentUpgrade> BODY_DEFAULT = defaultKey(ID_WONDERLAND_BODY);
    public static final ResourceKey<EquipmentUpgrade> LEGS_DEFAULT = defaultKey(ID_WONDERLAND_LEGS);
    public static final ResourceKey<EquipmentUpgrade> FEET_DEFAULT = defaultKey(ID_WONDERLAND_FEET);

    // Tool upgrades
    public static final ResourceKey<EquipmentUpgrade> TOOL_ENERGY_UPGRADE = key("tool_energy_upgrade");
    public static final ResourceKey<EquipmentUpgrade> EPSILON_FISHING_LURE = key("epsilon_fishing_lure");
    public static final ResourceKey<EquipmentUpgrade> TOOL_NETHERITE_LEVEL = key("tool_netherite_level");
    public static final ResourceKey<EquipmentUpgrade> EPSILON_OMNI_DRILL = key("epsilon_omni_drill");
    public static final ResourceKey<EquipmentUpgrade> TREE_VEIN_MINE = key("tree_vein_mine");
    public static final ResourceKey<EquipmentUpgrade> TOOL_VIBRATION_CANCEL = key("tool_vibration_cancel");
    public static final ResourceKey<EquipmentUpgrade> TOOL_DIRECT_DROPS = key("tool_direct_drops");

    // Weapon upgrades
    public static final ResourceKey<EquipmentUpgrade> WEAPON_VIBRATION_CANCEL = key("weapon_vibration_cancel");
    public static final ResourceKey<EquipmentUpgrade> WEAPON_DIRECT_DROPS = key("weapon_direct_drops");
    public static final ResourceKey<EquipmentUpgrade> LIGHTWEIGHT_ENERGY_ADAPTER = key("lightweight_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> SPECIALIST_ENERGY_ADAPTER = key("specialist_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> EXPLOSIVES_ENERGY_ADAPTER = key("explosives_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> HEAVY_ENERGY_ADAPTER = key("heavy_energy_adapter");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_INFINITE_AMMO = key("universal_infinite_ammo");
    public static final ResourceKey<EquipmentUpgrade> UNIVERSAL_STEALTH_DAMAGE = key("universal_stealth_damage");
    public static final ResourceKey<EquipmentUpgrade> NEUTRAL_ENEMY_TARGET_FILTER = key("target_filter/neutral_enemy");
    public static final ResourceKey<EquipmentUpgrade> HOSTILE_TARGET_FILTER = key("target_filter/hostile");
    public static final ResourceKey<EquipmentUpgrade> HEAVY_PISTOL_GOD_ROUNDS = key("heavy_pistol_god_rounds");
    public static final ResourceKey<EquipmentUpgrade> GRENADE_LAUNCHER_PROJECTILE_SPEED = key("grenade_launcher_projectile_speed");

    // Armor upgrades
    public static final ResourceKey<EquipmentUpgrade> HEAD_NIGHT_VISION = key("night_vision");
    public static final ResourceKey<EquipmentUpgrade> ARMOR_PASSIVE_SHIELD = key("armor_passive_shield");

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

    private static ResourceKey<EquipmentUpgrade> defaultKey(String name)
    {
        return key("default/" + name);
    }

    public static void bootstrap(BootstrapContext<EquipmentUpgrade> context)
    {
        // Holder getters
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<GameEvent> gameEvents = context.lookup(Registries.GAME_EVENT);
        HolderGetter<EquipmentUpgrade> holders = context.lookup(LTXIRegistries.Keys.EQUIPMENT_UPGRADES);
        HolderGetter<MobEffect> mobEffects = context.lookup(Registries.MOB_EFFECT);

        // AnyHolderSets
        HolderSet<Block> anyBlockHolderSet = new AnyHolderSet<>(BuiltInRegistries.BLOCK.asLookup());
        HolderSet<Item> anyItemHolderSet = new AnyHolderSet<>(BuiltInRegistries.ITEM.asLookup());

        // Common holder sets
        HolderSet<Item> allTools = items.getOrThrow(LTXITags.Items.ALL_TOOLS);
        HolderSet<Item> miningTools = items.getOrThrow(LTXITags.Items.MINING_TOOLS);
        HolderSet<Item> modularMiningTools = items.getOrThrow(LTXITags.Items.MODULAR_MINING_TOOLS);
        HolderSet<Item> meleeWeapons = items.getOrThrow(LTXITags.Items.MELEE_WEAPONS);
        HolderSet<Item> projectileWeapons = items.getOrThrow(LTXITags.Items.ENERGY_PROJECTILE_WEAPONS);
        HolderSet<Item> allWeapons = items.getOrThrow(LTXITags.Items.ALL_WEAPONS);
        HolderSet<Item> wonderlandArmor = items.getOrThrow(LTXITags.Items.WONDERLAND_ARMOR);

        // Built in upgrades
        final Component defaultToolTitle = LTXILangKeys.TOOL_DEFAULT_UPGRADE_TITLE.translate().withStyle(LIME_GREEN.chatStyle());
        EquipmentUpgrade.builder(LTX_SHOVEL_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LTXIItems.LTX_SHOVEL)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(defaultModuleIcon(LTXIItems.LTX_SHOVEL))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(LTX_WRENCH_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(LTXIItems.LTX_WRENCH)
                .withEffect(CAPTURE_BLOCK_DROPS, CaptureBlockDrops.captureItems(items.getOrThrow(LTXITags.Items.WRENCH_BREAKABLE)))
                .effectIcon(defaultModuleIcon(LTXIItems.LTX_WRENCH))
                .category("default/tool")
                .register(context);
        EquipmentUpgrade.builder(LTX_MELEE_DEFAULT)
                .setTitle(defaultToolTitle)
                .supports(meleeWeapons)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(RAZOR), 1, 5))
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(Enchantments.LOOTING), 1, 5))
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(ConstantDouble.of(0.2d), MathOperation.ADD_PERCENT_OF_TOTAL), NumberComparisonLootCondition.comparingValues(
                        EntityAttributeValueProvider.totalValue(LootContext.EntityTarget.THIS, Attributes.ARMOR),
                        EntityAttributeValueProvider.baseValue(LootContext.EntityTarget.THIS, Attributes.ARMOR),
                        CompareOperation.LESS_THAN_OR_EQUALS))
                .tooltip(0, key -> TranslatableTooltip.create(key, ValueComponent.of(ConstantDouble.of(0.2d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE)))
                .effectIcon(defaultOverlay(sprite("razor")))
                .category("default/tool")
                .register(context);

        ContextlessValue gslEnergyCap = ConstantDouble.of(50_000);
        ContextlessValue gslEnergyUse = ConstantDouble.of(5000);
        EquipmentUpgrade.builder(GLOWSTICK_LAUNCHER_DEFAULT)
                .supports(LTXIItems.GLOWSTICK_LAUNCHER)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(gslEnergyCap, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(gslEnergyUse, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(gslEnergyCap, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(gslEnergyUse, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(defaultModuleIcon(LTXIItems.GLOWSTICK_LAUNCHER))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(SUBMACHINE_GUN_DEFAULT)
                .supports(LTXIItems.SUBMACHINE_GUN)
                .withEffect(SUPPRESS_VIBRATIONS, SuppressVibrations.mainHand(gameEvents.getOrThrow(LTXITags.GameEvents.WEAPON_VIBRATIONS)))
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_ANGER)
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_KNOCKBACK)
                .staticTooltip(0)
                .effectIcon(defaultModuleIcon(LTXIItems.SUBMACHINE_GUN))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(SHOTGUN_DEFAULT)
                .supports(LTXIItems.SHOTGUN)
                .itemAttributes(Attributes.MOVEMENT_SPEED, "speed", LevelBasedValue.constant(0.25f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.MAINHAND)
                .itemAttributes(Attributes.STEP_HEIGHT, "step_height", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND)
                .itemAttributes(Attributes.SAFE_FALL_DISTANCE, "safe_fall_dist", LevelBasedValue.constant(3), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND)
                .damageAttributes(Attributes.KNOCKBACK_RESISTANCE, "knockback_resist", LevelBasedValue.constant(-0.5f), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .damageAttributes(LimaCoreAttributes.KNOCKBACK_MULTIPLIER, "knockback", LevelBasedValue.constant(0.5f), AttributeModifier.Operation.ADD_VALUE)
                .effectIcon(defaultModuleIcon(LTXIItems.SHOTGUN))
                .category("default/weapon")
                .register(context);
        ContextlessValue lfrBonusDmg = ConstantDouble.of(25);
        EquipmentUpgrade.builder(LFR_DEFAULT)
                .supports(LTXIItems.LINEAR_FUSION_RIFLE)
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(lfrBonusDmg, MathOperation.ADD),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                .distance(DistancePredicate.absolute(MinMaxBounds.Doubles.atLeast(40.0d)))))
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(lfrBonusDmg, MathOperation.ADD),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity()
                                .moving(MovementPredicate.speed(MinMaxBounds.Doubles.atMost(1e-3d)))
                                .flags(EntityFlagsPredicate.Builder.flags().setCrouching(true))))
                .tooltip(0, key -> TranslatableTooltip.create(key, ValueComponent.of(lfrBonusDmg, ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .tooltip(1, key -> TranslatableTooltip.create(key, ValueComponent.of(lfrBonusDmg, ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .effectIcon(defaultModuleIcon(LTXIItems.LINEAR_FUSION_RIFLE))
                .category("default/weapon")
                .register(context);
        EquipmentUpgrade.builder(HEAVY_PISTOL_DEFAULT)
                .supports(LTXIItems.HEAVY_PISTOL)
                .damageAttributes(Attributes.KNOCKBACK_RESISTANCE, "knockback_resist", LevelBasedValue.constant(-1), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .damageAttributes(LimaCoreAttributes.KNOCKBACK_MULTIPLIER, "knockback", LevelBasedValue.constant(2f), AttributeModifier.Operation.ADD_VALUE)
                .effectIcon(defaultModuleIcon(LTXIItems.HEAVY_PISTOL))
                .category("default/weapon")
                .register(context);

        ContextlessValue headMECost = ConstantDouble.of(1);
        EquipmentUpgrade.builder(HEAD_DEFAULT)
                .supports(LTXIItems.WONDERLAND_HEAD)
                .withEffect(MOB_EFFECT_IMMUNITY, MobEffectImmunity.immuneTo(mobEffects, LTXITags.MobEffects.VISION_DEBUFF, headMECost))
                .tooltip(0, key -> TranslatableTooltip.create(key, energyActionsTooltip(headMECost)))
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_HEAD))
                .category("default/armor/1")
                .register(context);
        EquipmentUpgrade.builder(BODY_DEFAULT)
                .supports(LTXIItems.WONDERLAND_BODY)
                .itemAttributes(Attributes.BLOCK_INTERACTION_RANGE, "block_reach", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST)
                .itemAttributes(Attributes.ENTITY_INTERACTION_RANGE, "entity_reach", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST)
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_BODY))
                .category("default/armor/2")
                .register(context);
        ContextlessValue legsMECost = ConstantDouble.of(1);
        EquipmentUpgrade.builder(LEGS_DEFAULT)
                .supports(LTXIItems.WONDERLAND_LEGS)
                .itemAttributes(Attributes.MOVEMENT_SPEED, "speed_boost", LevelBasedValue.constant(0.1f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, EquipmentSlotGroup.LEGS)
                .itemAttributes(Attributes.WATER_MOVEMENT_EFFICIENCY, "water_move", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS)
                .withEffect(MOB_EFFECT_IMMUNITY, MobEffectImmunity.immuneTo(mobEffects, LTXITags.MobEffects.MOVEMENT_DEBUFF, legsMECost))
                .tooltip(0, key -> TranslatableTooltip.create(key, energyActionsTooltip(legsMECost)))
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_LEGS))
                .category("default/armor/3")
                .register(context);
        EquipmentUpgrade.builder(FEET_DEFAULT)
                .supports(LTXIItems.WONDERLAND_FEET)
                .itemAttributes(Attributes.STEP_HEIGHT, "step_boost", LevelBasedValue.constant(1), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET)
                .itemAttributes(Attributes.SAFE_FALL_DISTANCE, "safe_fall", LevelBasedValue.constant(5), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET)
                .withConditionUnit(DAMAGE_IMMUNITY, DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))))
                .staticTooltip(0)
                .effectIcon(defaultModuleIcon(LTXIItems.WONDERLAND_FEET))
                .category("default/armor/4")
                .register(context);

        // Tool upgrades
        ContextlessValue toolEnergy = ExponentialDouble.of(2, LinearDouble.oneIncrement(2));
        EquipmentUpgrade.builder(TOOL_ENERGY_UPGRADE)
                .createDefaultTitle(REM_BLUE)
                .supports(allTools)
                .setMaxRank(4)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(toolEnergy, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(toolEnergy, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .effectIcon(sprite("extra_energy"))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(EPSILON_FISHING_LURE)
                .createDefaultTitle(LIME_GREEN)
                .supports(LTXIItems.LTX_FISHING_ROD)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LURE)))
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LUCK_OF_THE_SEA)))
                .effectIcon(plusOverlay(itemIcon(LTXIItems.LTX_FISHING_ROD)))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(TOOL_NETHERITE_LEVEL)
                .supports(modularMiningTools)
                .exclusiveWith(holders, MINING_LEVEL_UPGRADES)
                .withEffect(MODULAR_TOOL, ModularTool.limitedTo(blocks.getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)))
                .effectIcon(greenArrowOverlay(itemIcon(Items.NETHERITE_PICKAXE)))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(EPSILON_OMNI_DRILL)
                .createDefaultTitle(LIME_GREEN)
                .supports(modularMiningTools)
                .withEffect(MODULAR_TOOL, ModularTool.effectiveOn(anyBlockHolderSet))
                .effectIcon(sprite("purple_drill_head"))
                .category("tools/drill")
                .register(context);
        EquipmentUpgrade.builder(TREE_VEIN_MINE)
                .supports(LTXIItems.LTX_AXE)
                .withEffect(VEIN_MINE, VeinMine.create(BlockPredicate.Builder.block().of(BlockTags.LOGS), BlockPredicate.Builder.block().of(BlockTags.LEAVES), VeinMine.MAX_BLOCK_LIMIT, false))
                .staticTooltip(0)
                .effectIcon(veinMineOverlay(itemIcon(Items.OAK_LOG)))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(TOOL_VIBRATION_CANCEL)
                .supports(items.getOrThrow(LTXITags.Items.ALL_TOOLS))
                .withEffect(SUPPRESS_VIBRATIONS, SuppressVibrations.forSlot(EquipmentSlotGroup.HAND, gameEvents.getOrThrow(LTXITags.GameEvents.HANDHELD_EQUIPMENT)))
                .effectIcon(redXOverlay(itemIcon(Items.SCULK_SENSOR)))
                .category("tools")
                .register(context);
        EquipmentUpgrade.builder(TOOL_DIRECT_DROPS)
                .supports(miningTools)
                .withEffect(CAPTURE_BLOCK_DROPS, CaptureBlockDrops.captureItems(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .category("tools")
                .register(context);

        // Weapon-specific upgrades
        EquipmentUpgrade.builder(HEAVY_PISTOL_GOD_ROUNDS)
                .supports(LTXIItems.HEAVY_PISTOL)
                .withEffect(EXTRA_DAMAGE_TAGS, LTXITags.DamageTypes.BYPASS_SURVIVAL_DEFENSES)
                .withConditionalEffect(EQUIPMENT_DAMAGE, ValueOperation.of(MathOpsNumberProvider.of(EntityAttributeValueProvider.totalValue(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH), ConstantValue.exactly(0.25f), MathOperation.MULTIPLY), MathOperation.ADD))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.ATTRIBUTE_SCALED_DAMAGE_UPGRADE,
                        ValueComponent.of(ConstantDouble.of(0.25d), ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE),
                        StaticTooltip.of(Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId()).withStyle(ChatFormatting.DARK_RED))))
                .effectIcon(plusOverlay(itemIcon(LTXIItems.HEAVY_PISTOL)))
                .register(context);
        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LTXIItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withEffect(WEAPON_RANGE, ValueOperation.of(LinearDouble.linearIncrement(0.5d), MathOperation.ADD))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.PROJECTILE_SPEED_UPGRADE, ValueComponent.of(LinearDouble.linearIncrement(0.5d), ValueFormat.SIGNED_FLAT_NUMBER, ValueSentiment.POSITIVE)))
                .effectIcon(yellowArrowOverlay(sprite("orb_grenade")))
                .register(context);

        // Universal upgrades
        EquipmentUpgrade.builder(WEAPON_VIBRATION_CANCEL)
                .supports(projectileWeapons)
                .withEffect(SUPPRESS_VIBRATIONS, SuppressVibrations.mainHand(gameEvents.getOrThrow(LTXITags.GameEvents.WEAPON_VIBRATIONS)))
                .effectIcon(redXOverlay(itemIcon(Items.SCULK_SENSOR)))
                .register(context);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supports(allWeapons)
                .withEffect(EXTRA_DAMAGE_TAGS, DamageTypeTags.NO_ANGER)
                .staticTooltip(0)
                .effectIcon(sprite("stealth_damage"))
                .register(context);
        EquipmentUpgrade.builder(WEAPON_DIRECT_DROPS)
                .supports(allWeapons)
                .withSpecialEffect(CAPTURE_MOB_DROPS, CaptureMobDrops.INSTANCE)
                .effectIcon(sprite("magnet"))
                .register(context);

        ContextlessValue lightweightEnergyCapacity = ConstantDouble.of(100_000);
        ContextlessValue lightweightEnergyUsage = ConstantDouble.of(10_000);
        EquipmentUpgrade.builder(LIGHTWEIGHT_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.LIGHTWEIGHT_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(lightweightEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(lightweightEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(lightweightEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(lightweightEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(LIGHTWEIGHT_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        ContextlessValue specialistEnergyCapacity = ConstantDouble.of(5_000_000);
        ContextlessValue specialistEnergyUsage = ConstantDouble.of(1_000_000);
        EquipmentUpgrade.builder(SPECIALIST_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.SPECIALIST_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(specialistEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(specialistEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(specialistEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(specialistEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(SPECIALIST_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        ContextlessValue explosivesEnergyCapacity = ConstantDouble.of(20_000_000);
        ContextlessValue explosivesEnergyUsage = ConstantDouble.of(10_000_000);
        EquipmentUpgrade.builder(EXPLOSIVES_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.EXPLOSIVE_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(explosivesEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(explosivesEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(explosivesEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(explosivesEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(EXPLOSIVES_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        ContextlessValue heavyEnergyCapacity = ConstantDouble.of(50_000_000);
        ContextlessValue heavyEnergyUsage = ConstantDouble.of(25_000_000);
        EquipmentUpgrade.builder(HEAVY_ENERGY_ADAPTER)
                .createDefaultTitle(REM_BLUE)
                .supports(items, LTXITags.Items.HEAVY_WEAPONS)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(heavyEnergyCapacity, MathOperation.REPLACE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(heavyEnergyUsage, MathOperation.REPLACE))
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.commonEnergy())
                .tooltip(energyCapacityTooltip(heavyEnergyCapacity, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .tooltip(energyUsageTooltip(heavyEnergyUsage, ValueFormat.FLAT_NUMBER, ValueSentiment.NEUTRAL))
                .effectIcon(sprite(HEAVY_ENERGY_ADAPTER.location().getPath()))
                .category("weapon/ammo")
                .register(context);

        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .createDefaultTitle(CREATIVE_PINK)
                .supports(projectileWeapons)
                .exclusiveWith(holders, AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(RELOAD_SOURCE, WeaponReloadSource.infiniteAmmo())
                .effectIcon(sprite("infinite_ammo"))
                .category("weapon/ammo")
                .register(context);
        EquipmentUpgrade.builder(NEUTRAL_ENEMY_TARGET_FILTER)
                .createDefaultTitle(HOSTILE_ORANGE)
                .supports(allWeapons)
                .targetRestriction(EntityHostilityLootCondition.create(ComparableBounds.atLeast(MobHostility.NEUTRAL_ENEMY)))
                .effectIcon(sprite("neutral_enemy_targets"))
                .category("target_filters")
                .register(context);
        EquipmentUpgrade.builder(HOSTILE_TARGET_FILTER)
                .createDefaultTitle(HOSTILE_ORANGE)
                .supports(allWeapons)
                .targetRestriction(EntityHostilityLootCondition.create(ComparableBounds.atLeast(MobHostility.HOSTILE)))
                .effectIcon(sprite("hostile_targets"))
                .category("target_filters")
                .register(context);

        // Armor upgrades
        EquipmentUpgrade.builder(HEAD_NIGHT_VISION)
                .supports(LTXIItems.WONDERLAND_HEAD)
                .withConditionalEffect(EQUIPMENT_TICK, ApplyMobEffect.applyEffect(MobEffects.NIGHT_VISION, LevelBasedValue.constant(400), LevelBasedValue.constant(0), true, false),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().periodicTick(60)))
                .effectIcon(sprite("blue_visor"))
                .category("armor")
                .register(context);
        EquipmentUpgrade.builder(ARMOR_PASSIVE_SHIELD)
                .createDefaultTitle(BUBBLE_SHIELD_BLUE)
                .supports(wonderlandArmor)
                .setMaxRank(4)
                .itemAttributes(LTXIAttributes.SHIELD_CAPACITY, "shield", LevelBasedValue.perLevel(5), AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.ARMOR)
                .effectIcon(sprite("bubble_shield"))
                .category("armor")
                .register(context);

        // Enchantments
        EquipmentUpgrade.builder(EFFICIENCY_ENCHANTMENT)
                .supports(miningTools)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.EFFICIENCY)))
                .effectIcon(yellowArrowOverlay(itemIcon(LTXIItems.LTX_DRILL)))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(SILK_TOUCH_ENCHANTMENT)
                .supports(miningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.fixed(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))
                .effectIcon(bottomRightOverlay(itemIcon(LTXIItems.LTX_DRILL), "silk_overlay", 8))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(FORTUNE_ENCHANTMENT)
                .supports(miningTools)
                .exclusiveWith(holders, MINING_DROPS_MODIFIERS)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.FORTUNE)))
                .effectIcon(luckOverlay(LTXIItems.LTX_DRILL))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supports(allWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(luckOverlay(LTXIItems.LTX_SWORD))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supports(allWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .category("enchants")
                .register(context);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supports(allWeapons)
                .setMaxRank(5)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(RAZOR)))
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