package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class AcidDripParticle extends SingleQuadParticle
{
    private final boolean createParticleOnLand;

    private AcidDripParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, boolean createParticleOnLand)
    {
        super(level, x, y, z, sprite);

        this.createParticleOnLand = createParticleOnLand;
        this.lifetime = createParticleOnLand ? random.nextIntBetweenInclusive(15, 30) : random.nextIntBetweenInclusive(10, 16);
        setSize(0.01f, 0.01f);
        LimaCoreClientUtil.setQuadParticleColor(this, LTXIConstants.ACID_GREEN);
    }

    @Override
    protected Layer getLayer()
    {
        return Layer.OPAQUE;
    }

    @Override
    protected int getLightCoords(float partialTick)
    {
        return 240;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (age++ >= lifetime)
        {
            remove();
            return;
        }

        move(xd, yd, zd);

        if (createParticleOnLand && onGround)
        {
            level.addParticle(LTXIParticles.ACID_LAND.get(), x, y, z, 0, 0, 0);
            remove();
            return;
        }

        xd *= 0.75d;
        yd = Math.max(-3d, yd - 0.0625d);
        zd *= 0.75d;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;
        private final boolean createParticleOnLand;
        private final boolean applyVelocity;

        public Provider(SpriteSet sprites, boolean createParticleOnLand, boolean applyVelocity)
        {
            this.sprites = sprites;
            this.createParticleOnLand = createParticleOnLand;
            this.applyVelocity = applyVelocity;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            TextureAtlasSprite sprite = sprites.get(random);
            AcidDripParticle particle = new AcidDripParticle(level, x, y, z, sprite, createParticleOnLand);
            if (applyVelocity) particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);

            return particle;
        }
    }
}