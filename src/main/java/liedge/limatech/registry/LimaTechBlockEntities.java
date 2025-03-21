package liedge.limatech.registry;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.*;
import liedge.limatech.blockentity.base.SidedAccessBlockEntityType;
import liedge.limatech.blockentity.base.SidedAccessRules;
import liedge.limatech.blockentity.base.BlockEntityInputType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;
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
        Stream.of(ENERGY_STORAGE_ARRAY, INFINITE_ENERGY_STORAGE_ARRAY, DIGITAL_FURNACE, GRINDER, RECOMPOSER, MATERIAL_FUSING_CHAMBER, FABRICATOR, ROCKET_TURRET).map(DeferredHolder::get).forEach(machineType ->
        {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, machineType, EnergyHolderBlockEntity::createEnergyIOWrapper);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, machineType, ItemHolderBlockEntity::createItemIOWrapper);
        });
    }

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<EnergyStorageArrayBlockEntity>> ENERGY_STORAGE_ARRAY = TYPES.register("energy_storage_array", () -> SidedAccessBlockEntityType.Builder.builder(EnergyStorageArrayBlockEntity::new)
            .withBlock(LimaTechBlocks.ENERGY_STORAGE_ARRAY)
            .withSideRules(BlockEntityInputType.ITEMS, BaseESABlockEntity.ITEM_ACCESS_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, builder -> builder
                    .setValidIOStates(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED)
                    .setDefaultIOState(IOAccess.INPUT_ONLY)
                    .defineAutoOutput(true, true))
            .build());
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<InfiniteESABlockEntity>> INFINITE_ENERGY_STORAGE_ARRAY = TYPES.register("infinite_energy_storage_array", () -> SidedAccessBlockEntityType.Builder.builder(InfiniteESABlockEntity::new)
            .withBlock(LimaTechBlocks.INFINITE_ENERGY_STORAGE_ARRAY)
            .withSideRules(BlockEntityInputType.ITEMS, BaseESABlockEntity.ITEM_ACCESS_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, builder -> builder
                    .setValidIOStates(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
                    .setDefaultIOState(IOAccess.OUTPUT_ONLY)
                    .defineAutoOutput(true, true))
            .build());
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<DigitalFurnaceBlockEntity>> DIGITAL_FURNACE = registerSimpleRecipeMachine("digital_furnace", DigitalFurnaceBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.DIGITAL_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<GrinderBlockEntity>> GRINDER = registerSimpleRecipeMachine("grinder", GrinderBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RecomposerBlockEntity>> RECOMPOSER = registerSimpleRecipeMachine("recomposer", RecomposerBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.RECOMPOSER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<MaterialFusingChamberBlockEntity>> MATERIAL_FUSING_CHAMBER = registerSimpleRecipeMachine("material_fusing_chamber", MaterialFusingChamberBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.MATERIAL_FUSING_CHAMBER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<FabricatorBlockEntity>> FABRICATOR = TYPES.register("fabricator", () -> SidedAccessBlockEntityType.Builder.builder(FabricatorBlockEntity::new)
            .withBlock(LimaTechBlocks.FABRICATOR)
            .withSideRules(BlockEntityInputType.ITEMS, SidedAccessRules.allSides(IOAccessSets.OUTPUT_ONLY_OR_DISABLED, IOAccess.DISABLED, false, true))
            .withSideRules(BlockEntityInputType.ENERGY, SidedAccessRules.allSides(IOAccessSets.INPUT_ONLY_OR_DISABLED, IOAccess.INPUT_ONLY, false, false)).build());
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EquipmentUpgradeStationBlockEntity>> EQUIPMENT_UPGRADE_STATION = TYPES.register("equipment_upgrade_station", () -> LimaBlockEntityType.of(EquipmentUpgradeStationBlockEntity::new, LimaTechBlocks.EQUIPMENT_UPGRADE_STATION));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = TYPES.register("rocket_turret", () -> SidedAccessBlockEntityType.Builder.builder(RocketTurretBlockEntity::new)
            .withBlock(LimaTechBlocks.ROCKET_TURRET)
            .withSideRules(BlockEntityInputType.ITEMS, RocketTurretBlockEntity.ITEM_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, RocketTurretBlockEntity.ENERGY_RULES)
            .build());

    private static <BE extends SimpleRecipeMachineBlockEntity<?, ?>> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerSimpleRecipeMachine(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builder)
    {
        return TYPES.register(name, () -> builder.apply(SidedAccessBlockEntityType.Builder.builder(factory))
                .withSideRules(BlockEntityInputType.ITEMS, SimpleRecipeMachineBlockEntity.ITEM_ACCESS_RULES)
                .withSideRules(BlockEntityInputType.ENERGY, SimpleRecipeMachineBlockEntity.ENERGY_ACCESS_RULES).build());
    }
}