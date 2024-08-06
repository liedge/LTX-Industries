package liedge.limatech.registry;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaCoreRegistries;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechNetworkSerializers
{
    private LimaTechNetworkSerializers() {}

    private static final DeferredRegister<NetworkSerializer<?>> SERIALIZERS = LimaTech.RESOURCES.deferredRegister(LimaCoreRegistries.NETWORK_SERIALIZERS_KEY);

    public static void initRegister(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ItemEquipmentUpgrades>> ITEM_EQUIPMENT_UPGRADES = SERIALIZERS.register("upgrades", id -> NetworkSerializer.create(id, ItemEquipmentUpgrades.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<MachineInputType>> MACHINE_INPUT_TYPE = SERIALIZERS.register("machine_input_type", id -> NetworkSerializer.create(id, MachineInputType.STREAM_CODEC));
}