package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.RandomSource;

public class ColorSonicBoomParticle extends SingleQuadParticle
{
    private final SpriteSet sprites;

    private ColorSonicBoomParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, LimaColor color)
    {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.quadSize = 1.28125f;
        this.hasPhysics = false;

        LimaCoreClientUtil.setQuadParticleColor(this, color);
        setSpriteFromAge(sprites);
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
    protected int getLightColor(float pPartialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    protected Layer getLayer()
    {
        return Layer.OPAQUE;
    }

    private static class Emitter extends NoRenderParticle
    {
        private final LimaColor color;

        public Emitter(ColorParticleOptions options, ClientLevel level, double x, double y, double z)
        {
            super(level, x, y, z);
            this.lifetime = 3;
            this.color = options.color();
        }

        @Override
        public void tick()
        {
            if (age++ >= lifetime)
            {
                remove();
            }

            for (int i = 0; i < 3; i++)
            {
                double px = x + (random.nextDouble() - random.nextDouble()) * 4.25d;
                double py = y + (random.nextDouble() - random.nextDouble()) * 4.25d;
                double pz = z + (random.nextDouble() - random.nextDouble()) * 4.25d;

                level.addAlwaysVisibleParticle(new ColorParticleOptions(LTXIParticles.COLOR_HALF_SONIC_BOOM, color), true, px, py, pz, 0, 0, 0);
            }
        }
    }

    public static final class Provider implements ParticleProvider<ColorParticleOptions>
    {
        private final SpriteSet sprites;
        private final boolean fullDuration;

        public Provider(SpriteSet sprites, boolean fullDuration)
        {
            this.sprites = sprites;
            this.fullDuration = fullDuration;
        }

        @Override
        public Particle createParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            ColorSonicBoomParticle particle = new ColorSonicBoomParticle(level, sprites, x, y, z, data.color());
            particle.lifetime = fullDuration ? 16 : 4 + random.nextInt(3);

            return particle;
        }
    }

    public static final class EmitterProvider implements ParticleProvider<ColorParticleOptions>
    {
        @Override
        public Particle createParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            return new Emitter(data, level, x, y, z);
        }
    }
}