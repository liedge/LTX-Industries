package liedge.limatech.item.weapon;

import liedge.limatech.LimaTech;
import liedge.limatech.entity.BaseMissileEntity;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechSounds;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrades;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public class RocketLauncherItem extends SemiAutoWeaponItem
{
    public RocketLauncherItem(Properties properties)
    {
        super(properties);
    }

    private boolean canLockOnTo(Player player, Entity target)
    {
        if (LimaTechEntityUtil.isValidWeaponTarget(player, target) && target.distanceTo(player) >= 10 && target instanceof LivingEntity)
        {
            Vec3 look = player.getViewVector(1f);
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 path = new Vec3(targetCenter.x - player.getX(), targetCenter.y - player.getEyeY(), targetCenter.z - player.getZ());

            double distanceBetween = path.length();
            double dot = look.dot(path.normalize());

            return dot > 1d - (target.getBoundingBox().getSize() / 16d) / distanceBetween;
        }

        return false;
    }

    @Override
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        boolean canTryFocus = super.canFocusReticle(heldItem, player, controls);

        if (canTryFocus)
        {
            Level level = player.level();

            Vec3 scanPath = player.getViewVector(1f).scale(50);
            level.getEntities(player, player.getBoundingBox().expandTowards(scanPath), e -> canLockOnTo(player, e)).stream()
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                    .ifPresent(e -> {
                        LimaTech.LOGGER.debug("Found valid lock-on target: {}", e.getName().getString());
                        // TODO Do lock on targeting stuff here, pending
                    });
        }

        return false;
    }

    @Override
    public int getEnergyCapacity(ItemStack stack)
    {
        return LimaTechWeaponsConfig.ROCKET_LAUNCHER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getEnergyReloadCost(ItemStack stack)
    {
        return LimaTechWeaponsConfig.ROCKET_LAUNCHER_ENERGY_AMMO_COST.getAsInt();
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            BaseMissileEntity.RocketLauncherMissile missile = new BaseMissileEntity.RocketLauncherMissile(level, upgrades);
            missile.setOwner(player);
            missile.aimAndSetPosFromShooter(player, calculateProjectileSpeed(upgrades, 2.2f), 0.1f);
            level.addFreshEntity(missile);

            postWeaponFiredGameEvent(upgrades, level, player);
        }

        level.playSound(player, player, LimaTechSounds.ROCKET_LAUNCHER_FIRE.get(), SoundSource.PLAYERS, 2f, Mth.randomBetween(level.random, 0.75f, 0.9f));
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LimaTechItems.EXPLOSIVES_AMMO_CANISTER.asItem();
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 2;
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 30;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 60;
    }
}