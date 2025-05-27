package liedge.limatech.registry.game;

import com.google.common.collect.ImmutableSet;
import liedge.limacore.blockentity.*;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechIds;
import liedge.limatech.blockentity.*;
import liedge.limatech.blockentity.base.BlockEntityInputType;
import liedge.limatech.blockentity.base.SidedAccessBlockEntityType;
import liedge.limatech.blockentity.base.SidedAccessRules;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class LimaTechBlockEntities
{
    private LimaTechBlockEntities() {}

    private static final DeferredRegister<BlockEntityType<?>> TYPES = LimaTech.RESOURCES.deferredRegister(Registries.BLOCK_ENTITY_TYPE);

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
        bus.addListener(RegisterCapabilitiesEvent.class, LimaTechBlockEntities::registerCapabilities);
    }

    private static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        // Machine capability registration (energy & items)
        Stream.of(ENERGY_STORAGE_ARRAY, INFINITE_ENERGY_STORAGE_ARRAY, DIGITAL_FURNACE, GRINDER, RECOMPOSER, MATERIAL_FUSING_CHAMBER, FABRICATOR, AUTO_FABRICATOR, MOLECULAR_RECONSTRUCTOR, ROCKET_TURRET, RAILGUN_TURRET).map(DeferredHolder::get).forEach(machineType ->
        {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, machineType, EnergyHolderBlockEntity::createEnergyIOWrapper);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, machineType, ItemHolderBlockEntity::createItemIOWrapper);
        });
    }

    // Sided Access Rules
    private static final Set<RelativeHorizontalSide> DOUBLE_BLOCK_VALID_SIDES = ImmutableSet.copyOf(EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT, RelativeHorizontalSide.RIGHT));
    private static final SidedAccessRules DOUBLE_MACHINE_ENERGY_RULES = SidedAccessRules.builder()
            .setValidSides(DOUBLE_BLOCK_VALID_SIDES)
            .setValidIOStates(IOAccessSets.INPUT_ONLY_OR_DISABLED)
            .setDefaultIOState(IOAccess.INPUT_ONLY).build();
    private static final SidedAccessRules DOUBLE_MACHINE_ITEM_RULES = SidedAccessRules.builder()
            .setValidSides(DOUBLE_BLOCK_VALID_SIDES)
            .setValidIOStates(IOAccessSets.ALL_ALLOWED)
            .setDefaultIOState(IOAccess.INPUT_ONLY)
            .defineAutoOutput(true).build();
    private static final SidedAccessRules TURRET_ITEM_RULES = SidedAccessRules.builder()
            .setValidSides(DOUBLE_BLOCK_VALID_SIDES)
            .setValidIOStates(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
            .setDefaultIOState(IOAccess.OUTPUT_ONLY)
            .defineAutoOutput(true).build();

    // Registrations
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<EnergyStorageArrayBlockEntity>> ENERGY_STORAGE_ARRAY = TYPES.register(LimaTechIds.ID_ENERGY_STORAGE_ARRAY, () -> SidedAccessBlockEntityType.Builder.builder(EnergyStorageArrayBlockEntity::new)
            .withBlock(LimaTechBlocks.ENERGY_STORAGE_ARRAY)
            .withSideRules(BlockEntityInputType.ITEMS, BaseESABlockEntity.ITEM_ACCESS_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, builder -> builder
                    .setValidIOStates(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED)
                    .setDefaultIOState(IOAccess.INPUT_ONLY)
                    .defineAutoOutput(true, true))
            .build());
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<InfiniteESABlockEntity>> INFINITE_ENERGY_STORAGE_ARRAY = TYPES.register(LimaTechIds.ID_INFINITE_ENERGY_STORAGE_ARRAY, () -> SidedAccessBlockEntityType.Builder.builder(InfiniteESABlockEntity::new)
            .withBlock(LimaTechBlocks.INFINITE_ENERGY_STORAGE_ARRAY)
            .withSideRules(BlockEntityInputType.ITEMS, BaseESABlockEntity.ITEM_ACCESS_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, builder -> builder
                    .setValidIOStates(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
                    .setDefaultIOState(IOAccess.OUTPUT_ONLY)
                    .defineAutoOutput(true, true))
            .build());

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<DigitalFurnaceBlockEntity>> DIGITAL_FURNACE = registerSimpleRecipeMachine(LimaTechIds.ID_DIGITAL_FURNACE, DigitalFurnaceBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.DIGITAL_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<GrinderBlockEntity>> GRINDER = registerSimpleRecipeMachine(LimaTechIds.ID_GRINDER, GrinderBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RecomposerBlockEntity>> RECOMPOSER = registerSimpleRecipeMachine(LimaTechIds.ID_RECOMPOSER, RecomposerBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.RECOMPOSER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<MaterialFusingChamberBlockEntity>> MATERIAL_FUSING_CHAMBER = registerSimpleRecipeMachine(LimaTechIds.ID_MATERIAL_FUSING_CHAMBER, MaterialFusingChamberBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.MATERIAL_FUSING_CHAMBER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<FabricatorBlockEntity>> FABRICATOR = registerSimpleRecipeMachine(LimaTechIds.ID_FABRICATOR, FabricatorBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<AutoFabricatorBlockEntity>> AUTO_FABRICATOR = registerSimpleRecipeMachine(LimaTechIds.ID_AUTO_FABRICATOR, AutoFabricatorBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.AUTO_FABRICATOR));

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<MolecularReconstructorBlockEntity>> MOLECULAR_RECONSTRUCTOR = registerDoubleBlockMachine(LimaTechIds.ID_MOLECULAR_RECONSTRUCTOR, MolecularReconstructorBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.MOLECULAR_RECONSTRUCTOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EquipmentUpgradeStationBlockEntity>> EQUIPMENT_UPGRADE_STATION = TYPES.register(LimaTechIds.ID_EQUIPMENT_UPGRADE_STATION, () -> LimaBlockEntityType.of(EquipmentUpgradeStationBlockEntity::new, LimaTechBlocks.EQUIPMENT_UPGRADE_STATION));

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = registerTurret(LimaTechIds.ID_ROCKET_TURRET, RocketTurretBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.ROCKET_TURRET));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RailgunTurretBlockEntity>> RAILGUN_TURRET = registerTurret(LimaTechIds.ID_RAILGUN_TURRET, RailgunTurretBlockEntity::new, builder -> builder.withBlock(LimaTechBlocks.RAILGUN_TURRET));

    // Helpers
    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerSimpleRecipeMachine(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builder)
    {
        return TYPES.register(name, () -> builder.apply(SidedAccessBlockEntityType.Builder.builder(factory))
                .withSideRules(BlockEntityInputType.ITEMS, SimpleRecipeMachineBlockEntity.ITEM_ACCESS_RULES)
                .withSideRules(BlockEntityInputType.ENERGY, SimpleRecipeMachineBlockEntity.ENERGY_ACCESS_RULES).build());
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerDoubleBlockMachine(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builder)
    {
        return TYPES.register(name, () -> builder.apply(SidedAccessBlockEntityType.Builder.builder(factory))
                .withSideRules(BlockEntityInputType.ITEMS, DOUBLE_MACHINE_ITEM_RULES)
                .withSideRules(BlockEntityInputType.ENERGY, DOUBLE_MACHINE_ENERGY_RULES).build());
    }

    private static <BE extends BaseTurretBlockEntity> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerTurret(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builder)
    {
        return TYPES.register(name, () -> builder.apply(SidedAccessBlockEntityType.Builder.builder(factory))
                .withSideRules(BlockEntityInputType.ITEMS, TURRET_ITEM_RULES)
                .withSideRules(BlockEntityInputType.ENERGY, DOUBLE_MACHINE_ENERGY_RULES).build());
    }
}