package liedge.limatech.item.weapon;

import liedge.limatech.entity.MissileEntity;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RocketLauncherItem extends SemiAutoWeaponItem
{
    public RocketLauncherItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            MissileEntity missile = new MissileEntity(level);
            missile.setOwner(player);
            missile.aimAndSetPosFromShooter(player, 2.2f, 0.1f);
            level.addFreshEntity(missile);
        }
    }

    @Override
    public Item getAmmoItem()
    {
        return LimaTechItems.EXPLOSIVES_AMMO_CANISTER.asItem();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 30;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 2;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 60;
    }

    @Override
    public int getRecoilA(ItemStack stack)
    {
        return 10;
    }
}