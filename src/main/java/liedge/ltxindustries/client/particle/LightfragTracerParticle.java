package liedge.ltxindustries.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.CustomRenderTypeParticle;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

public class LightfragTracerParticle extends CustomRenderTypeParticle
{
    public static @Nullable Particle createLightfragTracer(ColorParticleOptions options, ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        Vec3 start = new Vec3(x1, y1, z1);
        Vec3 end = new Vec3(x2, y2, z2);
        float tracerDistance = (float) start.distanceTo(end);

        if (tracerDistance >= 2f && tracerDistance <= 215f) // Longest weapon range is 200, but this gives us some margin
            return new LightfragTracerParticle(level, start, end, options.color(), tracerDistance);
        else
            return null;
    }

    private static final float LIGHTFRAG_HALF_SIZE = 0.015625f;

    private final Quaternionf xRot;
    private final Quaternionf yRot;
    private final LimaColor color;
    private final float trailLength;

    private LightfragTracerParticle(ClientLevel level, Vec3 start, Vec3 end, LimaColor color, float tracerDistance)
    {
        super(level, start.x, start.y, start.z);

        Vector2f angles = LimaCoreMath.xyRotBetweenPoints(start, end); // Using old start doesn't matter for angles
        int n = LimaCoreMath.round(tracerDistance / 20d, LimaRoundingMode.NATURAL) * 2;
        this.lifetime = Mth.clamp(n, 2, 12);

        //Math.min(Mth.ceil(tracerDistance / 20f), 7);
        this.trailLength = 0.750f * lifetime;
        Vec3 motion = LimaCoreMath.createMotionVector(angles, 1d); // Normalized motion vector

        // Move start point to offset trail length
        start = start.add(motion.scale(trailLength));
        setPos(start.x, start.y, start.z);

        // Get the final motion vector with trail length taken into account
        motion = motion.scale(LimaCoreMath.divideDouble(tracerDistance - trailLength, lifetime));
        setParticleSpeed(motion.x, motion.y, motion.z);

        this.xRot = Axis.XP.rotationDegrees(angles.x);
        this.yRot = Axis.YP.rotationDegrees(-angles.y);
        this.color = color;
        this.hasPhysics = false;
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks)
    {
        return AABB.INFINITE;
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
    protected final void renderParticle(VertexConsumer buffer, Matrix4f mx4, Camera camera, float partialTicks)
    {
        if (age == 0) return;

        mx4.rotate(yRot);
        mx4.rotate(xRot);

        float a1, a2;
        if (age == 1)
        {
            a1 = partialTicks;
            a2 = partialTicks * 0.4f;
        }
        else
        {
            a1 = 1f;
            a2 = 0.4f;
        }

        // Render main 'bolt'
        LTXIRenderUtil.renderPositionColorCuboid(buffer, mx4, -LIGHTFRAG_HALF_SIZE, -LIGHTFRAG_HALF_SIZE, 0f, LIGHTFRAG_HALF_SIZE, LIGHTFRAG_HALF_SIZE, 0.125f, color, a1, LTXIRenderUtil.ALL_SIDES);
        // Render trail
        LTXIRenderUtil.renderPositionColorCuboid(buffer, mx4, -LIGHTFRAG_HALF_SIZE, -LIGHTFRAG_HALF_SIZE, -trailLength, LIGHTFRAG_HALF_SIZE, LIGHTFRAG_HALF_SIZE, 0f, color, a2, LTXIRenderUtil.ALL_SIDES);
    }

    @Override
    protected RenderType getCustomRenderType()
    {
        return LTXIRenderTypes.POSITION_COLOR_QUADS;
    }
}