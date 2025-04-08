package liedge.limatech.registry.game;

import liedge.limatech.LimaTech;
import liedge.limatech.lib.upgrades.effect.UpgradeDataComponentType;
import liedge.limatech.lib.upgrades.effect.UpgradeValueDataComponentType;
import liedge.limatech.lib.upgrades.effect.equipment.*;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public final class LimaTechUpgradeEffectComponents
{
    private static final DeferredRegister COMPONENTS = new DeferredRegister();

    private LimaTechUpgradeEffectComponents() {}

    public static void register(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }

    // Universal effects
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> ENERGY_CAPACITY = COMPONENTS.registerValue("energy_capacity");
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> ENERGY_TRANSFER_RATE = COMPONENTS.registerValue("energy_transfer_rate");
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> ENERGY_USAGE = COMPONENTS.registerValue("energy_usage");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<DirectDropsUpgradeEffect>>> DIRECT_DROPS = COMPONENTS.register("direct_drops", () -> UpgradeDataComponentType.createList(DirectDropsUpgradeEffect.CODEC));

    // Equipment related
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentUpgradeEffect>>> ENCHANTMENT_LEVEL = COMPONENTS.register("enchantments", () -> UpgradeDataComponentType.createList(EnchantmentUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AttributeModifierUpgradeEffect>>> ITEM_ATTRIBUTE_MODIFIERS = COMPONENTS.register("attribute_modifiers", () -> UpgradeDataComponentType.createList(AttributeModifierUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolProfileUpgradeEffect>> TOOL_PROFILE = COMPONENTS.register("tool_profile", () -> UpgradeDataComponentType.createSingle(ToolProfileUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EquipmentUpgradeEffect>>>> EQUIPMENT_PRE_ATTACK = COMPONENTS.register("pre_attack", () -> UpgradeDataComponentType.createTargetedList(EquipmentUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EquipmentUpgradeEffect>>>> EQUIPMENT_KILL = COMPONENTS.register("on_kill", () -> UpgradeDataComponentType.createTargetedList(EquipmentUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GrenadeType>>> GRENADE_UNLOCK = COMPONENTS.register("grenade_unlock", () -> UpgradeDataComponentType.createList(GrenadeType.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAmmoSource>> AMMO_SOURCE = COMPONENTS.register("ammo_source", () -> UpgradeDataComponentType.createSingle(WeaponAmmoSource.CODEC.restricted(List.of(WeaponAmmoSource.COMMON_ENERGY_UNIT, WeaponAmmoSource.INFINITE))));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<PreventVibrationUpgradeEffect>>> PREVENT_VIBRATION = COMPONENTS.register("prevent_vibration", () -> UpgradeDataComponentType.createList(PreventVibrationUpgradeEffect.CODEC));

    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> EQUIPMENT_DAMAGE = COMPONENTS.registerConditionalValue("equipment_damage", LootContextParamSets.ENCHANTED_DAMAGE);
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> ENTITY_PUNCH_THROUGH = COMPONENTS.registerValue("entity_punch_through");
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> BLOCK_PUNCH_THROUGH = COMPONENTS.registerValue("block_punch_through");
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> WEAPON_PROJECTILE_SPEED = COMPONENTS.registerValue("weapon_projectile_speed");

    // Machine related
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> MACHINE_ENERGY_PRODUCTION = COMPONENTS.registerValue("energy_production");
    public static final DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> TICKS_PER_OPERATION = COMPONENTS.registerValue("ticks_per_operation");

    private static class DeferredRegister extends net.neoforged.neoforge.registries.DeferredRegister.DataComponents
    {
        private DeferredRegister()
        {
            super(LimaTechRegistries.Keys.UPGRADE_COMPONENT_TYPES, LimaTech.MODID);
        }

        public DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ValueUpgradeEffect>>> registerValue(String name)
        {
            return register(name, UpgradeValueDataComponentType.DirectValueList::new);
        }

        public DeferredHolder<DataComponentType<?>, UpgradeValueDataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> registerConditionalValue(String name, LootContextParamSet params)
        {
            return register(name, id -> new UpgradeValueDataComponentType.ConditionalValueList(id, params));
        }
    }
}