package liedge.limatech.registry;

import liedge.limatech.lib.upgrades.effect.*;
import liedge.limatech.lib.upgrades.effect.equipment.EquipmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.lib.upgrades.effect.UpgradeDataComponentType.listType;
import static liedge.limatech.lib.upgrades.effect.UpgradeDataComponentType.singletonType;

public final class LimaTechUpgradeEffectComponents
{
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = RESOURCES.deferredDataComponents(LimaTechRegistries.Keys.UPGRADE_COMPONENT_TYPES);

    private LimaTechUpgradeEffectComponents() {}

    public static void initRegister(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }

    public static final DeferredHolder<DataComponentType<?>, UpgradeDataComponentType<List<EnchantmentUpgradeEffect>>> ITEM_ENCHANTMENTS = COMPONENTS.register("enchantments", () -> listType(EnchantmentUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, UpgradeDataComponentType<List<EquipmentUpgradeEffect>>> WEAPON_PRE_ATTACK = COMPONENTS.register("weapon_pre_attack", () -> listType(EquipmentUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> ARMOR_BYPASS = valueComponent("armor_bypass", false);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> WEAPON_DAMAGE = valueComponent("weapon_damage", true);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> ENTITY_PUNCH_THROUGH = valueComponent("entity_punch_through", true);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> BLOCK_PUNCH_THROUGH = valueComponent("block_punch_through", true);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> WEAPON_PROJECTILE_SPEED = valueComponent("weapon_projectile_speed", true);
    public static final DeferredHolder<DataComponentType<?>, UpgradeDataComponentType<List<EquipmentUpgradeEffect>>> WEAPON_KILL = COMPONENTS.register("weapon_kill", () -> listType(EquipmentUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, UpgradeDataComponentType<AmmoSourceUpgradeEffect>> AMMO_SOURCE = COMPONENTS.register("ammo_source", () -> singletonType(AmmoSourceUpgradeEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, GrenadeUnlockUpgradeEffect.DataComponentType> GRENADE_UNLOCK = COMPONENTS.register("grenade_unlock", GrenadeUnlockUpgradeEffect.DataComponentType::new);
    public static final DeferredHolder<DataComponentType<?>, UpgradeDataComponentType<NoSculkVibrationEffect>> PREVENT_SCULK_VIBRATION = COMPONENTS.register("prevent_sculk_vibration", () -> singletonType(NoSculkVibrationEffect.CODEC));
    public static final DeferredHolder<DataComponentType<?>, UpgradeDataComponentType<List<AttributeModifierUpgradeEffect>>> ITEM_ATTRIBUTE_MODIFIERS = COMPONENTS.register("attribute_modifier", () -> listType(AttributeModifierUpgradeEffect.CODEC));

    // Machine related
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> ENERGY_CAPACITY = valueComponent("energy_capacity", true);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> ENERGY_TRANSFER_RATE = valueComponent("energy_transfer_rate", true);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> MACHINE_ENERGY_USAGE = valueComponent("energy_usage", false);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> MACHINE_ENERGY_PRODUCTION = valueComponent("energy_production", true);
    public static final DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> TICKS_PER_OPERATION = valueComponent("ticks_per_operation", false);

    // Helper factories
    private static DeferredHolder<DataComponentType<?>, ValueUpgradeEffect.ComponentType> valueComponent(String name, boolean beneficial)
    {
        return COMPONENTS.register(name, id -> new ValueUpgradeEffect.ComponentType(id, beneficial));
    }
}