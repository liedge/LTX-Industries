package liedge.ltxindustries.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import liedge.ltxindustries.client.model.custom.EnergyBoltModel;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class FixedElectricBoltParticle extends CustomRenderTypeParticle
{
    public static @Nullable FixedElectricBoltParticle create(ColorParticleOptions options, ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double length = LimaMathUtil.distanceBetween(x1, y1, z1, x2, y2, z2);
        return length <= 100 ? new FixedElectricBoltParticle(level, x1, y1, z1, x2, y2, z2, options.color(), length) : null;
    }

    private final LimaColor boltColor;
    private final EnergyBoltModel boltA;
    private final EnergyBoltModel boltB;

    private EnergyBoltModel boltToRender;

    private FixedElectricBoltParticle(ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2, LimaColor boltColor, double length)
    {
        super(level, x1, y1, z1);

        this.boltColor = boltColor;
        this.boltA = EnergyBoltModel.twoFixedPointBolt(x1, y1, z1, x2, y2, z2, 0.015625f);
        this.boltB = EnergyBoltModel.twoFixedPointBolt(x1, y1, z1, x2, y2, z2, 0.015625f);
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