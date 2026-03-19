package liedge.ltxindustries.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.advancements.criterion.HomingTargetSubPredicate;
import liedge.ltxindustries.advancements.criterion.GrenadeElementSubPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXILootRegistries
{
    private LTXILootRegistries() {}

    private static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> SUB_PREDICATES = RESOURCES.deferredRegister(Registries.ENTITY_SUB_PREDICATE_TYPE);

    public static void register(IEventBus bus)
    {
        SUB_PREDICATES.register(bus);
    }

    // Entity sub-predicates
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<GrenadeElementSubPredicate>> GRENADE_ELEMENT_SUB_PREDICATE = SUB_PREDICATES.register("grenade_element", () -> GrenadeElementSubPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<HomingTargetSubPredicate>> HOMING_TARGET_SUB_PREDICATE = SUB_PREDICATES.register("homing_target", () -> HomingTargetSubPredicate.CODEC);
}