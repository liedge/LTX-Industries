package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class FullAutoWeaponItem extends WeaponItem
{
    protected FullAutoWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        if (!player.level().isClientSide() && input.canStartShootingWeapon(heldItem, player, this))
        {
            input.startHoldingTrigger(heldItem, player, this);
        }
    }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        if (player.level().isClientSide())
        {
            return true;
        }
        else
        {
            return input.canContinueShootingWeapon(heldItem, player, this);
        }
    }

    @Override
    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        if (input.canStartShootingWeapon(heldItem, player, this))
        {
            input.shootWeapon(heldItem, player, this, false);
        }
    }
}