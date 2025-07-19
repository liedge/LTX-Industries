package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.RocketTurretBlockEntity;
import liedge.ltxindustries.entity.damage.TurretDamageSource;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.*;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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

        getEntitiesInAOE(level, hitLocation, 3d, owner).forEach(hitEntity -> damageTarget(level, owner, hitEntity, hitLocation));

        if (shouldPostGameEvent()) level.gameEvent(owner, LTXIGameEvents.PROJECTILE_EXPLODED, hitLocation);
        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LTXISounds.ROCKET_EXPLODE.get(), SoundSource.PLAYERS, 4f, 0.9f);
        LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LTXIParticles.HALF_SONIC_BOOM_EMITTER, LTXIConstants.LIME_GREEN), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);

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
            level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_GLITTER, LTXIConstants.LIME_GREEN, 1.75f), true, dx, getY(0.5d), dz, 0, 0, 0);
        }
    }

    public static class TurretRocket extends BaseRocketEntity
    {
        private MachineUpgrades upgrades = MachineUpgrades.EMPTY;
        private @Nullable BlockPos turretPos;

        public TurretRocket(EntityType<?> type, Level level, @Nullable BlockPos turretPos)
        {
            super(type, level);
            this.turretPos = turretPos;
        }

        public TurretRocket(EntityType<?> type, Level level)
        {
            this(type, level, null);
        }

        public TurretRocket(Level level, RocketTurretBlockEntity blockEntity)
        {
            this(LTXIEntities.TURRET_ROCKET.get(), level, blockEntity.getBlockPos());
            this.upgrades = blockEntity.getUpgrades();
        }

        @Override
        protected boolean shouldPostGameEvent()
        {
            return true;
        }

        @Override
        protected void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation)
        {
            float baseDamage = (float) LTXIMachinesConfig.ATMOS_TURRET_ROCKET_DAMAGE.getAsDouble();

            RocketTurretBlockEntity be = LimaBlockUtil.getSafeBlockEntity(level, turretPos, RocketTurretBlockEntity.class);
            if (be != null)
            {
                // Auto-wrap player owners with a Fake Player instance that has the turret's enchantments
                LTXIEntityUtil.hurtWithEnchantedFakePlayer((ServerLevel) level, targetEntity, owner, be.getUpgrades(), le -> TurretDamageSource.create(level, LTXIDamageTypes.TURRET_ROCKET, be, this, le, null), baseDamage);
            }
            else
            {
                targetEntity.hurt(level.damageSources().source(LTXIDamageTypes.TURRET_ROCKET, this, owner), baseDamage); // Standard damage source if parent turret is missing/invalid
            }
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag)
        {
            super.readAdditionalSaveData(tag);
            upgrades = readMachineUpgrades(tag);
            if (tag.contains("turret_pos")) turretPos = LimaNbtUtil.strictDecode(BlockPos.CODEC, NbtOps.INSTANCE, tag, "turret_pos");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag)
        {
            super.addAdditionalSaveData(tag);
            writeMachineUpgrades(upgrades, tag);
            if (turretPos != null) tag.put("turret_pos", LimaCoreCodecs.strictEncode(BlockPos.CODEC, NbtOps.INSTANCE, turretPos));
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
            this(LTXIEntities.DAYBREAK_ROCKET.get(), level);
            this.upgrades = upgrades;
        }

        @Override
        protected boolean shouldPostGameEvent()
        {
            return upgrades.noneMatch(LTXIUpgradeEffectComponents.PREVENT_VIBRATION.get(), (effect, $) -> effect.apply(null, LTXIGameEvents.PROJECTILE_EXPLODED));
        }

        @Override
        protected void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation)
        {
            LTXIItems.ROCKET_LAUNCHER.get().causeProjectileDamage(upgrades, this, owner, LTXIDamageTypes.ROCKET_LAUNCHER, targetEntity, LTXIWeaponsConfig.ROCKET_LAUNCHER_BASE_DAMAGE.getAsDouble());
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag)
        {
            super.readAdditionalSaveData(tag);
            upgrades = readEquipmentUpgrades(tag);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag)
        {
            super.addAdditionalSaveData(tag);
            writeEquipmentUpgrades(upgrades, tag);
        }
    }
}