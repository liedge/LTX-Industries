package liedge.limatech.registry.bootstrap;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static liedge.limacore.data.generation.LimaBootstrapUtil.orePlacement;
import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechPlacedFeatures
{
    private LimaTechPlacedFeatures() {}

    public static final ResourceKey<PlacedFeature> TITANIUM_ORE_PLACEMENT = key("titanium_ore");
    public static final ResourceKey<PlacedFeature> NIOBIUM_ORE_PLACEMENT = key("niobium_ore");

    private static ResourceKey<PlacedFeature> key(String name)
    {
        return RESOURCES.resourceKey(Registries.PLACED_FEATURE, name);
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configs = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacedFeature titaniumOre = orePlacement(configs.getOrThrow(LimaTechConfiguredFeatures.TITANIUM_ORE_CONFIG), 10,
                HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(60)));

        PlacedFeature niobiumOre = orePlacement(configs.getOrThrow(LimaTechConfiguredFeatures.NIOBIUM_ORE_CONFIG), 2, HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP));


        context.register(LimaTechPlacedFeatures.TITANIUM_ORE_PLACEMENT, titaniumOre);
        context.register(NIOBIUM_ORE_PLACEMENT, niobiumOre);
    }
}