package liedge.limatech.client.renderer.item;

import liedge.limacore.lib.LimaColor;
import liedge.limatech.client.LimaTechClient;
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

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.MAGNUM_CROSSHAIR_1;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.MAGNUM_CROSSHAIR_2;

public class MagnumRenderProperties extends SimpleWeaponRenderProperties
{
    private static final float ANIMATION_FACTOR = 1 / 0.75f;

    private final WeaponAmmoDisplay ammoDisplay = WeaponAmmoDisplay.createDisplay(7.01f, 9.01f, 7.51f, 8.99f, 10.99f, 13.49f, Direction.Axis.Z, null);

    MagnumRenderProperties()
    {
        super(25f, 0.3125f);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.MAGNUM.get();
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

        float bloom = 2.25f + 8f * LimaTechClient.animationCurveB(weaponInput.lerpTriggerTimer(weaponItem, partialTicks));

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
    protected WeaponAmmoDisplay mainAmmoDisplay()
    {
        return ammoDisplay;
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