package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.ltxindustries.LTXIConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class AnimatedGlowParticle extends SimpleAnimatedParticle
{
    private AnimatedGlowParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, float sizeMultiplier)
    {
        super(level, x, y, z, sprites, 0f);
        this.quadSize *= sizeMultiplier;
        setSpriteFromAge(sprites);
    }

    @Override
    public int getLightCoords(float a)
    {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static final class ColorGlitterProvider implements ParticleProvider<ColorSizeParticleOptions>
    {
        private final SpriteSet sprites;

        public ColorGlitterProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColorSizeParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            AnimatedGlowParticle particle = new AnimatedGlowParticle(level, sprites, x, y, z, data.size());
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            particle.lifetime = 20;
            LimaCoreClientUtil.setQuadParticleColor(particle, data.color());

            return particle;
        }
    }

    public static final class ElectricSparkProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public ElectricSparkProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            AnimatedGlowParticle particle = new AnimatedGlowParticle(level, sprites, x, y, z, 0.75f);
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            particle.lifetime = 17;
            particle.alpha = 0.9f;
            particle.friction = 0.85f;
            LimaCoreClientUtil.setQuadParticleColor(particle, LTXIConstants.ELECTRIC_GREEN);

            return particle;
        }
    }

    public static final class SnowflakeProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public SnowflakeProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            return new SnowflakeParticle(level, sprites, x, y, z);
        }
    }

    private static class SnowflakeParticle extends AnimatedGlowParticle
    {
        private SnowflakeParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z)
        {
            super(level, sprites, x, y, z, 0.825f);

            this.friction = 0.95f;
            this.lifetime = random.nextIntBetweenInclusive(60, 120);
        }

        @Override
        public void tick()
        {
            this.xo = x;
            this.yo = y;
            this.zo = z;

            setSprite(sprites.get(age % 20, 20));

            if (age++ > lifetime)
            {
                remove();
            }
            else
            {
                if (!onGround)
                {
                    xd *= friction;
                    yd = Math.max(-0.1d, yd - 0.005d);
                    zd *= friction;
                }
                else
                {
                    lifetime = random.nextIntBetweenInclusive(8, 14);
                }

                move(xd, yd, zd);
            }
        }
    }
}