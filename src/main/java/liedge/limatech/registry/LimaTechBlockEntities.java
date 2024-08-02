package liedge.limatech.registry;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.IORestrictedItemHandler;
import liedge.limacore.inventory.LimaItemStackHandler;
import liedge.limacore.lib.IODirection;
import liedge.limacore.lib.energy.IORestrictedEnergyStorage;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FABRICATOR.get(), (fab, side) -> new IORestrictedItemHandler((LimaItemStackHandler) fab.getItemHandler()));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FABRICATOR.get(), (fab, side) -> new IORestrictedEnergyStorage(fab.getEnergyStorage(), IODirection.OUTPUT_ONLY));
    }

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<FabricatorBlockEntity>> FABRICATOR = TYPES.register("fabricator", () -> LimaBlockEntityType.forBlock(FabricatorBlockEntity::new, LimaTechBlocks.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = TYPES.register("rocket_turret", () -> LimaBlockEntityType.forBlock(RocketTurretBlockEntity::new, LimaTechBlocks.ROCKET_TURRET));
}