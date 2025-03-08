package liedge.limatech.item.weapon;

import liedge.limatech.entity.BaseRocketEntity;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechSounds;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

import static liedge.limatech.registry.LimaTechAttachmentTypes.WEAPON_CONTROLS;

public class RocketLauncherWeaponItem extends SemiAutoWeaponItem
{
    public RocketLauncherWeaponItem(Properties properties)
    {
        super(properties);
    }

    private boolean isInTargetScanPath(Player player, Entity target)
    {
        if (LimaTechEntityUtil.isValidWeaponTarget(player, target) && target.distanceTo(player) >= 10 && target instanceof LivingEntity)
        {
            Vec3 look = player.getViewVector(1f);
            Vec3 path = target.getBoundingBox().getCenter().subtract(player.getEyePosition());

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
        Level level = player.level();

        if (!level.isClientSide() && canTryFocus)
        {
            Vec3 scanPath = player.getViewVector(1f).scale(60);

            Optional<Entity> entity = level.getEntities(player, player.getBoundingBox().expandTowards(scanPath), e -> isInTargetScanPath(player, e))
                    .stream()
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                    .filter(e -> {
                        HitResult blockTrace = level.clip(new ClipContext(player.getEyePosition(), e.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                        return blockTrace.getType() == HitResult.Type.MISS || e.getBoundingBox().contains(blockTrace.getLocation());
                    });

            if (entity.isPresent() && entity.get() instanceof LivingEntity livingEntity)
            {
                if (!player.level().isClientSide()) controls.asServerControls().setFocusedTargetAndNotify(player, livingEntity);

                return true;
            }
        }

        return false;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration)
    {
        livingEntity.getExistingData(WEAPON_CONTROLS).ifPresent(controls -> {
            if (controls.getFocusedTarget() != null && !controls.getFocusedTarget().isAlive())
            {
                livingEntity.stopUsingItem();
            }
        });
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count)
    {
        if (entity instanceof Player player && !player.level().isClientSide())
        {
            player.getData(WEAPON_CONTROLS).asServerControls().setFocusedTargetAndNotify(player, null);
        }
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
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            BaseRocketEntity.DaybreakRocket missile = new BaseRocketEntity.DaybreakRocket(level, upgrades);
            missile.setOwner(player);

            LivingEntity focusedTarget = controls.getFocusedTarget();
            missile.aimAndSetPosFromShooter(player, calculateProjectileSpeed(upgrades, 2.75d), 0d);
            if (focusedTarget != null && controls.getTargetTicks() > 20) missile.setTargetEntity(focusedTarget);

            level.addFreshEntity(missile);

            postWeaponFiredGameEvent(upgrades, level, player);
        }

        level.playSound(player, player, LimaTechSounds.ROCKET_LAUNCHER_FIRE.get(), SoundSource.PLAYERS, 2f, Mth.randomBetween(level.random, 0.75f, 0.9f));
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LimaTechItems.ROCKET_LAUNCHER_AMMO.get();
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