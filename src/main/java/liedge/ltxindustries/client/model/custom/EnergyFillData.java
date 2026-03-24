package liedge.ltxindustries.client.model.custom;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public final class EnergyFillData
{
    @ApiStatus.Internal
    private static EnergyFillData create(float x1, float y1, float z1, float x2, float y2, float z2, Direction.Axis drainAxis, @Nullable Vector3f pivot, @Nullable Quaternionf rotation)
    {
        Preconditions.checkArgument((pivot != null) == (rotation != null), "Pivot and rotation must either be both null or both non-null.");

        Vector3f from = new Vector3f(x1, y1, z1).mul(0.0625f);
        Vector3f to = new Vector3f(x2, y2, z2).mul(0.0625f);
        float length = (float) Math.abs(drainAxis.choose(from.x, from.y, from.z) - drainAxis.choose(to.x, to.y, to.z));

        return new EnergyFillData(from, to, length, drainAxis, pivot, rotation);
    }

    public static EnergyFillData createStatic(float x, float y, float z, float xSize, float ySize, float zSize, Direction.Axis drainAxis)
    {
        return create(x + 0.01f, y + 0.01f, z + 0.01f, x + xSize - 0.01f, y + ySize - 0.01f, z + zSize - 0.01f, drainAxis, null, null);
    }

    private final Vector3f from;
    private final Vector3f to;
    private final float length;
    private final Direction.Axis drainAxis;

    private final @Nullable Vector3f pivot;
    private final @Nullable Quaternionf rotation;

    public EnergyFillData(Vector3f from, Vector3f to, float length, Direction.Axis drainAxis, @Nullable Vector3f pivot, @Nullable Quaternionf rotation)
    {
        this.from = from;
        this.to = to;
        this.length = length;
        this.drainAxis = drainAxis;
        this.pivot = pivot;
        this.rotation = rotation;
    }

    public void render(PoseStack poseStack, SubmitNodeCollector nodeCollector, LimaColor color, float fill)
    {
        if (fill <= 1e-4) return;

        poseStack.pushPose();

        if (pivot != null && rotation != null)
        {
            poseStack.translate(pivot.x, pivot.y, pivot.z);
            poseStack.mulPose(rotation);
            poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
        }

        float lengthComponent = (float) drainAxis.choose(from.x, from.y, from.z);
        Vector3f adjustedTo = new Vector3f(to).setComponent(drainAxis.ordinal(), lengthComponent + (length * fill));

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.ENERGY_FILL, (pose, buffer) ->
                LTXIRenderer.submitUnlitCuboid(pose, buffer, Direction.values(), from.x, from.y, from.z, adjustedTo.x, adjustedTo.y, adjustedTo.z, color, 0.85f));

        poseStack.popPose();
    }
}