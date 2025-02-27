package liedge.limatech.world;

import com.mojang.serialization.MapCodec;
import liedge.limatech.entity.AutoTrackingProjectile;
import liedge.limatech.registry.LimaTechLootRegistries;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AutoTrackingTargetSubPredicate(Optional<EntityPredicate> targetPredicate) implements EntitySubPredicate
{
    public static final MapCodec<AutoTrackingTargetSubPredicate> CODEC = EntityPredicate.CODEC.optionalFieldOf("target_predicate").xmap(AutoTrackingTargetSubPredicate::new, AutoTrackingTargetSubPredicate::targetPredicate);

    public static AutoTrackingTargetSubPredicate trackingAny()
    {
        return new AutoTrackingTargetSubPredicate(Optional.empty());
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LimaTechLootRegistries.AUTO_TRACKING_TARGET_SUB_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        if (entity instanceof AutoTrackingProjectile projectile)
        {
            Entity targetEntity = projectile.getTargetEntity();

            if (targetEntity != null)
            {
                return targetPredicate.map(p -> p.matches(level, targetEntity.position(), targetEntity)).orElse(true);
            }
        }

        return false;
    }
}