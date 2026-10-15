package liedge.ltxindustries.client.particle;

import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class SnowflakeParticle extends SimpleAnimatedParticle
{
    private SnowflakeParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z)
    {
        super(level, x, y, z, sprites, 0.00625f);

        this.friction = 0.75f;
        this.lifetime = random.nextIntBetweenInclusive(60, 120);
        this.quadSize *= 0.825f;
    }

    @Override
    public int getLightCoords(float a)
    {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    public void tick()
    {
        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.oRoll = roll;
        this.roll += LimaCoreMath.toRad(2.5f);

        setSprite(sprites.get(age % 20, 20));

        if (age++ > lifetime)
        {
            remove();
        }
        else
        {
            yd -= 0.005d;

            move(xd, yd, zd);

            if (!onGround)
            {
                xd *= friction;
                yd *= friction;
                zd *= friction;
            }
            else
            {
                lifetime = random.nextIntBetweenInclusive(8, 14);
            }
        }
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;
        private final boolean applySpeed;

        public Provider(SpriteSet sprites, boolean applySpeed)
        {
            this.sprites = sprites;
            this.applySpeed = applySpeed;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            SnowflakeParticle particle = new SnowflakeParticle(level, sprites, x, y, z);
            if (applySpeed) particle.setParticleSpeed(xAux, yAux, zAux);

            return particle;
        }
    }
}