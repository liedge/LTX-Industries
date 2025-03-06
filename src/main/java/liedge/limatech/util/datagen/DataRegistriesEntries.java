package liedge.limatech.util.datagen;

import liedge.limacore.LimaCoreTags;
import liedge.limacore.data.generation.LimaDatagenBootstrapBuilder;
import liedge.limacore.registry.LimaCoreDataComponents;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.LimaTechDeathMessageTypes;
import liedge.limatech.lib.math.CompoundOperation;
import liedge.limatech.lib.math.LevelBasedDoubleValue;
import liedge.limatech.lib.upgrades.UpgradeIcon;
import liedge.limatech.lib.upgrades.effect.*;
import liedge.limatech.lib.upgrades.effect.equipment.BubbleShieldUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.equipment.DynamicDamageTagUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.equipment.KnockbackStrengthUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
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
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.registry.LimaTechDamageTypes.*;
import static liedge.limatech.registry.LimaTechEnchantments.AMMO_SCAVENGER;
import static liedge.limatech.registry.LimaTechEnchantments.RAZOR;
import static liedge.limatech.registry.LimaTechEquipmentUpgrades.*;
import static liedge.limatech.registry.LimaTechMachineUpgrades.*;
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
        DeathMessageType traceableMsgType = LimaTechDeathMessageTypes.TRACEABLE_PROJECTILE_MESSAGE_TYPE.getValue();

        registerDamageType(ctx, LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, MAGNUM_LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, EXPLOSIVE_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, FLAME_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.BURNING, weaponMsgType);
        registerDamageType(ctx, CRYO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.FREEZING, weaponMsgType);
        registerDamageType(ctx, ELECTRIC_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, ACID_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, NEURO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, ROCKET_LAUNCHER, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);

        registerDamageType(ctx, STICKY_FLAME, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, traceableMsgType);
        registerDamageType(ctx, TURRET_ROCKET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, traceableMsgType);
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
                .withListEffect(LimaTechUpgradeEffectComponents.ARMOR_BYPASS, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.constant(-4), CompoundOperation.FLAT_ADDITION))
                .effectIcon(sprite("lightfrags"))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(SMG_BUILT_IN)
                .supports(HolderSet.direct(LimaTechItems.SUBMACHINE_GUN))
                .withEffect(LimaTechUpgradeEffectComponents.PREVENT_SCULK_VIBRATION, NoSculkVibrationEffect.preventSculkVibrations())
                .withListEffects(LimaTechUpgradeEffectComponents.WEAPON_PRE_ATTACK,
                        new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_ANGER),
                        new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_KNOCKBACK))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.SUBMACHINE_GUN))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(SHOTGUN_BUILT_IN)
                .supports(LimaTechItems.SHOTGUN)
                .withListEffects(LimaTechUpgradeEffectComponents.ITEM_ATTRIBUTE_MODIFIERS,
                        AttributeModifierUpgradeEffect.constantMainHand(Attributes.MOVEMENT_SPEED, RESOURCES.location("shotgun_speed_boost"), 0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        AttributeModifierUpgradeEffect.constantMainHand(Attributes.STEP_HEIGHT, RESOURCES.location("shotgun_step_height_boost"), 1, AttributeModifier.Operation.ADD_VALUE))
                .withListEffect(LimaTechUpgradeEffectComponents.ARMOR_BYPASS, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.constant(-0.15f), CompoundOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.SHOTGUN))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(HIGH_IMPACT_ROUNDS)
                .supports(LimaTechItems.SHOTGUN, LimaTechItems.MAGNUM)
                .withListEffects(LimaTechUpgradeEffectComponents.WEAPON_PRE_ATTACK,
                        new DynamicDamageTagUpgradeEffect(LimaCoreTags.DamageTypes.IGNORES_KNOCKBACK_RESISTANCE),
                        new KnockbackStrengthUpgradeEffect(LevelBasedValue.perLevel(1.5f)))
                .effectIcon(sprite("powerful_lightfrag"))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(MAGNUM_SCALING_ROUNDS)
                .supports(LimaTechItems.MAGNUM)
                .withListEffect(LimaTechUpgradeEffectComponents.WEAPON_DAMAGE, ValueUpgradeEffect.addEnemyAttribute(Attributes.MAX_HEALTH, LevelBasedDoubleValue.constant(0.2d)))
                .effectIcon(intrinsicTypeIcon(LimaTechItems.MAGNUM))
                .effectIcon(itemWithSpriteOverlay(LimaTechItems.MAGNUM, "powerful_lightfrag", 10, 10, 0, 6))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withListEffect(LimaTechUpgradeEffectComponents.WEAPON_PROJECTILE_SPEED, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.perLevel(0.5d), CompoundOperation.FLAT_ADDITION))
                .effectIcon(sprite("grenade_speed_boost"))
                .buildAndRegister(ctx);

        // Universal upgrades
        EquipmentUpgrade.builder(UNIVERSAL_ANTI_VIBRATION)
                .supportsLTXWeapons(items)
                .withEffect(LimaTechUpgradeEffectComponents.PREVENT_SCULK_VIBRATION, NoSculkVibrationEffect.preventSculkVibrations())
                .effectIcon(sprite("no_vibration"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supportsLTXWeapons(items)
                .withListEffect(LimaTechUpgradeEffectComponents.WEAPON_PRE_ATTACK, new DynamicDamageTagUpgradeEffect(DamageTypeTags.NO_ANGER))
                .effectIcon(sprite("stealth_damage"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_ENERGY_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withEffect(LimaTechUpgradeEffectComponents.AMMO_SOURCE, new AmmoSourceUpgradeEffect(WeaponAmmoSource.COMMON_ENERGY_UNIT))
                .effectIcon(sprite("energy_ammo"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withEffect(LimaTechUpgradeEffectComponents.AMMO_SOURCE, new AmmoSourceUpgradeEffect(WeaponAmmoSource.INFINITE))
                .effectIcon(sprite("infinite_ammo"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_ARMOR_PIERCE)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withListEffect(LimaTechUpgradeEffectComponents.ARMOR_BYPASS, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.perLevel(-0.1f), CompoundOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(sprite("armor_bypass"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_SHIELD_REGEN)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withListEffect(LimaTechUpgradeEffectComponents.WEAPON_KILL, new BubbleShieldUpgradeEffect(LevelBasedValue.constant(4), LevelBasedValue.perLevel(10)))
                .effectIcon(sprite("bubble_shield"))
                .buildAndRegister(ctx);

        // Enchantments
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withListEffect(LimaTechUpgradeEffectComponents.ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(sprite("looting"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withListEffect(LimaTechUpgradeEffectComponents.ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withListEffect(LimaTechUpgradeEffectComponents.ITEM_ENCHANTMENTS, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor_enchant"))
                .buildAndRegister(ctx);

        // Hanabi grenade cores
        EquipmentUpgrade.builder(FLAME_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withListEffect(LimaTechUpgradeEffectComponents.GRENADE_UNLOCK, new GrenadeUnlockUpgradeEffect(GrenadeType.FLAME))
                .effectIcon(hanabiCoreIcon(GrenadeType.FLAME))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(CRYO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withListEffect(LimaTechUpgradeEffectComponents.GRENADE_UNLOCK, new GrenadeUnlockUpgradeEffect(GrenadeType.CRYO))
                .effectIcon(hanabiCoreIcon(GrenadeType.CRYO))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(ELECTRIC_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withListEffect(LimaTechUpgradeEffectComponents.GRENADE_UNLOCK, new GrenadeUnlockUpgradeEffect(GrenadeType.ELECTRIC))
                .effectIcon(hanabiCoreIcon(GrenadeType.ELECTRIC))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(ACID_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withListEffect(LimaTechUpgradeEffectComponents.GRENADE_UNLOCK, new GrenadeUnlockUpgradeEffect(GrenadeType.ACID))
                .effectIcon(hanabiCoreIcon(GrenadeType.ACID))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(NEURO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withListEffect(LimaTechUpgradeEffectComponents.GRENADE_UNLOCK, new GrenadeUnlockUpgradeEffect(GrenadeType.NEURO))
                .effectIcon(hanabiCoreIcon(GrenadeType.NEURO))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(OMNI_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withListEffects(LimaTechUpgradeEffectComponents.GRENADE_UNLOCK,
                        new GrenadeUnlockUpgradeEffect(GrenadeType.FLAME),
                        new GrenadeUnlockUpgradeEffect(GrenadeType.CRYO),
                        new GrenadeUnlockUpgradeEffect(GrenadeType.ELECTRIC),
                        new GrenadeUnlockUpgradeEffect(GrenadeType.ACID),
                        new GrenadeUnlockUpgradeEffect(GrenadeType.NEURO))
                .effectIcon(sprite("omni_grenade_core"))
                .buildAndRegister(ctx);
    }

    private void createMachineEffects(BootstrapContext<MachineUpgrade> ctx)
    {
        MachineUpgrade.builder(ESA_CAPACITY_UPGRADE)
                .supports(LimaTechBlockEntities.ENERGY_STORAGE_ARRAY)
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.exponential(2, LevelBasedDoubleValue.linear(3, 1)), CompoundOperation.MULTIPLY))
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.exponential(2, LevelBasedDoubleValue.linear(3, 1)), CompoundOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("extra_energy"))
                .buildAndRegister(ctx);

        MachineUpgrade.builder(ALPHA_MACHINE_SYSTEMS)
                .supports(LimaTechBlockEntities.DIGITAL_FURNACE, LimaTechBlockEntities.GRINDER, LimaTechBlockEntities.RECOMPOSER, LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER)
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.perLevel(0.5d), CompoundOperation.ADD_MULTIPLIED_BASE))
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.perLevel(0.5d), CompoundOperation.ADD_MULTIPLIED_BASE))
                .withListEffect(LimaTechUpgradeEffectComponents.MACHINE_ENERGY_USAGE, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.linearExponent(1.5d), CompoundOperation.MULTIPLY))
                .withListEffect(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.exponential(0.725d, LevelBasedDoubleValue.perLevel(1)), CompoundOperation.MULTIPLY))
                .setMaxRank(6)
                .effectIcon(sprite("alpha_systems"))
                .buildAndRegister(ctx);

        MachineUpgrade.builder(EPSILON_MACHINE_SYSTEMS)
                .modifyTitle(title -> title.copy().withStyle(LimaTechConstants.LIME_GREEN.chatStyle()))
                .supports(LimaTechBlockEntities.DIGITAL_FURNACE, LimaTechBlockEntities.GRINDER, LimaTechBlockEntities.RECOMPOSER, LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER)
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.constant(8), CompoundOperation.MULTIPLY))
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.constant(16), CompoundOperation.MULTIPLY))
                .withListEffect(LimaTechUpgradeEffectComponents.MACHINE_ENERGY_USAGE, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.constant(256), CompoundOperation.MULTIPLY))
                .withListEffect(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.constant(-1), CompoundOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(sprite("epsilon_systems"))
                .buildAndRegister(ctx);

        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LimaTechBlockEntities.FABRICATOR)
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.exponential(2, LevelBasedDoubleValue.linear(2, 1)), CompoundOperation.MULTIPLY))
                .withListEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueUpgradeEffect.simpleValue(LevelBasedDoubleValue.exponential(2, LevelBasedDoubleValue.linear(2, 1)), CompoundOperation.MULTIPLY))
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