package liedge.limatech.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.LimaTechClient;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.*;

public class RocketLauncherRenderProperties extends SimpleWeaponRenderProperties
{
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(
            6.51f, 0.51f, 5.76f,
            9.49f, 4.49f, 8.74f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 7f, 7f, -22.5f, Direction.Axis.X));

    RocketLauncherRenderProperties()
    {
        super(3f, 0.8f, 10);
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 5) / 2;
        final int centerY = (screenHeight - 5) / 2;
        float bloom = 4f * LimaTechClient.animationCurveB(controls.lerpTriggerTimer(weaponItem, partialTicks));

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
    protected TranslucentFillModel getMagazineFillModel()
    {
        return magazineFillModel;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LimaTechClient.animationCurveB(recoilA);
    }
}