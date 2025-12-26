package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class SemiAutoWeaponItem extends WeaponItem
{
    protected SemiAutoWeaponItem(Properties properties, int baseMagCapacity, double baseRange, int baseReloadSpeed, Holder<Item> defaultAmmoItem, int baseMaxHits, double basePunchTrough)
    {
        super(properties, baseMagCapacity, baseRange, baseReloadSpeed, defaultAmmoItem, baseMaxHits, basePunchTrough);
    }

    @Override
    public void triggerPressed(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        if (!player.level().isClientSide() && input.canStartShootingWeapon(heldItem, player, this))
        {
            input.shootWeapon(heldItem, player, this, true);
        }
    }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, LTXIExtendedInput input)
    {
        return false;
    }
}