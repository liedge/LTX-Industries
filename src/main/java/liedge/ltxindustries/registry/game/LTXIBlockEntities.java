package liedge.ltxindustries.registry.game;

import com.google.common.collect.ImmutableSet;
import liedge.limacore.blockentity.*;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.ltxindustries.LTXICommonIds;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.*;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import liedge.ltxindustries.blockentity.base.SidedAccessRules;
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

public final class LTXIBlockEntities
{
    private LTXIBlockEntities() {}

    private static final DeferredRegister<BlockEntityType<?>> TYPES = LTXIndustries.RESOURCES.deferredRegister(Registries.BLOCK_ENTITY_TYPE);

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
        bus.addListener(RegisterCapabilitiesEvent.class, LTXIBlockEntities::registerCapabilities);
    }

    private static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        // Machine capability registration (energy & items)
        Stream.of(ENERGY_STORAGE_ARRAY, INFINITE_ENERGY_STORAGE_ARRAY, DIGITAL_FURNACE, DIGITAL_SMOKER, DIGITAL_BLAST_FURNACE, GRINDER,
                MATERIAL_FUSING_CHAMBER, FABRICATOR, AUTO_FABRICATOR, MOLECULAR_RECONSTRUCTOR, ROCKET_TURRET, RAILGUN_TURRET).map(DeferredHolder::get).forEach(machineType ->
        {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, machineType, EnergyHolderBlockEntity::createEnergyIOWrapper);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, machineType, ItemHolderBlockEntity::createItemIOWrapper);
        });
    }

    //#region Sided Access Rules
    private static final SidedAccessRules MACHINE_ITEM_RULES = SidedAccessRules.allSides(IOAccessSets.ALL_ALLOWED, IOAccess.INPUT_ONLY, false, true);
    private static final SidedAccessRules MACHINE_ENERGY_RULES = SidedAccessRules.allSides(IOAccessSets.INPUT_ONLY_OR_DISABLED, IOAccess.INPUT_ONLY, false, false);
    private static final SidedAccessRules FABRICATOR_ITEM_RULES = SidedAccessRules.allSides(IOAccessSets.OUTPUT_ONLY_OR_DISABLED, IOAccess.OUTPUT_ONLY, false, true);

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
    //#endregion

    //#region Registrations
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<EnergyStorageArrayBlockEntity>> ENERGY_STORAGE_ARRAY = TYPES.register(LTXICommonIds.ID_ENERGY_STORAGE_ARRAY, () -> SidedAccessBlockEntityType.sidedBuilder(EnergyStorageArrayBlockEntity::new)
            .withBlock(LTXIBlocks.ENERGY_STORAGE_ARRAY)
            .withSideRules(BlockEntityInputType.ITEMS, MACHINE_ITEM_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, builder -> builder
                    .setValidIOStates(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED)
                    .setDefaultIOState(IOAccess.INPUT_ONLY)
                    .defineAutoOutput(true, true))
            .build());
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<InfiniteESABlockEntity>> INFINITE_ENERGY_STORAGE_ARRAY = TYPES.register(LTXICommonIds.ID_INFINITE_ENERGY_STORAGE_ARRAY, () -> SidedAccessBlockEntityType.sidedBuilder(InfiniteESABlockEntity::new)
            .withBlock(LTXIBlocks.INFINITE_ENERGY_STORAGE_ARRAY)
            .withSideRules(BlockEntityInputType.ITEMS, MACHINE_ITEM_RULES)
            .withSideRules(BlockEntityInputType.ENERGY, builder -> builder
                    .setValidIOStates(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
                    .setDefaultIOState(IOAccess.OUTPUT_ONLY)
                    .defineAutoOutput(true, true))
            .build());

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<DigitalFurnaceBlockEntity>> DIGITAL_FURNACE = registerItemEnergyMachine(LTXICommonIds.ID_DIGITAL_FURNACE, DigitalFurnaceBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.DIGITAL_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<DigitalSmokerBlockEntity>> DIGITAL_SMOKER = registerItemEnergyMachine(LTXICommonIds.ID_DIGITAL_SMOKER, DigitalSmokerBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.DIGITAL_SMOKER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<DigitalBlastFurnaceBlockEntity>> DIGITAL_BLAST_FURNACE = registerItemEnergyMachine(LTXICommonIds.ID_DIGITAL_BLAST_FURNACE, DigitalBlastFurnaceBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.DIGITAL_BLAST_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<GrinderBlockEntity>> GRINDER = registerItemEnergyMachine(LTXICommonIds.ID_GRINDER, GrinderBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<MaterialFusingChamberBlockEntity>> MATERIAL_FUSING_CHAMBER = registerItemEnergyMachine(LTXICommonIds.ID_MATERIAL_FUSING_CHAMBER, MaterialFusingChamberBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.MATERIAL_FUSING_CHAMBER));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<FabricatorBlockEntity>> FABRICATOR = registerItemEnergyMachine(LTXICommonIds.ID_FABRICATOR, FabricatorBlockEntity::new, FABRICATOR_ITEM_RULES, MACHINE_ENERGY_RULES, builder -> builder.withBlock(LTXIBlocks.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<AutoFabricatorBlockEntity>> AUTO_FABRICATOR = registerItemEnergyMachine(LTXICommonIds.ID_AUTO_FABRICATOR, AutoFabricatorBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.AUTO_FABRICATOR));

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<MolecularReconstructorBlockEntity>> MOLECULAR_RECONSTRUCTOR = registerItemEnergyMachine(LTXICommonIds.ID_MOLECULAR_RECONSTRUCTOR, MolecularReconstructorBlockEntity::new, DOUBLE_MACHINE_ITEM_RULES, DOUBLE_MACHINE_ENERGY_RULES, builder -> builder.withBlock(LTXIBlocks.MOLECULAR_RECONSTRUCTOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EquipmentUpgradeStationBlockEntity>> EQUIPMENT_UPGRADE_STATION = TYPES.register(LTXICommonIds.ID_EQUIPMENT_UPGRADE_STATION, () -> LimaBlockEntityType.of(EquipmentUpgradeStationBlockEntity::new, LTXIBlocks.EQUIPMENT_UPGRADE_STATION));

    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = registerTurret(LTXICommonIds.ID_ROCKET_TURRET, RocketTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.ROCKET_TURRET));
    public static final DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<RailgunTurretBlockEntity>> RAILGUN_TURRET = registerTurret(LTXICommonIds.ID_RAILGUN_TURRET, RailgunTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.RAILGUN_TURRET));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MeshBlockEntity>> MESH_BLOCK = TYPES.register("mesh_block", () -> LimaBlockEntityType.of(MeshBlockEntity::new, LTXIBlocks.MESH_BLOCK));
    //#endregion

    // Helpers
    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerItemEnergyMachine(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, SidedAccessRules itemRules, SidedAccessRules energyRules, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builderOp)
    {
        return TYPES.register(name, () -> builderOp.apply(SidedAccessBlockEntityType.sidedBuilder(beFactory))
                .withSideRules(BlockEntityInputType.ITEMS, itemRules)
                .withSideRules(BlockEntityInputType.ENERGY, energyRules).build());
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerItemEnergyMachine(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builderOp)
    {
        return registerItemEnergyMachine(name, beFactory, MACHINE_ITEM_RULES, MACHINE_ENERGY_RULES, builderOp);
    }

    private static <BE extends BaseTurretBlockEntity> DeferredHolder<BlockEntityType<?>, SidedAccessBlockEntityType<BE>> registerTurret(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, UnaryOperator<SidedAccessBlockEntityType.Builder<BE>> builderOp)
    {
        return registerItemEnergyMachine(name, beFactory, TURRET_ITEM_RULES, DOUBLE_MACHINE_ENERGY_RULES, builderOp);
    }
}