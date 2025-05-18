package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

public abstract class AbstractTracerParticle extends CustomRenderTypeParticle
{
    private static final float TRACER_QUAD_SIZE = 0.015625f;

    public static @Nullable AbstractTracerParticle createLightfragTracer(ColorEndpointParticleOptions options, ClientLevel level, double x, double y, double z)
    {
        Vec3 start = new Vec3(x, y, z);
        Vec3 end = options.endpoint();
        float tracerDistance = (float) start.distanceTo(end);

        return tracerDistance <= 100 ? new Lightfrag(level, start, end, options.color(), tracerDistance) : null;
    }

    public static @Nullable AbstractTracerParticle createLFRBolt(ColorEndpointParticleOptions options, ClientLevel level, double x, double y, double z)
    {
        Vec3 start = new Vec3(x, y, z);
        Vec3 end = options.endpoint();
        float tracerDistance = (float) start.distanceTo(end);

        return tracerDistance <= 250 ? new LFRBolt(level, start, end, options.color(), tracerDistance) : null;
    }

    private final Quaternionf xRot;
    private final Quaternionf yRot;
    private final LimaColor color;
    final Vector2f angles;

    AbstractTracerParticle(ClientLevel level, Vec3 start, Vec3 end, LimaColor color)
    {
        super(level, start.x, start.y, start.z);
        this.angles = LimaMathUtil.xyRotBetweenPoints(start, end);
        this.xRot = Axis.XP.rotationDegrees(angles.x);
        this.yRot = Axis.YP.rotationDegrees(-angles.y);
        this.color = color;
        this.hasPhysics = false;
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

    protected abstract void renderTracerParticle(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float partialTick);

    @Override
    protected final void renderParticle(VertexConsumer buffer, Matrix4f mx4, Camera camera, float partialTicks)
    {
        mx4.rotate(yRot);
        mx4.rotate(xRot);
        renderTracerParticle(buffer, mx4, color, partialTicks);
    }

    @Override
    protected RenderType getCustomRenderType()
    {
        return LimaTechRenderTypes.POSITION_COLOR_QUADS;
    }

    private static class Lightfrag extends AbstractTracerParticle
    {
        Lightfrag(ClientLevel level, Vec3 start, Vec3 end, LimaColor color, float tracerDistance)
        {
            super(level, start, end, color);
            this.lifetime = tracerDistance <= 3 ? 1 : 2;

            double step = tracerDistance / (double) lifetime;
            Vec3 path = LimaMathUtil.createMotionVector(angles, step);
            setParticleSpeed(path.x, path.y, path.z);
        }

        @Override
        protected void renderTracerParticle(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float partialTick)
        {
            float alpha = age == 0 ? partialTick : 1f;
            LimaTechRenderUtil.renderPositionColorCuboid(buffer, mx4, -TRACER_QUAD_SIZE, -TRACER_QUAD_SIZE, -0.0625f, TRACER_QUAD_SIZE, TRACER_QUAD_SIZE, 0.0625f, color, alpha, LimaTechRenderUtil.ALL_SIDES);
        }
    }

    private static class LFRBolt extends AbstractTracerParticle
    {
        private final float tracerDistance;

        LFRBolt(ClientLevel level, Vec3 start, Vec3 end, LimaColor color, float tracerDistance)
        {
            super(level, start, end, color);
            this.tracerDistance = tracerDistance;
            this.lifetime = 6;
        }

        @Override
        public AABB getRenderBoundingBox(float partialTicks)
        {
            return AABB.INFINITE;
        }

        @Override
        protected void renderTracerParticle(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float partialTick)
        {
            float ageLerp = age < lifetime ? (age + partialTick) / (float) lifetime : 0f;
            LimaTechRenderUtil.renderPositionColorCuboid(buffer, mx4, -TRACER_QUAD_SIZE, -TRACER_QUAD_SIZE, 0, TRACER_QUAD_SIZE, TRACER_QUAD_SIZE, tracerDistance, color, LimaTechRenderUtil.animationCurveB(ageLerp), LimaTechRenderUtil.ALL_SIDES);
        }
    }
}