package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.world.generation.RandomVerticalScanPlacement;
import liedge.limacore.world.generation.StructurePlacementFilter;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

import static liedge.limacore.data.generation.LimaBootstrapUtil.orePlacement;
import static liedge.limacore.util.LimaRegistryUtil.keyHolderSet;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIPlacedFeatures
{
    private LTXIPlacedFeatures() {}

    public static final ResourceKey<PlacedFeature> TITANIUM_ORE_PLACEMENT = key("titanium_ore");
    public static final ResourceKey<PlacedFeature> NIOBIUM_ORE_PLACEMENT = key("niobium_ore");
    public static final ResourceKey<PlacedFeature> SPARK_FRUIT_PLACEMENT = key("jungle_spark_fruits");
    public static final ResourceKey<PlacedFeature> FORTRESS_BILEVINE = key("fortress_bilevine");
    public static final ResourceKey<PlacedFeature> BASTION_BILEVINE = key("bastion_bilevine");
    public static final ResourceKey<PlacedFeature> GLOOM_SHROOM_PLACEMENT = key("underground_gloom_shrooms");

    private static ResourceKey<PlacedFeature> key(String name)
    {
        return RESOURCES.resourceKey(Registries.PLACED_FEATURE, name);
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configs = context.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        PlacedFeature titaniumOre = orePlacement(configs.getOrThrow(LTXIConfiguredFeatures.TITANIUM_ORE_CONFIG), 10,
                HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(60)));

        PlacedFeature niobiumOre = orePlacement(configs.getOrThrow(LTXIConfiguredFeatures.NIOBIUM_ORE_CONFIG), 2, HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP));

        PlacedFeature sparkFruits = new PlacedFeature(configs.getOrThrow(LTXIConfiguredFeatures.SPARK_FRUIT_CONFIG), List.of(
                CountPlacement.of(10),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(60), VerticalAnchor.absolute(128)),
                new RandomVerticalScanPlacement(16, BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.JUNGLE_LEAVES), BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.AIR)), BlockPredicate.alwaysTrue(), false),
                RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                BiomeFilter.biome()));
        PlacedFeature fortressBilevine = new PlacedFeature(configs.getOrThrow(LTXIConfiguredFeatures.BILEVINE_CONFIG), List.of(
                CountPlacement.of(96),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.allOf(BlockPredicate.hasSturdyFace(Direction.DOWN), BlockPredicate.matchesBlocks(Blocks.NETHER_BRICKS)), BlockPredicate.ONLY_IN_AIR_PREDICATE, 15),
                RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                StructurePlacementFilter.placeInSameChunkAsStructure(keyHolderSet(structures, BuiltinStructures.FORTRESS))));
        PlacedFeature bastionBilevine = new PlacedFeature(configs.getOrThrow(LTXIConfiguredFeatures.BILEVINE_CONFIG), List.of(
                CountPlacement.of(32),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN), BlockPredicate.ONLY_IN_AIR_PREDICATE, 15),
                RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                StructurePlacementFilter.placeInsideStructure(keyHolderSet(structures, BuiltinStructures.BASTION_REMNANT))));
        PlacedFeature gloomShrooms = new PlacedFeature(configs.getOrThrow(LTXIConfiguredFeatures.GLOOM_SHROOM_CONFIG), List.of(
                CountPlacement.of(2),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
                new RandomVerticalScanPlacement(16, BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.SCULK), BlockPredicate.matchesBlocks(Direction.UP.getNormal(), Blocks.AIR)), BlockPredicate.alwaysTrue(), false),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()));

        context.register(LTXIPlacedFeatures.TITANIUM_ORE_PLACEMENT, titaniumOre);
        context.register(NIOBIUM_ORE_PLACEMENT, niobiumOre);
        context.register(SPARK_FRUIT_PLACEMENT, sparkFruits);
        context.register(FORTRESS_BILEVINE, fortressBilevine);
        context.register(BASTION_BILEVINE, bastionBilevine);
        context.register(GLOOM_SHROOM_PLACEMENT, gloomShrooms);
    }
}