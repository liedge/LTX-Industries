package liedge.limatech.registry;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
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
        // Machine capability registration (energy & items)
        Stream.of(TIERED_ENERGY_STORAGE_ARRAY, INFINITE_ENERGY_STORAGE_ARRAY, DIGITAL_FURNACE, GRINDER, RECOMPOSER, MATERIAL_FUSING_CHAMBER, FABRICATOR).map(DeferredHolder::get).forEach(machineType ->
        {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, machineType, EnergyHolderBlockEntity::createEnergyIOWrapper);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, machineType, ItemHolderBlockEntity::createItemIOWrapper);
        });
    }

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EnergyStorageArrayBlockEntity>> TIERED_ENERGY_STORAGE_ARRAY = TYPES.register("tiered_energy_storage_array", () -> LimaBlockEntityType.of(TieredESABlockEntity::new, LimaTechBlocks.TIERED_ENERGY_STORAGE_ARRAY));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EnergyStorageArrayBlockEntity>> INFINITE_ENERGY_STORAGE_ARRAY = TYPES.register("infinite_energy_storage_array", () -> LimaBlockEntityType.of(InfiniteESABlockEntity::new, LimaTechBlocks.INFINITE_ENERGY_STORAGE_ARRAY));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<DigitalFurnaceBlockEntity>> DIGITAL_FURNACE = TYPES.register("digital_furnace", () -> LimaBlockEntityType.of(DigitalFurnaceBlockEntity::new, LimaTechBlocks.DIGITAL_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<GrinderBlockEntity>> GRINDER = TYPES.register("grinder", () -> LimaBlockEntityType.of(GrinderBlockEntity::new, LimaTechBlocks.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RecomposerBlockEntity>> RECOMPOSER = TYPES.register("recomposer", () -> LimaBlockEntityType.of(RecomposerBlockEntity::new, LimaTechBlocks.RECOMPOSER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MaterialFusingChamberBlockEntity>> MATERIAL_FUSING_CHAMBER = TYPES.register("material_fusing_chamber", () -> LimaBlockEntityType.of(MaterialFusingChamberBlockEntity::new, LimaTechBlocks.MATERIAL_FUSING_CHAMBER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<FabricatorBlockEntity>> FABRICATOR = TYPES.register("fabricator", () -> LimaBlockEntityType.of(FabricatorBlockEntity::new, LimaTechBlocks.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EquipmentModTableBlockEntity>> EQUIPMENT_MOD_TABLE = TYPES.register("equipment_mod_table", () -> LimaBlockEntityType.of(EquipmentModTableBlockEntity::new, LimaTechBlocks.EQUIPMENT_MOD_TABLE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = TYPES.register("rocket_turret", () -> LimaBlockEntityType.of(RocketTurretBlockEntity::new, LimaTechBlocks.ROCKET_TURRET));
}