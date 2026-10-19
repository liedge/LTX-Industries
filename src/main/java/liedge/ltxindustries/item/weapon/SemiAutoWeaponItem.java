package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SemiAutoWeaponItem extends WeaponItem
{
    protected SemiAutoWeaponItem(Properties properties, int baseMagCapacity, double baseRange, int baseReloadSpeed)
    {
        super(properties, baseMagCapacity, baseRange, baseReloadSpeed, WeaponReloadSource.commonEnergy());
    }

    @Override
    public boolean triggerPressed(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        if (input.canStartShootingWeapon(heldItem, player, this))
        {
            input.shootWeapon(heldItem, player, this);
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        return false;
    }
}