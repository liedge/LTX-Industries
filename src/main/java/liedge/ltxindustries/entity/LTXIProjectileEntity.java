package liedge.ltxindustries.entity;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.world.level.DynamicClipContext;
import liedge.ltxindustries.world.level.FluidCollision;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

import static liedge.limacore.lib.math.LimaCoreMath.createMotionVector;
import static liedge.limacore.lib.math.LimaCoreMath.xyRotBetweenPoints;

public abstract class LTXIProjectileEntity extends UpgradesAwareEntity
{
    private double blockPierce = -1;

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
        needsSync = true;
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
    }

    public void aimAtEntity(Entity target, double speed)
    {
        aimTowardsPoint(target.getBoundingBox().getCenter(), speed, 0);
    }

    public void aimAtEntity(Entity target, double speed, float maxTurnAngle)
    {
        aimTowardsPoint(target.getBoundingBox().getCenter(), speed, 0, maxTurnAngle);
    }

    //#endregion

    private double getBlockPierce(ServerLevel level, Upgrades upgrades)
    {
        if (blockPierce < 0)
        {
            LootContext context = LimaLootUtil.emptyLootContext(level);
            blockPierce = upgrades.runValueOps(LTXIUpgradeEffectComponents.BLOCK_PIERCE, context, 0d);
        }

        return blockPierce;
    }

    protected ClipContext blockTraceContext(Vec3 start, Vec3 end, double blockPierce)
    {
        return new DynamicClipContext(start, end, this, FluidCollision.NONE, blockPierce);
    }

    private HitResult tracePath(ServerLevel level, @Nullable LivingEntity owner)
    {
        Upgrades upgrades = getUpgrades();

        Vec3 start = position();
        BlockHitResult blockTrace = level.clip(blockTraceContext(start, start.add(getDeltaMovement()), getBlockPierce(level, upgrades)));
        Vec3 end = blockTrace.getLocation();

        TargetPredicate predicate = TargetPredicate.create(upgrades);
        double bbSize = getBoundingBox().getSize() * 0.9d;
        EntityHitResult entityTrace = LTXIEntityUtil.traceEntities(level, this, owner, start, end, predicate, _ -> bbSize).findFirst().orElse(null);

        return entityTrace != null ? entityTrace : blockTrace;
    }

    protected abstract CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation);

    @Override
    protected void tickServer(ServerLevel level)
    {
        LivingEntity owner = getOwner();
        HitResult hitResult = tracePath(level, owner);

        if (hitResult.getType() != HitResult.Type.MISS)
        {
            Vec3 hitLocation = hitResult.getLocation();
            CollisionResult result = onCollision(level, owner, hitResult, hitLocation);

            if (result != CollisionResult.NO_OP)
            {
                boolean postEvent = getUpgrades().noneMatch(LTXIUpgradeEffectComponents.SUPPRESS_VIBRATIONS, (effect, _) -> effect.test(EquipmentSlot.MAINHAND, LTXIGameEvents.PROJECTILE_IMPACT));
                if (postEvent) level.gameEvent(owner, LTXIGameEvents.PROJECTILE_IMPACT, hitLocation);
            }

            if (result == CollisionResult.DESTROY)
            {
                discard();
            }
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return distance <= 16384;
    }
}