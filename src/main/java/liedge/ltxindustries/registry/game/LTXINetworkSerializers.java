package liedge.ltxindustries.registry.game;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaCoreRegistries;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXINetworkSerializers
{
    private LTXINetworkSerializers() {}

    private static final DeferredRegister<NetworkSerializer<?>> SERIALIZERS = LTXIndustries.RESOURCES.deferredRegister(LimaCoreRegistries.Keys.NETWORK_SERIALIZERS);

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<EquipmentUpgrades>> ITEM_EQUIPMENT_UPGRADES = SERIALIZERS.register("equipment_upgrades", id -> NetworkSerializer.create(id, EquipmentUpgrades.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<MachineUpgrades>> ITEM_MACHINE_UPGRADES = SERIALIZERS.register("machine_upgrades", id -> NetworkSerializer.create(id, MachineUpgrades.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockEntityInputType>> MACHINE_INPUT_TYPE = SERIALIZERS.register("machine_input_type", id -> NetworkSerializer.create(id, BlockEntityInputType.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockIOConfiguration>> BLOCK_IO_CONFIG = SERIALIZERS.register("block_io_config", id -> NetworkSerializer.create(id, BlockIOConfiguration.STREAM_CODEC));
}