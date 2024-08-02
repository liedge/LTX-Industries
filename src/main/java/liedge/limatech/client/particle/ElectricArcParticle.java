package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.client.model.custom.EnergyBoltModel;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

import static liedge.limatech.client.model.custom.EnergyBoltModel.multiPointBolt;

public class ElectricArcParticle extends CustomRenderTypeParticle
{
    private final EnergyBoltModel boltA;
    private final EnergyBoltModel boltB;

    private float agePercentage = 0f;
    private EnergyBoltModel boltToRender;

    public ElectricArcParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z);

        this.lifetime = 8;
        this.hasPhysics = false;
        this.gravity = 0;
        this.friction = 1;

        int segments = 3 + (random.nextBoolean() ? 1 : 0);

        List<Vec3> segmentPoints = new ObjectArrayList<>();
        for (int i = 0; i < segments; i++)
        {
            float segmentLength = 2.0f + random.nextFloat();
            float px = (random.nextFloat() - random.nextFloat()) * segmentLength;
            float py = (random.nextFloat() - random.nextFloat()) * segmentLength;
            float pz = (random.nextFloat() - random.nextFloat()) * segmentLength;
            segmentPoints.add(new Vec3(px, py, pz));
        }

        double biggestLength = segmentPoints.stream().mapToDouble(v -> v.distanceToSqr(0, 0, 0)).max().orElseThrow();
        setBoundingBox(AABB.ofSize(getPos(), biggestLength, biggestLength, biggestLength));

        this.boltA = multiPointBolt(segmentPoints, 0.015625f);
        this.boltB = multiPointBolt(segmentPoints, 0.015625f);
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
        agePercentage = LimaMathUtil.divideFloat(age, lifetime);
    }

    @Override
    protected void renderParticle(VertexConsumer buffer, Matrix4f mx4, Camera camera, float partialTicks)
    {
        boltToRender.renderPartialBolt(buffer, mx4, agePercentage, OrbGrenadeElement.ELECTRIC.getColor(), 0.9f);
    }

    @Override
    protected RenderType getCustomRenderType()
    {
        return RenderType.lightning();
    }
}