package liedge.limatech.registry;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.upgrades.effect.equipment.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechEntityUpgradeEffects
{
    private static final DeferredRegister<MapCodec<? extends EquipmentUpgradeEffect>> TYPES = RESOURCES.deferredRegister(LimaTechRegistries.PLAYER_UPGRADE_EFFECT_TYPE_KEY);

    private LimaTechEntityUpgradeEffects() {}

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<MapCodec<? extends EquipmentUpgradeEffect>, MapCodec<ArmorBypassUpgradeEffect>> ARMOR_BYPASS_ENTITY_EFFECT = TYPES.register("armor_bypass", () -> ArmorBypassUpgradeEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EquipmentUpgradeEffect>, MapCodec<DynamicDamageTagUpgradeEffect>> DYNAMIC_DAMAGE_TAG_ENTITY_EFFECT = TYPES.register("dynamic_damage_tag", () -> DynamicDamageTagUpgradeEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EquipmentUpgradeEffect>, MapCodec<KnockbackStrengthUpgradeEffect>> KNOCKBACK_STRENGTH_ENTITY_EFFECT = TYPES.register("knockback_strength", () -> KnockbackStrengthUpgradeEffect.CODEC);
}