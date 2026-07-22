package liedge.ltxindustries.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;
import java.util.function.BiPredicate;

public record MachineLocation(Optional<ResourceKey<Level>> dimension, Optional<HolderSet<Biome>> biomes, boolean needsWaterlog) implements BiPredicate<Level, BlockPos>
{
    public static final Codec<MachineLocation> CODEC = RecordCodecBuilder.create(i -> i.group(
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(MachineLocation::dimension),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(MachineLocation::biomes),
            Codec.BOOL.optionalFieldOf("needs_waterlog", false).forGetter(MachineLocation::needsWaterlog))
            .apply(i, MachineLocation::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MachineLocation> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public static final MachineLocation ANY = new MachineLocation(Optional.empty(), Optional.empty(), false);

    @Override
    public boolean test(Level level, BlockPos pos)
    {
        if (dimension.isPresent() && !dimension.get().equals(level.dimension()))
        {
            return false;
        }
        else if (biomes.isPresent() && !biomes.get().contains(level.getBiome(pos)))
        {
            return false;
        }
        else if (needsWaterlog)
        {
            return level.getFluidState(pos).is(FluidTags.WATER);
        }

        return true;
    }
}