package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.world.generation.PlaceOnSideFeature;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

import static liedge.limacore.data.generation.LimaBootstrapUtil.*;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIConfiguredFeatures
{
    private LTXIConfiguredFeatures() {}

    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_CONFIG = key("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIOBIUM_ORE_CONFIG = key("niobium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_CLUSTERS_CONFIG = key("titanium_clusters");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIOBIUM_CLUSTERS_CONFIG = key("niobium_clusters");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPARK_FRUIT_CONFIG = key("jungle_spark_fruits");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BILEVINE_CONFIG = key("bilevine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOOM_SHROOM_CONFIG = key("underground_gloom_shrooms");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name)
    {
        return RESOURCES.resourceKey(Registries.CONFIGURED_FEATURE, name);
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context)
    {
        // Ores
        ConfiguredFeature<?, ?> titaniumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(10, tagMatchOreTarget(BlockTags.STONE_ORE_REPLACEABLES, LTXIBlocks.TITANIUM_ORE), tagMatchOreTarget(BlockTags.DEEPSLATE_ORE_REPLACEABLES, LTXIBlocks.DEEPSLATE_TITANIUM_ORE)));
        ConfiguredFeature<?, ?> niobiumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(3, singleBlockOreTarget(Blocks.END_STONE, LTXIBlocks.NIOBIUM_ORE)));
        ConfiguredFeature<?, ?> titaniumClusters = PlaceOnSideFeature.placeOnSide(simpleState(LTXIBlocks.RAW_TITANIUM_CLUSTER), Direction.UP);
        ConfiguredFeature<?, ?> niobiumClusters = PlaceOnSideFeature.placeOnAnySide(simpleState(LTXIBlocks.RAW_NIOBIUM_CLUSTER));

        // Spark fruit
        ConfiguredFeature<?, ?> sparkFruits = new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new RandomizedIntStateProvider(BlockStateProvider.simple(LTXIBlocks.SPARK_FRUIT.get()), BlockStateProperties.AGE_2, UniformInt.of(0, 2))));

        // Bilevine
        BlockState bilevineState = LTXIBlocks.BILEVINE.get().defaultBlockState();
        BlockState bilevinePlantState = LTXIBlocks.BILEVINE_PLANT.get().defaultBlockState();
        WeightedStateProvider bilevinePlantProvider = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                .add(bilevinePlantState, 5)
                .add(bilevinePlantState.setValue(CaveVines.BERRIES, true), 1));
        RandomizedIntStateProvider bilevineProvider = new RandomizedIntStateProvider(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                .add(bilevineState, 5)
                .add(bilevineState.setValue(CaveVines.BERRIES, true), 1)),
                BlockStateProperties.AGE_25,
                UniformInt.of(23, 25));
        ConfiguredFeature<?, ?> bilevine = new ConfiguredFeature<>(Feature.BLOCK_COLUMN, new BlockColumnConfiguration(
                List.of(
                        BlockColumnConfiguration.layer(UniformInt.of(0, 7), bilevinePlantProvider),
                        BlockColumnConfiguration.layer(ConstantInt.of(1), bilevineProvider)),
                Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));

        // Gloom Shroom
        ConfiguredFeature<?, ?> gloomShroom = new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(LTXIBlocks.GLOOM_SHROOM.get())));

        context.register(TITANIUM_ORE_CONFIG, titaniumOre);
        context.register(NIOBIUM_ORE_CONFIG, niobiumOre);
        context.register(TITANIUM_CLUSTERS_CONFIG, titaniumClusters);
        context.register(NIOBIUM_CLUSTERS_CONFIG, niobiumClusters);
        context.register(SPARK_FRUIT_CONFIG, sparkFruits);
        context.register(BILEVINE_CONFIG, bilevine);
        context.register(GLOOM_SHROOM_CONFIG, gloomShroom);
    }
}