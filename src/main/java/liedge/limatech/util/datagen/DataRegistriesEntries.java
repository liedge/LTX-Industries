package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaDatagenBootstrapBuilder;
import liedge.limatech.LimaTechTags;
import liedge.limatech.client.UpgradeIcon;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.lib.LevelBasedDoubleValue;
import liedge.limatech.lib.LimaTechDeathMessageTypes;
import liedge.limatech.lib.upgradesystem.calculation.AdditionCalculation;
import liedge.limatech.lib.upgradesystem.calculation.LivingAttributeCalculation;
import liedge.limatech.lib.upgradesystem.calculation.MultiplyTotalCalculation;
import liedge.limatech.lib.upgradesystem.calculation.OverrideBaseCalculation;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgradesystem.equipment.effect.*;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgrade;
import liedge.limatech.lib.upgradesystem.machine.effect.ModifyEnergyStorageUpgradeEffect;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.LimaTechBlockEntities;
import liedge.limatech.registry.LimaTechBlocks;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
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
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.registry.LimaTechDamageTypes.*;
import static liedge.limatech.registry.LimaTechEnchantments.AMMO_SCAVENGER;
import static liedge.limatech.registry.LimaTechEnchantments.RAZOR;
import static liedge.limatech.registry.LimaTechEquipmentUpgrades.*;
import static liedge.limatech.registry.LimaTechMachineUpgrades.ESA_CAPACITY_UPGRADE;
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
        builder.add(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, this::createEquipmentEffects);
        builder.add(LimaTechRegistries.MACHINE_UPGRADES_KEY, this::createMachineEffects);
    }

    private void createDamageTypes(BootstrapContext<DamageType> ctx)
    {
        DeathMessageType weaponMsgType = LimaTechDeathMessageTypes.WEAPON_DEATH_MESSAGE_TYPE.getValue();
        DeathMessageType traceableMsgType = LimaTechDeathMessageTypes.TRACEABLE_PROJECTILE_MESSAGE_TYPE.getValue();

        registerDamageType(ctx, LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, MAGNUM_LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, EXPLOSIVE_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(ctx, FLAME_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.BURNING, weaponMsgType);
        registerDamageType(ctx, FREEZE_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.FREEZING, weaponMsgType);
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
                EquipmentSlotGroup.MAINHAND));

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
        HolderGetter<EquipmentUpgrade> holders = ctx.lookup(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY);

        // Weapon-specific upgrades
        EnchantmentEntityEffect cd;
        EquipmentUpgrade.builder(SMG_BUILT_IN)
                .supports(HolderSet.direct(LimaTechItems.SUBMACHINE_GUN))
                .withEffect(NoVibrationUpgradeEffect.NO_VIBRATIONS_EFFECT)
                .withEffect(NoAngerUpgradeEffect.NO_ANGER_EFFECT)
                .withEffect(new WeaponKnockbackUpgradeEffect(false, new MultiplyTotalCalculation(LevelBasedDoubleValue.constant(-1d))))
                .withEffect(new BypassArmorUpgradeEffect(LevelBasedValue.constant(0)))
                .effectIcon(spriteOverlayIcon(LimaTechItems.SUBMACHINE_GUN, null, 10, 10, 0, 6))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(SHOTGUN_BUILT_IN)
                .supports(LimaTechItems.SHOTGUN)
                .withEffect(ItemAttributeModifierUpgradeEffect.create(Attributes.MOVEMENT_SPEED, RESOURCES.location("shotgun_speed_boost"), 0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .withEffect(ItemAttributeModifierUpgradeEffect.create(Attributes.STEP_HEIGHT, RESOURCES.location("shotgun_step_boost"), 1, AttributeModifier.Operation.ADD_VALUE))
                .withEffect(new BypassArmorUpgradeEffect(LevelBasedValue.constant(0.15f)))
                .effectIcon(spriteOverlayIcon(LimaTechItems.SHOTGUN, null, 10, 10, 0, 6))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(HIGH_IMPACT_ROUNDS)
                .supports(LimaTechItems.SHOTGUN, LimaTechItems.MAGNUM)
                .withEffect(new WeaponKnockbackUpgradeEffect(true, new AdditionCalculation(LevelBasedDoubleValue.constant(1.5d))))
                .effectIcon(sprite("armor_bypass"))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(MAGNUM_SCALING_ROUNDS)
                .supports(LimaTechItems.MAGNUM)
                .withEffect(new WeaponDamageUpgradeEffect(new LivingAttributeCalculation(Attributes.MAX_HEALTH, LevelBasedDoubleValue.constant(0.2d))))
                .withEffect(new BypassArmorUpgradeEffect(LevelBasedValue.constant(1f)))
                .buildAndRegister(ctx);

        EquipmentUpgrade.builder(GRENADE_LAUNCHER_PROJECTILE_SPEED)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .setMaxRank(2)
                .withEffect(new ProjectileSpeedUpgradeEffect(new AdditionCalculation(LevelBasedDoubleValue.perLevel(0.5d))))
                .effectIcon(sprite("grenade_speed_boost"))
                .buildAndRegister(ctx);

        // Universal upgrades
        EquipmentUpgrade.builder(UNIVERSAL_ANTI_VIBRATION)
                .supportsLTXWeapons(items)
                .withEffect(NoVibrationUpgradeEffect.NO_VIBRATIONS_EFFECT)
                .effectIcon(sprite("no_vibration"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_STEALTH_DAMAGE)
                .supportsLTXWeapons(items)
                .withEffect(NoAngerUpgradeEffect.NO_ANGER_EFFECT)
                .effectIcon(sprite("stealth_damage"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_ENERGY_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withEffect(new SetAmmoSourceUpgradeEffect(WeaponAmmoSource.COMMON_ENERGY_UNIT))
                .effectIcon(sprite("energy_ammo"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_INFINITE_AMMO)
                .supportsLTXWeapons(items)
                .exclusiveWith(holders, LimaTechTags.EquipmentUpgrades.AMMO_SOURCE_MODIFIERS)
                .withEffect(new SetAmmoSourceUpgradeEffect(WeaponAmmoSource.INFINITE))
                .effectIcon(sprite("infinite_ammo"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_ARMOR_PIERCE)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withEffect(new BypassArmorUpgradeEffect(LevelBasedValue.perLevel(0.1f)))
                .effectIcon(sprite("armor_bypass"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(UNIVERSAL_SHIELD_REGEN)
                .supportsLTXWeapons(items)
                .setMaxRank(3)
                .withEffect(new BubbleShieldUpgradeEffect(LevelBasedValue.constant(4), LevelBasedValue.perLevel(10)))
                .effectIcon(sprite("shield_regen"))
                .buildAndRegister(ctx);

        // Enchantments
        EquipmentUpgrade.builder(LOOTING_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(new EnchantmentLevelUpgradeEffect(enchantments.getOrThrow(Enchantments.LOOTING)))
                .effectIcon(sprite("looting"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(AMMO_SCAVENGER_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(new EnchantmentLevelUpgradeEffect(enchantments.getOrThrow(AMMO_SCAVENGER)))
                .effectIcon(sprite("ammo_scavenger"))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(RAZOR_ENCHANTMENT)
                .supportsLTXWeapons(items)
                .setMaxRank(5)
                .withEffect(new EnchantmentLevelUpgradeEffect(enchantments.getOrThrow(RAZOR)))
                .effectIcon(sprite("razor_enchant"))
                .buildAndRegister(ctx);

        // Hanabi grenade cores
        EquipmentUpgrade.builder(FLAME_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GrenadeTypeSelectionUpgradeEffect.unlockSingle(GrenadeType.FLAME))
                .effectIcon(hanabiCoreIcon(GrenadeType.FLAME))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(FREEZE_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GrenadeTypeSelectionUpgradeEffect.unlockMultiple(GrenadeType.FREEZE))
                .effectIcon(hanabiCoreIcon(GrenadeType.FREEZE))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(ELECTRIC_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GrenadeTypeSelectionUpgradeEffect.unlockSingle(GrenadeType.ELECTRIC))
                .effectIcon(hanabiCoreIcon(GrenadeType.ELECTRIC))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(ACID_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GrenadeTypeSelectionUpgradeEffect.unlockSingle(GrenadeType.ACID))
                .effectIcon(hanabiCoreIcon(GrenadeType.ACID))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(NEURO_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GrenadeTypeSelectionUpgradeEffect.unlockSingle(GrenadeType.NEURO))
                .effectIcon(hanabiCoreIcon(GrenadeType.NEURO))
                .buildAndRegister(ctx);
        EquipmentUpgrade.builder(OMNI_GRENADE_CORE)
                .supports(LimaTechItems.GRENADE_LAUNCHER)
                .withEffect(GrenadeTypeSelectionUpgradeEffect.unlockMultiple(GrenadeType.FLAME, GrenadeType.FREEZE, GrenadeType.ELECTRIC, GrenadeType.ACID, GrenadeType.NEURO))
                .effectIcon(sprite("omni_grenade_core"))
                .buildAndRegister(ctx);
    }

    private void createMachineEffects(BootstrapContext<MachineUpgrade> ctx)
    {
        LevelBasedDoubleValue esaCapacities = LevelBasedDoubleValue.lookup(1, 10_000_000, 30_000_000, 50_000_000, 100_000_000);
        LevelBasedDoubleValue esaTransferRates = LevelBasedDoubleValue.lookup(1, 100_000, 300_000, 500_000, 1_000_000);

        MachineUpgrade.builder(ESA_CAPACITY_UPGRADE)
                .supports(LimaTechBlockEntities.ENERGY_STORAGE_ARRAY)
                .withEffect(new ModifyEnergyStorageUpgradeEffect(new OverrideBaseCalculation(esaCapacities), new OverrideBaseCalculation(esaTransferRates)))
                .setMaxRank(4)
                .effectIcon(sprite("extra_batteries"))
                .buildAndRegister(ctx);
    }

    // Utility objects
    private UpgradeIcon sprite(String name)
    {
        return new UpgradeIcon.SpriteSheetIcon(RESOURCES.location(name));
    }

    private UpgradeIcon spriteOverlayIcon(ItemStack stack, @Nullable String spriteName, int width, int height, int xOffset, int yOffset)
    {
        ResourceLocation spriteLocation = spriteName != null ? RESOURCES.location(spriteName) : UpgradeIcon.DEFAULT_ICON_LOCATION;
        return new UpgradeIcon.ItemStackWithSpriteIcon(stack, spriteLocation, width, height, xOffset, yOffset);
    }

    private UpgradeIcon spriteOverlayIcon(ItemLike item, @Nullable String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return spriteOverlayIcon(item.asItem().getDefaultInstance(), spriteName, width, height, xOffset, yOffset);
    }

    private UpgradeIcon hanabiCoreIcon(GrenadeType grenadeType)
    {
        GrenadeLauncherWeaponItem gl = LimaTechItems.GRENADE_LAUNCHER.get();
        ItemStack stack = new ItemStack(gl);
        gl.setAmmoLoaded(stack, gl.getAmmoCapacity(stack));
        gl.setGrenadeType(stack, grenadeType);

        return spriteOverlayIcon(stack, grenadeType.getSerializedName() + "_grenade_core", 10, 10, 0, 6);
    }
}