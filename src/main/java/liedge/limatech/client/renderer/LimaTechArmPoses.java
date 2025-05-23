package liedge.limatech.client.renderer;

import liedge.limacore.util.LimaMathUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public final class LimaTechArmPoses
{
    private static final float UPPER_PITCH_LIMIT = LimaMathUtil.toRad(80f);
    private static final float SHIELD_LOWER_PITCH_LIMIT = LimaMathUtil.toRad(25f);
    private static final float MAIN_YAW_OFFSET = LimaMathUtil.toRad(6f);
    private static final float OFFHAND_YAW_OFFSET = LimaMathUtil.toRad(35f);
    private static final float MAIN_PITCH_OFFSET = LimaMathUtil.toRad(3f);

    private LimaTechArmPoses() {}

    public static final EnumProxy<HumanoidModel.ArmPose> TWO_HANDED_WEAPON = new EnumProxy<>(HumanoidModel.ArmPose.class, true, (IArmPoseTransformer) LimaTechArmPoses::twoHandedPose);
    public static final EnumProxy<HumanoidModel.ArmPose> ONE_HANDED_WEAPON = new EnumProxy<>(HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) LimaTechArmPoses::oneHandedPose);
    public static final EnumProxy<HumanoidModel.ArmPose> WEAPON_SHIELD_POSE = new EnumProxy<>(HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) LimaTechArmPoses::weaponShieldPose);

    private static void twoHandedPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm)
    {
        final boolean rightHanded = arm == HumanoidArm.RIGHT;
        ModelPart mainArm = rightHanded ? model.rightArm : model.leftArm;
        ModelPart offArm = rightHanded ? model.leftArm : model.rightArm;

        // Horizontal rotation
        mainArm.yRot = model.head.yRot + (rightHanded ? -MAIN_YAW_OFFSET : MAIN_YAW_OFFSET);
        offArm.yRot = model.head.yRot + (rightHanded ? OFFHAND_YAW_OFFSET : -OFFHAND_YAW_OFFSET);

        // Vertical rotation
        float armPitch = Mth.clamp(model.head.xRot, -UPPER_PITCH_LIMIT, UPPER_PITCH_LIMIT) - Mth.HALF_PI;
        mainArm.xRot = armPitch + MAIN_PITCH_OFFSET;
        offArm.xRot = armPitch;
    }

    private static void oneHandedPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm)
    {
        final boolean rightHanded = arm == HumanoidArm.RIGHT;
        ModelPart mainArm = rightHanded ? model.rightArm : model.leftArm;

        // Horizontal rotation
        mainArm.yRot = model.head.yRot + (rightHanded ? -MAIN_YAW_OFFSET : MAIN_YAW_OFFSET);

        // Vertical rotation
        float armPitch = Mth.clamp(model.head.xRot, -UPPER_PITCH_LIMIT, UPPER_PITCH_LIMIT) - Mth.HALF_PI;
        mainArm.xRot = armPitch + MAIN_PITCH_OFFSET;
    }

    private static void weaponShieldPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm)
        {
            final boolean rightHanded = arm == HumanoidArm.RIGHT;
            ModelPart mainArm = rightHanded ? model.rightArm : model.leftArm;

            // Turn arm sideways
            mainArm.zRot = Mth.HALF_PI;

            // Horizontal rotation
            float armYaw = (model.head.yRot - Mth.HALF_PI) * (rightHanded ? 1 : -1);
            mainArm.xRot = armYaw - MAIN_YAW_OFFSET;

            // Vertical rotation
            float headXRot = model.head.xRot;
            float armPitch = rightHanded ? -Mth.clamp(headXRot, -UPPER_PITCH_LIMIT, SHIELD_LOWER_PITCH_LIMIT) : Mth.PI + Mth.clamp(-headXRot, -SHIELD_LOWER_PITCH_LIMIT, UPPER_PITCH_LIMIT);
            mainArm.yRot = armPitch + 0.261799f;
        }
    }