package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import static liedge.limacore.util.LimaMathUtil.*;

public class LightfragTracerParticle extends CustomRenderTypeParticle
{
    private static final float TRACER_SIZE = 0.015625f;

    private final Quaternionf yRot;
    private final Quaternionf xRot;

    public LightfragTracerParticle(SimpleParticleType type, ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        super(level, x1, y1, z1);

        Vector2f angles = xyRotBetweenPoints(x1, y1, z1, x2, y2, z2);
        this.yRot = Axis.YP.rotationDegrees(-angles.y);
        this.xRot = Axis.XP.rotationDegrees(angles.x);
        this.hasPhysics = false;
        double distance = distanceBetween(x1, y1, z1, x2, y2, z2);
        this.lifetime = distance < 3.0d ? 1 : 2;
        double step = divideDouble(distance, lifetime);
        Vec3 path = createMotionVector(angles, step);
        setParticleSpeed(path.x, path.y, path.z);
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;

        if (age++ >= lifetime)
        {
            remove();
        }
        else
        {
            move(xd, yd, zd);
        }
    }

    @Override
    protected void renderParticle(VertexConsumer buffer, Matrix4f mx4, Camera camera, float partialTicks)
    {
        mx4.rotate(yRot);
        mx4.rotate(xRot);

        float alpha = age == 0 ? partialTicks : 1f;

        LimaTechRenderUtil.renderPositionColorCuboid(buffer, mx4, -TRACER_SIZE, -TRACER_SIZE, -0.0625f, TRACER_SIZE, TRACER_SIZE, 0.0625f, LimaTechConstants.LIME_GREEN, alpha, LimaTechRenderUtil.ALL_SIDES);
    }

    @Override
    protected RenderType getCustomRenderType()
    {
        return LimaTechRenderTypes.POSITION_COLOR_QUADS;
    }
}