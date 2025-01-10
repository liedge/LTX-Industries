package liedge.limatech.registry;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgrade;
import liedge.limatech.lib.upgradesystem.machine.effect.ModifyEnergyStorageUpgradeEffect;
import liedge.limatech.lib.upgradesystem.machine.effect.MachineUpgradeEffect;
import liedge.limatech.lib.upgradesystem.machine.effect.MachineUpgradeEffectType;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechMachineUpgrades
{
    private static final DeferredRegister<MachineUpgradeEffectType<?>> EFFECT_TYPES = RESOURCES.deferredRegister(LimaTechRegistries.MACHINE_UPGRADE_EFFECT_TYPE_KEY);

    private LimaTechMachineUpgrades() {}

    public static void initRegister(IEventBus bus)
    {
        EFFECT_TYPES.register(bus);
    }

    // Upgrade effect types
    public static final DeferredHolder<MachineUpgradeEffectType<?>, MachineUpgradeEffectType<ModifyEnergyStorageUpgradeEffect>> MODIFY_ENERGY_STORAGE = registerType("modify_energy_storage", ModifyEnergyStorageUpgradeEffect.CODEC);

    // Built-in upgrade resource keys
    public static final ResourceKey<MachineUpgrade> ESA_CAPACITY_UPGRADE = key("esa_capacity");

    private static ResourceKey<MachineUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LimaTechRegistries.MACHINE_UPGRADES_KEY, name);
    }

    private static <T extends MachineUpgradeEffect> DeferredHolder<MachineUpgradeEffectType<?>, MachineUpgradeEffectType<T>> registerType(String name, MapCodec<T> codec)
    {
        return EFFECT_TYPES.register(name, id -> new MachineUpgradeEffectType<>(id, codec));
    }
}