package liedge.ltxindustries.advancements.criterion;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.entity.ShellGrenade;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record GrenadeElementSubPredicate(GrenadeType type) implements EntitySubPredicate
{
    public static final MapCodec<GrenadeElementSubPredicate> CODEC = GrenadeType.CODEC.fieldOf("element").xmap(GrenadeElementSubPredicate::new, GrenadeElementSubPredicate::type);

    public static GrenadeElementSubPredicate of(GrenadeType type)
    {
        return new GrenadeElementSubPredicate(type);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return entity instanceof ShellGrenade grenade && grenade.getGrenadeType() == type;
    }
}