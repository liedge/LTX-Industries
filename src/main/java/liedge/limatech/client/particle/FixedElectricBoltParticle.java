package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.client.model.custom.EnergyBoltModel;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class FixedElectricBoltParticle extends CustomRenderTypeParticle
{
    public static @Nullable FixedElectricBoltParticle create(ColorEndpointParticleOptions options, ClientLevel level, double x, double y, double z)
    {
        Vec3 endpoint = options.endpoint();

        double length = LimaMathUtil.distanceBetween(x, y, z, endpoint.x, endpoint.y, endpoint.z);
        if (length >= 100) return null;

        return new FixedElectricBoltParticle(level, x, y, z, endpoint,options.color(), length);
    }

    private final LimaColor boltColor;
    private final EnergyBoltModel boltA;
    private final EnergyBoltModel boltB;

    private EnergyBoltModel boltToRender;

    private FixedElectricBoltParticle(ClientLevel level, double x, double y, double z, Vec3 endpoint, LimaColor boltColor, double length)
    {
        super(level, x, y, z);

        this.boltColor = boltColor;
        this.boltA = EnergyBoltModel.twoFixedPointBolt(x, y, z, endpoint.x, endpoint.y, endpoint.z, 0.015625f);
        this.boltB = EnergyBoltModel.twoFixedPointBolt(x, y, z, endpoint.x, endpoint.y, endpoint.z, 0.015625f);
        this.boltToRender = boltA;

        setBoundingBox(AABB.ofSize(getPos(), length, length, length));
        this.lifetime = random.nextIntBetweenInclusive(6, 12);
        this.hasPhysics = false;
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
        boltToRender.renderEnergyBolt(buffer, mx4, boltColor, 0.85f);
    }

    @Override
    protected RenderType getCustomRenderType()
    {
        return RenderType.lightning();
    }
}