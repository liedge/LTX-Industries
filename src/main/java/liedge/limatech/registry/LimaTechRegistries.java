package liedge.limatech.registry;

import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgradesystem.equipment.effect.EquipmentUpgradeEffectType;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgrade;
import liedge.limatech.lib.upgradesystem.machine.effect.MachineUpgradeEffectType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechRegistries
{
    private LimaTechRegistries() {}

    // Custom registries
    public static final ResourceKey<Registry<EquipmentUpgradeEffectType<?>>> EQUIPMENT_UPGRADE_EFFECT_TYPE_KEY = RESOURCES.registryResourceKey("upgrade_effect_type/equipment");
    public static final ResourceKey<Registry<MachineUpgradeEffectType<?>>> MACHINE_UPGRADE_EFFECT_TYPE_KEY = RESOURCES.registryResourceKey("upgrade_effect_type/machine");

    public static final Registry<EquipmentUpgradeEffectType<?>> EQUIPMENT_UPGRADE_EFFECT_TYPE = RESOURCES.registryBuilder(EQUIPMENT_UPGRADE_EFFECT_TYPE_KEY).sync(true).create();
    public static final Registry<MachineUpgradeEffectType<?>> MACHINE_UPGRADE_EFFECT_TYPE = RESOURCES.registryBuilder(MACHINE_UPGRADE_EFFECT_TYPE_KEY).sync(true).create();

    // Data pack registries
    public static final ResourceKey<Registry<EquipmentUpgrade>> EQUIPMENT_UPGRADES_KEY = RESOURCES.registryResourceKey("equipment_upgrade");
    public static final ResourceKey<Registry<MachineUpgrade>> MACHINE_UPGRADES_KEY = RESOURCES.registryResourceKey("machine_upgrade");
}