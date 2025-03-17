package liedge.limatech.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.*;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRocketEntity extends AutoTrackingProjectile
{
    protected BaseRocketEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    protected abstract boolean shouldPostGameEvent();

    protected abstract void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation);

    @Override
    public int getLifetime()
    {
        return 1000;
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation)
    {
        LivingEntity owner = getOwner();
        AABB aabb = AABB.ofSize(hitLocation, 5, 5, 5);

        level.getEntities(this, aabb, e -> LimaTechEntityUtil.isValidWeaponTarget(owner, e)).forEach(hitEntity -> damageTarget(level, owner, hitEntity, hitLocation));

        if (shouldPostGameEvent()) level.gameEvent(owner, LimaTechGameEvents.PROJECTILE_EXPLODED, hitLocation);
        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LimaTechSounds.ROCKET_EXPLODE.get(), SoundSource.PLAYERS, 4f, 0.9f);
        LimaNetworkUtil.spawnAlwaysVisibleParticle(level, new ColorParticleOptions(LimaTechParticles.HALF_SONIC_BOOM_EMITTER, LimaTechConstants.LIME_GREEN), hitLocation);

        discard();
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        super.tickProjectile(level, isClientSide);

        if (isClientSide)
        {
            double dx = getX() + (random.nextDouble() - random.nextDouble()) * 0.35d;
            double dz = getZ() + (random.nextDouble() - random.nextDouble()) * 0.35d;
            level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LimaTechParticles.COLOR_GLITTER, LimaTechConstants.LIME_GREEN, 1.75f), true, dx, getY(0.5d), dz, 0, 0, 0);
        }
    }

    public static class TurretRocket extends BaseRocketEntity
    {
        public TurretRocket(EntityType<?> type, Level level)
        {
            super(type, level);
        }

        public TurretRocket(Level level)
        {
            this(LimaTechEntities.TURRET_ROCKET.get(), level);
        }

        @Override
        protected boolean shouldPostGameEvent()
        {
            return true;
        }

        @Override
        protected void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation)
        {
            targetEntity.hurt(level.damageSources().source(LimaTechDamageTypes.TURRET_ROCKET, this, owner), (float) LimaTechMachinesConfig.ROCKET_TURRET_DAMAGE.getAsDouble());
        }
    }

    public static class DaybreakRocket extends BaseRocketEntity
    {
        private EquipmentUpgrades upgrades = EquipmentUpgrades.EMPTY;

        public DaybreakRocket(EntityType<?> type, Level level)
        {
            super(type, level);
        }

        public DaybreakRocket(Level level, EquipmentUpgrades upgrades)
        {
            this(LimaTechEntities.DAYBREAK_ROCKET.get(), level);
            this.upgrades = upgrades;
        }

        @Override
        protected boolean shouldPostGameEvent()
        {
            return upgrades.upgradeEffectTypeAbsent(LimaTechUpgradeEffectComponents.PREVENT_SCULK_VIBRATION.get());
        }

        @Override
        protected void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation)
        {
            LimaTechItems.ROCKET_LAUNCHER.get().causeProjectileDamage(upgrades, this, owner, LimaTechDamageTypes.ROCKET_LAUNCHER, targetEntity, LimaTechWeaponsConfig.ROCKET_LAUNCHER_BASE_DAMAGE.getAsDouble());
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag)
        {
            super.readAdditionalSaveData(tag);
            if (tag.contains("upgrades")) upgrades = LimaNbtUtil.lenientDecode(EquipmentUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registryAccess()), tag, "upgrades");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag)
        {
            super.addAdditionalSaveData(tag);
            if (upgrades != EquipmentUpgrades.EMPTY) tag.put("upgrades", LimaCoreCodecs.strictEncode(EquipmentUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registryAccess()), upgrades));
        }
    }
}