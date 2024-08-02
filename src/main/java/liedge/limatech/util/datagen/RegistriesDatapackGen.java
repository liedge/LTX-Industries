package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaRegistriesDatapackGenerator;
import liedge.limatech.registry.LimaTechBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limacore.lib.ModResources.translationKeyFromId;
import static liedge.limatech.lib.weapons.WeaponDeathMessageType.getDeathMessageType;
import static liedge.limatech.registry.LimaTechDamageTypes.*;
import static liedge.limatech.registry.LimaTechWorldGen.*;

class RegistriesDatapackGen extends LimaRegistriesDatapackGenerator
{
    RegistriesDatapackGen(ExistingFileHelper helper)
    {
        super(helper);
    }

    @Override
    protected void createAllDataObjects()
    {
        createDataFor(Registries.DAMAGE_TYPE, this::createDamageTypes);
        createDataFor(Registries.CONFIGURED_FEATURE, this::createConfiguredFeatures);
        createDataFor(Registries.PLACED_FEATURE, this::createPlacedFeatures);
        createDataFor(NeoForgeRegistries.Keys.BIOME_MODIFIERS, this::createBiomeModifiers);
    }

    private void createDamageTypes(BootstrapContext<DamageType> ctx)
    {
        ctx.register(LIGHTFRAG, new DamageType(translationKeyFromId(LIGHTFRAG.location()), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, getDeathMessageType()));
        ctx.register(WEAPON_DAMAGE, new DamageType(translationKeyFromId(WEAPON_DAMAGE.location()), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, getDeathMessageType()));
        ctx.register(ROCKET_TURRET, new DamageType(translationKeyFromId(ROCKET_TURRET.location()), 0f));
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
}