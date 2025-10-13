package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.lib.upgrades.effect.entity.BubbleShieldUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.DynamicDamageTagUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffectType;
import liedge.ltxindustries.lib.upgrades.effect.entity.MobEffectUpgradeEffect;
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

    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<DynamicDamageTagUpgradeEffect>> DYNAMIC_DAMAGE_TAG_EQUIPMENT_EFFECT = TYPES.register("dynamic_damage_tag", () -> EntityUpgradeEffectType.create(DynamicDamageTagUpgradeEffect.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<BubbleShieldUpgradeEffect>> BUBBLE_SHIELD_EQUIPMENT_EFFECT = TYPES.register("bubble_shield", () -> EntityUpgradeEffectType.create(BubbleShieldUpgradeEffect.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<MobEffectUpgradeEffect>> MOB_EFFECT_EQUIPMENT_EFFECT = TYPES.register("mob_effect", () -> EntityUpgradeEffectType.create(MobEffectUpgradeEffect.CODEC));
}