package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.model.baked.LimaLayerBakedModel;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.registry.game.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

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
        final int centerX = (screenWidth - 5) / 2;
        final int centerY = (screenHeight - 5) / 2;

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

        float bloom = baseBloom + 2f * (controls.isTriggerHeld() ? LimaTechRenderUtil.animationCurveSin(partialTicks) : 0f);

        blitSprite(graphics, centerX, centerY, 5, 5, crosshairColor, HOLLOW_DOT);
        blitSprite(graphics, centerX - 4 - bloom, centerY - 4, 6, 13, crosshairColor, CIRCLE_BRACKET);
        blitMirroredUSprite(graphics, centerX + 3 + bloom, centerY - 4, 6, 13, crosshairColor, CIRCLE_BRACKET);
    }

    @Override
    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientWeaponControls controls) {}

    @Override
    protected void loadWeaponModelParts(WeaponItem item, LimaLayerBakedModel model) {}

    @Override
    protected void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        rootBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        rootEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        renderStaticMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, LimaTechConstants.LIME_GREEN);
    }

    @Override
    protected void renderWeaponFirstPerson(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientWeaponControls controls)
    {
        if (controls.isTriggerHeld())
        {
            poseStack.translate(0, 0, 0.09375f * LimaTechRenderUtil.animationCurveSin(partialTick));
        }

        rootBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        rootEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        renderAnimatedMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, LimaTechConstants.LIME_GREEN, partialTick, controls);
    }
}