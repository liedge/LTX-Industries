package liedge.limatech.registry;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.upgrades.effect.equipment.EquipmentUpgradeEffect;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechRegistries
{
    private LimaTechRegistries() {}

    public static final Registry<DataComponentType<?>> UPGRADE_COMPONENT_TYPES = RESOURCES.registryBuilder(Keys.UPGRADE_COMPONENT_TYPES).sync(true).create();
    public static final Registry<MapCodec<? extends EquipmentUpgradeEffect>> EQUIPMENT_UPGRADE_EFFECT_TYPES = RESOURCES.registryBuilder(Keys.EQUIPMENT_UPGRADE_EFFECT_TYPES).sync(true).create();

    public static final class Keys
    {
        private Keys() {}

        public static final ResourceKey<Registry<DataComponentType<?>>> UPGRADE_COMPONENT_TYPES = RESOURCES.registryResourceKey("upgrade_component");
        public static final ResourceKey<Registry<MapCodec<? extends EquipmentUpgradeEffect>>> EQUIPMENT_UPGRADE_EFFECT_TYPES = RESOURCES.registryResourceKey("equipment_upgrade_effect");
        public static final ResourceKey<Registry<EquipmentUpgrade>> EQUIPMENT_UPGRADES = RESOURCES.registryResourceKey("equipment_upgrade");
        public static final ResourceKey<Registry<MachineUpgrade>> MACHINE_UPGRADES = RESOURCES.registryResourceKey("machine_upgrade");
    }
}