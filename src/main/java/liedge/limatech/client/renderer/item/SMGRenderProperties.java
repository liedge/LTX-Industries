package liedge.limatech.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.LimaTechClient;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.baked.WeaponAmmoDisplay;
import liedge.limatech.client.renderer.LimaTechArmPoses;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.LocalWeaponInput;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.AUTO_CROSSHAIR_1;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.AUTO_CROSSHAIR_2;

public class SMGRenderProperties extends SimpleWeaponRenderProperties
{
    private final WeaponAmmoDisplay ammoDisplay = WeaponAmmoDisplay.createDisplay(
            7.01f, 1.52f, 6.01f,
            8.99f, 7.5f, 8.49f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9f, 13.5f, -22.5f, Direction.Axis.X));

    SMGRenderProperties()
    {
        super(0f, 0.0625f);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.SUBMACHINE_GUN.get();
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack heldItem)
    {
        return LimaTechArmPoses.twoHandedWeapon();
    }

    @Override
    public void renderCrosshair(WeaponItem weaponItem, LocalWeaponInput weaponInput, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 1) / 2;

        float bloom = 2.5f + 1.5f * LimaTechClient.animationCurveSin(weaponInput.getRecoilTimerB().lerpProgressNotPaused(partialTicks));

        AUTO_CROSSHAIR_1.directColorBlit(graphics, centerX - 6 - bloom, centerY, crosshairColor);
        AUTO_CROSSHAIR_1.directColorBlit(graphics, centerX + 1 + bloom, centerY, crosshairColor);
        AUTO_CROSSHAIR_2.directColorBlit(graphics, centerX, centerY + 1 + bloom, crosshairColor);
    }

    @Override
    protected WeaponAmmoDisplay mainAmmoDisplay()
    {
        return ammoDisplay;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LimaTechClient.animationCurveSin(recoilA);
    }
}