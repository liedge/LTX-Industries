package liedge.ltxindustries.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.baked.BakedRotation;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;

public class ShotgunRenderer extends SimpleWeaponRenderer
{
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(
            6.76f, 2.76f, 3.51f,
            9.24f, 8.74f, 5.99f,
            Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 9f, 13.5f, -22.5f, Direction.Axis.X));

    ShotgunRenderer()
    {
        super(5f, 0.75f, 5);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LTXIItems.SHOTGUN.get();
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 13) / 2;
        float bloom = 3 + 4 * LTXIRenderUtil.animationCurveB(controls.lerpTriggerTimer(weaponItem, partialTicks));

        blitSprite(graphics, centerX - 6 - bloom, centerY, 6, 13, crosshairColor, CIRCLE_BRACKET);
        blitMirroredUSprite(graphics, centerX + 1 + bloom, centerY, 6, 13, crosshairColor, CIRCLE_BRACKET);
    }

    @Override
    public TranslucentFillModel getMagazineFillModel()
    {
        return magazineFillModel;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LTXIRenderUtil.animationCurveA(recoilA);
    }
}