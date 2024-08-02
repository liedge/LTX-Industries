package liedge.limatech.entity;

import liedge.limacore.util.LimaMathUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public record WeaponRayTrace(Vec3 start, Vec3 end, List<Entity> hits)
{
    public static WeaponRayTrace traceWithDynamicMagnetism(Level level, LivingEntity shooter, double range, double inaccuracy, ToDoubleFunction<Entity> magnetismFunction, int maxHits)
    {
        Vec3 start = shooter.getEyePosition();
        Vec3 path = LimaMathUtil.createMotionVector(shooter, range, inaccuracy);
        Vec3 end = level.clip(new ClipContext(start, start.add(path), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter)).getLocation();

        List<Entity> hits = level.getEntities(shooter, shooter.getBoundingBox().expandTowards(path).inflate(1), e -> LimaTechProjectile.canHitEntity(shooter, e))
                .stream()
                .filter(hit -> {
                    AABB bb = hit.getBoundingBox().inflate(Math.max(hit.getPickRadius(), magnetismFunction.applyAsDouble(hit)));
                    return bb.contains(start) || bb.clip(start, end).map(v -> v.distanceToSqr(start) <= (range * range)).orElse(false);
                })
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start)))
                .limit(maxHits)
                .toList();

        return new WeaponRayTrace(start, end, hits);
    }

    public static WeaponRayTrace traceOnPath(Level level, LivingEntity shooter, double range, double inaccuracy, double magnetism, int maxHits)
    {
        return traceWithDynamicMagnetism(level, shooter, range, inaccuracy, ignored -> magnetism, maxHits);
    }
}