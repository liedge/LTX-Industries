package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class FullAutoWeaponItem extends WeaponItem
{
    protected FullAutoWeaponItem(Properties properties, int baseMagCapacity, double baseRange, int baseReloadSpeed, Holder<Item> defaultAmmoItem)
    {
        super(properties, baseMagCapacity, baseRange, baseReloadSpeed, defaultAmmoItem);
    }

    @Override
    public void triggerPressed(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        if (input.canStartShootingWeapon(heldItem, player, this))
        {
            input.startTriggerHold(player);
        }
    }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        return input.canContinueShootingWeapon(heldItem, player, this);
    }

    @Override
    public void triggerHoldingTick(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        if (input.canStartShootingWeapon(heldItem, player, this))
        {
            input.shootWeapon(heldItem, player, this);
        }
    }
}