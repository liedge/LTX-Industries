package liedge.ltxindustries.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.util.LimaCoreClientUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public final class ColorExplosionParticle extends SingleQuadParticle
{
    private final SpriteSet sprites;

    private ColorExplosionParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, int color)
    {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.hasPhysics = false;

        LimaCoreClientUtil.setQuadParticleColor(this, color);
    }

    @Override
    public void tick()
    {
        if (age++ >= lifetime)
        {
            remove();
        }
        else
        {
            setSpriteFromAge(sprites);
        }
    }

    @Override
    protected int getLightCoords(float a)
    {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    protected Layer getLayer()
    {
        return Layer.OPAQUE;
    }

    // Emitter classes

    private static abstract class BaseEmitter extends NoRenderParticle
    {
        private final int color;
        private final float radius;
        private final int density;

        private BaseEmitter(ClientLevel level, double x, double y, double z, int color, float radius, int density, int lifetime)
        {
            super(level, x, y, z);

            this.color = color;
            this.radius = radius;
            this.density = density;
            this.lifetime = lifetime;
        }

        abstract void spawnParticle(double x, double y, double z, int color);

        @Override
        public void tick()
        {
            if (age++ >= lifetime)
            {
                remove();
            }
            else
            {
                for (int i = 0; i < density; i++)
                {
                    double px = x + (2d * random.nextDouble() - 1d) * radius;
                    double py = y + (2d * random.nextDouble() - 1d) * radius;
                    double pz = z + (2d * random.nextDouble() - 1d) * radius;

                    spawnParticle(px, py, pz, color);
                }
            }
        }
    }

    private static class GloomBurstEmitter extends BaseEmitter
    {
        private GloomBurstEmitter(ClientLevel level, double x, double y, double z)
        {
            super(level, x, y, z, -1, 4.5f, 8, 5);
        }

        @Override
        void spawnParticle(double x, double y, double z, int color)
        {
            level.addParticle(LTXIParticles.GLOOM_BURST.get(), true, false, x, y, z, 0, 0, 0);
        }
    }

    // Providers

    public static final class FullSonicBoomProvider implements ParticleProvider<ColorParticleOptions>
    {
        private final SpriteSet sprites;

        public FullSonicBoomProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColorParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            ColorExplosionParticle particle = new ColorExplosionParticle(level, sprites, x, y, z, options.color());

            particle.quadSize = 0.875f;
            particle.setSize(0.875f, 0.875f);
            particle.setLifetime(16);

            return particle;
        }
    }

    public static final class HalfSonicBoomProvider implements ParticleProvider<ColorParticleOptions>
    {
        private final SpriteSet sprites;

        public HalfSonicBoomProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColorParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            ColorExplosionParticle particle = new ColorExplosionParticle(level, sprites, x, y, z, options.color());

            particle.quadSize = 2f;
            particle.setSize(2f, 2f);
            particle.setLifetime(4 + random.nextInt(3));

            return particle;
        }
    }

    public static final class GloomBurstProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public GloomBurstProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            ColorExplosionParticle particle = new ColorExplosionParticle(level, sprites, x, y, z, LTXIConstants.GLOOM_BLUE);

            particle.quadSize = 1f;
            particle.setSize(1f, 1f);
            particle.setLifetime(10 + random.nextInt(5));

            return particle;
        }
    }

    public static final class GloomBurstEmitterProvider implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            return new GloomBurstEmitter(level, x, y, z);
        }
    }
}