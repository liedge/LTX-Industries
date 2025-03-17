package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.data.generation.RegistryBootstrapExtensions;
import liedge.limatech.registry.LimaTechBlocks;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

import static liedge.limatech.registry.LimaTechWorldGen.NIOBIUM_ORE_CONFIG;
import static liedge.limatech.registry.LimaTechWorldGen.TITANIUM_ORE_CONFIG;

class ConfiguredFeatures implements RegistryBootstrapExtensions<ConfiguredFeature<?, ?>>
{
    @Override
    public void run(BootstrapContext<ConfiguredFeature<?, ?>> context)
    {
        ConfiguredFeature<?, ?> titaniumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(10, tagMatchOreTarget(BlockTags.STONE_ORE_REPLACEABLES, LimaTechBlocks.TITANIUM_ORE), tagMatchOreTarget(BlockTags.DEEPSLATE_ORE_REPLACEABLES, LimaTechBlocks.DEEPSLATE_TITANIUM_ORE)));
        ConfiguredFeature<?, ?> niobiumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(3, singleBlockOreTarget(Blocks.END_STONE, LimaTechBlocks.NIOBIUM_ORE)));

        context.register(TITANIUM_ORE_CONFIG, titaniumOre);
        context.register(NIOBIUM_ORE_CONFIG, niobiumOre);
    }
}