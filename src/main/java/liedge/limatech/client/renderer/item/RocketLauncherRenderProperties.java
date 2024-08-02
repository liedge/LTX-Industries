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

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.*;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.LAUNCHER_CROSSHAIR_RIGHT;

public class RocketLauncherRenderProperties extends SimpleWeaponRenderProperties
{
    private final WeaponAmmoDisplay ammoDisplay = WeaponAmmoDisplay.createDisplay(
            6.51f, 0.51f, 5.76f,
            9.49f, 4.49f, 8.74f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 7f, 7f, -22.5f, Direction.Axis.X));

    RocketLauncherRenderProperties()
    {
        super(3f, 0.8f);
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
        float bloom = 4f * LimaTechClient.animationCurveB(weaponInput.lerpTriggerTimer(weaponItem, partialTicks));

        LAUNCHER_CROSSHAIR_CENTER.directColorBlit(graphics, centerX, centerY, crosshairColor);
        LAUNCHER_CROSSHAIR_UP.directColorBlit(graphics, centerX - 1, centerY - 4 - bloom, crosshairColor);
        LAUNCHER_CROSSHAIR_DOWN.directColorBlit(graphics, centerX - 1, centerY + 7 + bloom, crosshairColor);
        LAUNCHER_CROSSHAIR_LEFT.directColorBlit(graphics, centerX - 4 - bloom, centerY - 1, crosshairColor);
        LAUNCHER_CROSSHAIR_RIGHT.directColorBlit(graphics, centerX + 7 + bloom, centerY - 1, crosshairColor);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.ROCKET_LAUNCHER.get();
    }

    @Override
    protected WeaponAmmoDisplay mainAmmoDisplay()
    {
        return ammoDisplay;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LimaTechClient.animationCurveB(recoilA);
    }
}