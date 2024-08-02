package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.TickTimer;
import liedge.limatech.client.LimaTechClient;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.model.baked.WeaponAmmoDisplay;
import liedge.limatech.client.renderer.LimaTechArmPoses;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.LocalWeaponInput;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.*;
import static liedge.limatech.registry.LimaTechDataComponents.GRENADE_ELEMENT;

public class GrenadeLauncherRenderProperties extends WeaponRenderProperties<WeaponItem>
{
    private final Vector3f ornamentPivot = new Vector3f(8f, 9.25f, 10).mul(0.0625f);
    private final WeaponAmmoDisplay ammoDisplay = WeaponAmmoDisplay.createDisplay(6.26f, 9.76f, 12.06f, 9.74f, 15.74f, 15.54f, Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9.5f, 14.5f, 45, Direction.Axis.X));

    private DynamicModularBakedModel.SubModel chamberOrnament;
    private DynamicModularBakedModel.SubModel chamberGlass;
    private float ornamentSpin0;
    private float ornamentSpin;

    public void tickItemRenderer()
    {
        TickTimer recoilB = LocalWeaponInput.LOCAL_WEAPON_INPUT.getRecoilTimerB();
        float speed;

        if (recoilB.getTimerState() == TickTimer.State.RUNNING)
        {
            speed = 0.95f * LimaTechClient.animationCurveSin(recoilB.getProgressPercent());
        }
        else
        {
            speed = 0.025f;
        }

        ornamentSpin0 = ornamentSpin;
        ornamentSpin = (ornamentSpin + (60 * speed)) % 360;
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack heldItem)
    {
        return LimaTechArmPoses.twoHandedWeapon();
    }

    @Override
    public void renderCrosshair(WeaponItem weaponItem, LocalWeaponInput weaponInput, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 5) / 2;
        final int centerY = (screenHeight - 5) / 2;

        float bloom = 4f * LimaTechClient.animationCurveA(weaponInput.lerpTriggerTimer(weaponItem, partialTicks));

        LAUNCHER_CROSSHAIR_CENTER.directColorBlit(graphics, centerX, centerY, crosshairColor);
        LAUNCHER_CROSSHAIR_UP.directColorBlit(graphics, centerX - 1, centerY - 4 - bloom, crosshairColor);
        LAUNCHER_CROSSHAIR_LEFT.directColorBlit(graphics, centerX - 4 - bloom, centerY - 1, crosshairColor);
        LAUNCHER_CROSSHAIR_RIGHT.directColorBlit(graphics, centerX + 7 + bloom, centerY - 1, crosshairColor);
        LAUNCHER_CROSSHAIR_DOWN_DROP.directColorBlit(graphics, centerX - 1, centerY + 7, crosshairColor);
    }

    @Override
    protected void loadWeaponModelParts(WeaponItem item, DynamicModularBakedModel model)
    {
        chamberOrnament = model.getSubmodel("chamber_ornament");
        chamberGlass = model.getSubmodel("chamber_glass");
    }

    @Override
    protected void renderStaticWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        LimaColor roundTypeColor = stack.getOrDefault(GRENADE_ELEMENT, OrbGrenadeElement.EXPLOSIVE).getColor();

        mainSubmodel.renderToBuffer(poseStack, bufferSource, light);
        chamberGlass.renderToBuffer(poseStack, bufferSource, light, roundTypeColor, LimaColor.WHITE);
        renderAmmoDisplay(item, stack, poseStack, bufferSource, ammoDisplay, roundTypeColor);
    }

    @Override
    protected void renderHeldWeapon(ItemStack stack, WeaponItem item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int light, int overlay, float recoilA, float recoilB)
    {
        LimaColor roundTypeColor = stack.getOrDefault(GRENADE_ELEMENT, OrbGrenadeElement.EXPLOSIVE).getColor();

        float mul = LimaTechClient.animationCurveA(recoilA);
        if (mul > 0)
        {
            poseStack.translate(0, 0, mul * 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(2.5f * mul));
        }

        poseStack.pushPose();

        poseStack.translate(ornamentPivot.x, ornamentPivot.y, ornamentPivot.z);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(partialTick, ornamentSpin0, ornamentSpin)));
        poseStack.translate(-ornamentPivot.x, -ornamentPivot.y, -ornamentPivot.z);

        chamberOrnament.renderToBuffer(poseStack, bufferSource, light, LimaColor.WHITE, roundTypeColor);

        poseStack.popPose();

        renderStaticWeapon(stack, item, displayContext, poseStack, bufferSource, light, overlay);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.GRENADE_LAUNCHER.get();
    }
}