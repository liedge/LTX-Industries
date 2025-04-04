package liedge.limatech.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.registry.game.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.MAGNUM_CROSSHAIR_1;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.MAGNUM_CROSSHAIR_2;

public class MagnumRenderProperties extends SimpleWeaponRenderProperties
{
    private static final float ANIMATION_FACTOR = 1 / 0.75f;

    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(7.01f, 9.01f, 7.51f, 8.99f, 10.99f, 13.49f, Direction.Axis.Z);

    MagnumRenderProperties()
    {
        super(17.5f, 0.375f, 6);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.MAGNUM.get();
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 1) / 2;

        float baseBloom;
        if (LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND))
        {
            float f = Math.min(1f, (player.getTicksUsingItem() + partialTicks) / 3f);
            baseBloom = 3f - (3f * f);
        }
        else
        {
            baseBloom = 3f;
        }

        float bloom = baseBloom + 6f * LimaTechRenderUtil.animationCurveB(controls.lerpTriggerTimer(weaponItem, partialTicks));

        float xl = centerX - 6 - bloom;
        float yu = centerY - 6 - bloom;
        float xr = centerX + 1 + bloom;
        float yd = centerY + 1 + bloom;

        MAGNUM_CROSSHAIR_1.directColorBlit(graphics, xl, yu, crosshairColor);
        MAGNUM_CROSSHAIR_2.directColorBlit(graphics, xr, yu, crosshairColor);
        MAGNUM_CROSSHAIR_2.directColorBlit(graphics, xl, yd, crosshairColor);
        MAGNUM_CROSSHAIR_1.directColorBlit(graphics, xr, yd, crosshairColor);
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