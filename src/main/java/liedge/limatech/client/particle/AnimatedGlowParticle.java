package liedge.limatech.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.LimaTechConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class AnimatedGlowParticle extends SimpleAnimatedParticle
{
    public static Particle colorGlitter(ColorSizeParticleOptions options, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        AnimatedGlowParticle particle = new AnimatedGlowParticle(level, spriteSet, options.size(), options.color(), x, y, z, dx, dy, dz);
        particle.lifetime = 20;
        return particle;
    }

    public static Particle electricSpark(SimpleParticleType type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        AnimatedGlowParticle particle = new AnimatedGlowParticle(level, spriteSet, 0.75f, LimaTechConstants.ELECTRIC_GREEN, x, y, z, dx, dy, dz);
        particle.lifetime = 17;
        particle.alpha = 0.9f;
        particle.friction = 0.85f;
        return particle;
    }

    public static Particle cryoSnowflake(SimpleParticleType type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        return new SnowflakeParticle(level, spriteSet, 0.825f, LimaColor.WHITE, x, y, z, 0, 0, 0);
    }

    private AnimatedGlowParticle(ClientLevel level, SpriteSet spriteSet, float quadSize, LimaColor color, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z, spriteSet, 0f);
        this.quadSize *= quadSize;
        setParticleSpeed(dx, dy, dz);
        setSpriteFromAge(spriteSet);
        LimaCoreClientUtil.setParticleColor(this, color);
    }

    @Override
    public int getLightColor(float partialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }

    private static class SnowflakeParticle extends AnimatedGlowParticle
    {
        private SnowflakeParticle(ClientLevel level, SpriteSet spriteSet, float quadSize, LimaColor color, double x, double y, double z, double dx, double dy, double dz)
        {
            super(level, spriteSet, quadSize, color, x, y, z, dx, dy, dz);
            this.friction = 0.95f;
            this.lifetime = random.nextIntBetweenInclusive(60, 120);

            setSpriteFromAge(spriteSet);
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