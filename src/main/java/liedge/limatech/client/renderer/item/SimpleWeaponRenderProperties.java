package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.model.baked.DynamicModularItemBakedModel;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

abstract class SimpleWeaponRenderProperties extends WeaponRenderProperties<WeaponItem>
{
    private final float recoilAngle;
    private final float recoilDistance;
    private final int recoilAnimationTime;

    SimpleWeaponRenderProperties(float recoilAngle, float recoilDistance, int recoilAnimationTime)
    {
        this.recoilAngle = recoilAngle;
        this.recoilDistance = recoilDistance;
        this.recoilAnimationTime = recoilAnimationTime;
    }

    protected abstract TranslucentFillModel getMagazineFillModel();

    protected abstract float applyAnimationCurve(float recoilA);

    @Override
    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientWeaponControls controls)
    {
        controls.getAnimationTimerA().startTimer(recoilAnimationTime);
    }

    @Override
    protected final void loadWeaponModelParts(WeaponItem item, DynamicModularItemBakedModel model) {}

    @Override
    protected final void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        renderSubModel(mainSubmodel, poseStack, bufferSource, light);
        renderStaticMagazineFill(item, stack, poseStack, bufferSource, getMagazineFillModel(), LimaTechConstants.LIME_GREEN);
    }

    @Override
    protected final void renderWeaponFirstPerson(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientWeaponControls controls)
    {
        float mul = applyAnimationCurve(controls.getAnimationTimerA().lerpProgressNotPaused(partialTick));
        poseStack.translate(0, 0, recoilDistance * mul);
        if (recoilAngle > 0) poseStack.mulPose(Axis.XP.rotationDegrees(recoilAngle * mul));
        renderSubModel(mainSubmodel, poseStack, bufferSource, light);
        renderAnimatedMagazineFill(item, stack, poseStack, bufferSource, getMagazineFillModel(), LimaTechConstants.LIME_GREEN, partialTick, controls);
    }
}