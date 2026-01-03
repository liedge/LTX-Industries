package liedge.ltxindustries.registry.game;

import com.google.common.collect.ImmutableSet;
import liedge.limacore.blockentity.*;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.fluid.FluidHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.*;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.IOConfigurationRules;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
        // Machine capability registration (item, energy)
        registerItemEnergyCaps(event, List.of(
                ENERGY_CELL_ARRAY,
                INFINITE_ENERGY_CELL_ARRAY,
                DIGITAL_FURNACE,
                DIGITAL_SMOKER,
                DIGITAL_BLAST_FURNACE,
                GRINDER,
                VOLTAIC_INJECTOR,
                FABRICATOR,
                AUTO_FABRICATOR,
                MOLECULAR_RECONSTRUCTOR,
                ROCKET_TURRET,
                RAILGUN_TURRET));

        // Machine capability registration (item, energy, fluid)
        registerItemEnergyFluidCaps(event, List.of(
                MATERIAL_FUSING_CHAMBER,
                ELECTROCENTRIFUGE,
                MIXER,
                CHEM_LAB,
                ASSEMBLER,
                GEO_SYNTHESIZER,
                DIGITAL_GARDEN));
    }

    private static <T extends LimaBlockEntity & ItemHolderBlockEntity & EnergyHolderBlockEntity> void registerItemEnergyCaps(RegisterCapabilitiesEvent event, Collection<? extends Supplier<? extends BlockEntityType<? extends T>>> types)
    {
        for (Supplier<? extends BlockEntityType<? extends T>> holder : types)
        {
            BlockEntityType<? extends T> type = holder.get();
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, ItemHolderBlockEntity::createItemIOWrapper);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, EnergyHolderBlockEntity::createEnergyIOWrapper);
        }
    }

    private static <T extends LimaBlockEntity & ItemHolderBlockEntity & EnergyHolderBlockEntity & FluidHolderBlockEntity> void registerItemEnergyFluidCaps(RegisterCapabilitiesEvent event, Collection<? extends Supplier<? extends BlockEntityType<? extends T>>> types)
    {
        for (Supplier<? extends BlockEntityType<? extends T>> holder : types)
        {
            BlockEntityType<? extends T> type = holder.get();
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, ItemHolderBlockEntity::createItemIOWrapper);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, EnergyHolderBlockEntity::createEnergyIOWrapper);
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, FluidHolderBlockEntity::createFluidIOWrapper);
        }
    }

    //#region Sided Access Rules
    private static final IOConfigurationRules MACHINE_STANDARD_IO = IOConfigurationRules.builder().forAllSides().permits(IOAccessSets.ALL_ALLOWED).withDefaultIOAccess(IOAccess.INPUT_ONLY).allowsAutoOutput().build();
    private static final IOConfigurationRules MACHINE_INPUT_ONLY = IOConfigurationRules.builder().forAllSides().permits(IOAccessSets.INPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).build();

    private static final Set<RelativeHorizontalSide> FABRICATOR_VALID_SIDES = ImmutableSet.copyOf(EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT));
    private static final IOConfigurationRules FABRICATOR_ITEM_RULES = IOConfigurationRules.builder()
            .forSides(FABRICATOR_VALID_SIDES)
            .permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.OUTPUT_ONLY)
            .allowsAutoOutput().build();
    private static final IOConfigurationRules FABRICATOR_ENERGY_RULES = IOConfigurationRules.builder().forSides(FABRICATOR_VALID_SIDES).permits(IOAccessSets.INPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).build();

    private static final Set<RelativeHorizontalSide> DOUBLE_BLOCK_VALID_SIDES = ImmutableSet.copyOf(EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT, RelativeHorizontalSide.RIGHT));
    private static final IOConfigurationRules DOUBLE_MACHINE_ENERGY_RULES = IOConfigurationRules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.INPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.INPUT_ONLY).build();
    private static final IOConfigurationRules DOUBLE_MACHINE_ITEM_RULES = IOConfigurationRules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.ALL_ALLOWED)
            .withDefaultIOAccess(IOAccess.INPUT_ONLY)
            .allowsAutoOutput().build();
    private static final IOConfigurationRules TURRET_ITEM_RULES = IOConfigurationRules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.OUTPUT_ONLY)
            .allowsAutoOutput().build();
    //#endregion

    //#region Registrations
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EquipmentUpgradeStationBlockEntity>> EQUIPMENT_UPGRADE_STATION = TYPES.register(LTXIIdentifiers.ID_EQUIPMENT_UPGRADE_STATION, () -> LimaBlockEntityType.of(EquipmentUpgradeStationBlockEntity::new, LTXIBlocks.EQUIPMENT_UPGRADE_STATION, LTXIMenus.EQUIPMENT_UPGRADE_STATION));

    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<EnergyCellArrayBlockEntity>> ENERGY_CELL_ARRAY = TYPES.register(LTXIIdentifiers.ID_ENERGY_CELL_ARRAY, () -> ConfigurableIOBlockEntityType.sidedBuilder(EnergyCellArrayBlockEntity::new)
            .withBlock(LTXIBlocks.ENERGY_CELL_ARRAY)
            .hasMenu(LTXIMenus.ENERGY_CELL_ARRAY)
            .withConfigRules(BlockEntityInputType.ITEMS, MACHINE_STANDARD_IO)
            .withConfigRules(BlockEntityInputType.ENERGY, builder -> builder.forAllSides().permits(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).autoOutputByDefault())
            .build());
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<InfiniteECABlockEntity>> INFINITE_ENERGY_CELL_ARRAY = TYPES.register(LTXIIdentifiers.ID_INFINITE_ENERGY_CELL_ARRAY, () -> ConfigurableIOBlockEntityType.sidedBuilder(InfiniteECABlockEntity::new)
            .withBlock(LTXIBlocks.INFINITE_ENERGY_CELL_ARRAY)
            .hasMenu(LTXIMenus.ENERGY_CELL_ARRAY)
            .withConfigRules(BlockEntityInputType.ITEMS, MACHINE_STANDARD_IO)
            .withConfigRules(BlockEntityInputType.ENERGY, builder -> builder.forAllSides().permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.OUTPUT_ONLY).autoOutputByDefault())
            .build());

    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<DigitalFurnaceBlockEntity>> DIGITAL_FURNACE = registerItemEnergyMachine(LTXIIdentifiers.ID_DIGITAL_FURNACE, DigitalFurnaceBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.DIGITAL_FURNACE).hasMenu(LTXIMenus.DIGITAL_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<DigitalSmokerBlockEntity>> DIGITAL_SMOKER = registerItemEnergyMachine(LTXIIdentifiers.ID_DIGITAL_SMOKER, DigitalSmokerBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.DIGITAL_SMOKER).hasMenu(LTXIMenus.DIGITAL_SMOKER));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<DigitalBlastFurnaceBlockEntity>> DIGITAL_BLAST_FURNACE = registerItemEnergyMachine(LTXIIdentifiers.ID_DIGITAL_BLAST_FURNACE, DigitalBlastFurnaceBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.DIGITAL_BLAST_FURNACE).hasMenu(LTXIMenus.DIGITAL_BLAST_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<GrinderBlockEntity>> GRINDER = registerItemEnergyMachine(LTXIIdentifiers.ID_GRINDER, GrinderBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.GRINDER).hasMenu(LTXIMenus.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<MaterialFusingChamberBlockEntity>> MATERIAL_FUSING_CHAMBER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_MATERIAL_FUSING_CHAMBER, MaterialFusingChamberBlockEntity::new, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, MACHINE_INPUT_ONLY,
            builder -> builder.withBlock(LTXIBlocks.MATERIAL_FUSING_CHAMBER).hasMenu(LTXIMenus.MATERIAL_FUSING_CHAMBER));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<ElectroCentrifugeBlockEntity>> ELECTROCENTRIFUGE = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_ELECTROCENTRIFUGE, ElectroCentrifugeBlockEntity::new, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, MACHINE_STANDARD_IO,
            builder -> builder.withBlock(LTXIBlocks.ELECTROCENTRIFUGE).hasMenu(LTXIMenus.ELECTROCENTRIFUGE));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<MixerBlockEntity>> MIXER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_MIXER, MixerBlockEntity::new, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, MACHINE_STANDARD_IO,
            builder -> builder.withBlock(LTXIBlocks.MIXER).hasMenu(LTXIMenus.MIXER));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<VoltaicInjectorBlockEntity>> VOLTAIC_INJECTOR = registerItemEnergyMachine(LTXIIdentifiers.ID_VOLTAIC_INJECTOR, VoltaicInjectorBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.VOLTAIC_INJECTOR).hasMenu(LTXIMenus.VOLTAIC_INJECTOR));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<ChemLabBlockEntity>> CHEM_LAB = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_CHEM_LAB, ChemLabBlockEntity::new, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, MACHINE_STANDARD_IO,
            builder -> builder.withBlock(LTXIBlocks.CHEM_LAB).hasMenu(LTXIMenus.CHEM_LAB));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<AssemblerBlockEntity>> ASSEMBLER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_ASSEMBLER, AssemblerBlockEntity::new, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, MACHINE_INPUT_ONLY, builder -> builder.withBlock(LTXIBlocks.ASSEMBLER).hasMenu(LTXIMenus.ASSEMBLER));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<GeoSynthesizerBlockEntity>> GEO_SYNTHESIZER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_GEO_SYNTHESIZER, GeoSynthesizerBlockEntity::new, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, MACHINE_INPUT_ONLY, builder -> builder.withBlock(LTXIBlocks.GEO_SYNTHESIZER).hasMenu(LTXIMenus.GEO_SYNTHESIZER));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<FabricatorBlockEntity>> FABRICATOR = registerItemEnergyMachine(LTXIIdentifiers.ID_FABRICATOR, FabricatorBlockEntity::new, FABRICATOR_ITEM_RULES, FABRICATOR_ENERGY_RULES, builder -> builder.withBlock(LTXIBlocks.FABRICATOR).hasMenu(LTXIMenus.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<AutoFabricatorBlockEntity>> AUTO_FABRICATOR = registerItemEnergyMachine(LTXIIdentifiers.ID_AUTO_FABRICATOR, AutoFabricatorBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.AUTO_FABRICATOR).hasMenu(LTXIMenus.AUTO_FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<MolecularReconstructorBlockEntity>> MOLECULAR_RECONSTRUCTOR = registerItemEnergyMachine(LTXIIdentifiers.ID_MOLECULAR_RECONSTRUCTOR, MolecularReconstructorBlockEntity::new, DOUBLE_MACHINE_ITEM_RULES, DOUBLE_MACHINE_ENERGY_RULES, builder -> builder.withBlock(LTXIBlocks.MOLECULAR_RECONSTRUCTOR).hasMenu(LTXIMenus.MOLECULAR_RECONSTRUCTOR));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<DigitalGardenBlockEntity>> DIGITAL_GARDEN = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_DIGITAL_GARDEN, DigitalGardenBlockEntity::new, DOUBLE_MACHINE_ITEM_RULES, DOUBLE_MACHINE_ENERGY_RULES, DOUBLE_MACHINE_ENERGY_RULES, builder -> builder.withBlock(LTXIBlocks.DIGITAL_GARDEN).hasMenu(LTXIMenus.DIGITAL_GARDEN));

    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = registerTurret(LTXIIdentifiers.ID_ROCKET_TURRET, RocketTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.ROCKET_TURRET).hasMenu(LTXIMenus.ROCKET_TURRET));
    public static final DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<RailgunTurretBlockEntity>> RAILGUN_TURRET = registerTurret(LTXIIdentifiers.ID_RAILGUN_TURRET, RailgunTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.RAILGUN_TURRET).hasMenu(LTXIMenus.RAILGUN_TURRET));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MeshBlockEntity>> MESH_BLOCK = TYPES.register("mesh_block", () -> LimaBlockEntityType.of(MeshBlockEntity::new, LTXIBlocks.MESH_BLOCK));
    //#endregion

    // Helpers
    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<BE>> registerItemEnergyMachine(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, IOConfigurationRules itemRules, IOConfigurationRules energyRules, UnaryOperator<ConfigurableIOBlockEntityType.Builder<BE>> builderOp)
    {
        return TYPES.register(name, () -> builderOp.apply(ConfigurableIOBlockEntityType.sidedBuilder(beFactory))
                .withConfigRules(BlockEntityInputType.ITEMS, itemRules)
                .withConfigRules(BlockEntityInputType.ENERGY, energyRules).build());
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<BE>> registerItemEnergyFluidMachine(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, IOConfigurationRules itemRules, IOConfigurationRules energyRules, IOConfigurationRules fluidRules, UnaryOperator<ConfigurableIOBlockEntityType.Builder<BE>> builderOp)
    {
        return TYPES.register(name, () -> builderOp.apply(ConfigurableIOBlockEntityType.sidedBuilder(beFactory))
                .withConfigRules(BlockEntityInputType.ITEMS, itemRules).withConfigRules(BlockEntityInputType.ENERGY, energyRules).withConfigRules(BlockEntityInputType.FLUIDS, fluidRules).build());
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<BE>> registerItemEnergyMachine(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, UnaryOperator<ConfigurableIOBlockEntityType.Builder<BE>> builderOp)
    {
        return registerItemEnergyMachine(name, beFactory, MACHINE_STANDARD_IO, MACHINE_INPUT_ONLY, builderOp);
    }

    private static <BE extends BaseTurretBlockEntity> DeferredHolder<BlockEntityType<?>, ConfigurableIOBlockEntityType<BE>> registerTurret(String name, BlockEntityType.BlockEntitySupplier<BE> beFactory, UnaryOperator<ConfigurableIOBlockEntityType.Builder<BE>> builderOp)
    {
        return registerItemEnergyMachine(name, beFactory, TURRET_ITEM_RULES, DOUBLE_MACHINE_ENERGY_RULES, builderOp);
    }
}