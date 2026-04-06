package liedge.ltxindustries.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import liedge.ltxindustries.client.renderer.BubbleShieldRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class LTXIRenderer
{
    public static final ContextKey<Boolean> SHOW_WONDERLAND_WINGS = LTXIndustries.RESOURCES.contextKey("show_wings");
    public static final ContextKey<BubbleShieldRenderer.RenderState> BUBBLE_SHIELD_STATE = LTXIndustries.RESOURCES.contextKey("shield_state");

    private LTXIRenderer() {}

    public static float facingYRotation(Direction facing)
    {
        return switch (facing)
        {
            case SOUTH -> 180f;
            case EAST -> -90f;
            case WEST -> 90f;
            default -> 0f;
        };
    }

    public static float sineAnimationCurve(float delta)
    {
        return Mth.sin(Mth.PI * delta);
    }

    public static float linearThresholdCurve(float delta, float threshold)
    {
        if (delta <= threshold)
        {
            return delta / threshold;
        }
        else
        {
            float slope = 1f / (1f - threshold);
            return 1f - (delta - threshold) * slope;
        }
    }

    public static float animationCurveA(float delta)
    {
        return linearThresholdCurve(delta, 0.2f);
    }

    public static float animationCurveB(float delta)
    {
        return linearThresholdCurve(delta, 0.1f);
    }

    //#region Rings
    private static float lerpArc(int i, int iMax, float start, float end)
    {
        return Mth.lerp(LimaCoreMath.divideFloat(i, iMax), start, end);
    }

    private static void submitArcSegment(PoseStack.Pose pose, VertexConsumer buffer, float innerRadius, float outerRadius, float a1, float a2, float red, float green, float blue, float alpha)
    {
        float cos1 = Mth.cos(a1);
        float sin1 = Mth.sin(a1);
        float cos2 = Mth.cos(a2);
        float sin2 = Mth.sin(a2);

        buffer.addVertex(pose, cos1 * innerRadius, sin1 * innerRadius, 0).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, cos2 * innerRadius, sin2 * innerRadius, 0).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, cos1 * outerRadius, sin1 * outerRadius, 0).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, cos1 * outerRadius, sin1 * outerRadius, 0).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, cos2 * innerRadius, sin2 * innerRadius, 0).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, cos2 * outerRadius, sin2 * outerRadius, 0).setColor(red, green, blue, alpha);
    }

    public static void renderArcRing(PoseStack.Pose pose, VertexConsumer buffer, float radius, float width, float startAngle, float endAngle, int segments, LimaColor color, float alpha)
    {
        if (endAngle <= startAngle || segments < 1) return;

        float arcStart = LimaCoreMath.toRad(startAngle);
        float arcEnd = LimaCoreMath.toRad(endAngle);
        float outerRadius = radius + width;

        for (int i = 0; i < segments; i++)
        {
            float a1 = lerpArc(i, segments, arcStart, arcEnd);
            float a2 = lerpArc(i + 1, segments, arcStart, arcEnd);
            submitArcSegment(pose, buffer, radius, outerRadius, a1, a2, color.red(), color.green(), color.blue(), alpha);
        }
    }

    public static void renderArcRing(PoseStack.Pose pose, VertexConsumer buffer, float radius, float width, float startAngle, float endAngle, int segments, LimaColor color)
    {
        renderArcRing(pose, buffer, radius, width, startAngle, endAngle, segments, color, 1f);
    }

    public static void renderGUIArcRing(PoseStack poseStack, VertexConsumer buffer, float radius, float width, float startAngle, float endAngle, float segmentDegrees, LimaColor color)
    {
        if (endAngle <= startAngle || segmentDegrees < 1) return;

        float arcStart = LimaCoreMath.toRad(startAngle);
        float arcEnd = LimaCoreMath.toRad(endAngle);
        float segLength = LimaCoreMath.toRad(segmentDegrees);
        int segments = Mth.ceil((arcEnd - arcStart) / segLength);
        float outerRadius = radius + width;

        Matrix4f mx4 = poseStack.last().pose();
        for (int i = 0; i < segments; i++)
        {
            float a1 = arcStart + i * segLength;
            float a2 = Math.min(arcEnd, arcStart + (i + 1) * segLength);
            //if (a2 > a1) renderArcSegment(buffer, mx4, radius, outerRadius, a1, a2, color.red(), color.green(), color.blue(), 1f);
        }
    }
    //#endregion
    
    //#region Cuboids
    public static void submitUnlitCuboidFace(PoseStack.Pose pose, VertexConsumer buffer, Direction side, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha)
    {
        switch (side)
        {
            case UP ->
            {
                buffer.addVertex(pose, x1, y2, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y2, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y2, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y2, z1).setColor(red, green, blue, alpha);
            }
            case DOWN ->
            {
                buffer.addVertex(pose, x1, y1, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y1, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y1, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y1, z2).setColor(red, green, blue, alpha);
            }
            case NORTH ->
            {
                buffer.addVertex(pose, x2, y1, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y1, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y2, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y2, z1).setColor(red, green, blue, alpha);
            }
            case SOUTH ->
            {
                buffer.addVertex(pose, x2, y2, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y2, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y1, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y1, z2).setColor(red, green, blue, alpha);
            }
            case EAST ->
            {
                buffer.addVertex(pose, x2, y1, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y1, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y2, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x2, y2, z2).setColor(red, green, blue, alpha);
            }
            case WEST ->
            {
                buffer.addVertex(pose, x1, y2, z2).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y2, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y1, z1).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, x1, y1, z2).setColor(red, green, blue, alpha);
            }
        }
    }

    public static void submitParticleFormatQuad(PoseStack.Pose pose, VertexConsumer buffer, Direction side, float x1, float y1, float z1, float x2, float y2, float z2, float u0, float v0, float u1, float v1, float red, float green, float blue, float alpha, int packedLight)
    {
        switch (side)
        {
            case UP ->
            {
                buffer.addVertex(pose, x1, y2, z2).setUv(u0, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y2, z2).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y2, z1).setUv(u1, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y2, z1).setUv(u0, v0).setColor(red, green, blue, alpha).setLight(packedLight);
            }
            case DOWN ->
            {
                buffer.addVertex(pose, x1, y1, z1).setUv(u0, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y1, z1).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y1, z2).setUv(u1, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y1, z2).setUv(u0, v0).setColor(red, green, blue, alpha).setLight(packedLight);
            }
            case NORTH ->
            {
                buffer.addVertex(pose, x2, y1, z1).setUv(u0, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y1, z1).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y2, z1).setUv(u1, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y2, z1).setUv(u0, v0).setColor(red, green, blue, alpha).setLight(packedLight);
            }
            case SOUTH ->
            {
                buffer.addVertex(pose, x2, y2, z2).setUv(u1, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y2, z2).setUv(u0, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y1, z2).setUv(u0, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y1, z2).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(packedLight);
            }
            case EAST ->
            {
                buffer.addVertex(pose, x2, y1, z2).setUv(u0, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y1, z1).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y2, z1).setUv(u1, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x2, y2, z2).setUv(u0, v0).setColor(red, green, blue, alpha).setLight(packedLight);
            }
            case WEST ->
            {
                buffer.addVertex(pose, x1, y2, z2).setUv(u1, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y2, z1).setUv(u0, v0).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y1, z1).setUv(u0, v1).setColor(red, green, blue, alpha).setLight(packedLight);
                buffer.addVertex(pose, x1, y1, z2).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(packedLight);
            }
        }
    }

    public static void submitUnlitCuboid(PoseStack.Pose pose, VertexConsumer buffer, Direction[] faces, float x1, float y1, float z1, float x2, float y2, float z2, LimaColor color, float alpha)
    {
        for (Direction side : faces)
        {
            submitUnlitCuboidFace(pose, buffer, side, x1, y1, z1, x2, y2, z2, color.red(), color.green(), color.blue(), alpha);
        }
    }
    //#endregion

    public static double[] lerpEntityCenter(Entity entity, double x0, double y0, double z0, float partialTick)
    {
        double x = Mth.lerp(partialTick, entity.xo - x0, entity.getX() - x0);
        double y = Mth.lerp(partialTick, entity.yo - y0, entity.getY() - y0) + (entity.getBoundingBox().getYsize() / 2d);
        double z = Mth.lerp(partialTick, entity.zo - z0, entity.getZ() - z0);

        return new double[] {x, y, z};
    }

    public static void submitBoltQuad(PoseStack.Pose pose, VertexConsumer buffer, Vector3f a, Vector3f b, Vector3f c, Vector3f d, LimaColor color, float alpha)
    {
        buffer.addVertex(pose, a.x, a.y, a.z).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(pose, b.x, b.y, b.z).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(pose, c.x, c.y, c.z).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(pose, d.x, d.y, d.z).setColor(color.red(), color.green(), color.blue(), alpha);
    }

    public static void submitEnergyBolt(PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType, EnergyBoltData data, LimaColor color, float alpha)
    {
        nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
        {
            for (Vector3f[] v : data.segments())
            {
                submitBoltQuad(pose, buffer, v[2], v[1], v[0], v[3], color, alpha);
                submitBoltQuad(pose, buffer, v[4], v[5], v[6], v[7], color, alpha);
                submitBoltQuad(pose, buffer, v[7], v[3], v[0], v[4], color, alpha);
                submitBoltQuad(pose, buffer, v[1], v[2], v[6], v[5], color, alpha);
                submitBoltQuad(pose, buffer, v[0], v[1], v[5], v[4], color, alpha);
                submitBoltQuad(pose, buffer, v[6], v[2], v[3], v[7], color, alpha);
            }
        });
    }
}