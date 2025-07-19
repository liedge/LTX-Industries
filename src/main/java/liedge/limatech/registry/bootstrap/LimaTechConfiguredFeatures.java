package liedge.limatech.registry.bootstrap;

import liedge.limatech.registry.game.LimaTechBlocks;
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
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

import static liedge.limacore.data.generation.LimaBootstrapUtil.*;
import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechConfiguredFeatures
{
    private LimaTechConfiguredFeatures() {}

    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_CONFIG = key("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIOBIUM_ORE_CONFIG = key("niobium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BILEVINE_CONFIG = key("bilevine");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name)
    {
        return RESOURCES.resourceKey(Registries.CONFIGURED_FEATURE, name);
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context)
    {
        ConfiguredFeature<?, ?> titaniumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(10, tagMatchOreTarget(BlockTags.STONE_ORE_REPLACEABLES, LimaTechBlocks.TITANIUM_ORE), tagMatchOreTarget(BlockTags.DEEPSLATE_ORE_REPLACEABLES, LimaTechBlocks.DEEPSLATE_TITANIUM_ORE)));
        ConfiguredFeature<?, ?> niobiumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(3, singleBlockOreTarget(Blocks.END_STONE, LimaTechBlocks.NIOBIUM_ORE)));

        BlockState bilevineState = LimaTechBlocks.BILEVINE.get().defaultBlockState();
        BlockState bilevinePlantState = LimaTechBlocks.BILEVINE_PLANT.get().defaultBlockState();

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

        context.register(TITANIUM_ORE_CONFIG, titaniumOre);
        context.register(NIOBIUM_ORE_CONFIG, niobiumOre);
        context.register(BILEVINE_CONFIG, bilevine);
    }
}