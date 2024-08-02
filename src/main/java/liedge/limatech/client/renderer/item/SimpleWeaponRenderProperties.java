package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.model.baked.WeaponAmmoDisplay;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

abstract class SimpleWeaponRenderProperties extends WeaponRenderProperties<WeaponItem>
{
    private final float recoilAngle;
    private final float recoilDistance;

    SimpleWeaponRenderProperties(float recoilAngle, float recoilDistance)
    {
        this.recoilAngle = recoilAngle;
        this.recoilDistance = recoilDistance;
    }

    protected abstract WeaponAmmoDisplay mainAmmoDisplay();

    protected abstract float applyAnimationCurve(float recoilA);

    @Override
    protected final void loadWeaponModelParts(WeaponItem item, DynamicModularBakedModel model) {}

    @Override
    protected final void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        mainSubmodel.renderToBuffer(poseStack, bufferSource, light);
        renderAmmoDisplay(item, stack, poseStack, bufferSource, mainAmmoDisplay(), LimaTechConstants.LIME_GREEN);
    }

    @Override
    protected final void renderHeldWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int light, int overlay, float recoilA, float recoilB)
    {
        float mul = applyAnimationCurve(recoilA);
        poseStack.translate(0, 0, recoilDistance * mul);
        if (recoilAngle > 0) poseStack.mulPose(Axis.XP.rotationDegrees(recoilAngle * mul));
        renderStaticWeapon(stack, item, displayContext, poseStack, bufferSource, light, overlay);
    }
}