package liedge.ltxindustries.entity;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import static liedge.limacore.lib.math.LimaCoreMath.*;

public abstract class LTXIProjectileEntity extends UpgradesAwareEntity
{
    protected LTXIProjectileEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    //#region Aiming methods
    public void aimAndSetPosFromShooter(LivingEntity shooter, double speed, double inaccuracy)
    {
        setPos(shooter.getEyePosition());
        Vec3 motion = createMotionVector(shooter, speed, inaccuracy);
        setRot(LimaCoreMath.getYRot(motion), LimaCoreMath.getXRot(motion));
        setDeltaMovement(motion);
    }

    public void aimTowardsPoint(Vec3 target, double speed, double inaccuracy)
    {
        Vector2f angles = xyRotBetweenPoints(position(), target);
        setRot(angles.y, angles.x);
        setDeltaMovement(createMotionVector(angles.x, angles.y, speed, inaccuracy));
    }

    public void aimTowardsPoint(Vec3 target, double speed, double inaccuracy, float maxTurnAngle)
    {
        //movement ONLY!
        Vector2f angles = xyRotBetweenPoints(position(), target);
        float yr = Mth.approachDegrees(getYRot(), angles.y, maxTurnAngle);
        float xr = Mth.approachDegrees(getXRot(), angles.x, maxTurnAngle);
        setDeltaMovement(createMotionVector(xr, yr, speed, inaccuracy));

        /*
        Vector2f angles = xyRotBetweenPoints(position(), target);
        float yr = Mth.approachDegrees(getYRot(), angles.y, maxTurnAngle);
        float xr = Mth.approachDegrees(getXRot(), angles.x, maxTurnAngle);
        setRot(yr, xr);
        setDeltaMovement(createMotionVector(xr, yr, speed, inaccuracy));
        */
    }

    public void aimAtEntity(Entity target, double speed)
    {
        aimTowardsPoint(target.getBoundingBox().getCenter(), speed, 0);
    }

    public void aimAtEntity(Entity target, double speed, float maxTurnAngle)
    {
        aimTowardsPoint(target.getBoundingBox().getCenter(), speed, 0, maxTurnAngle);
    }

    public void rotateTowardsMotion()
    {
        Vec3 delta = getDeltaMovement();

        setXRot(Mth.rotLerp(0.5f, xRotO, LimaCoreMath.getXRot(delta)));
        setYRot(Mth.rotLerp(0.5f, yRotO, LimaCoreMath.getYRot(delta)));
    }
    //#endregion

    public abstract int getLifetime();

    protected float getProjectileGravity()
    {
        return 0f;
    }

    protected ClipContext blockTraceContext(Vec3 start, Vec3 path)
    {
        return new ClipContext(start, start.add(path), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
    }

    protected HitResult tracePath(Level level)
    {
        Vec3 start = position();
        Vec3 path = getDeltaMovement();

        BlockHitResult blockTrace = level.clip(blockTraceContext(start, path));
        Vec3 end = blockTrace.getLocation();

        TargetPredicate predicate = TargetPredicate.create(level, getUpgrades());
        double bbSize = getBoundingBox().getSize() * 0.9d;
        EntityHitResult entityTrace = LTXIEntityUtil.traceEntities(level, this, getOwner(), start, end, predicate, $ -> bbSize).findFirst().orElse(null);

        return entityTrace != null ? entityTrace : blockTrace;
    }

    protected abstract CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation);

    protected void tickServer(ServerLevel level, @Nullable LivingEntity owner) {}

    protected void tickClient(Level level) {}

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source.getDirectEntity() instanceof LTXIProjectileEntity || source.getEntity() == getOwner())
        {
            return false;
        }
        else
        {
            return super.hurt(source, amount);
        }
    }

    @Override
    public final void tick()
    {
        super.tick();
        Level level = level();

        if (level instanceof ServerLevel serverLevel)
        {
            LivingEntity owner = getOwner();
            HitResult hitResult = tracePath(level);

            if (hitResult.getType() != HitResult.Type.MISS)
            {
                Vec3 hitLocation = hitResult.getLocation();
                CollisionResult result = onCollision(serverLevel, owner, hitResult, hitLocation);

                if (result != CollisionResult.NO_OP)
                {
                    boolean postEvent = getUpgrades().noneMatch(LTXIUpgradeEffectComponents.SUPPRESS_VIBRATIONS, (effect, rank) -> effect.test(EquipmentSlot.MAINHAND, LTXIGameEvents.PROJECTILE_IMPACT));
                    if (postEvent) serverLevel.gameEvent(owner, LTXIGameEvents.PROJECTILE_IMPACT, hitLocation);
                }

                if (result == CollisionResult.DESTROY)
                {
                    discard();
                    return;
                }
            }

            tickServer(serverLevel, owner);
        }
        else
        {
            tickClient(level);
        }

        // Motion update
        float gravity = getProjectileGravity();
        if (!isNoGravity() && gravity > 0)
        {
            Vec3 delta = getDeltaMovement();
            setDeltaMovement(delta.x, delta.y - gravity, delta.z);
        }

        rotateTowardsMotion();
        setPos(position().add(getDeltaMovement()));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return distance <= 16384;
    }

    @Override
    public void lerpMotion(double x, double y, double z)
    {
        setDeltaMovement(x, y, z);
        if (xRotO == 0f && yRotO == 0f)
        {
            setYRot(toDeg(Mth.atan2(z, x)) - 90f);
            setXRot(toDeg(-Mth.atan2(y, vec2Length(x, z))));
            yRotO = getYRot();
            xRotO = getXRot();
            moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }
}