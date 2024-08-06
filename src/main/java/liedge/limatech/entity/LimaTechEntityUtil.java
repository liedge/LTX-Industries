package liedge.limatech.entity;

import liedge.limatech.LimaTechTags;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.stream.Stream;

public final class LimaTechEntityUtil
{
    private LimaTechEntityUtil() {}

    public static boolean isValidWeaponTarget(@Nullable Entity owner, Entity target)
    {
        // Don't hurt the owner, removed/dead entities and immune dataTag entities
        if (!target.isAlive() || target == owner || target.getType().is(LimaTechTags.EntityTypes.IMMUNE_TO_LTX_WEAPONS))
            return false;

        // Don't hurt traceable entities with the same owner
        if (target instanceof TraceableEntity traceable && traceable.getOwner() == owner)
            return false;

        // Don't hurt ownable mobs depending on config
        if (target instanceof OwnableEntity ownable && ownable.getOwner() == owner && !LimaTechWeaponsConfig.WEAPONS_HURT_OWNED_ENTITIES.getAsBoolean())
            return false;

        // Finally, don't hurt the vehicle entity owner is riding (if any)
        return owner == null || !owner.isPassengerOfSameVehicle(target);
    }

    /**
     * Determines if a vector intersects/clips with an entity's bounding box.
     * @param target The entity who's bounding box will be checked.
     * @param start The start position of the path vector to be checked.
     * @param end The end position of the path vector to be checked.
     * @param bbExpansion How much to expand the target's bounding box before performing the clip check.
     * @return The hit result containing entity and the clip point the entity's bounding box or null if vector did not clip entity's bounding box.
     */
    public static @Nullable EntityHitResult clipEntityBoundingBox(Entity target, Vec3 start, Vec3 end, double bbExpansion)
    {
        AABB bb = target.getBoundingBox().inflate(Math.max(target.getPickRadius(), bbExpansion));
        if (bb.contains(start))
        {
            return new EntityHitResult(target, start);
        }
        else
        {
            return bb.clip(start, end).map(vec -> new EntityHitResult(target, vec)).orElse(null);
        }
    }

    public static <T extends Entity & TraceableEntity> HitResult traceProjectileEntityPath(Level level, T projectile, ClipContext.Block blockCollision, ClipContext.Fluid fluidCollision, double bbExpansion)
    {
        Vec3 origin = projectile.position();
        Vec3 path = projectile.getDeltaMovement();
        BlockHitResult blockTrace = level.clip(new ClipContext(origin, origin.add(path), blockCollision, fluidCollision, projectile));
        Vec3 impact = blockTrace.getLocation();

        EntityHitResult entityTrace = level.getEntities(projectile, projectile.getBoundingBox().expandTowards(path).inflate(1d), hit -> isValidWeaponTarget(projectile.getOwner(), hit))
                .stream()
                .flatMap(hit -> Stream.ofNullable(clipEntityBoundingBox(hit, origin, impact, bbExpansion)))
                .min(Comparator.comparingDouble(result -> result.getLocation().distanceToSqr(origin)))
                .orElse(null);

        return entityTrace != null ? entityTrace : blockTrace;
    }
}