package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIEntities
{
    private LTXIEntities() {}

    private static final DeferredRegister.Entities ENTITIES = DeferredRegister.Entities.createEntities(LTXIndustries.MODID);

    public static void register(IEventBus bus)
    {
        ENTITIES.register(bus);
    }

    // Entity types
    public static final DeferredHolder<EntityType<?>, EntityType<GlowstickProjectile>> GLOWSTICK_PROJECTILE = projectile(LTXIIdentifiers.ID_GLOWSTICK_PROJECTILE, GlowstickProjectile::new, 0.25f, 0.25f, 10);
    public static final DeferredHolder<EntityType<?>, EntityType<ShellGrenade>> SHELL_GRENADE = projectile(LTXIIdentifiers.ID_SHELL_GRENADE, ShellGrenade::new, 0.35f, 0.35f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<DaybreakRocket>> DAYBREAK_ROCKET = projectile("daybreak_rocket", DaybreakRocket::new, 0.6f, 0.6f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<TurretRocket>> TURRET_ROCKET = projectile("turret_rocket", TurretRocket::new, 0.6f, 0.6f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<HanabiFlameField>> FLAME_FIELD = ENTITIES.registerEntityType("flame_field", HanabiFlameField::new, MobCategory.MISC, builder -> builder
            .sized(4f, 4f).clientTrackingRange(10).updateInterval(10).fireImmune());

    private static <T extends LTXIProjectileEntity> DeferredHolder<EntityType<?>, EntityType<T>> projectile(String name, EntityType.EntityFactory<T> factory, float width, float height, int updateInterval)
    {
        return ENTITIES.registerEntityType(name, factory, MobCategory.MISC, builder -> builder.sized(width, height).clientTrackingRange(10).updateInterval(updateInterval).fireImmune());
    }
}