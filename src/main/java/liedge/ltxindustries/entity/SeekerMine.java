package liedge.ltxindustries.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.client.util.LimaCoreClientUtil;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.registry.game.LTXIEntities;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class SeekerMine extends PathfinderMob implements TraceableEntity
{
    private static final EntityDataAccessor<Integer> DATA_TARGETED_ENTITY = SynchedEntityData.defineId(SeekerMine.class, EntityDataSerializers.INT);

    private @Nullable UUID ownerId;
    private @Nullable LivingEntity owner;
    private int age;

    public float wheelSpin0;
    public float wheelSpin;
    public float capSpin0;
    public float capSpin;
    private @Nullable LivingEntity remoteTarget;

    public SeekerMine(EntityType<? extends SeekerMine> type, Level level)
    {
        super(type, level);
    }

    public SeekerMine(Level level)
    {
        this(LTXIEntities.SEEKER_MINE.get(), level);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new Explode(this));
        goalSelector.addGoal(4, new ChaseTarget(this));
        goalSelector.addGoal(5, new FollowOwner(this));

        final TargetingConditions.Selector selector = (target, level) -> LTXIEntityUtil.isValidBaseTarget(target, this) && LimaEntityUtil.getEntityHostility(level, target, getOwner()) == MobHostility.HOSTILE;
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, selector));
    }

    @Override
    public @Nullable LivingEntity getOwner()
    {
        if (owner == null && ownerId != null && level() instanceof ServerLevel level)
        {
            owner = LimaCoreObjects.tryCast(LivingEntity.class, level.getEntity(ownerId));
        }

        return owner;
    }

    public void setOwner(@Nullable LivingEntity owner)
    {
        this.owner = owner;
        this.ownerId = owner != null ? owner.getUUID() : null;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        super.setTarget(target);
        getEntityData().set(DATA_TARGETED_ENTITY, LimaEntityUtil.getEntityId(getTarget()));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor)
    {
        super.onSyncedDataUpdated(accessor);

        if (accessor.equals(DATA_TARGETED_ENTITY))
        {
            int eid = getEntityData().get(DATA_TARGETED_ENTITY);
            this.remoteTarget = LimaCoreClientUtil.getClientEntity(eid, LivingEntity.class);
        }
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!level().isClientSide())
        {
            age++;

            if (age >= 600)
            {
                discard();
            }
        }
        else
        {
            wheelSpin0 = wheelSpin;
            capSpin0 = capSpin;

            if (isAggressive() && onGround())
            {
                wheelSpin = (wheelSpin - 45) % 360f;
                capSpin = (capSpin - 20) % 360f;
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage)
    {
        return false;
    }

    @Override
    public void kill(ServerLevel level)
    {
        remove(RemovalReason.KILLED);
        gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    protected MovementEmission getMovementEmission()
    {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isPickable()
    {
        return false;
    }

    @Override
    public boolean isPushable()
    {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction()
    {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean attackable()
    {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);
        this.ownerId = input.read(LimaCommonConstants.KEY_OWNER, UUIDUtil.CODEC).orElse(null);
        this.age = input.getShortOr(LimaCommonConstants.KEY_AGE, (short) 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.storeNullable(LimaCommonConstants.KEY_OWNER, UUIDUtil.CODEC, ownerId);
        output.putShort(LimaCommonConstants.KEY_AGE, (short) age);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData)
    {
        super.defineSynchedData(entityData);
        entityData.define(DATA_TARGETED_ENTITY, 0);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return distance <= 16384;
    }

    public @Nullable LivingEntity getRemoteTarget()
    {
        return remoteTarget;
    }

    private boolean unableToGoToOwner()
    {
        LivingEntity owner = getOwner();

        if (owner == null)
        {
            return true;
        }
        else
        {
            return !owner.isAlive() || owner.isSpectator() || distanceToSqr(owner) >= 576d;
        }
    }

    // Attributes
    public static AttributeSupplier createAttributes()
    {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0d)
                .add(Attributes.MOVEMENT_SPEED, 0.4d)
                .add(Attributes.FOLLOW_RANGE, 96d)
                .add(Attributes.STEP_HEIGHT, 1.25d)
                .add(Attributes.JUMP_STRENGTH, 1.125d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1d)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1d)
                .build();
    }

    // Goals
    private static class Explode extends Goal
    {
        private final SeekerMine mine;

        Explode(SeekerMine mine)
        {
            this.mine = mine;
        }

        @Override
        public boolean canUse()
        {
            LivingEntity target = mine.getTarget();
            return target != null && target.isAlive() && mine.distanceToSqr(target) <= 9d;
        }

        @Override
        public boolean requiresUpdateEveryTick()
        {
            return true;
        }

        @Override
        public void tick()
        {
            mine.discard();

            Vec3 pos = mine.position();

            LivingEntity target = mine.getTarget();
            if (target != null)
            {
                pos = pos.add(target.position()).scale(0.5d);
            }

            ServerLevel level = getServerLevel(mine);

            // Main exp
            level.playSound(mine, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 2f, Mth.randomBetween(mine.random, 0.775f, 0.95f));
            level.getEntities(mine, AABB.ofSize(pos, 4f, 4f, 4f), e -> LTXIEntityUtil.isValidBaseTarget(e, mine.getOwner()))
                    .forEach(hit -> {
                        hit.hurtServer(level, level.damageSources().explosion(mine, mine.getOwner()), 40f);
                    });

            double arc = Math.PI * 2 / 8d;
            for (int i = 0; i < 8; i++)
            {
                double a = arc * i;
                double hv = Mth.nextDouble(level.getRandom(), 0.1d, 0.2d);
                double dx = Math.cos(a) * hv;
                double dy = Mth.nextDouble(level.getRandom(), 0.25d, 0.4d);
                double dz = Math.sin(a) * hv;

                ClusterMunition sub = new ClusterMunition(level);
                sub.setPos(pos);
                sub.setDeltaMovement(dx, dy, dz);
                level.addFreshEntity(sub);
            }
        }
    }

    private static class ChaseTarget extends Goal
    {
        private final PathfinderMob mob;
        private @Nullable Path path;
        private int ticksUntilNextPathRecalc;
        private long lastCanUseCheck;

        ChaseTarget(PathfinderMob mob)
        {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            long time = mob.level().getGameTime();

            if (time - lastCanUseCheck < 20L)
            {
                return false;
            }
            else
            {
                lastCanUseCheck = time;
                LivingEntity target = mob.getTarget();

                if (target == null || !target.isAlive())
                {
                    return false;
                }
                else
                {
                    path = mob.getNavigation().createPath(target, 0);
                    return path != null;
                }
            }
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity target = mob.getTarget();

            if (target == null || !target.isAlive())
            {
                return false;
            }
            else
            {
                return !(target instanceof Player player) || (!player.isSpectator() && !player.isCreative());
            }
        }

        @Override
        public void start()
        {
            mob.getNavigation().moveTo(path, 1d);
            mob.setAggressive(true);
            ticksUntilNextPathRecalc = 0;
        }

        @Override
        public void stop()
        {
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick()
        {
            return true;
        }

        @Override
        public void tick()
        {
            LivingEntity target = mob.getTarget();
            if (target != null)
            {
                mob.getLookControl().setLookAt(target, 30f, 30f);
                ticksUntilNextPathRecalc = Math.max(ticksUntilNextPathRecalc - 1, 0);

                if (ticksUntilNextPathRecalc == 0)
                {
                    ticksUntilNextPathRecalc = mob.distanceToSqr(target) >= 256 ? 20 : 10;

                    if (!mob.getNavigation().moveTo(target, 1d)) ticksUntilNextPathRecalc += 10;

                    ticksUntilNextPathRecalc = adjustedTickDelay(ticksUntilNextPathRecalc);
                }
            }
        }
    }

    private static class FollowOwner extends Goal
    {
        private final SeekerMine mine;
        private @Nullable LivingEntity owner;
        private int timeToRecalcPath;

        FollowOwner(SeekerMine mine)
        {
            this.mine = mine;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            LivingEntity owner = mine.getOwner();

            if (owner == null || mine.unableToGoToOwner() || mine.getTarget() != null)
            {
                return false;
            }
            else
            {
                this.owner = owner;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse()
        {
            return !mine.unableToGoToOwner() && mine.getTarget() == null;
        }

        @Override
        public void start()
        {
            timeToRecalcPath = 0;
        }

        @Override
        public void stop()
        {
            owner = null;
        }

        @Override
        public void tick()
        {
            if (owner == null) return;

            if (--timeToRecalcPath <= 0)
            {
                timeToRecalcPath = adjustedTickDelay(10);
                mine.getNavigation().moveTo(owner, 1d);
            }
        }
    }
}