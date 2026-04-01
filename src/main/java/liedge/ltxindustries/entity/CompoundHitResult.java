package liedge.ltxindustries.entity;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.ToDoubleFunction;

public record CompoundHitResult(Vec3 origin, List<EntityHitResult> entityHits, HitResult impact)
{
    public static CompoundHitResult tracePath(Level level, LivingEntity sourceEntity, UpgradesContainerBase<?, ?> upgrades, double range, double deviation, int maxHits, double blockPierceDistance,
                                              DynamicClipContext.FluidCollisionPredicate fluidCollision, ToDoubleFunction<Entity> bbExpansionFunction)
    {
        // Path points
        Vec3 origin = sourceEntity.getEyePosition();
        Vec3 path = LimaCoreMath.createMotionVector(sourceEntity, range, deviation);
        Vec3 pathEnd = range <= 32 ? origin.add(path) : LimaBlockUtil.traceLoadedChunks(level, origin, path);
        Vec3 end;

        // Do block trace
        BlockHitResult blockTrace = level.clip(new DynamicClipContext(origin, pathEnd, sourceEntity, fluidCollision, blockPierceDistance));
        end = blockTrace.getLocation();

        TargetPredicate predicate = TargetPredicate.create(upgrades);
        List<EntityHitResult> entityHits = LTXIEntityUtil.traceEntities(level, sourceEntity, sourceEntity, origin, end, predicate, bbExpansionFunction).limit(maxHits).toList();

        HitResult impact = (entityHits.size() < maxHits) ? blockTrace : entityHits.getLast();

        return new CompoundHitResult(origin, entityHits, impact);
    }

    public static CompoundHitResult tracePath(Level level, LivingEntity sourceEntity, UpgradesContainerBase<?, ?> upgrades, double range, double deviation, int maxHits, double blockPierceDistance,
                                              DynamicClipContext.FluidCollisionPredicate fluidCollision, double bbExpansion)
    {
        return tracePath(level, sourceEntity, upgrades, range, deviation, maxHits, blockPierceDistance, fluidCollision, ignored -> bbExpansion);
    }

    public double traceDistance()
    {
        return origin.distanceTo(impactLocation());
    }

    public Vec3 impactLocation()
    {
        return impact.getLocation();
    }
}