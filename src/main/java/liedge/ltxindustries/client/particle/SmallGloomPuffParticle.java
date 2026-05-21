package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.LTXIConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public final class SmallGloomPuffParticle extends SpellParticle
{
    private SmallGloomPuffParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, SpriteSet sprites)
    {
        super(level, x, y, z, xa, ya, za, sprites);

        LimaCoreClientUtil.setQuadParticleColor(this, LTXIConstants.GLOOM_BLUE);
    }

    @Override
    protected int getLightCoords(float a)
    {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            return new SmallGloomPuffParticle(level, x, y, z, xAux, yAux, zAux, this.sprites);
        }
    }
}