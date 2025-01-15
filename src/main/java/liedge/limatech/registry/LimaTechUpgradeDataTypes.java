package liedge.limatech.registry;

import liedge.limatech.lib.upgrades.effect.AmmoSourceUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.*;
import liedge.limatech.lib.upgrades.effect.equipment.EquipmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.GrenadeUnlockUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechUpgradeDataTypes
{
    private static final DeferredRegister<UpgradeEffectDataType<?>> TYPES = RESOURCES.deferredRegister(LimaTechRegistries.UPGRADE_DATA_TYPE_KEY);

    private LimaTechUpgradeDataTypes() {}

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<UpgradeEffectDataType<?>, UpgradeEffectDataType<List<EnchantmentUpgradeEffect>>> ITEM_ENCHANTMENTS = TYPES.register("enchantments", id -> UpgradeEffectDataType.listOf(id, EnchantmentUpgradeEffect.CODEC));
    public static final DeferredHolder<UpgradeEffectDataType<?>, UpgradeEffectDataType<List<EquipmentUpgradeEffect>>> WEAPON_PRE_ATTACK = TYPES.register("weapon_pre_attack", id -> UpgradeEffectDataType.listOf(id, EquipmentUpgradeEffect.CODEC));
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> WEAPON_DAMAGE = registerValue("weapon_damage", true);
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> WEAPON_PROJECTILE_SPEED = registerValue("weapon_projectile_speed", true);
    public static final DeferredHolder<UpgradeEffectDataType<?>, UpgradeEffectDataType<List<EquipmentUpgradeEffect>>> WEAPON_KILL = TYPES.register("weapon_kill", id -> UpgradeEffectDataType.listOf(id, EquipmentUpgradeEffect.CODEC));
    public static final DeferredHolder<UpgradeEffectDataType<?>, UpgradeEffectDataType<AmmoSourceUpgradeEffect>> AMMO_SOURCE = TYPES.register("ammo_source", id -> UpgradeEffectDataType.of(id, AmmoSourceUpgradeEffect.CODEC));
    public static final DeferredHolder<UpgradeEffectDataType<?>, GrenadeUnlockUpgradeEffect.DataType> GRENADE_UNLOCK = TYPES.register("grenade_unlock", GrenadeUnlockUpgradeEffect.DataType::new);
    public static final DeferredHolder<UpgradeEffectDataType<?>, UpgradeEffectDataType<NoSculkVibrationEffect>> PREVENT_SCULK_VIBRATION = TYPES.register("prevent_sculk_vibration", id -> UpgradeEffectDataType.of(id, NoSculkVibrationEffect.CODEC));
    public static final DeferredHolder<UpgradeEffectDataType<?>, UpgradeEffectDataType<List<AttributeModifierUpgradeEffect>>> ITEM_ATTRIBUTE_MODIFIERS = TYPES.register("attribute_modifier", id -> UpgradeEffectDataType.listOf(id, AttributeModifierUpgradeEffect.CODEC));

    // Machine related
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> ENERGY_CAPACITY = registerValue("energy_capacity", true);
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> ENERGY_TRANSFER_RATE = registerValue("energy_transfer_rate", true);
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> MACHINE_ENERGY_USAGE = registerValue("energy_usage", false);
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> MACHINE_ENERGY_PRODUCTION = registerValue("energy_production", true);
    public static final DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> TICKS_PER_OPERATION = registerValue("ticks_per_operation", false);

    // Helper factories
    private static DeferredHolder<UpgradeEffectDataType<?>, ValueUpgradeEffect.DataType> registerValue(String name, boolean beneficial)
    {
        return TYPES.register(name, id -> ValueUpgradeEffect.createDataType(id, beneficial));
    }
}