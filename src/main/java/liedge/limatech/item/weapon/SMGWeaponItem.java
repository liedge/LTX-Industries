package liedge.limatech.item.weapon;

import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.entity.WeaponRayTrace;
import liedge.limatech.lib.weapons.AbstractWeaponInput;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import liedge.limatech.registry.LimaTechDamageTypes;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechParticles;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SMGWeaponItem extends WeaponItem
{
    public SMGWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponInput input)
    {
        if (input.canShootWeapon(heldItem, player, this))
        {
            input.setTriggerHeld(true);
        }
    }

    @Override
    public void triggerRelease(ItemStack heldItem, Player player, AbstractWeaponInput input, boolean releasedByPlayer) { }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponInput input)
    {
        return input.canShootWeapon(heldItem, player, this);
    }

    @Override
    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponInput input)
    {
        input.shootWeapon(heldItem, player, this, false);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            WeaponRayTrace rayTrace = WeaponRayTrace.traceOnPath(level, player, 12d, 3.5d, 0.2d, 1);
            rayTrace.hits().forEach(e -> e.hurt(WeaponDamageSource.directEntityDamage(player, LimaTechDamageTypes.LIGHTFRAG, this), modifyDamage(e, 2f)));
            LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.LIGHTFRAG_TRACER.get(), rayTrace.start(), rayTrace.end());
        }
    }

    private float modifyDamage(Entity hit, float damage)
    {
        if (hit.getType().is(LimaTechTags.EntityTypes.ELITE_MOBS))
        {
            return damage * 0.85f;
        }
        else if (hit.getType().is(LimaTechTags.EntityTypes.BOSS_MOBS))
        {
            return damage * 0.4f;
        }

        return damage;
    }

    @Override
    public Item getAmmoItem()
    {
        return LimaTechItems.AUTO_AMMO_CANISTER.asItem();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 0;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 45;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 20;
    }

    @Override
    public int getRecoilA(ItemStack stack)
    {
        return 1;
    }

    @Override
    public int getRecoilB(ItemStack stack)
    {
        return 2;
    }

    @Override
    public void killedByWeapon(LivingEntity killerEntity, LivingEntity killedEntity)
    {
        if (!killerEntity.level().isClientSide() && killerEntity instanceof Player)
        {
            killerEntity.getData(LimaTechAttachmentTypes.BUBBLE_SHIELD).addShieldHealth(4, 40);
        }
    }
}