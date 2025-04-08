package liedge.limatech.world;

import com.mojang.serialization.MapCodec;
import liedge.limatech.entity.OrbGrenadeEntity;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.game.LimaTechLootRegistries;
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
        return LimaTechLootRegistries.GRENADE_TYPE_SUB_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return entity instanceof OrbGrenadeEntity grenade && grenade.getGrenadeType() == type;
    }
}