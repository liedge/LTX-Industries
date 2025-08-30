package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeDataComponentType;
import liedge.ltxindustries.lib.upgrades.effect.ValueUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.equipment.*;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponAmmoSource;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public final class LTXIUpgradeEffectComponents
{
    private static final DeferredRegister COMPONENTS = new DeferredRegister();

    private LTXIUpgradeEffectComponents() {}

    public static void register(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }

    // Universal effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> ENERGY_CAPACITY = COMPONENTS.registerValue("energy_capacity");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> ENERGY_TRANSFER_RATE = COMPONENTS.registerValue("energy_transfer_rate");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> ENERGY_USAGE = COMPONENTS.registerValue("energy_usage");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<DirectDropsUpgradeEffect>>> DIRECT_DROPS = COMPONENTS.register("direct_drops", () -> UpgradeDataComponentType.listOf(DirectDropsUpgradeEffect.CODEC));

    // Equipment related
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentUpgradeEffect>>> ENCHANTMENT_LEVEL = COMPONENTS.register("enchantments", () -> UpgradeDataComponentType.listOf(EnchantmentUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AttributeModifierUpgradeEffect>>> ITEM_ATTRIBUTE_MODIFIERS = COMPONENTS.register("attribute_modifiers", () -> UpgradeDataComponentType.listOf(AttributeModifierUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<MiningRuleUpgradeEffect>>> MINING_RULES = COMPONENTS.register("mining_rules", () -> UpgradeDataComponentType.listOf(MiningRuleUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EquipmentUpgradeEffect>>>> EQUIPMENT_PRE_ATTACK = COMPONENTS.register("pre_attack", () -> UpgradeDataComponentType.targetedConditionalListOf(EquipmentUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EquipmentUpgradeEffect>>>> EQUIPMENT_KILL = COMPONENTS.register("on_kill", () -> UpgradeDataComponentType.targetedConditionalListOf(EquipmentUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GrenadeType>>> GRENADE_UNLOCK = COMPONENTS.register("grenade_unlock", () -> UpgradeDataComponentType.listOf(GrenadeType.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAmmoSource>> AMMO_SOURCE = COMPONENTS.register("ammo_source", () -> UpgradeDataComponentType.of(WeaponAmmoSource.CODEC.restricted(List.of(WeaponAmmoSource.COMMON_ENERGY_UNIT, WeaponAmmoSource.INFINITE))));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<PreventVibrationUpgradeEffect>>> PREVENT_VIBRATION = COMPONENTS.register("prevent_vibration", () -> UpgradeDataComponentType.listOf(PreventVibrationUpgradeEffect.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> EQUIPMENT_DAMAGE = COMPONENTS.registerConditionalValue("equipment_damage", LootContextParamSets.ENCHANTED_DAMAGE);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> ENTITY_PUNCH_THROUGH = COMPONENTS.registerValue("entity_punch_through");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> BLOCK_PUNCH_THROUGH = COMPONENTS.registerValue("block_punch_through");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> WEAPON_PROJECTILE_SPEED = COMPONENTS.registerValue("weapon_projectile_speed");

    // Machine related
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> MACHINE_ENERGY_PRODUCTION = COMPONENTS.registerValue("energy_production");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINIMUM_MACHINE_SPEED = COMPONENTS.register("minimum_speed", () -> UpgradeDataComponentType.of(ExtraCodecs.NON_NEGATIVE_INT,
            (i, lines) -> lines.accept(LTXILangKeys.MINIMUM_MACHINE_SPEED_EFFECT.translateArgs(i))));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> TICKS_PER_OPERATION = COMPONENTS.registerValue("ticks_per_operation");

    private static class DeferredRegister extends net.neoforged.neoforge.registries.DeferredRegister.DataComponents
    {
        private DeferredRegister()
        {
            super(LTXIRegistries.Keys.UPGRADE_COMPONENT_TYPES, LTXIndustries.MODID);
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> registerValue(String name)
        {
            return register(name, () -> UpgradeDataComponentType.listOf(ValueUpgradeEffect.CODEC));
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> registerConditionalValue(String name, LootContextParamSet params)
        {
            return register(name, () -> UpgradeDataComponentType.conditionalListOf(ValueUpgradeEffect.CODEC, params));
        }
    }
}