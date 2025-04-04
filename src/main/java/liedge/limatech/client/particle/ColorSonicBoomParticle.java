package liedge.limatech.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.registry.game.LimaTechParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;

public class ColorSonicBoomParticle extends TextureSheetParticle
{
    public static ColorSonicBoomParticle halfSonicBoom(ColorParticleOptions options, ClientLevel level, SpriteSet spriteSet, double x, double y, double z)
    {
        ColorSonicBoomParticle particle = new ColorSonicBoomParticle(options, level, spriteSet, x, y, z);
        particle.lifetime = 4 + particle.random.nextInt(3);
        return particle;
    }

    public static ColorSonicBoomParticle fullSonicBoom(ColorParticleOptions options, ClientLevel level, SpriteSet spriteSet, double x, double y, double z)
    {
        ColorSonicBoomParticle particle = new ColorSonicBoomParticle(options, level, spriteSet, x, y, z);
        particle.lifetime = 16;
        return particle;
    }

    private final SpriteSet spriteSet;

    private ColorSonicBoomParticle(ColorParticleOptions options, ClientLevel level, SpriteSet spriteSet, double x, double y, double z)
    {
        super(level, x, y, z);

        this.spriteSet = spriteSet;
        this.quadSize = 1.28125f;
        this.hasPhysics = false;
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

                level.addAlwaysVisibleParticle(new ColorParticleOptions(LimaTechParticles.COLOR_HALF_SONIC_BOOM, color), true, px, py, pz, 0, 0, 0);
            }
        }
    }
}