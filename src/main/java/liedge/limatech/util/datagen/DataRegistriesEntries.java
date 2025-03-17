package liedge.limatech.util.datagen;

import liedge.limacore.LimaCoreTags;
import liedge.limacore.data.generation.LimaDatagenBootstrapBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.LimaCoreDataComponents;
import liedge.limacore.world.loot.number.MathOpsNumberProvider;
import liedge.limacore.world.loot.number.TargetedAttributeValueProvider;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.LimaTechDeathMessageTypes;
import liedge.limatech.lib.upgrades.UpgradeIcon;
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
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.*;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.registry.LimaTechDamageTypes.*;
import static liedge.limatech.registry.LimaTechEnchantments.AMMO_SCAVENGER;
import static liedge.limatech.registry.LimaTechEnchantments.RAZOR;
import static liedge.limatech.registry.LimaTechEquipmentUpgrades.*;
import static liedge.limatech.registry.LimaTechMachineUpgrades.*;
import static liedge.limatech.registry.LimaTechUpgradeEffectComponents.*;
import static liedge.limatech.registry.LimaTechWorldGen.*;

class DataRegistriesEntries extends LimaDatagenBootstrapBuilder
{
    @Override
    protected void buildDataRegistryEntries(RegistrySetBuilder builder)
    {
        builder.add(Registries.DAMAGE_TYPE, this::createDamageTypes);
        builder.add(Registries.CONFIGURED_FEATURE, this::createConfiguredFeatures);
        builder.add(Registries.PLACED_FEATURE, this::createPlacedFeatures);
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, this::createBiomeModifiers);
        builder.add(Registries.ENCHANTMENT, this::createEnchantments);
        builder.add(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, this::createEquipmentEffects);
        builder.add(LimaTechRegistries.Keys.MACHINE_UPGRADES, this::createMachineEffects);
    }

    private void createDamageTypes(BootstrapContext<DamageType> ctx)
    {
        DeathMessageType weaponMsgType = LimaTechDeathMessageTypes.WEAPON_DEATH_MESSAGE_TYPE.getValue();
        DeathMessageType noItemCausedOnlyMsg = LimaTechDeathMessageTypes.NO_ITEM_CAUSING_ENTITY_ONLY.getValue();

        registerDamageType(ctx, LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, MAGNUM_LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, EXPLOSIVE_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, FLAME_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.BURNING, weaponMsgType);
        registerDamageType(ctx, CRYO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.FREEZING, weaponMsgType);
        registerDamageType(ctx, ELECTRIC_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, ACID_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, NEURO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, ROCKET_LAUNCHER, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);

        registerDamageType(ctx, STICKY_FLAME, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
        registerDamageType(ctx, TURRET_ROCKET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
    }

    private void createConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> ctx)
    {
        ConfiguredFeature<?, ?> titaniumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(10, tagMatchOreTarget(BlockTags.STONE_ORE_REPLACEABLES, LimaTechBlocks.TITANIUM_ORE), tagMatchOreTarget(BlockTags.DEEPSLATE_ORE_REPLACEABLES, LimaTechBlocks.DEEPSLATE_TITANIUM_ORE)));
        ConfiguredFeature<?, ?> niobiumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(3, singleBlockOreTarget(Blocks.END_STONE, LimaTechBlocks.NIOBIUM_ORE)));

        ctx.register(TITANIUM_ORE_CONFIG, titaniumOre);
        ctx.register(NIOBIUM_ORE_CONFIG, niobiumOre);
    }

    private void createPlacedFeatures(BootstrapContext<PlacedFeature> ctx)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configs = ctx.lookup(Registries.CONFIGURED_FEATURE);

        PlacedFeature titaniumOre = orePlacement(configs.getOrThrow(TITANIUM_ORE_CONFIG), 10,
                HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(60)));

        PlacedFeature niobiumOre = orePlacement(configs.getOrThrow(NIOBIUM_ORE_CONFIG), 2, HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP));


        ctx.register(TITANIUM_ORE_PLACEMENT, titaniumOre);
        ctx.register(NIOBIUM_ORE_PLACEMENT, niobiumOre);
    }

    private void createBiomeModifiers(BootstrapContext<BiomeModifier> ctx)
    {
        HolderGetter<PlacedFeature> placements = ctx.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);

        BiomeModifier titaniumOre = new AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placements.getOrThrow(TITANIUM_ORE_PLACEMENT)),
                GenerationStep.Decoration.UNDERGROUND_ORES);

        BiomeModifier niobiumOre = new AddFeaturesBiomeModifier(
                HolderSet.direct(biomes::getOrThrow, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS),
                HolderSet.direct(placements.getOrThrow(NIOBIUM_ORE_PLACEMENT)),
                GenerationStep.Decoration.UNDERGROUND_ORES);

        ctx.register(TITANIUM_ORE_BIOMES, titaniumOre);
        ctx.register(NIOBIUM_ORE_BIOMES, niobiumOre);
    }

    private void createEnchantments(BootstrapContext<Enchantment> ctx)
    {
        HolderGetter<Item> items = ctx.lookup(Registries.ITEM);

        Enchantment.Builder razor = Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(10, 9),
                Enchantment.dynamicCost(60, 9),
                3,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(LimaCoreDataComponents.EXTRA_LOOT_TABLE_EFFECT.get(), LimaTechLootTables.RAZOR_LOOT_TABLE,
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(EntityType.PLAYER)));

        Enchantment.Builder ammoScavenger = Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(10, 9),
                Enchantment.dynamicCost(60, 9),
                2,
                EquipmentSlotGroup.MAINHAND));

        registerEnchantment(ctx, RAZOR, razor);
        registerEnchantment(ctx, AMMO_SCAVENGER, ammoScavenger);
    }

    private void createEquipmentEffects(BootstrapContext<EquipmentUpgrade> ctx)
    {
        HolderGetter<Item> items = ctx.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = ctx.lookup(Registries.ENCHANTMENT);
        HolderGetter<EquipmentUpgrade> holders = ctx.lookup(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);

        // Weapon-specific upgrades
        EquipmentUpgrade.builder(LIGHTFRAG_BASE_ARMOR_BYPASS)
                .supports(HolderSet.direct(LimaTechItems.SUBMACHINE_GUN, LimaTechItems.SHOTGUN,  LimaTechItems.MAGNUM))
                .withConditionalEffect(ARMOR_BYPASS, ComplexValueUpgradeEffect.simpleConstant(-4f, CompoundValueOperation.FLAT_ADDITION))
                .effectIcon(sprite("lightfrags"))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(SMG_BUILT_IN)
                .supports(HolderSet.direct(LimaTechItems.SUBMACHINE_GUN))
                .withUnitEffect(PREVENT_SCULK_VIBRATION)
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_ANGER))
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_KNOCKBACK))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.SUBMACHINE_GUN))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(SHOTGUN_BUILT_IN)
                .supports(LimaTechItems.SHOTGUN)
                .withEffect(ITEM_ATTRIBUTE_MODIFIERS, AttributeModifierUpgradeEffect.constantMainHand(Attributes.MOVEMENT_SPEED, RESOURCES.location("shotgun_speed_boost"), 0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(ITEM_ATTRIBUTE_MODIFIERS, AttributeModifierUpgradeEffect.constantMainHand(Attributes.STEP_HEIGHT, RESOURCES.location("shotgun_step_height_boost"), 1, AttributeModifier.Operation.ADD_VALUE))
                .withConditionalEffect(ARMOR_BYPASS, ComplexValueUpgradeEffect.simpleConstant(-0.15f, CompoundValueOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.SHOTGUN))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(HIGH_IMPACT_ROUNDS)
                .supports(LimaTechItems.SHOTGUN, LimaTechItems.MAGNUM)
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(LimaCoreTags.DamageTypes.IGNORES_KNOCKBACK_RESISTANCE))
                .withConditionalEffect(WEAPON_PRE_ATTACK, new KnockbackStrengthUpgradeEffect(LevelBasedValue.perLevel(1.5f)))
                .effectIcon(sprite("powerful_lightfrag"))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(MAGNUM_SCALING_ROUNDS)
                .supports(LimaTechItems.MAGNUM)
                .withConditionalEffect(WEAPON_DAMAGE, ComplexValueUpgradeEffect.of(MathOpsNumberProvider.of(TargetedAttributeValueProvider.of(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH), ConstantValue.exactly(0.25f), MathOperation.MULTIPLICATION), CompoundValueOperation.FLAT_ADDITION,
                        ComplexValueTooltip.attributeValueTooltip(LootContext.EntityTarget.THIS, Attributes.MAX_HEALTH, LevelBasedValue.constant(0.25f))))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.MAGNUM))
                .effectIcon(itemWithSpriteOverlay(LimaTechItems.MAGNUM, "powerful_lightfrag", 10, 10, 0, 6))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withEffect(WEAPON_PROJECTILE_SPEED, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.FLAT_ADDITION))
                .effectIcon(sprite("grenade_speed_boost"))
                .buildAndRegister(ctx);

        // Universal upgrades
        EquipmentUpgrade.builder(UNIVERSAL_ANTI_VIBRATION)
                .supportsLTXWeapons(items)
                .withUnitEffect(PREVENT_SCULK_VIBRATION)
                .effectIcon(sprite("no_vibration"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supportsLTXWeapons(items)
                .withConditionalEffect(WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_ANGER))
                .effectIcon(sprite("stealth_damage"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_ENERGY_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(AMMO_SOURCE, new AmmoSourceUpgradeEffect(WeaponAmmoSource.COMMON_ENERGY_UNIT))
                .effectIcon(sprite("energy_ammo"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withSpecialEffect(AMMO_SOURCE, new AmmoSourceUpgradeEffect(WeaponAmmoSource.INFINITE))
                .effectIcon(sprite("infinite_ammo"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_ARMOR_PIERCE)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withConditionalEffect(ARMOR_BYPASS, ComplexValueUpgradeEffect.simpleRankBased(LevelBasedValue.perLevel(-0.1f), CompoundValueOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(sprite("armor_bypass"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_SHIELD_REGEN)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withConditionalEffect(WEAPON_KILL, new BubbleShieldUpgradeEffect(LevelBasedValue.constant(4), LevelBasedValue.perLevel(10)))
                .effectIcon(sprite("bubble_shield"))
                .buildAndRegister(ctx);

        // Enchantments
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(sprite("looting"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor_enchant"))
                .buildAndRegister(ctx);

        // Hanabi grenade cores
        EquipmentUpgrade.builder(FLAME_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .effectIcon(hanabiCoreIcon(GrenadeType.FLAME))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(CRYO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .effectIcon(hanabiCoreIcon(GrenadeType.CRYO))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(ELECTRIC_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .effectIcon(hanabiCoreIcon(GrenadeType.ELECTRIC))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(ACID_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .effectIcon(hanabiCoreIcon(GrenadeType.ACID))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(NEURO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(hanabiCoreIcon(GrenadeType.NEURO))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(OMNI_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GRENADE_UNLOCK, GrenadeType.FLAME)
                .withEffect(GRENADE_UNLOCK, GrenadeType.CRYO)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ELECTRIC)
                .withEffect(GRENADE_UNLOCK, GrenadeType.ACID)
                .withEffect(GRENADE_UNLOCK, GrenadeType.NEURO)
                .effectIcon(sprite("omni_grenade_core"))
                .buildAndRegister(ctx);
    }

    private void createMachineEffects(BootstrapContext<MachineUpgrade> ctx)
    {
        MachineUpgrade.builder(ESA_CAPACITY_UPGRADE)
                .supports(LimaTechBlockEntities.ENERGY_STORAGE_ARRAY)
                .withEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1)), CompoundValueOperation.MULTIPLY))
                .withEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("extra_energy"))
                .buildAndRegister(ctx);

        MachineUpgrade.builder(ALPHA_MACHINE_SYSTEMS)
                .supports(LimaTechBlockEntities.DIGITAL_FURNACE, LimaTechBlockEntities.GRINDER, LimaTechBlockEntities.RECOMPOSER, LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER)
                .withEffect(ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE))
                .withEffect(MACHINE_ENERGY_USAGE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linearExponent(1.5d), CompoundValueOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(0.725d, DoubleLevelBasedValue.linear(1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(6)
                .effectIcon(sprite("alpha_systems"))
                .buildAndRegister(ctx);

        MachineUpgrade.builder(EPSILON_MACHINE_SYSTEMS)
                .createDefaultTitle(key -> Component.translatable(key).withStyle(LimaTechConstants.LIME_GREEN.chatStyle()))
                .supports(LimaTechBlockEntities.DIGITAL_FURNACE, LimaTechBlockEntities.GRINDER, LimaTechBlockEntities.RECOMPOSER, LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER)
                .withEffect(ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(8), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(16), CompoundValueOperation.MULTIPLY))
                .withEffect(MACHINE_ENERGY_USAGE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(256), CompoundValueOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(-1), CompoundValueOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(sprite("epsilon_systems"))
                .buildAndRegister(ctx);

        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LimaTechBlockEntities.FABRICATOR)
                .withEffect(ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .buildAndRegister(ctx);
    }

    // Utility objects
    private UpgradeIcon intrinsicTypeIcon(ItemLike item)
    {
        return itemWithSpriteOverlay(item, "generic", 10, 10, 0, 6);
    }

    private UpgradeIcon hanabiCoreIcon(GrenadeType grenadeType)
    {
        return itemWithSpriteOverlay(LimaTechItems.GRENADE_LAUNCHER.get().createDefaultStack(null, true, grenadeType), grenadeType.getSerializedName() + "_grenade_core", 10, 10, 0, 6);
    }

    private UpgradeIcon sprite(String spriteName)
    {
        return new UpgradeIcon.SpriteSheetIcon(RESOURCES.location(spriteName));
    }

    private UpgradeIcon itemIcon(ItemStack stack)
    {
        return new UpgradeIcon.ItemStackIcon(stack);
    }

    private UpgradeIcon itemIcon(ItemLike itemLike)
    {
        return itemIcon(new ItemStack(itemLike.asItem()));
    }

    private UpgradeIcon itemWithSpriteOverlay(ItemStack stack, String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return new UpgradeIcon.ItemStackWithSpriteIcon(stack, RESOURCES.location(spriteName), width, height, xOffset, yOffset);
    }

    private UpgradeIcon itemWithSpriteOverlay(ItemLike itemLike, String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return itemWithSpriteOverlay(new ItemStack(itemLike.asItem()), spriteName, width, height, xOffset, yOffset);
    }
}