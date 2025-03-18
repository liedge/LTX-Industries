package liedge.limatech.registry.bootstrap;

import liedge.limatech.LimaTech;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limatech.registry.bootstrap.LimaTechPlacedFeatures.*;

public final class LimaTechBiomeModifiers
{
    private LimaTechBiomeModifiers() {}

    public static final ResourceKey<BiomeModifier> TITANIUM_ORE_BIOMES = key("titanium_ore");
    public static final ResourceKey<BiomeModifier> NIOBIUM_ORE_BIOMES = key("niobium_ore");

    private static ResourceKey<BiomeModifier> key(String name)
    {
        return LimaTech.RESOURCES.resourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, name);
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context)
    {
        HolderGetter<PlacedFeature> placements = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        BiomeModifier titaniumOre = new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placements.getOrThrow(TITANIUM_ORE_PLACEMENT)),
                GenerationStep.Decoration.UNDERGROUND_ORES);

        BiomeModifier niobiumOre = new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes::getOrThrow, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS),
                HolderSet.direct(placements.getOrThrow(NIOBIUM_ORE_PLACEMENT)),
                GenerationStep.Decoration.UNDERGROUND_ORES);

        context.register(TITANIUM_ORE_BIOMES, titaniumOre);
        context.register(NIOBIUM_ORE_BIOMES, niobiumOre);
    }
}