package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.LTXIConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class BigColorSmokeParticle extends CampfireSmokeParticle
{
    private BigColorSmokeParticle(ClientLevel level, TextureAtlasSprite sprite, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, false, sprite);
        this.friction = 0.96f;
        this.alpha = 0.9f;
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;

        if (age++ > lifetime)
        {
            remove();
        }
        else
        {
            xd *= friction;
            zd *= friction;
            yd -= gravity;

            move(xd, yd, zd);

            if (age > lifetime / 2)
            {
                float alphaChange = (age - lifetime / 2f) / (float) lifetime;
                setAlpha(1.0f - alphaChange);
            }
        }
    }

    @Override
    protected int getLightCoords(float a)
    {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static final class NeuroSmokeProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public NeuroSmokeProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            BigColorSmokeParticle particle = new BigColorSmokeParticle(level, sprites.get(random), x, y, z, xSpeed, ySpeed, zSpeed);
            particle.lifetime = 30 + random.nextInt(20);
            particle.quadSize *= 8f + random.nextFloat() * 6f;
            LimaCoreClientUtil.setQuadParticleColor(particle, LTXIConstants.NEURO_BLUE);

            return particle;
        }
    }
}