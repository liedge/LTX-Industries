package liedge.limatech.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.SHOTGUN_CROSSHAIR_LEFT;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.SHOTGUN_CROSSHAIR_RIGHT;

public class ShotgunRenderProperties extends SimpleWeaponRenderProperties
{
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(
            6.76f, 2.76f, 3.51f,
            9.24f, 8.74f, 5.99f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9f, 13.5f, -22.5f, Direction.Axis.X));

    ShotgunRenderProperties()
    {
        super(5f, 0.75f, 5);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.SHOTGUN.get();
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 13) / 2;
        float bloom = 2.5f + 3.5f * LimaTechRenderUtil.animationCurveB(controls.lerpTriggerTimer(weaponItem, partialTicks));

        SHOTGUN_CROSSHAIR_LEFT.directColorBlit(graphics, centerX - 6 - bloom, centerY, crosshairColor);
        SHOTGUN_CROSSHAIR_RIGHT.directColorBlit(graphics, centerX + 1 + bloom, centerY, crosshairColor);
    }

    @Override
    public TranslucentFillModel getMagazineFillModel()
    {
        return magazineFillModel;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LimaTechRenderUtil.animationCurveA(recoilA);
    }
}