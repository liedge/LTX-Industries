package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SemiAutoWeaponItem extends WeaponItem
{
    protected SemiAutoWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        if (!player.level().isClientSide() && input.canStartShootingWeapon(heldItem, player, this))
        {
            input.shootWeapon(heldItem, player, this, true);
        }
    }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        return false;
    }
}