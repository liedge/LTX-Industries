package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.BakedQuadGroup;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.TickTimer;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.baked.DynamicModularItemBakedModel;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.*;

public class GrenadeLauncherRenderProperties extends WeaponRenderProperties<WeaponItem>
{
    private final Vector3f ornamentPivot = new Vector3f(8f, 9.25f, 10).mul(0.0625f); // 9.96
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(6.26f, 10.46f, 12.06f, 9.74f, 15.94f, 15.54f, Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9.5f, 14.5f, 45, Direction.Axis.X));

    private BakedQuadGroup chamberOrnament;
    private BakedQuadGroup chamberGlass;
    private float ornamentSpin0;
    private float ornamentSpin;

    public void tickItemRenderer(Player player)
    {
        TickTimer animationB = ClientWeaponControls.of(player).getAnimationTimerB();
        float speed;

        if (animationB.getTimerState() == TickTimer.State.RUNNING)
        {
            speed = 0.95f * LimaTechRenderUtil.animationCurveSin(animationB.getProgressPercent());
        }
        else
        {
            speed = 0.025f;
        }

        ornamentSpin0 = ornamentSpin;
        ornamentSpin = (ornamentSpin + (60 * speed)) % 360;
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 5) / 2;
        final int centerY = (screenHeight - 5) / 2;

        float bloom = 4f * LimaTechRenderUtil.animationCurveA(controls.lerpTriggerTimer(weaponItem, partialTicks));

        LAUNCHER_CROSSHAIR_CENTER.directColorBlit(graphics, centerX, centerY, crosshairColor);
        LAUNCHER_CROSSHAIR_UP.directColorBlit(graphics, centerX - 1, centerY - 4 - bloom, crosshairColor);
        LAUNCHER_CROSSHAIR_LEFT.directColorBlit(graphics, centerX - 4 - bloom, centerY - 1, crosshairColor);
        LAUNCHER_CROSSHAIR_RIGHT.directColorBlit(graphics, centerX + 7 + bloom, centerY - 1, crosshairColor);
        LAUNCHER_CROSSHAIR_DOWN_DROP.directColorBlit(graphics, centerX - 1, centerY + 7, crosshairColor);
    }

    @Override
    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientWeaponControls controls)
    {
        controls.getAnimationTimerA().startTimer(6);
        controls.getAnimationTimerB().startTimer(10);
    }

    @Override
    protected void loadWeaponModelParts(WeaponItem item, DynamicModularItemBakedModel model)
    {
        chamberOrnament = model.getSubmodel("chamber ornament");
        chamberGlass = model.getSubmodel("chamber glass");
    }

    @Override
    protected void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        LimaColor grenadeColor = GrenadeLauncherWeaponItem.getGrenadeTypeFromItem(stack).getColor();

        renderSubModel(chamberOrnament, poseStack, bufferSource, LimaColor.WHITE, grenadeColor, light);
        renderSubModel(mainSubmodel, poseStack, bufferSource, light);
        renderSubModel(chamberGlass, poseStack, bufferSource, grenadeColor, LimaColor.WHITE, light);
        renderStaticMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, grenadeColor);
    }

    @Override
    protected void renderWeaponFirstPerson(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientWeaponControls controls)
    {
        LimaColor grenadeColor = GrenadeLauncherWeaponItem.getGrenadeTypeFromItem(stack).getColor();

        float mul = LimaTechRenderUtil.animationCurveA(controls.getAnimationTimerA().lerpProgressNotPaused(partialTick));
        if (mul > 0)
        {
            poseStack.translate(0, 0, mul * 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(2.5f * mul));
        }

        poseStack.pushPose();

        poseStack.translate(ornamentPivot.x, ornamentPivot.y, ornamentPivot.z);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(partialTick, ornamentSpin0, ornamentSpin)));
        poseStack.translate(-ornamentPivot.x, -ornamentPivot.y, -ornamentPivot.z);

        renderSubModel(chamberOrnament, poseStack, bufferSource, LimaColor.WHITE, grenadeColor, light);

        poseStack.popPose();

        renderSubModel(mainSubmodel, poseStack, bufferSource, light);
        renderSubModel(chamberGlass, poseStack, bufferSource, grenadeColor, LimaColor.WHITE, light);
        renderAnimatedMagazineFill(item, stack, poseStack, bufferSource, magazineFillModel, grenadeColor, partialTick, controls);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.GRENADE_LAUNCHER.get();
    }
}