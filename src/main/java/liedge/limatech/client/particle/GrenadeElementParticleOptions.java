package liedge.limatech.client.particle;

import liedge.limacore.client.particle.LimaParticleType;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public record GrenadeElementParticleOptions(ParticleType<GrenadeElementParticleOptions> type, OrbGrenadeElement element) implements ParticleOptions
{
    public GrenadeElementParticleOptions(Supplier<? extends ParticleType<GrenadeElementParticleOptions>> typeSupplier, OrbGrenadeElement element)
    {
        this(typeSupplier.get(), element);
    }

    public static LimaParticleType<GrenadeElementParticleOptions> newParticleType(boolean overrideLimiter)
    {
        return LimaParticleType.referenceCodecs(overrideLimiter,
                type -> OrbGrenadeElement.CODEC.fieldOf("element").xmap(e -> new GrenadeElementParticleOptions(type, e), GrenadeElementParticleOptions::element),
                type -> OrbGrenadeElement.STREAM_CODEC.map(e -> new GrenadeElementParticleOptions(type, e), GrenadeElementParticleOptions::element));
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}