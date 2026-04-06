package liedge.ltxindustries.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.CustomGeometryParticle;
import liedge.limacore.client.particle.CustomGeometryParticleEntry;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

public class LightfragTracerParticle extends CustomGeometryParticle
{
    private static final float LIGHTFRAG_HALF_SIZE = 0.015625f;

    private final float xRot;
    private final float yRot;
    private final LimaColor color;
    private final float trailLength;

    private LightfragTracerParticle(ClientLevel level, Vec3 start, Vec3 end, LimaColor color, float tracerDistance)
    {
        super(level, start.x, start.y, start.z);

        Vector2f angles = LimaCoreMath.xyRotBetweenPoints(start, end); // Using old start doesn't matter for angles
        int n = LimaCoreMath.round(tracerDistance / 20d, LimaRoundingMode.NATURAL) * 2;
        this.lifetime = Mth.clamp(n, 2, 12);

        this.trailLength = 0.750f * lifetime;
        Vec3 motion = LimaCoreMath.createMotionVector(angles, 1d); // Normalized motion vector

        // Move start point to offset trail length
        start = start.add(motion.scale(trailLength));
        setPos(start.x, start.y, start.z);

        // Get the final motion vector with trail length taken into account
        motion = motion.scale(LimaCoreMath.divideDouble(tracerDistance - trailLength, lifetime));
        setParticleSpeed(motion.x, motion.y, motion.z);

        this.xRot = angles.x;
        this.yRot = -angles.y;
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

    @Override
    public @Nullable CustomGeometryParticleEntry extractEntry(float x, float y, float z, Camera camera, float partialTick)
    {
        if (age == 0) return null;

        float alpha1, alpha2;
        if (age == 1)
        {
            alpha1 = partialTick;
            alpha2 = partialTick * 0.4f;
        }
        else
        {
            alpha1 = 1f;
            alpha2 = 0.4f;
        }

        return new Entry(x, y, z, xRot, yRot, trailLength, color, alpha1, alpha2);
    }

    private record Entry(float x, float y, float z, float xRot, float yRot, float length, LimaColor color, float alpha1, float alpha2) implements CustomGeometryParticleEntry
    {
        @Override
        public RenderType renderType()
        {
            return RenderTypes.lightning();
        }

        @Override
        public void render(PoseStack.Pose pose, VertexConsumer consumer)
        {
            pose.rotate(Axis.YP.rotationDegrees(yRot));
            pose.rotate(Axis.XP.rotationDegrees(xRot));

            LTXIRenderer.submitUnlitCuboid(pose, consumer, Direction.values(), -LIGHTFRAG_HALF_SIZE, -LIGHTFRAG_HALF_SIZE, 0f, LIGHTFRAG_HALF_SIZE, LIGHTFRAG_HALF_SIZE, 0.125f, color, alpha1);
            LTXIRenderer.submitUnlitCuboid(pose, consumer, Direction.values(), -LIGHTFRAG_HALF_SIZE, -LIGHTFRAG_HALF_SIZE, -length, LIGHTFRAG_HALF_SIZE, LIGHTFRAG_HALF_SIZE, 0f, color, alpha2);
        }
    }

    public static final class Provider implements ParticleProvider<ColorParticleOptions>
    {
        @Override
        public @Nullable Particle createParticle(ColorParticleOptions particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            Vec3 start = new Vec3(x, y, z);
            Vec3 end = new Vec3(xSpeed, ySpeed, zSpeed);
            float tracerDistance = (float) start.distanceTo(end);

            if (tracerDistance >= 2f && tracerDistance <= 215f) // Longest weapon range is 200
            {
                return new LightfragTracerParticle(level, start, end, particleType.color(), tracerDistance);
            }
            else
            {
                return null;
            }
        }
    }
}