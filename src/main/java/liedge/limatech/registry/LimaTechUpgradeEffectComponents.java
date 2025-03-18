package liedge.limatech.registry;

import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.effect.*;
import liedge.limatech.lib.upgrades.effect.equipment.EquipmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.ComplexValueUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.SimpleValueUpgradeEffect;
import liedge.limatech.lib.weapons.GrenadeType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechUpgradeEffectComponents
{
    private static final DeferredRegister.DataComponents COMPONENTS = RESOURCES.deferredDataComponents(LimaTechRegistries.Keys.UPGRADE_COMPONENT_TYPES);

    private LimaTechUpgradeEffectComponents() {}

    public static void initRegister(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }

    // Universal effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> DIRECT_ITEM_TELEPORT = COMPONENTS.register("direct_item_teleport", () -> EffectDataComponentType.createSpecialUnit(() -> LimaTechLang.DIRECT_ITEM_TELEPORT_EFFECT.translate().withStyle(LimaTechConstants.LIME_GREEN.chatStyle())));

    // Equipment related
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentUpgradeEffect>>> ENCHANTMENT_LEVEL = COMPONENTS.register("enchantments", () -> EffectDataComponentType.createList(EnchantmentUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AttributeModifierUpgradeEffect>>> ITEM_ATTRIBUTE_MODIFIERS = COMPONENTS.register("attribute_modifiers", () -> EffectDataComponentType.createList(AttributeModifierUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EquipmentUpgradeEffect>>>> WEAPON_PRE_ATTACK = COMPONENTS.register("weapon_pre_attack", () -> EffectDataComponentType.createConditionalList(EquipmentUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EquipmentUpgradeEffect>>>> WEAPON_KILL = COMPONENTS.register("weapon_kill", () -> EffectDataComponentType.createConditionalList(EquipmentUpgradeEffect.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GrenadeType>>> GRENADE_UNLOCK = COMPONENTS.register("grenade_unlock", () -> EffectDataComponentType.createList(GrenadeType.CODEC, (type, $) -> LimaTechLang.GRENADE_UNLOCK_EFFECT.translateArgs(type.translate())));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AmmoSourceUpgradeEffect>> AMMO_SOURCE = COMPONENTS.register("ammo_source", () -> EffectDataComponentType.createSingle(AmmoSourceUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> PREVENT_SCULK_VIBRATION = COMPONENTS.register("prevent_sculk_vibration", () -> EffectDataComponentType.createSpecialUnit(() -> LimaTechLang.NO_SCULK_VIBRATIONS_EFFECT.translate().withStyle(ChatFormatting.DARK_AQUA)));

    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<ConditionalEffect<ComplexValueUpgradeEffect>>>> WEAPON_DAMAGE = COMPONENTS.register("weapon_damage", id -> ValueEffectDataComponentType.createComplexList(LootContextParamSets.ENCHANTED_DAMAGE, id, true));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<ConditionalEffect<ComplexValueUpgradeEffect>>>> ARMOR_BYPASS = COMPONENTS.register("armor_bypass", id -> ValueEffectDataComponentType.createComplexList(LootContextParamSets.ENCHANTED_DAMAGE, id, false));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> ENTITY_PUNCH_THROUGH = COMPONENTS.register("entity_punch_through", id -> ValueEffectDataComponentType.createSimpleList(id, true));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> BLOCK_PUNCH_THROUGH = COMPONENTS.register("block_punch_through", id -> ValueEffectDataComponentType.createSimpleList(id, true));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> WEAPON_PROJECTILE_SPEED = COMPONENTS.register("weapon_projectile_speed", id -> ValueEffectDataComponentType.createSimpleList(id, true));

    // Machine related
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> ENERGY_CAPACITY = COMPONENTS.register("energy_capacity", id -> ValueEffectDataComponentType.createSimpleList(id, true));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> ENERGY_TRANSFER_RATE = COMPONENTS.register("energy_transfer_rate", id -> ValueEffectDataComponentType.createSimpleList(id, true));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> MACHINE_ENERGY_USAGE = COMPONENTS.register("energy_usage", id -> ValueEffectDataComponentType.createSimpleList(id, false));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> MACHINE_ENERGY_PRODUCTION = COMPONENTS.register("energy_production", id -> ValueEffectDataComponentType.createSimpleList(id, true));
    public static final DeferredHolder<DataComponentType<?>, ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>> TICKS_PER_OPERATION = COMPONENTS.register("ticks_per_operation", id -> ValueEffectDataComponentType.createSimpleList(id, false));
}