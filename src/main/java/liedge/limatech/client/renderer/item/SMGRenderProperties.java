package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechClient;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.AUTO_CROSSHAIR_1;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.AUTO_CROSSHAIR_2;

public class SMGRenderProperties extends WeaponRenderProperties<WeaponItem>
{
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(
            7.01f, 1.52f, 6.01f,
            8.99f, 7.5f, 8.49f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9f, 13.5f, -22.5f, Direction.Axis.X));

    SMGRenderProperties() {}

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.SUBMACHINE_GUN.get();
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 1) / 2;

        float baseBloom;
        if (LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND))
        {
            float f = Math.min(1f, (player.getTicksUsingItem() + partialTicks) / 3f);
            baseBloom = 3f - (3f * f);
        }
        else
        {
            baseBloom = 3f;
        }

        float bloom = baseBloom + 2f * (controls.isTriggerHeld() ? LimaTechClient.animationCurveSin(partialTicks) : 0f);

        AUTO_CROSSHAIR_1.directColorBlit(graphics, centerX - 6 - bloom, centerY, crosshairColor);
        AUTO_CROSSHAIR_1.directColorBlit(graphics, centerX + 1 + bloom, centerY, crosshairColor);
        AUTO_CROSSHAIR_2.directColorBlit(graphics, centerX, centerY + 1 + bloom, crosshairColor);
    }

    @Override
    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientWeaponControls controls) {}

    @Override
    protected void loadWeaponModelParts(WeaponItem item, DynamicModularBakedModel model) {}

    @Override
    protected void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        mainSubmodel.renderToBuffer(poseStack, bufferSource, light);
        renderStaticMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, LimaTechConstants.LIME_GREEN);
    }

    @Override
    protected void renderWeaponFirstPerson(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientWeaponControls controls)
    {
        if (controls.isTriggerHeld())
        {
            poseStack.translate(0, 0, 0.09375f * LimaTechClient.animationCurveSin(partialTick));
        }

        mainSubmodel.renderToBuffer(poseStack, bufferSource, light);
        renderAnimatedMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, LimaTechConstants.LIME_GREEN, partialTick, controls);
    }
}