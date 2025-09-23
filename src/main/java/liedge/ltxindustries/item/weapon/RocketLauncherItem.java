package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.entity.BaseRocketEntity;
import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

import static liedge.ltxindustries.registry.game.LTXIAttachmentTypes.WEAPON_CONTROLS;

public class RocketLauncherItem extends SemiAutoWeaponItem
{
    public RocketLauncherItem(Properties properties)
    {
        super(properties, 2, 1.5d, 60, LTXIItems.EXPLOSIVES_WEAPON_ENERGY, 1, 0);
    }

    private boolean isInTargetScanPath(Player player, Entity target)
    {
        if (LTXIEntityUtil.isValidWeaponTarget(player, target) && target.distanceTo(player) >= 10 && target instanceof LivingEntity)
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
            if (controls.getFocusedTarget() != null && !LTXIEntityUtil.isEntityAlive(controls.getFocusedTarget()))
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
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            BaseRocketEntity.DaybreakRocket missile = new BaseRocketEntity.DaybreakRocket(level, upgrades);
            missile.setOwner(player);

            LivingEntity focusedTarget = controls.getFocusedTarget();
            missile.aimAndSetPosFromShooter(player, Math.min(heldItem.getOrDefault(LTXIDataComponents.WEAPON_RANGE, getWeaponRange(heldItem)), MAX_PROJECTILE_SPEED), 0d);
            if (focusedTarget != null && controls.getTargetTicks() > 20) missile.setTargetEntity(focusedTarget);

            level.addFreshEntity(missile);
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());
        }

        level.playSound(player, player, LTXISounds.ROCKET_LAUNCHER_FIRE.get(), SoundSource.PLAYERS, 2f, Mth.randomBetween(level.random, 0.75f, 0.9f));
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 30;
    }
}