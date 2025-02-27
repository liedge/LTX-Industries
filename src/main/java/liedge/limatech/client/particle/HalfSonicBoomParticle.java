package liedge.limatech.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.registry.LimaTechParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;

public class HalfSonicBoomParticle extends TextureSheetParticle
{
    private final SpriteSet spriteSet;

    public HalfSonicBoomParticle(ColorParticleOptions options, ClientLevel level, SpriteSet spriteSet, double x, double y, double z)
    {
        super(level, x, y, z);

        this.spriteSet = spriteSet;
        this.quadSize = 1.28125f;
        this.hasPhysics = false;
        this.lifetime = 4 + random.nextInt(3);

        LimaCoreClientUtil.setParticleColor(this, options.color());

        setSpriteFromAge(spriteSet);
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
            setSpriteFromAge(spriteSet);
        }
    }

    @Override
    protected int getLightColor(float pPartialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class EmitterParticle extends NoRenderParticle
    {
        private final LimaColor color;

        public EmitterParticle(ColorParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
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

                level.addAlwaysVisibleParticle(new ColorParticleOptions(LimaTechParticles.HALF_SONIC_BOOM, color), true, px, py, pz, 0, 0, 0);
            }
        }
    }
}