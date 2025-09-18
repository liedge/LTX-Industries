package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limacore.util.LimaRegistryUtil.keyHolderSet;
import static liedge.ltxindustries.registry.bootstrap.LTXIPlacedFeatures.*;

public final class LTXIBiomeModifiers
{
    private LTXIBiomeModifiers() {}

    public static final ResourceKey<BiomeModifier> TITANIUM_ORE_BIOMES = key("titanium_ore");
    public static final ResourceKey<BiomeModifier> NIOBIUM_ORE_BIOMES = key("niobium_ore");
    public static final ResourceKey<BiomeModifier> SPARK_FRUIT_BIOMES = key("jungle_spark_fruits");
    public static final ResourceKey<BiomeModifier> BILEVINE_BIOMES = key("bilevine");
    public static final ResourceKey<BiomeModifier> GLOOM_SHROOM_BIOMES = key("underground_gloom_shrooms");

    private static ResourceKey<BiomeModifier> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, name);
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context)
    {
        HolderGetter<PlacedFeature> placements = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        BiomeModifier titaniumOre = new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                keyHolderSet(placements, TITANIUM_ORE_PLACEMENT),
                GenerationStep.Decoration.UNDERGROUND_ORES);

        BiomeModifier niobiumOre = new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                keyHolderSet(biomes, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS),
                keyHolderSet(placements, NIOBIUM_ORE_PLACEMENT),
                GenerationStep.Decoration.UNDERGROUND_ORES);

        BiomeModifier sparkFruits = new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                keyHolderSet(placements, SPARK_FRUIT_PLACEMENT),
                GenerationStep.Decoration.VEGETAL_DECORATION);

        BiomeModifier bilevine = new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                keyHolderSet(placements, FORTRESS_BILEVINE, BASTION_BILEVINE),
                GenerationStep.Decoration.VEGETAL_DECORATION);

        BiomeModifier gloomShrooms = new BiomeModifiers.AddFeaturesBiomeModifier(
                keyHolderSet(biomes, Biomes.DEEP_DARK),
                keyHolderSet(placements, GLOOM_SHROOM_PLACEMENT),
                GenerationStep.Decoration.VEGETAL_DECORATION);

        context.register(TITANIUM_ORE_BIOMES, titaniumOre);
        context.register(NIOBIUM_ORE_BIOMES, niobiumOre);
        context.register(SPARK_FRUIT_BIOMES, sparkFruits);
        context.register(BILEVINE_BIOMES, bilevine);
        context.register(GLOOM_SHROOM_BIOMES, gloomShrooms);
    }
}