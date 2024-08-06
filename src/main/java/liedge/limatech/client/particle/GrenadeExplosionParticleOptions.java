package liedge.limatech.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.LimaTechParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GrenadeExplosionParticleOptions(GrenadeType element, double explosionSize) implements ParticleOptions
{
    public static final MapCodec<GrenadeExplosionParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GrenadeType.CODEC.fieldOf("element").forGetter(GrenadeExplosionParticleOptions::element),
            Codec.DOUBLE.fieldOf("size").forGetter(GrenadeExplosionParticleOptions::explosionSize))
            .apply(instance, GrenadeExplosionParticleOptions::new));
    public static final StreamCodec<FriendlyByteBuf, GrenadeExplosionParticleOptions> STREAM_CODEC = StreamCodec.composite(
            GrenadeType.STREAM_CODEC, GrenadeExplosionParticleOptions::element,
            ByteBufCodecs.DOUBLE, GrenadeExplosionParticleOptions::explosionSize,
            GrenadeExplosionParticleOptions::new);

    @Override
    public ParticleType<?> getType()
    {
        return LimaTechParticles.GRENADE_EXPLOSION.get();
    }
}