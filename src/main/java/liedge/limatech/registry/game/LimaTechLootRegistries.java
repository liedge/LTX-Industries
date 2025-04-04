package liedge.limatech.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limatech.world.AutoTrackingTargetSubPredicate;
import liedge.limatech.world.GrenadeSubPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechLootRegistries
{
    private LimaTechLootRegistries() {}

    private static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> SUB_PREDICATES = RESOURCES.deferredRegister(Registries.ENTITY_SUB_PREDICATE_TYPE);

    public static void register(IEventBus bus)
    {
        SUB_PREDICATES.register(bus);
    }

    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<GrenadeSubPredicate>> GRENADE_TYPE_SUB_PREDICATE = SUB_PREDICATES.register("grenade_type", () -> GrenadeSubPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<AutoTrackingTargetSubPredicate>> AUTO_TRACKING_TARGET_SUB_PREDICATE = SUB_PREDICATES.register("auto_tracking_target", () -> AutoTrackingTargetSubPredicate.CODEC);
}