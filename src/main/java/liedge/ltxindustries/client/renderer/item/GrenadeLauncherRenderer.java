package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.TickTimer;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.baked.BakedRotation;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.item.weapon.GrenadeLauncherItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class GrenadeLauncherRenderer extends WeaponRenderer<WeaponItem>
{
    private final Vector3f ornamentPivot = new Vector3f(8f, 9.25f, 10).mul(0.0625f); // 9.96
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(6.26f, 10.46f, 12.06f, 9.74f, 15.94f, 15.54f, Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9.5f, 14.5f, 45, Direction.Axis.X));

    private BakedItemLayer chamberBaseLayer;
    private BakedItemLayer chamberEmissiveLayer;
    private BakedItemLayer chamberGlassLayer;
    private float ornamentSpin0;
    private float ornamentSpin;

    GrenadeLauncherRenderer() {}

    public void tickItemRenderer(Player player)
    {
        TickTimer animationB = ClientExtendedInput.of(player).getAnimationTimerB();
        float speed;

        if (animationB.getTimerState() == TickTimer.State.RUNNING)
        {
            speed = 0.95f * LTXIRenderUtil.sineAnimationCurve(animationB.getProgressPercent());
        }
        else
        {
            speed = 0.025f;
        }

        ornamentSpin0 = ornamentSpin;
        ornamentSpin = (ornamentSpin + (60 * speed)) % 360;
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 5) / 2;
        final int centerY = (screenHeight - 5) / 2;
        float bloom = 4f * LTXIRenderUtil.animationCurveA(controls.lerpTriggerTimer(weaponItem, partialTicks));

        blitSprite(graphics, centerX, centerY, 5, 5, crosshairColor, HOLLOW_DOT);
        blitMirroredVSprite(graphics, centerX - 1, centerY - 4 - bloom, 7, 2, crosshairColor, AOE_VERTICAL);
        blitSprite(graphics, centerX - 1, centerY + 7 + bloom, 7, 2, crosshairColor, AOE_VERTICAL);
        blitSprite(graphics, centerX - 4 - bloom, centerY - 1, 2, 7, crosshairColor, AOE_HORIZONTAL);
        blitMirroredUSprite(graphics, centerX + 7 + bloom, centerY - 1, 2, 7, crosshairColor, AOE_HORIZONTAL);
    }

    @Override
    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientExtendedInput controls)
    {
        controls.getAnimationTimerA().startTimer(6);
        controls.getAnimationTimerB().startTimer(10);
    }

    @Override
    protected void loadWeaponModelParts(WeaponItem item, ItemLayerBakedModel model)
    {
        chamberBaseLayer = model.getLayer("chamber base");
        chamberEmissiveLayer = model.getLayer("chamber emissive");
        chamberGlassLayer = model.getLayer("chamber glass");
    }

    @Override
    protected void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        LimaColor grenadeColor = GrenadeLauncherItem.getGrenadeTypeFromItem(stack).getColor();

        rootBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        rootEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        chamberBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        chamberEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, grenadeColor, light);
        chamberGlassLayer.putQuadsInBuffer(poseStack, bufferSource, grenadeColor, light);
        renderStaticMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, grenadeColor);
    }

    @Override
    protected void renderWeaponFirstPerson(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientExtendedInput controls)
    {
        LimaColor grenadeColor = GrenadeLauncherItem.getGrenadeTypeFromItem(stack).getColor();

        float mul = LTXIRenderUtil.animationCurveA(controls.getAnimationTimerA().lerpProgressNotPaused(partialTick));
        if (mul > 0)
        {
            poseStack.translate(0, 0, mul * 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(2.5f * mul));
        }

        poseStack.pushPose();

        poseStack.translate(ornamentPivot.x, ornamentPivot.y, ornamentPivot.z);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(partialTick, ornamentSpin0, ornamentSpin)));
        poseStack.translate(-ornamentPivot.x, -ornamentPivot.y, -ornamentPivot.z);

        chamberBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        chamberEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, grenadeColor, light);

        poseStack.popPose();

        rootBaseLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        rootEmissiveLayer.putQuadsInBuffer(poseStack, bufferSource, light);
        chamberGlassLayer.putQuadsInBuffer(poseStack, bufferSource, grenadeColor, light);
        renderAnimatedMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, grenadeColor, partialTick, controls);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LTXIItems.GRENADE_LAUNCHER.get();
    }
}