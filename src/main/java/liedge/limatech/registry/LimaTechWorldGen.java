package liedge.limatech.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechWorldGen
{
    private LimaTechWorldGen() {}

    private static ResourceKey<ConfiguredFeature<?, ?>> cf(String name)
    {
        return RESOURCES.resourceKey(Registries.CONFIGURED_FEATURE, name);
    }

    private static ResourceKey<PlacedFeature> pf(String name)
    {
        return RESOURCES.resourceKey(Registries.PLACED_FEATURE, name);
    }

    private static ResourceKey<BiomeModifier> bm(String name)
    {
        return RESOURCES.resourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, name);
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_CONFIG = cf("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIOBIUM_ORE_CONFIG = cf("niobium_ore");

    public static final ResourceKey<PlacedFeature> TITANIUM_ORE_PLACEMENT = pf("titanium_ore");
    public static final ResourceKey<PlacedFeature> NIOBIUM_ORE_PLACEMENT = pf("niobium_ore");

    public static final ResourceKey<BiomeModifier> TITANIUM_ORE_BIOMES = bm("titanium_ore");
    public static final ResourceKey<BiomeModifier> NIOBIUM_ORE_BIOMES = bm("niobium_ore");
}