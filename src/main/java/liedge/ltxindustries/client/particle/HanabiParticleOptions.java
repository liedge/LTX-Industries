package liedge.ltxindustries.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HanabiParticleOptions(GrenadeType element, int color, float size) implements ParticleOptions
{
    public static final MapCodec<HanabiParticleOptions> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            GrenadeType.CODEC.fieldOf("element").forGetter(HanabiParticleOptions::element),
            Codec.INT.fieldOf("color").forGetter(HanabiParticleOptions::color),
            Codec.FLOAT.fieldOf("size").forGetter(HanabiParticleOptions::size))
            .apply(i, HanabiParticleOptions::new));
    public static final StreamCodec<FriendlyByteBuf, HanabiParticleOptions> STREAM_CODEC = StreamCodec.composite(
            GrenadeType.STREAM_CODEC, HanabiParticleOptions::element,
            ByteBufCodecs.VAR_INT, HanabiParticleOptions::color,
            ByteBufCodecs.FLOAT, HanabiParticleOptions::size,
            HanabiParticleOptions::new);

    @Override
    public ParticleType<?> getType()
    {
        return LTXIParticles.HANABI_EXPLOSION.get();
    }
}