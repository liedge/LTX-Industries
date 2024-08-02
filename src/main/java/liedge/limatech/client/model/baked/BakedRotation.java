package liedge.limatech.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record BakedRotation(Vector3f pivot, Quaternionf rotation)
{
    public static final Codec<BakedRotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LimaCoreCodecs.VECTOR3F_16X.fieldOf("pivot").forGetter(BakedRotation::pivot),
            LimaCoreCodecs.UNIT_QUATERNION.forGetter(BakedRotation::rotation)).apply(instance, BakedRotation::new));

    public static BakedRotation fromAxisAngle(float pivotX, float pivotY, float pivotZ, float angle, Direction.Axis axis)
    {
        Vector3f pivot = new Vector3f(pivotX * 0.0625f, pivotY * 0.0625f, pivotZ * 0.0625f);
        Axis rot = switch (axis)
        {
            case X -> Axis.XP;
            case Y -> Axis.YP;
            case Z -> Axis.ZP;
        };
        Quaternionf rotation = rot.rotationDegrees(angle);

        return new BakedRotation(pivot, rotation);
    }

    public void applyRotation(PoseStack poseStack)
    {
        poseStack.translate(pivot.x, pivot.y, pivot.z);
        poseStack.mulPose(rotation);
        poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
    }
}