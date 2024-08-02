package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.entity.LimaTechProjectile;
import liedge.limatech.entity.MissileEntity;
import liedge.limatech.entity.OrbGrenadeEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechEntities
{
    private LimaTechEntities() {}

    private static final DeferredRegister<EntityType<?>> ENTITIES = LimaTech.RESOURCES.deferredRegister(BuiltInRegistries.ENTITY_TYPE);

    public static void initRegister(IEventBus bus)
    {
        ENTITIES.register(bus);
    }

    // Entity types
    public static final DeferredHolder<EntityType<?>, EntityType<OrbGrenadeEntity>> ORB_GRENADE = projectile("orb_grenade", OrbGrenadeEntity::new, 0.4f, 0.4f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<MissileEntity>> MISSILE = projectile("missile", MissileEntity::new, 0.85f, 0.45f, 2);

    private static <T extends LimaTechProjectile> DeferredHolder<EntityType<?>, EntityType<T>> projectile(String name, EntityType.EntityFactory<T> factory, float width, float height, int updateInterval)
    {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, MobCategory.MISC).sized(width, height).clientTrackingRange(4).updateInterval(updateInterval).fireImmune().build(name));
    }
}