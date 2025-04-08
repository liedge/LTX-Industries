package liedge.limatech.entity;

import liedge.limacore.util.LimaMathUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public record CompoundHitResult(Vec3 origin, List<EntityHitResult> entityHits, HitResult impact)
{
    public static CompoundHitResult tracePath(Level level, LivingEntity sourceEntity, double range, double deviation, ClipContext.Block blockCollision, ClipContext.Fluid fluidCollision, ToDoubleFunction<Entity> bbExpansionFunction, int maxHits)
    {
        Vec3 origin = sourceEntity.getEyePosition();
        Vec3 path = LimaMathUtil.createMotionVector(sourceEntity, range, deviation);
        BlockHitResult blockTrace = level.clip(new ClipContext(origin, origin.add(path), blockCollision, fluidCollision, sourceEntity));
        Vec3 end = blockTrace.getLocation();

        List<EntityHitResult> entityHits = level.getEntities(sourceEntity, sourceEntity.getBoundingBox().expandTowards(path).inflate(0.3d), hit -> LimaTechEntityUtil.isValidWeaponTarget(sourceEntity, hit))
                .stream()
                .sorted(Comparator.comparingDouble(hit -> hit.distanceToSqr(origin)))
                .flatMap(hit -> Stream.ofNullable(LimaTechEntityUtil.clipEntityBoundingBox(hit, origin, end, bbExpansionFunction.applyAsDouble(hit))))
                .limit(maxHits)
                .toList();

        HitResult impact = (entityHits.size() < maxHits) ? blockTrace : entityHits.getLast();

        return new CompoundHitResult(origin, entityHits, impact);
    }

    public static CompoundHitResult tracePath(Level level, LivingEntity sourceEntity, double range, double deviation, ToDoubleFunction<Entity> bbExpansionFunction, int maxHits)
    {
        return tracePath(level, sourceEntity, range, deviation, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, bbExpansionFunction, maxHits);
    }

    public static CompoundHitResult tracePath(Level level, LivingEntity sourceEntity, double range, double deviation, double bbExpansion, int maxHits)
    {
        return tracePath(level, sourceEntity, range, deviation, ignored -> bbExpansion, maxHits);
    }
}