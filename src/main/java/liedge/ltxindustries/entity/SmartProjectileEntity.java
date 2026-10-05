package liedge.ltxindustries.entity;

import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface SmartProjectileEntity extends TraceableEntity
{
    Upgrades getUpgrades();

    @Override
    @Nullable LivingEntity getOwner();

    void setOwner(@Nullable LivingEntity owner);

    default TargetPredicate getTargets()
    {
        return TargetPredicate.ALL;
    }

    default List<Entity> getEntities(ServerLevel level, AABB bb, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        return level.getEntities((Entity) this, bb, e -> LTXIEntityUtil.isValidContextTarget(e, owner, getTargets()) && !Objects.equals(directHit, e));
    }

    default List<Entity> getEntities(ServerLevel level, Vec3 hitLocation, double radius, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        radius *= 2;
        return getEntities(level, AABB.ofSize(hitLocation, radius, radius, radius), owner, directHit);
    }
}