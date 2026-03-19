package liedge.ltxindustries.advancements.criterion;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.entity.HomingProjectileEntity;
import liedge.ltxindustries.registry.game.LTXILootRegistries;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record HomingTargetSubPredicate(Optional<EntityPredicate> predicate) implements EntitySubPredicate
{
    public static final MapCodec<HomingTargetSubPredicate> CODEC = EntityPredicate.CODEC.optionalFieldOf("target_predicate").xmap(HomingTargetSubPredicate::new, HomingTargetSubPredicate::predicate);

    public static HomingTargetSubPredicate trackingAny()
    {
        return new HomingTargetSubPredicate(Optional.empty());
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LTXILootRegistries.HOMING_TARGET_SUB_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        if (entity instanceof HomingProjectileEntity projectile)
        {
            Entity targetEntity = projectile.getTargetEntity();

            if (targetEntity != null)
            {
                return predicate.map(p -> p.matches(level, targetEntity.position(), targetEntity)).orElse(true);
            }
        }

        return false;
    }
}