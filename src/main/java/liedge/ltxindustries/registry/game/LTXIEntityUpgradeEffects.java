package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.lib.upgrades.effect.entity.RestoreShieldUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.ModifyDamageTagsUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffectType;
import liedge.ltxindustries.lib.upgrades.effect.entity.ApplyEffectUpgradeEffect;
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

    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<ModifyDamageTagsUpgradeEffect>> MODIFY_DAMAGE_TAGS = TYPES.register("modify_damage_tags", () -> EntityUpgradeEffectType.create(ModifyDamageTagsUpgradeEffect.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<RestoreShieldUpgradeEffect>> RESTORE_BUBBLE_SHIELD = TYPES.register("restore_shield", () -> EntityUpgradeEffectType.create(RestoreShieldUpgradeEffect.CODEC));
    public static final DeferredHolder<EntityUpgradeEffectType<?>, EntityUpgradeEffectType<ApplyEffectUpgradeEffect>> APPLY_MOB_EFFECT = TYPES.register("apply_effect", () -> EntityUpgradeEffectType.create(ApplyEffectUpgradeEffect.CODEC));
}