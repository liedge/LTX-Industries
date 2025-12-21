package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.lib.upgrades.effect.entity.*;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIEntityUpgradeEffects
{
    private static final DeferredRegister<EntityUpgradeEffectType<?>> TYPES = RESOURCES.deferredRegister(LTXIRegistries.Keys.ENTITY_UPGRADE_EFFECT_TYPES);

    private LTXIEntityUpgradeEffects() {}

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<AddDamageTags>> MODIFY_DAMAGE_TAGS = TYPES.register("modify_damage_tags", () -> EntityUpgradeEffectType.create(AddDamageTags.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<RestoreShield>> RESTORE_BUBBLE_SHIELD = TYPES.register("restore_shield", () -> EntityUpgradeEffectType.create(RestoreShield.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<ApplyMobEffect>> APPLY_MOB_EFFECT = TYPES.register("apply_effect", () -> EntityUpgradeEffectType.create(ApplyMobEffect.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<IgniteEntity>> IGNITE_ENTITY = TYPES.register("ignite", () -> EntityUpgradeEffectType.create(IgniteEntity.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<ApplyInArea>> APPLY_IN_AREA = TYPES.register("aoe", () -> EntityUpgradeEffectType.create(ApplyInArea.CODEC));
}