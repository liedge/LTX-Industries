package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgradeEntry;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrades;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgrades;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgradeEntry;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechDataComponents
{
    private LimaTechDataComponents() {}

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = LimaTech.RESOURCES.deferredRegister(Registries.DATA_COMPONENT_TYPE);

    public static void initRegister(IEventBus bus)
    {
        DATA_COMPONENT_TYPES.register(bus);
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EquipmentUpgrades>> EQUIPMENT_UPGRADES = DATA_COMPONENT_TYPES.register("equipment_upgrades",
            () -> DataComponentType.<EquipmentUpgrades>builder().persistent(EquipmentUpgrades.CODEC).networkSynchronized(EquipmentUpgrades.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineUpgrades>> MACHINE_UPGRADES = DATA_COMPONENT_TYPES.register("machine_upgrades",
            () -> DataComponentType.<MachineUpgrades>builder().persistent(MachineUpgrades.CODEC).networkSynchronized(MachineUpgrades.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EquipmentUpgradeEntry>> EQUIPMENT_UPGRADE_ENTRY = DATA_COMPONENT_TYPES.register("equipment_upgrade",
            () -> DataComponentType.<EquipmentUpgradeEntry>builder().persistent(EquipmentUpgradeEntry.CODEC).networkSynchronized(EquipmentUpgradeEntry.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineUpgradeEntry>> MACHINE_UPGRADE_ENTRY = DATA_COMPONENT_TYPES.register("machine_upgrade",
            () -> DataComponentType.<MachineUpgradeEntry>builder().persistent(MachineUpgradeEntry.CODEC).networkSynchronized(MachineUpgradeEntry.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WEAPON_AMMO = DATA_COMPONENT_TYPES.register("weapon_ammo",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAmmoSource>> WEAPON_AMMO_SOURCE = DATA_COMPONENT_TYPES.register("ammo_source",
            () -> DataComponentType.<WeaponAmmoSource>builder().persistent(WeaponAmmoSource.CODEC).networkSynchronized(WeaponAmmoSource.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GrenadeType>> GRENADE_TYPE = DATA_COMPONENT_TYPES.register("grenade_type",
            () -> DataComponentType.<GrenadeType>builder().persistent(GrenadeType.CODEC).networkSynchronized(GrenadeType.STREAM_CODEC).build());
}