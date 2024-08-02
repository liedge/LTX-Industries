package liedge.limatech.item.weapon;

import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.entity.WeaponRayTrace;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechDamageTypes;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechParticles;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MagnumWeaponItem extends SemiAutoWeaponItem
{
    public MagnumWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            WeaponRayTrace rayTrace = WeaponRayTrace.traceOnPath(level, player, 25d, 1.25d, 0.325f, 1000);
            rayTrace.hits().forEach(hit -> hit.hurt(WeaponDamageSource.directEntityDamage(player, LimaTechDamageTypes.WEAPON_DAMAGE, this), calculateDamage(hit)));
            LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.LIGHTFRAG_TRACER.get(), rayTrace.start(), rayTrace.end());
        }
    }

    @Override
    public Item getAmmoItem()
    {
        return LimaTechItems.LEGENDARY_AMMO_CANISTER.asItem();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 15;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 7;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 30;
    }

    @Override
    public int getRecoilA(ItemStack stack)
    {
        return 11;
    }

    private float calculateDamage(Entity hit)
    {
        if (hit instanceof LivingEntity living)
        {
            return Math.max(living.getMaxHealth() / 7f, 50f);
        }

        return 50f;
    }
}