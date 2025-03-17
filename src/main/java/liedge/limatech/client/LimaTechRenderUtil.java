package liedge.limatech.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

import static liedge.limacore.util.LimaMathUtil.toRad;
import static liedge.limatech.LimaTechConstants.HOSTILE_ORANGE;
import static liedge.limatech.LimaTechConstants.LIME_GREEN;

public final class LimaTechRenderUtil
{
    private static final float ANIMATION_B_FACTOR = 1 / 0.9f;
    private static final float ANIMATION_C_FACTOR = 1 / 0.7f;

    public static final Direction[] ALL_SIDES = Direction.values();

    private LimaTechRenderUtil() {}

    public static float animationCurveSin(float delta)
    {
        return Mth.sin(Mth.PI * delta);
    }

    public static float animationCurveA(float delta)
    {
        return delta <= 0.2f ? delta / 0.2f : 1 - ((delta - 0.2f) * 1.25f);
    }

    public static float animationCurveB(float delta)
    {
        return delta <= 0.1f ? delta / 0.1f : 1 - ((delta - 0.1f) * ANIMATION_B_FACTOR);
    }

    public static float animationCurveC(float delta)
    {
        return delta <= 0.3f ? delta / 0.3f : 1 - ((delta - 0.3f) * ANIMATION_C_FACTOR);
    }

    public static void renderLockOnIndicatorOnEntity(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, double xOffset, double yOffset, double zOffset, float partialTick, float lerpLockProgress)
    {
        poseStack.pushPose();

        VertexConsumer buffer = bufferSource.getBuffer(LimaTechRenderTypes.LOCK_ON_INDICATOR);
        Matrix4f mx4 = poseStack.last().pose();

        double[] pos = lerpEntityCenter(entity, xOffset, yOffset, zOffset, partialTick);
        float size = (float) entity.getBoundingBox().getSize();

        poseStack.translate(pos[0], pos[1], pos[2]);
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));

        poseStack.scale(size, size, size);
        float f0 = (Util.getMillis() % 5000L) / 5000f;
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.wrapDegrees(f0 * 360f)));

        float f1 = -0.6875f - (2f * (1 - lerpLockProgress));
        LimaColor color = lerpLockProgress >= 1 ? LIME_GREEN : HOSTILE_ORANGE;

        drawLockOnQuad(buffer, mx4, f1, 0, color);
        drawLockOnQuad(buffer, mx4, f1, 120, color);
        drawLockOnQuad(buffer, mx4, f1, 240, color);

        poseStack.popPose();
    }

    public static double[] lerpEntityCenter(Entity entity, double x0, double y0, double z0, float partialTick)
    {
        double x = Mth.lerp(partialTick, entity.xo - x0, entity.getX() - x0);
        double y = Mth.lerp(partialTick, entity.yo - y0, entity.getY() - y0) + (entity.getBoundingBox().getYsize() / 2d);
        double z = Mth.lerp(partialTick, entity.zo - z0, entity.getZ() - z0);

        return new double[] {x, y, z};
    }

    public static void renderPositionColorCuboid(VertexConsumer buffer, Matrix4f mx4, float x1, float y1, float z1, float x2, float y2, float z2, LimaColor color, float alpha, Direction[] sides)
    {
        for (Direction side : sides)
        {
            switch (side)
            {
                case UP ->
                {
                    buffer.addVertex(mx4, x2, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case DOWN ->
                {
                    buffer.addVertex(mx4, x1, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case WEST ->
                {
                    buffer.addVertex(mx4, x1, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case EAST ->
                {
                    buffer.addVertex(mx4, x2, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case SOUTH ->
                {
                    buffer.addVertex(mx4, x1, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case NORTH ->
                {
                    buffer.addVertex(mx4, x2, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                }
            }
        }
    }

    private static void drawLockOnQuad(VertexConsumer buffer, Matrix4f mx4, float yOffset, int angle, LimaColor color)
    {
        float centerX = yOffset * Mth.cos(toRad(angle));
        float centerY = yOffset * Mth.sin(toRad(angle));

        float[] vtx = {
                -0.5f, -0.5f,
                -0.5f, 0.5f,
                0.5f, 0.5f,
                0.5f, -0.5f
        };

        float cos = Mth.cos(toRad(angle) - Mth.HALF_PI);
        float sin = Mth.sin(toRad(angle) - Mth.HALF_PI);

        for (int i = 0; i < vtx.length; i += 2)
        {
            float x = vtx[i] ;
            float y = vtx[i + 1];

            vtx[i] = (x * cos - y * sin) + centerX;
            vtx[i + 1] = (x * sin + y * cos) + centerY;
        }

        buffer.addVertex(mx4, vtx[0], vtx[1], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(0, 0);
        buffer.addVertex(mx4, vtx[2], vtx[3], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(0, 1);
        buffer.addVertex(mx4, vtx[4], vtx[5], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(1, 1);
        buffer.addVertex(mx4, vtx[6], vtx[7], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(1, 0);
    }
}