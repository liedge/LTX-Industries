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

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.SHOTGUN_CROSSHAIR_LEFT;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.SHOTGUN_CROSSHAIR_RIGHT;

public class ShotgunRenderProperties extends SimpleWeaponRenderProperties
{
    private final WeaponAmmoDisplay ammoDisplay = WeaponAmmoDisplay.createDisplay(
            6.76f, 2.76f, 3.51f,
            9.24f, 8.74f, 5.99f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9f, 13.5f, -22.5f, Direction.Axis.X));

    ShotgunRenderProperties()
    {
        super(5f, 0.75f);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.SHOTGUN.get();
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
        final int centerY = (screenHeight - 13) / 2;
        float bloom = 2.5f + 3.5f * LimaTechClient.animationCurveA(weaponInput.lerpTriggerTimer(weaponItem, partialTicks));

        SHOTGUN_CROSSHAIR_LEFT.directColorBlit(graphics, centerX - 6 - bloom, centerY, crosshairColor);
        SHOTGUN_CROSSHAIR_RIGHT.directColorBlit(graphics, centerX + 1 + bloom, centerY, crosshairColor);
    }

    @Override
    protected WeaponAmmoDisplay mainAmmoDisplay()
    {
        return ammoDisplay;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LimaTechClient.animationCurveA(recoilA);
    }
}