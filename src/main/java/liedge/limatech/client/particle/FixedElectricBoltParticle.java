package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.model.custom.EnergyBoltModel;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class FixedElectricBoltParticle extends CustomRenderTypeParticle
{
    private final EnergyBoltModel boltA;
    private final EnergyBoltModel boltB;

    private EnergyBoltModel boltToRender;

    public FixedElectricBoltParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z);

        this.lifetime = random.nextIntBetweenInclusive(6, 12);
        this.hasPhysics = false;
        this.gravity = 0;
        this.friction = 1;

        double sizeDim = LimaMathUtil.distanceBetween(x, y, z, dx, dy, dz);
        setBoundingBox(AABB.ofSize(getPos(), sizeDim, sizeDim, sizeDim));

        this.boltA = EnergyBoltModel.twoFixedPointBolt(x, y, z, dx, dy, dz, 0.015625f);
        this.boltB = EnergyBoltModel.twoFixedPointBolt(x, y, z, dx, dy, dz, 0.015625f);
        this.boltToRender = boltA;
    }

    @Override
    public void tick()
    {
        if (age++ >= lifetime)
        {
            remove();
        }

        boltToRender = ((age & 1) == 0) ? boltA : boltB;
    }

    @Override
    protected void renderParticle(VertexConsumer buffer, Matrix4f mx4, Camera camera, float partialTicks)
    {
        boltToRender.renderEnergyBolt(buffer, mx4, LimaTechConstants.ELECTRIC_GREEN, 0.85f);
    }

    @Override
    protected RenderType getCustomRenderType()
    {
        return RenderType.lightning();
    }
}