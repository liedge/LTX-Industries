package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeDataComponentType;
import liedge.ltxindustries.lib.upgrades.effect.value.ValueUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.equipment.*;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<LootItemCondition>>> TARGET_CONDITIONS = COMPONENTS.register("target_conditions", () -> UpgradeDataComponentType.custom(ConditionalEffect.conditionCodec(LootContextParamSets.CHEST).listOf()));

    // Equipment related
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentLevelsUpgradeEffect>>> ENCHANTMENT_LEVELS = COMPONENTS.register("enchantment_levels", () -> UpgradeDataComponentType.listOf(EnchantmentLevelsUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemAttributesUpgradeEffect>>> ITEM_ATTRIBUTE_MODIFIERS = COMPONENTS.register("item_attribute_modifiers", () -> UpgradeDataComponentType.listOf(ItemAttributesUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<MiningRuleUpgradeEffect>>> MINING_RULES = COMPONENTS.register("mining_rules", () -> UpgradeDataComponentType.listOf(MiningRuleUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EntityUpgradeEffect>>>> EQUIPMENT_PRE_ATTACK = COMPONENTS.register("pre_attack", () -> UpgradeDataComponentType.targetedConditionalListOf(EntityUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EntityUpgradeEffect>>>> EQUIPMENT_KILL = COMPONENTS.register("on_kill", () -> UpgradeDataComponentType.targetedConditionalListOf(EntityUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<DamageAttributesUpgradeEffect>>> DAMAGE_ATTRIBUTE_MODIFIERS = COMPONENTS.register("damage_attribute_modifiers", () -> UpgradeDataComponentType.listOf(DamageAttributesUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ModifyReductionsUpgradeEffect>>> DAMAGE_REDUCTION_MODIFIER = COMPONENTS.register("damage_reduction_modifier", () -> UpgradeDataComponentType.listOf(ModifyReductionsUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GrenadeType>>> GRENADE_UNLOCK = COMPONENTS.register("grenade_unlock", () -> UpgradeDataComponentType.listOf(GrenadeType.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<PreventVibrationUpgradeEffect>>> PREVENT_VIBRATION = COMPONENTS.register("prevent_vibration", () -> UpgradeDataComponentType.listOf(PreventVibrationUpgradeEffect.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> EQUIPMENT_DAMAGE = COMPONENTS.registerConditionalValue("equipment_damage", LootContextParamSets.ENCHANTED_DAMAGE);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> MAGAZINE_CAPACITY = COMPONENTS.registerValue("magazine_capacity");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> WEAPON_RANGE = COMPONENTS.registerValue("weapon_range");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> RELOAD_SPEED = COMPONENTS.registerValue("reload_speed");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponReloadSource>> RELOAD_SOURCE = COMPONENTS.register("reload_source", () -> UpgradeDataComponentType.of(WeaponReloadSource.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> MAX_HITS = COMPONENTS.registerValue("max_hits");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> BLOCK_PIERCE_DISTANCE = COMPONENTS.registerValue("punch_through");

    // Machine related
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueUpgradeEffect>>> MACHINE_ENERGY_PRODUCTION = COMPONENTS.registerValue("energy_production");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINIMUM_MACHINE_SPEED = COMPONENTS.register("minimum_speed", () -> UpgradeDataComponentType.custom(ExtraCodecs.NON_NEGATIVE_INT,
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
            return register(name, () -> UpgradeDataComponentType.custom(ValueUpgradeEffect.CODEC.listOf()));
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> registerConditionalValue(String name, LootContextParamSet params)
        {
            return register(name, () -> UpgradeDataComponentType.custom(ConditionalEffect.codec(ValueUpgradeEffect.CODEC, params).listOf()));
        }
    }
}