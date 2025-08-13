package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
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

    protected void renderModelLayers(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        rootBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        rootEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, light);
    }

    @Override
    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientWeaponControls controls)
    {
        controls.getAnimationTimerA().startTimer(recoilAnimationTime);
    }

    @Override
    protected void loadWeaponModelParts(WeaponItem item, ItemLayerBakedModel model) {}

    @Override
    protected final void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        renderModelLayers(poseStack, bufferSource, light, overlay);
        renderStaticMagazineFill(item, stack, poseStack, bufferSource, getMagazineFillModel(), LTXIConstants.LIME_GREEN);
    }

    @Override
    protected final void renderWeaponFirstPerson(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientWeaponControls controls)
    {
        float mul = applyAnimationCurve(controls.getAnimationTimerA().lerpProgressNotPaused(partialTick));
        poseStack.translate(0, 0, recoilDistance * mul);
        if (recoilAngle > 0) poseStack.mulPose(Axis.XP.rotationDegrees(recoilAngle * mul));

        renderModelLayers(poseStack, bufferSource, light, overlay);

        renderAnimatedMagazineFill(item, stack, poseStack, bufferSource, getMagazineFillModel(), LTXIConstants.LIME_GREEN, partialTick, controls);
    }
}