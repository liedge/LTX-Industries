package liedge.limatech.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.registry.game.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;

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
        float bloom = 4f * LimaTechRenderUtil.animationCurveB(controls.lerpTriggerTimer(weaponItem, partialTicks));

        blitSprite(graphics, centerX, centerY, 5, 5, crosshairColor, HOLLOW_DOT);
        blitMirroredVSprite(graphics, centerX - 1, centerY - 4 - bloom, 7, 2, crosshairColor, AOE_VERTICAL);
        blitSprite(graphics, centerX - 1, centerY + 7 + bloom, 7, 2, crosshairColor, AOE_VERTICAL);
        blitSprite(graphics, centerX - 4 - bloom, centerY - 1, 2, 7, crosshairColor, AOE_HORIZONTAL);
        blitMirroredUSprite(graphics, centerX + 7 + bloom, centerY - 1, 2, 7, crosshairColor, AOE_HORIZONTAL);
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
        return LimaTechRenderUtil.animationCurveB(recoilA);
    }
}