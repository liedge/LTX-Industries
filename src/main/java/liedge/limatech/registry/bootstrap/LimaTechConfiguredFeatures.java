package liedge.limatech.registry.bootstrap;

import liedge.limatech.registry.LimaTechBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

import static liedge.limacore.data.generation.LimaBootstrapUtil.*;
import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechConfiguredFeatures
{
    private LimaTechConfiguredFeatures() {}

    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_CONFIG = key("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIOBIUM_ORE_CONFIG = key("niobium_ore");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name)
    {
        return RESOURCES.resourceKey(Registries.CONFIGURED_FEATURE, name);
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context)
    {
        ConfiguredFeature<?, ?> titaniumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(10, tagMatchOreTarget(BlockTags.STONE_ORE_REPLACEABLES, LimaTechBlocks.TITANIUM_ORE), tagMatchOreTarget(BlockTags.DEEPSLATE_ORE_REPLACEABLES, LimaTechBlocks.DEEPSLATE_TITANIUM_ORE)));
        ConfiguredFeature<?, ?> niobiumOre = new ConfiguredFeature<>(Feature.ORE, oreConfig(3, singleBlockOreTarget(Blocks.END_STONE, LimaTechBlocks.NIOBIUM_ORE)));

        context.register(TITANIUM_ORE_CONFIG, titaniumOre);
        context.register(NIOBIUM_ORE_CONFIG, niobiumOre);
    }
}