package liedge.limatech.client.particle;

import liedge.limacore.lib.LimaColor;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class AnimatedGlowParticle extends SimpleAnimatedParticle
{
    public static Particle electricSpark(SimpleParticleType type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        AnimatedGlowParticle particle = new AnimatedGlowParticle(level, spriteSet, 0.75f, OrbGrenadeElement.ELECTRIC.getColor(), x, y, z, dx, dy, dz);
        particle.lifetime = 17;
        particle.alpha = 0.9f;
        particle.friction = 0.85f;
        return particle;
    }

    public static Particle missileTrail(SimpleParticleType type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        AnimatedGlowParticle particle = new AnimatedGlowParticle(level, spriteSet, 1.75f, LimaTechConstants.REM_BLUE, x, y, z, dx, dy, dz);
        particle.lifetime = 20;
        return particle;
    }

    private AnimatedGlowParticle(ClientLevel level, SpriteSet spriteSet, float quadSize, LimaColor color, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z, spriteSet, 0f);
        this.quadSize *= quadSize;
        setParticleSpeed(dx, dy, dz);
        setSpriteFromAge(spriteSet);
        setColor(color.red(), color.green(), color.blue());
    }

    @Override
    public int getLightColor(float partialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }
}