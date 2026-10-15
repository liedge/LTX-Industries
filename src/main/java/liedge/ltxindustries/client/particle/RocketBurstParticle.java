package liedge.ltxindustries.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public final class RocketBurstParticle extends NoRenderParticle
{
    private final int color;

    private RocketBurstParticle(ClientLevel level, int color, double x, double y, double z, double xa, double ya, double za)
    {
        super(level, x, y, z);

        this.color = color;
        this.xd = xa;
        this.yd = ya;
        this.zd = za;
    }

    private void createBurst(ParticleOptions particle, int count, double xd, double yd, double zd, double variance)
    {
        double bSpeedMul = Mth.nextDouble(random, 0.25d, 0.4375d);

        for (int i = 0; i < count; i++)
        {
            double dx = xd * bSpeedMul + random.nextGaussian() * variance;
            double dy = yd * bSpeedMul + random.nextGaussian() * variance;
            double dz = zd * bSpeedMul + random.nextGaussian() * variance;

            level.addParticle(particle, true, false, x, y, z, dx, dy, dz);
        }
    }

    @Override
    public void tick()
    {
        createBurst(ColorParticleOptions.of(LTXIParticles.COLOR_SPARK, color), 40, -xd, -yd, -zd, 0.2d);
        createBurst(ColorParticleOptions.of(LTXIParticles.COLOR_TWINKLE_SPARK, color), 30, xd, yd, zd, 0.0825d);

        remove();
    }

    public static final class Provider implements ParticleProvider<ColorParticleOptions>
    {
        @Override
        public Particle createParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            return new RocketBurstParticle(level, data.color(), x, y, z, xAux, yAux, zAux);
        }
    }
}