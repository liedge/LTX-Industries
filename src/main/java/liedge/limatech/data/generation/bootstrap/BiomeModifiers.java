package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.data.generation.RegistryBootstrapExtensions;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;

import static liedge.limatech.registry.LimaTechWorldGen.*;
import static liedge.limatech.registry.LimaTechWorldGen.NIOBIUM_ORE_BIOMES;

class BiomeModifiers implements RegistryBootstrapExtensions<BiomeModifier>
{
    @Override
    public void run(BootstrapContext<BiomeModifier> context)
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