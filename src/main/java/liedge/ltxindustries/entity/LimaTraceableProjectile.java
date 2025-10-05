package liedge.ltxindustries.entity;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;

import static liedge.limacore.lib.math.LimaCoreMath.*;

public abstract class LimaTraceableProjectile extends LimaTraceableEntity
{
    protected LimaTraceableProjectile(EntityType<?> type, Level level)
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
        Vector2f angles = xyRotBetweenPoints(position(), target);
        float yr = Mth.approachDegrees(getYRot(), angles.y, maxTurnAngle);
        float xr = Mth.approachDegrees(getXRot(), angles.x, maxTurnAngle);
        setRot(yr, xr);
        setDeltaMovement(createMotionVector(xr, yr, speed, inaccuracy));
    }

    public void aimTowardsEntity(Entity target, double speed, double inaccuracy)
    {
        aimTowardsPoint(target.getBoundingBox().getCenter(), speed, inaccuracy);
    }

    public void aimTowardsEntity(Entity target, double speed, double inaccuracy, float maxTurnAngle)
    {
        aimTowardsPoint(target.getBoundingBox().getCenter(), speed, inaccuracy, maxTurnAngle);
    }
    //#endregion

    public abstract int getLifetime();

    protected float getProjectileGravity()
    {
        return 0f;
    }

    protected HitResult tracePath(Level level)
    {
        return LTXIEntityUtil.traceProjectileEntityPath(level, this, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, getBoundingBox().getSize());
    }

    protected abstract void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation);

    protected abstract void tickProjectile(Level level, boolean isClientSide);

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source.getDirectEntity() instanceof LimaTraceableProjectile || source.getEntity() == getOwner())
        {
            return false;
        }
        else
        {
            return super.hurt(source, amount);
        }
    }

    @Override
    public final void baseTick()
    {
        Level level = level();
        level.getProfiler().push("entityBaseTick");

        // Discard projectile if lifetime is complete
        if (tickCount >= getLifetime())
        {
            this.discard();
            return;
        }

        // Do fluid calculations
        updateFluidHeightOnly(level);

        // Handle motion
        final double x = getX();
        final double y = getY();
        final double z = getZ();

        this.yRotO = getYRot();
        this.xRotO = getXRot();
        this.xo = x;
        this.yo = y;
        this.zo = z;

        Vec3 delta = getDeltaMovement();

        float gravity = getProjectileGravity();
        if (!isNoGravity() && gravity > 0)
        {
            setDeltaMovement(delta.x, delta.y - gravity, delta.z);
        }

        setPos(x + delta.x, y + delta.y, z + delta.z);
        checkInsideBlocks();

        // MC entity base checks
        handlePortal();
        checkBelowWorld();
        this.firstTick = false;

        level().getProfiler().pop();
    }

    @Override
    public final void tick()
    {
        Level level = level();
        boolean isClientSide = level.isClientSide();

        if (!isClientSide)
        {
            HitResult hitResult = tracePath(level);
            if (hitResult.getType() != HitResult.Type.MISS)
            {
                Vec3 hitLocation = hitResult.getLocation();
                setPos(hitLocation);
                onProjectileHit(level, hitResult, hitLocation);

                boolean postGameEvent = getUpgrades().noneMatch(LTXIUpgradeEffectComponents.PREVENT_VIBRATION.get(), (effect, rank) -> effect.apply(null, LTXIGameEvents.PROJECTILE_IMPACT));
                if (postGameEvent)
                {
                    level.gameEvent(getOwner(), LTXIGameEvents.PROJECTILE_IMPACT, hitLocation);
                }

                this.discard();
                return;
            }
        }

        tickProjectile(level, isClientSide);
        baseTick();
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