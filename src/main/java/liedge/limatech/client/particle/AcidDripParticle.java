package liedge.limatech.client.particle;

import liedge.limacore.client.particle.LimaParticleUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.registry.game.LimaTechParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class AcidDripParticle extends TextureSheetParticle
{
    public static TextureSheetParticle corrosiveDripParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        return new AcidDripParticle(level, true, x, y, z, 0, 0, 0);
    }

    public static TextureSheetParticle createFallParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        return new AcidDripParticle(level, true, x, y, z, dx, dy, dz);
    }

    public static TextureSheetParticle createLandParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        return new AcidDripParticle(level, false, x, y, z, dx, dy, dz);
    }

    private final boolean createLandingParticle;

    private AcidDripParticle(ClientLevel level, boolean createLandingParticle, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z);
        this.createLandingParticle = createLandingParticle;
        this.lifetime = createLandingParticle ? random.nextIntBetweenInclusive(15, 30) : random.nextIntBetweenInclusive(10, 16);
        setSize(0.01f, 0.01f);
        setParticleSpeed(dx, dy, dz);
        LimaParticleUtil.setColor(this, LimaTechConstants.ACID_GREEN);
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getLightColor(float partialTick)
    {
        return 240;
    }

    @Override
    public void tick()
    {
        this.xo = x;
        this.yo = y;
        this.zo = z;

        if (age++ >= lifetime)
        {
            remove();
            return;
        }

        move(xd, yd, zd);

        if (createLandingParticle && onGround)
        {
            level.addParticle(LimaTechParticles.ACID_LAND.get(), x, y, z, xd, yd, zd);
            remove();
        }

        xd *= 0.75d;
        yd = Math.max(-3d, yd - 0.065d);
        zd *= 0.75d;
    }
}