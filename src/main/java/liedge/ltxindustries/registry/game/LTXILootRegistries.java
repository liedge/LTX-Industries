package liedge.ltxindustries.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.world.AutoTrackingTargetSubPredicate;
import liedge.ltxindustries.world.GrenadeSubPredicate;
import liedge.ltxindustries.world.loot.ContextKeyProvider;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXILootRegistries
{
    private LTXILootRegistries() {}

    private static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> SUB_PREDICATES = RESOURCES.deferredRegister(Registries.ENTITY_SUB_PREDICATE_TYPE);
    private static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = RESOURCES.deferredRegister(Registries.LOOT_NUMBER_PROVIDER_TYPE);

    public static void register(IEventBus bus)
    {
        SUB_PREDICATES.register(bus);
        NUMBER_PROVIDERS.register(bus);
    }

    // Entity sub-predicates
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<GrenadeSubPredicate>> GRENADE_TYPE_SUB_PREDICATE = SUB_PREDICATES.register("grenade_type", () -> GrenadeSubPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<AutoTrackingTargetSubPredicate>> AUTO_TRACKING_TARGET_SUB_PREDICATE = SUB_PREDICATES.register("auto_tracking_target", () -> AutoTrackingTargetSubPredicate.CODEC);

    // Loot number providers
    public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> CONTEXT_VALUE_PROVIDER = NUMBER_PROVIDERS.register("context_value", () -> new LootNumberProviderType(ContextKeyProvider.CODEC));
}