package liedge.limatech.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.LimaTechClient;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;

public record WeaponAmmoDisplay(Vector3f from, Vector3f to, @Nullable BakedRotation rotation, float length, Direction.Axis drainAxis)
{
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static WeaponAmmoDisplay create(Vector3f from, Vector3f to, Optional<BakedRotation> optionalRotation, Direction.Axis drainAxis)
    {
        float length = (float) Math.abs(drainAxis.choose(from.x, from.y, from.z) - drainAxis.choose(to.x, to.y, to.z));
        BakedRotation rotation = optionalRotation.orElse(null);
        return new WeaponAmmoDisplay(from, to, rotation, length, drainAxis);
    }

    public static WeaponAmmoDisplay createDisplay(float x1, float y1, float z1, float x2, float y2, float z2, Direction.Axis drainAxis, @Nullable BakedRotation rotation)
    {
        Vector3f from = new Vector3f(x1, y1, z1).mul(0.0625f);
        Vector3f to = new Vector3f(x2, y2, z2).mul(0.0625f);
        return create(from, to, Optional.ofNullable(rotation), drainAxis);
    }

    public static final Codec<WeaponAmmoDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LimaCoreCodecs.VECTOR3F_16X.fieldOf("from").forGetter(WeaponAmmoDisplay::from),
            LimaCoreCodecs.VECTOR3F_16X.fieldOf("to").forGetter(WeaponAmmoDisplay::to),
            BakedRotation.CODEC.optionalFieldOf("rotation").forGetter(o -> Optional.ofNullable(o.rotation)),
            Direction.Axis.CODEC.fieldOf("drain_axis").forGetter(WeaponAmmoDisplay::drainAxis))
            .apply(instance, WeaponAmmoDisplay::create));

    public void renderAmmoDisplay(VertexConsumer buffer, PoseStack poseStack, LimaColor fillColor, float fillPercentage)
    {
        poseStack.pushPose();

        if (rotation != null) rotation.applyRotation(poseStack);

        float f0 = (float) drainAxis.choose(from.x, from.y, from.z);
        to.setComponent(drainAxis.ordinal(), f0 + (length * fillPercentage));

        LimaTechClient.renderPositionColorCuboid(buffer, poseStack.last().pose(), from.x, from.y, from.z, to.x, to.y, to.z, fillColor, 0.85f, LimaTechClient.ALL_SIDES);

        poseStack.popPose();
    }
}