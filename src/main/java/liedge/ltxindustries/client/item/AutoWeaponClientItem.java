package liedge.ltxindustries.client.item;

import liedge.ltxindustries.lib.weapons.ClientExtendedInput;

public abstract class AutoWeaponClientItem extends WeaponClientItem
{
    AutoWeaponClientItem(int crosshairWidth, int crosshairHeight, int animationA, int animationB)
    {
        super(crosshairWidth, crosshairHeight, animationA, animationB);
    }

    @Override
    protected float getSpinSpeed(ClientExtendedInput controls, boolean timerActive, float timerProgress)
    {
        return controls.isTriggerHeld() ? CHAMBER_FULL_SPEED : super.getSpinSpeed(controls, timerActive, timerProgress);
    }
}