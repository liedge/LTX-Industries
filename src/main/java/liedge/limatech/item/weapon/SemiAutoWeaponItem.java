package liedge.limatech.item.weapon;

import liedge.limatech.lib.weapons.AbstractWeaponInput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SemiAutoWeaponItem extends WeaponItem
{
    protected SemiAutoWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponInput input)
    {
        if (!player.level().isClientSide() && input.canShootWeapon(heldItem, player, this))
        {
            input.shootWeapon(heldItem, player, this, true);
        }
    }

    @Override
    public void triggerRelease(ItemStack heldItem, Player player, AbstractWeaponInput input, boolean releasedByPlayer) {}
}