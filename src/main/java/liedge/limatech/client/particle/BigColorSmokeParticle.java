package liedge.limatech.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.LimaTechConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class BigColorSmokeParticle extends CampfireSmokeParticle
{
    public static Particle neuroSmokeParticle(SimpleParticleType particleType, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        BigColorSmokeParticle particle = new BigColorSmokeParticle(level, spriteSet, LimaTechConstants.NEURO_BLUE, 1f, x, y, z, dx, dy, dz);
        float size = 8f + particle.random.nextFloat() * 6f;
        particle.lifetime = 30 + particle.random.nextInt(20);
        particle.quadSize *= size;
        return particle;
    }

    private BigColorSmokeParticle(ClientLevel level, SpriteSet spriteSet, LimaColor color, float size, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z, dx, dy, dz, false);
        float originalSize = quadSize / 3f;
        this.quadSize = originalSize * size;
        this.friction = 0.96f;
        this.lifetime = 60 + random.nextInt(40);
        LimaCoreClientUtil.setParticleColor(this, color);
        setAlpha(0.9f);
        pickSprite(spriteSet);
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
    protected int getLightColor(float partialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }
}