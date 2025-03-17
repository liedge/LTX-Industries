package liedge.limatech.registry;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaCoreRegistries;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.base.BlockEntityInputType;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechNetworkSerializers
{
    private LimaTechNetworkSerializers() {}

    private static final DeferredRegister<NetworkSerializer<?>> SERIALIZERS = LimaTech.RESOURCES.deferredRegister(LimaCoreRegistries.Keys.NETWORK_SERIALIZERS);

    public static void initRegister(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<EquipmentUpgrades>> ITEM_EQUIPMENT_UPGRADES = SERIALIZERS.register("equipment_upgrades", id -> NetworkSerializer.create(id, EquipmentUpgrades.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<MachineUpgrades>> ITEM_MACHINE_UPGRADES = SERIALIZERS.register("machine_upgrades", id -> NetworkSerializer.create(id, MachineUpgrades.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockEntityInputType>> MACHINE_INPUT_TYPE = SERIALIZERS.register("machine_input_type", id -> NetworkSerializer.create(id, BlockEntityInputType.STREAM_CODEC));
}