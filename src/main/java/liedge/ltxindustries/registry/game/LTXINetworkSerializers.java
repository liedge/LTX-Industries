package liedge.ltxindustries.registry.game;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaDeferredNetworkSerializers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class LTXINetworkSerializers
{
    private LTXINetworkSerializers() {}

    private static final LimaDeferredNetworkSerializers SERIALIZERS = LTXIndustries.RESOURCES.deferredNetworkSerializers();

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<EquipmentUpgrades>> ITEM_EQUIPMENT_UPGRADES = SERIALIZERS.registerCodec("equipment_upgrades", () -> EquipmentUpgrades.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<MachineUpgrades>> ITEM_MACHINE_UPGRADES = SERIALIZERS.registerCodec("machine_upgrades", () -> MachineUpgrades.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockEntityInputType>> MACHINE_INPUT_TYPE = SERIALIZERS.registerCodec("machine_input_type", () -> BlockEntityInputType.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockIOConfiguration>> BLOCK_IO_CONFIG = SERIALIZERS.registerCodec("block_io_config", () -> BlockIOConfiguration.STREAM_CODEC);
}