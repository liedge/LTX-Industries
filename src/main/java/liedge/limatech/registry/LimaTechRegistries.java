package liedge.limatech.registry;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.upgrades.effect.equipment.EquipmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.UpgradeEffectDataType;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechRegistries
{
    private LimaTechRegistries() {}

    // Custom registries
    public static final ResourceKey<Registry<UpgradeEffectDataType<?>>> UPGRADE_DATA_TYPE_KEY = RESOURCES.registryResourceKey("upgrade_data_type");
    public static final ResourceKey<Registry<MapCodec<? extends EquipmentUpgradeEffect>>> EQUIPMENT_UPGRADE_EFFECT_TYPE_KEY = RESOURCES.registryResourceKey("equipment_upgrade_effect");

    public static final Registry<UpgradeEffectDataType<?>> UPGRADE_DATA_TYPE = RESOURCES.registryBuilder(UPGRADE_DATA_TYPE_KEY).sync(true).create();
    public static final Registry<MapCodec<? extends EquipmentUpgradeEffect>> EQUIPMENT_UPGRADE_EFFECT_TYPE = RESOURCES.registryBuilder(EQUIPMENT_UPGRADE_EFFECT_TYPE_KEY).sync(true).create();

    // Data pack registries
    public static final ResourceKey<Registry<EquipmentUpgrade>> EQUIPMENT_UPGRADES_KEY = RESOURCES.registryResourceKey("equipment_upgrade");
    public static final ResourceKey<Registry<MachineUpgrade>> MACHINE_UPGRADES_KEY = RESOURCES.registryResourceKey("machine_upgrade");
}