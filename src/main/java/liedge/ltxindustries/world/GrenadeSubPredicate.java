package liedge.ltxindustries.world;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.entity.OrbGrenadeEntity;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.game.LTXILootRegistries;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record GrenadeSubPredicate(GrenadeType type) implements EntitySubPredicate
{
    public static final MapCodec<GrenadeSubPredicate> CODEC = GrenadeType.CODEC.fieldOf("grenade_element").xmap(GrenadeSubPredicate::new, GrenadeSubPredicate::type);

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LTXILootRegistries.GRENADE_TYPE_SUB_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return entity instanceof OrbGrenadeEntity grenade && grenade.getGrenadeType() == type;
    }
}