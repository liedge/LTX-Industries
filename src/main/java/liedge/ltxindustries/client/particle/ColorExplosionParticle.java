package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.LimaColor;
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

    private ColorExplosionParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, LimaColor color)
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
        private final LimaColor color;
        private final float radius;
        private final int density;

        private BaseEmitter(ClientLevel level, double x, double y, double z, LimaColor color, float radius, int density, int lifetime)
        {
            super(level, x, y, z);

            this.color = color;
            this.radius = radius;
            this.density = density;
            this.lifetime = lifetime;
        }

        abstract void spawnParticle(double x, double y, double z, LimaColor color);

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

    private static class SonicBoomEmitter extends BaseEmitter
    {
        private SonicBoomEmitter(ClientLevel level, double x, double y, double z, LimaColor color, float radius)
        {
            super(level, x, y, z, color, radius, 6, 4);
        }

        @Override
        void spawnParticle(double x, double y, double z, LimaColor color)
        {
            level.addAlwaysVisibleParticle(new ColorParticleOptions(LTXIParticles.COLOR_HALF_SONIC_BOOM, color), x, y, z, 0, 0, 0);
        }
    }

    private static class GloomBurstEmitter extends BaseEmitter
    {
        private GloomBurstEmitter(ClientLevel level, double x, double y, double z)
        {
            super(level, x, y, z, LimaColor.WHITE, 4.5f, 8, 5);
        }

        @Override
        void spawnParticle(double x, double y, double z, LimaColor color)
        {
            level.addAlwaysVisibleParticle(LTXIParticles.GLOOM_BURST.get(), x, y, z, 0, 0, 0);
        }
    }

    // Providers

    public static final class SonicBoomProvider implements ParticleProvider<ColorParticleOptions>
    {
        private final SpriteSet sprites;
        private final boolean fullDuration;

        public SonicBoomProvider(SpriteSet sprites, boolean fullDuration)
        {
            this.sprites = sprites;
            this.fullDuration = fullDuration;
        }

        @Override
        public Particle createParticle(ColorParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            ColorExplosionParticle particle = new ColorExplosionParticle(level, sprites, x, y, z, options.color());

            particle.quadSize = 1.275f;
            particle.setSize(1.275f, 1.275f);
            particle.setLifetime(fullDuration ? 16 : 4 + random.nextInt(3));

            return particle;
        }
    }

    public static final class SonicBoomEmitterProvider implements ParticleProvider<ColorSizeParticleOptions>
    {
        @Override
        public Particle createParticle(ColorSizeParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            return new SonicBoomEmitter(level, x, y, z, options.color(), options.size());
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