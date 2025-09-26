package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

public class GlowstickLauncherRenderer extends SimpleWeaponRenderer
{
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(
            6.51f, 8.01f, 17.015f,
            9.49f, 10.99f, 20.99f,
            Direction.Axis.Z);

    private BakedItemLayer barrelTubeLayer;

    GlowstickLauncherRenderer()
    {
        super(2f, 0.21875f, 4);
    }

    @Override
    protected void loadWeaponModelParts(WeaponItem item, ItemLayerBakedModel model)
    {
        super.loadWeaponModelParts(item, model);
        barrelTubeLayer = model.getLayer("barrel tube");
    }

    @Override
    protected void renderModelLayers(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        super.renderModelLayers(poseStack, bufferSource, light, overlay);
        barrelTubeLayer.putQuadsInBuffer(poseStack, bufferSource, light);
    }

    @Override
    protected TranslucentFillModel getMagazineFillModel()
    {
        return magazineFillModel;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LTXIRenderUtil.linearThresholdCurve(recoilA, 0.25f);
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 5) / 2;
        final int centerY = (screenHeight - 5) / 2;
        float bloom = 5f * LTXIRenderUtil.animationCurveB(controls.lerpTriggerTimer(weaponItem, partialTicks));

        blitSprite(graphics, centerX, centerY, 5, 5, crosshairColor, HOLLOW_DOT);
        blitSprite(graphics, centerX - 6 - bloom, centerY - 1, 4, 7, crosshairColor, ANGLE_BRACKET);
        blitMirroredUSprite(graphics, centerX + 7 + bloom, centerY - 1, 4, 7, crosshairColor, ANGLE_BRACKET);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LTXIItems.GLOWSTICK_LAUNCHER.get();
    }
}