package liedge.limatech.client.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import liedge.limacore.client.particle.LimaParticleType;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public record ColorEndpointParticleOptions(ParticleType<ColorEndpointParticleOptions> type, LimaColor color, Vec3 endpoint) implements ParticleOptions
{
    private static MapCodec<ColorEndpointParticleOptions> codec(ParticleType<ColorEndpointParticleOptions> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                LimaColor.CODEC.fieldOf("color").forGetter(ColorEndpointParticleOptions::color),
                Vec3.CODEC.fieldOf("end").forGetter(ColorEndpointParticleOptions::endpoint))
                .apply(instance, (color, end) -> new ColorEndpointParticleOptions(type, color, end)));
    }

    private static StreamCodec<ByteBuf, ColorEndpointParticleOptions> streamCodec(ParticleType<ColorEndpointParticleOptions> type)
    {
        return StreamCodec.composite(
                LimaColor.STREAM_CODEC, ColorEndpointParticleOptions::color,
                LimaStreamCodecs.VEC3D, ColorEndpointParticleOptions::endpoint,
                (color, end) -> new ColorEndpointParticleOptions(type, color, end));
    }

    public static LimaParticleType<ColorEndpointParticleOptions> type(boolean overrideLimiter)
    {
        return LimaParticleType.createWithTypedCodecs(overrideLimiter, ColorEndpointParticleOptions::codec, ColorEndpointParticleOptions::streamCodec);
    }

    public ColorEndpointParticleOptions(Supplier<? extends ParticleType<ColorEndpointParticleOptions>> typeSupplier, LimaColor color, Vec3 end)
    {
        this(typeSupplier.get(), color, end);
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}