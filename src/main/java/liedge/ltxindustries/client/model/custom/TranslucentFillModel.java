package liedge.ltxindustries.client.model.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.baked.BakedRotation;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public record TranslucentFillModel(Vector3f from, Vector3f to, float length, Direction.Axis drainAxis, @Nullable BakedRotation rotation)
{
    public static TranslucentFillModel create(float x1, float y1, float z1, float x2, float y2, float z2, Direction.Axis drainAxis, @Nullable BakedRotation rotation)
    {
        Vector3f from = new Vector3f(x1, y1, z1).mul(0.0625f);
        Vector3f to = new Vector3f(x2, y2, z2).mul(0.0625f);
        float length = (float) Math.abs(drainAxis.choose(from.x, from.y, from.z) - drainAxis.choose(to.x, to.y, to.z));
        return new TranslucentFillModel(from, to, length, drainAxis, rotation);
    }

    public static TranslucentFillModel create(float x1, float y1, float z1, float x2, float y2, float z2, Direction.Axis drainAxis)
    {
        return create(x1, y1, z1, x2, y2, z2, drainAxis, null);
    }

    public void render(VertexConsumer buffer, PoseStack poseStack, LimaColor fillColor, float fillPercentage)
    {
        float lengthComponent = (float) drainAxis.choose(from.x, from.y, from.z);
        Vector3f to1 = new Vector3f(to).setComponent(drainAxis.ordinal(), lengthComponent + (length * fillPercentage));
        LTXIRenderUtil.renderPositionColorCuboid(buffer, poseStack.last().pose(), from.x, from.y, from.z, to1.x, to1.y, to1.z, fillColor, 0.85f, LTXIRenderUtil.ALL_SIDES);
    }

    public void renderRotated(VertexConsumer buffer, PoseStack poseStack, LimaColor fillColor, float fillPercentage)
    {
        poseStack.pushPose();

        if (rotation != null) rotation.applyRotation(poseStack);

        render(buffer, poseStack, fillColor, fillPercentage);

        poseStack.popPose();
    }
}