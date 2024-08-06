package liedge.limatech.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public final class LimaTechArmPoses
{
    private LimaTechArmPoses() {}

    public static final EnumProxy<HumanoidModel.ArmPose> TWO_HANDED_WEAPON = new EnumProxy<>(HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) (model, entity, arm) -> {
        final boolean rightHanded = arm == HumanoidArm.RIGHT;
        ModelPart mainArm = rightHanded ? model.rightArm : model.leftArm;
        ModelPart offArm = rightHanded ? model.leftArm : model.rightArm;

        float armXRot = (-Mth.PI / 2f) + model.head.xRot;
        float mainYRot = rightHanded ? -0.1f : 0.1f;
        float offYRot = rightHanded ? 0.62f : -0.62f;

        mainArm.yRot = model.head.yRot + mainYRot;
        mainArm.xRot = armXRot + 0.05f;

        offArm.yRot = model.head.yRot + offYRot;
        offArm.xRot = armXRot;
    });

    public static final EnumProxy<HumanoidModel.ArmPose> WEAPON_SHIELD_POSE = new EnumProxy<>(HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) LimaTechArmPoses::shieldPose);

    private static void shieldPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm)
    {
        final boolean rightHanded = arm == HumanoidArm.RIGHT;
        ModelPart mainArm = rightHanded ? model.rightArm : model.leftArm;

        mainArm.xRot = Mth.DEG_TO_RAD * 45f;

    }
}