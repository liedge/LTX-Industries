package liedge.limatech.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.upgrades.effect.equipment.*;
import liedge.limatech.registry.LimaTechRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechEquipmentUpgradeEffects
{
    private static final DeferredRegister<MapCodec<? extends EquipmentUpgradeEffect>> TYPES = RESOURCES.deferredRegister(LimaTechRegistries.Keys.EQUIPMENT_UPGRADE_EFFECT_TYPES);

    private LimaTechEquipmentUpgradeEffects() {}

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<MapCodec<? extends EquipmentUpgradeEffect>, MapCodec<DynamicDamageTagUpgradeEffect>> DYNAMIC_DAMAGE_TAG_EQUIPMENT_EFFECT = TYPES.register("dynamic_damage_tag", () -> DynamicDamageTagUpgradeEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EquipmentUpgradeEffect>, MapCodec<KnockbackStrengthUpgradeEffect>> KNOCKBACK_STRENGTH_EQUIPMENT_EFFECT = TYPES.register("knockback_strength", () -> KnockbackStrengthUpgradeEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EquipmentUpgradeEffect>, MapCodec<BubbleShieldUpgradeEffect>> BUBBLE_SHIELD_EQUIPMENT_EFFECT = TYPES.register("bubble_shield", () -> BubbleShieldUpgradeEffect.CODEC);
}