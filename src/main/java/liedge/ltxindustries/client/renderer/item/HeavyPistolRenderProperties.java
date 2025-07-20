package liedge.ltxindustries.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

public class HeavyPistolRenderProperties extends SimpleWeaponRenderProperties
{
    private static final float ANIMATION_FACTOR = 1 / 0.75f;

    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(7.01f, 9.01f, 7.51f, 8.99f, 10.99f, 13.49f, Direction.Axis.Z);

    HeavyPistolRenderProperties()
    {
        super(17.5f, 0.375f, 6);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LTXIItems.HEAVY_PISTOL.get();
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 1) / 2;

        boolean aiming = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND);
        float triggerLerp = controls.lerpTriggerTimer(weaponItem, partialTicks);
        float bloom = 7f * LTXIRenderUtil.animationCurveB(triggerLerp);

        if (aiming)
        {
            float f = Math.min(1f, (player.getTicksUsingItem() + partialTicks) / 3f);
            bloom += 3f - 3f * f;
        }
        else
        {
            bloom += 3f;
        }

        float xl = centerX - 6 - bloom;
        float yu = centerY - 6 - bloom;
        float xr = centerX + 2 + bloom;
        float yd = centerY + 2 + bloom;

        blitSprite(graphics, xl, yu, 5, 5, crosshairColor, HEAVY_PISTOL_CROSSHAIR);
        blitMirroredUSprite(graphics, xr, yu, 5, 5, crosshairColor, HEAVY_PISTOL_CROSSHAIR);
        blitMirroredUSprite(graphics, xl, yd, 5, 5, crosshairColor, HEAVY_PISTOL_CROSSHAIR);
        blitSprite(graphics, xr, yd, 5, 5, crosshairColor, HEAVY_PISTOL_CROSSHAIR);

        if (aiming && triggerLerp == 0) graphics.fill(RenderType.gui(), centerX, centerY, centerX + 1, centerY + 1, LTXIConstants.LIME_GREEN.argb32());
    }

    @Override
    public TranslucentFillModel getMagazineFillModel()
    {
        return magazineFillModel;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        if (recoilA <= 0.125f)
        {
            return recoilA / 0.125f;
        }
        else if (recoilA <= 0.25f)
        {
            return 1f;
        }
        else
        {
            return 1 - ((recoilA - 0.25f) * ANIMATION_FACTOR);
        }
    }
}