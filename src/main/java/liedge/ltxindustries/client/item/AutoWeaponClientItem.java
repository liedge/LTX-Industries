package liedge.ltxindustries.client.item;

import liedge.limacore.lib.TickTimer;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.world.item.ItemStack;

public abstract class AutoWeaponClientItem extends WeaponClientItem
{
    AutoWeaponClientItem(int crosshairWidth, int crosshairHeight, int animationA, int animationB)
    {
        super(crosshairWidth, crosshairHeight, animationA, animationB);
    }

    @Override
    public void onMainHandTick(ItemStack stack, WeaponItem weaponItem, ClientExtendedInput controls)
    {
        TickTimer timerB = controls.getAnimationTimerB();
        float speed;

        if (controls.isTriggerHeld())
        {
            speed = 1.25f;
        }
        else if (timerB.getTimerState() == TickTimer.State.RUNNING)
        {
            speed = 1.25f * LTXIRenderer.sineAnimationCurve(timerB.getProgressPercent());
        }
        else
        {
            speed = 0.025f;
        }

        updateSpinAnimation(controls, speed);
    }
}