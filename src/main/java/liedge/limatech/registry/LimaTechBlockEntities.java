package liedge.limatech.registry;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.IORestrictedItemHandler;
import liedge.limacore.lib.IODirection;
import liedge.limacore.lib.energy.IORestrictedEnergyStorage;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.stream.Stream;

public final class LimaTechBlockEntities
{
    private LimaTechBlockEntities() {}

    private static final DeferredRegister<BlockEntityType<?>> TYPES = LimaTech.RESOURCES.deferredRegister(Registries.BLOCK_ENTITY_TYPE);

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        Stream.of(GRINDER, MATERIAL_FUSING_CHAMBER, FABRICATOR).map(DeferredHolder::get).forEach(machineType -> {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, machineType, (be, side) -> new IORestrictedEnergyStorage(be.getEnergyStorage(), IODirection.OUTPUT_ONLY));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, machineType, (be, side) -> new IORestrictedItemHandler(be.getItemHandler()));
        });
    }

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<GrinderBlockEntity>> GRINDER = TYPES.register("grinder", () -> LimaBlockEntityType.forBlock(GrinderBlockEntity::new, LimaTechBlocks.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MaterialFusingChamberBlockEntity>> MATERIAL_FUSING_CHAMBER = TYPES.register("material_fusing_chamber", () -> LimaBlockEntityType.forBlock(MaterialFusingChamberBlockEntity::new, LimaTechBlocks.MATERIAL_FUSING_CHAMBER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<FabricatorBlockEntity>> FABRICATOR = TYPES.register("fabricator", () -> LimaBlockEntityType.forBlock(FabricatorBlockEntity::new, LimaTechBlocks.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = TYPES.register("rocket_turret", () -> LimaBlockEntityType.forBlock(RocketTurretBlockEntity::new, LimaTechBlocks.ROCKET_TURRET));
}