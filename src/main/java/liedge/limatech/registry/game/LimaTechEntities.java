package liedge.limatech.registry.game;

import liedge.limatech.LimaTech;
import liedge.limatech.entity.LimaTechProjectile;
import liedge.limatech.entity.BaseRocketEntity;
import liedge.limatech.entity.OrbGrenadeEntity;
import liedge.limatech.entity.StickyFlameEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public final class LimaTechEntities
{
    private LimaTechEntities() {}

    private static final DeferredRegister<EntityType<?>> ENTITIES = LimaTech.RESOURCES.deferredRegister(BuiltInRegistries.ENTITY_TYPE);

    public static void register(IEventBus bus)
    {
        ENTITIES.register(bus);
    }

    // Entity types
    public static final DeferredHolder<EntityType<?>, EntityType<OrbGrenadeEntity>> ORB_GRENADE = projectile("orb_grenade", OrbGrenadeEntity::new, 0.4f, 0.4f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseRocketEntity.DaybreakRocket>> DAYBREAK_ROCKET = projectile("daybreak_rocket", BaseRocketEntity.DaybreakRocket::new, 0.6f, 0.6f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseRocketEntity.TurretRocket>> TURRET_ROCKET = projectile("turret_rocket", BaseRocketEntity.TurretRocket::new, 0.6f, 0.6f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyFlameEntity>> STICKY_FLAME = register("sticky_flame", StickyFlameEntity::new, MobCategory.MISC, builder -> builder.sized(2f, 2f).clientTrackingRange(10).updateInterval(20));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builderOp)
    {
        return ENTITIES.register(name, () -> {
            EntityType.Builder<T> builder = builderOp.apply(EntityType.Builder.of(factory, category));
            return builder.build(name);
        });
    }

    private static <T extends LimaTechProjectile> DeferredHolder<EntityType<?>, EntityType<T>> projectile(String name, EntityType.EntityFactory<T> factory, float width, float height, int updateInterval)
    {
        return register(name, factory, MobCategory.MISC, builder -> builder.sized(width, height).clientTrackingRange(10).updateInterval(updateInterval).fireImmune());
    }
}