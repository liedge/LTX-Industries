package liedge.limatech.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.util.LimaMathUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.util.config.LimaTechServerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.UUID;

import static liedge.limacore.util.LimaMathUtil.*;

public abstract class LimaTechProjectile extends Entity implements TraceableEntity
{
    private @Nullable UUID ownerUUID;
    private @Nullable LivingEntity owner;

    protected LimaTechProjectile(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    //#region Aiming methods
    public void aimAndSetPosFromShooter(LivingEntity shooter, double speed, double inaccuracy)
    {
        setPos(shooter.getEyePosition());
        Vec3 motion = createMotionVector(shooter, speed, inaccuracy);
        setRot(LimaMathUtil.getYRot(motion), LimaMathUtil.getXRot(motion));
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

    protected abstract void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation);

    protected abstract void tickProjectile(Level level, boolean isClientSide);

    @Override
    public @Nullable LivingEntity getOwner()
    {
        if (owner != null && !owner.isRemoved())
        {
            return owner;
        }
        else if (ownerUUID != null && level() instanceof ServerLevel serverLevel)
        {
            Entity e = serverLevel.getEntity(ownerUUID);
            if (e instanceof LivingEntity) owner = (LivingEntity) e;
            return owner;
        }
        else
        {
            return null;
        }
    }

    public void setOwner(@Nullable LivingEntity owner)
    {
        if (owner != null)
        {
            this.ownerUUID = owner.getUUID();
            this.owner = owner;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source.getDirectEntity() instanceof LimaTechProjectile || source.getEntity() == getOwner())
        {
            return false;
        }
        else
        {
            return super.hurt(source, amount);
        }
    }

    @Override
    public void baseTick()
    {
        level().getProfiler().push("entityBaseTick");

        // Discard projectile if lifetime is complete
        if (tickCount >= getLifetime())
        {
            this.discard();
        }

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

        // MC entity base checks
        handlePortal();
        checkBelowWorld();
        this.firstTick = false;

        level().getProfiler().pop();
    }

    @Override
    public void tick()
    {
        Level level = level();
        boolean isClientSide = level.isClientSide();

        if (!isClientSide)
        {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, hit -> canHitEntity(getOwner(), hit));
            if (hitResult.getType() != HitResult.Type.MISS)
            {
                Vec3 hitLocation;
                if (hitResult instanceof EntityHitResult)
                {
                    hitLocation = ((EntityHitResult) hitResult).getEntity().getBoundingBox().getCenter();
                }
                else
                {
                    hitLocation = hitResult.getLocation();
                }

                setPos(hitLocation);
                onProjectileHit(level, hitResult, hitLocation);
            }
        }

        tickProjectile(level, isClientSide);

        super.tick();
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

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        tickCount = tag.getInt("age");
        ownerUUID = LimaNbtUtil.getOptionalUUID(tag, LimaCommonConstants.KEY_OWNER);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putInt("age", tickCount);
        LimaNbtUtil.putOptionalUUID(tag, LimaCommonConstants.KEY_OWNER, ownerUUID);
    }

    // Utility methods
    public static boolean canHitEntity(@Nullable LivingEntity owner, Entity hitEntity)
    {
        // Don't hurt the owner and non-alive entities, and don't hurt item & experience orbs
        boolean flag1 = hitEntity.isAlive() && hitEntity != owner && LimaTechServerConfig.canWeaponDamage(hitEntity.getType());

        // Don't hurt traceable entities (i.e. projectiles) with the same owner
        boolean flag2 = !(hitEntity instanceof TraceableEntity) || ((TraceableEntity) hitEntity).getOwner() != owner;

        // Don't hurt owned mobs (if config)
        boolean flag3 = (!(hitEntity instanceof OwnableEntity) || LimaTechServerConfig.weaponsHurtOwnedEntities()) || ((OwnableEntity) hitEntity).getOwner() != owner;

        return flag1 && flag2 && flag3;
    }
}