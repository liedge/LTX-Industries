package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.data.generation.RegistryBootstrapExtensions;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static liedge.limatech.registry.LimaTechWorldGen.*;
import static liedge.limatech.registry.LimaTechWorldGen.NIOBIUM_ORE_PLACEMENT;

class PlacedFeatures implements RegistryBootstrapExtensions<PlacedFeature>
{
    @Override
    public void run(BootstrapContext<PlacedFeature> context)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configs = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacedFeature titaniumOre = orePlacement(configs.getOrThrow(TITANIUM_ORE_CONFIG), 10,
                HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(60)));

        PlacedFeature niobiumOre = orePlacement(configs.getOrThrow(NIOBIUM_ORE_CONFIG), 2, HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP));


        context.register(TITANIUM_ORE_PLACEMENT, titaniumOre);
        context.register(NIOBIUM_ORE_PLACEMENT, niobiumOre);
    }
}