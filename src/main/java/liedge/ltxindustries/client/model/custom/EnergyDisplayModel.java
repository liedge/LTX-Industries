package liedge.ltxindustries.client.model.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import org.joml.*;

import java.lang.Math;

public record EnergyDisplayModel(Vector3fc from, Vector3fc to, float length, Direction.Axis axis, Rotation rotation)
{
    public static final Codec<EnergyDisplayModel> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.VECTOR3F.fieldOf("from").forGetter(EnergyDisplayModel::from),
            ExtraCodecs.VECTOR3F.fieldOf("to").forGetter(EnergyDisplayModel::to),
            Codec.FLOAT.fieldOf("length").forGetter(EnergyDisplayModel::length),
            Direction.Axis.CODEC.fieldOf("axis").forGetter(EnergyDisplayModel::axis),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.NONE).forGetter(EnergyDisplayModel::rotation))
            .apply(i, EnergyDisplayModel::new));

    public static EnergyDisplayModel create(float x, float y, float z, float xSize, float ySize, float zSize, Direction.Axis axis, Rotation rotation)
    {
        Vector3fc from = new Vector3f(x + 0.01f, y + 0.01f, z + 0.01f).mul(0.0625f);
        Vector3fc to = new Vector3f(x + xSize - 0.01f, y + ySize - 0.01f, z + zSize - 0.01f).mul(0.0625f);
        float length = Math.abs(from.get(axis.ordinal()) - to.get(axis.ordinal()));

        return new EnergyDisplayModel(from, to, length, axis, rotation);
    }

    public static EnergyDisplayModel create(float x, float y, float z, float xSize, float ySize, float zSize, Direction.Axis axis)
    {
        return create(x, y, z, xSize, ySize, zSize, axis, Rotation.NONE);
    }

    // Class def

    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, float fill, int color, float alpha)
    {
        if (fill <= 1e-4) return;

        poseStack.pushPose();

        if (!rotation.equals(Rotation.NONE)) rotation.apply(poseStack);

        float lengthComponent = from.get(axis.ordinal());
        Vector3f scaledTo = new Vector3f(to).setComponent(axis.ordinal(), lengthComponent + (length * fill));

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.ENERGY_FILL, (pose, buffer) ->
                LTXIRenderer.submitUnlitCuboid(pose, buffer, Direction.values(), from.x(), from.y(), from.z(), scaledTo.x(), scaledTo.y(), scaledTo.z(), color, alpha));

        poseStack.popPose();
    }

    public record Rotation(Vector3fc origin, Quaternionfc value)
    {
        private static final Codec<Rotation> CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter(Rotation::origin),
                ExtraCodecs.QUATERNIONF.fieldOf("value").forGetter(Rotation::value))
                .apply(i, Rotation::new));

        public static Rotation NONE = new Rotation(new Vector3f(), new Quaternionf());

        public static Rotation axisAngle(float originX, float originY, float originZ, Direction.Axis axis, float angleDegrees)
        {
            Vector3f origin = new Vector3f(originX, originY, originZ).mul(0.0625f);
            AxisAngle4f aa = new AxisAngle4f(LimaCoreMath.toRad(angleDegrees), LimaCoreMath.unitVecForAxis(axis));
            Quaternionfc value = new Quaternionf(aa);

            return new Rotation(origin, value);
        }

        private void apply(PoseStack poseStack)
        {
            poseStack.translate(origin.x(), origin.y(), origin.z());
            poseStack.mulPose(value);
            poseStack.translate(-origin.x(), -origin.y(), -origin.z());
        }
    }
}