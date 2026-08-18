package liedge.ltxindustries.registry;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public final class LTXIDataMaps
{
    private LTXIDataMaps() { }

    public static final DataMapType<RecipeType<?>, HolderSet<RecipeMode>> DEFAULT_RECIPE_MODES = builder("default_modes", Registries.RECIPE_TYPE, RegistryCodecs.homogeneousList(LTXIRegistries.Keys.RECIPE_MODES)).build();

    private static <T, R> DataMapType.Builder<T, R> builder(String name, ResourceKey<Registry<R>> registryKey, Codec<T> codec)
    {
        return DataMapType.builder(LTXIndustries.RESOURCES.id(name), registryKey, codec);
    }
}