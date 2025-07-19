package liedge.ltxindustries.registry.bootstrap;

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
    public static final ResourceKey<PlacedFeature> FORTRESS_BILEVINE = key("fortress_bilevine");
    public static final ResourceKey<PlacedFeature> BASTION_BILEVINE = key("bastion_bilevine");

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

        context.register(LTXIPlacedFeatures.TITANIUM_ORE_PLACEMENT, titaniumOre);
        context.register(NIOBIUM_ORE_PLACEMENT, niobiumOre);
        context.register(FORTRESS_BILEVINE, fortressBilevine);
        context.register(BASTION_BILEVINE, bastionBilevine);
    }
}