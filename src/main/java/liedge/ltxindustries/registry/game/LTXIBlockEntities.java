package liedge.ltxindustries.registry.game;

import liedge.limacore.blockentity.*;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.*;
import liedge.ltxindustries.blockentity.base.IORuleSet;
import liedge.ltxindustries.blockentity.base.IORules;
import liedge.ltxindustries.blockentity.turret.ArcTurretBlockEntity;
import liedge.ltxindustries.blockentity.turret.RailgunTurretBlockEntity;
import liedge.ltxindustries.blockentity.turret.RocketTurretBlockEntity;
import liedge.ltxindustries.blockentity.turret.TurretBlockEntity;
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
                VOLTAIC_INJECTOR,
                FABRICATOR,
                AUTO_FABRICATOR,
                PORTABLE_GENERATOR,
                REPAIR_STATION,
                ARC_TURRET,
                ROCKET_TURRET,
                RAILGUN_TURRET));

        // Machine capability registration (item, energy, fluid)
        registerItemEnergyFluidCaps(event, List.of(
                GRINDER,
                MATERIAL_PRESS,
                ARC_FURNACE,
                HYDROSIEVE,
                ELECTROCENTRIFUGE,
                MIXER,
                CHEM_LAB,
                ASSEMBLER,
                GEO_SYNTHESIZER,
                ATMOSPHERIC_SCRUBBER,
                DIGITAL_GARDEN));

        // Energy only
        registerEnergyCap(event, SOLAR_PANEL);

        // Fluids only
        registerFluidCap(event, PORTABLE_TANK);
        registerFluidCap(event, INFINITE_WATER_TANK);
        registerFluidCap(event, INFINITE_LAVA_TANK);

        // Mesh BE (special for Jade)
        event.registerBlockEntity(Capabilities.Item.BLOCK, MESH_BLOCK.get(), MeshBlockEntity::getPrimaryItems);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, MESH_BLOCK.get(), MeshBlockEntity::getPrimaryEnergy);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, MESH_BLOCK.get(), MeshBlockEntity::getPrimaryFluids);
    }

    private static <T extends LimaBlockEntity & ItemHolderBlockEntity & EnergyHolderBlockEntity> void registerItemEnergyCaps(RegisterCapabilitiesEvent event, Collection<? extends Supplier<? extends BlockEntityType<? extends T>>> types)
    {
        for (Supplier<? extends BlockEntityType<? extends T>> holder : types)
        {
            registerItemCap(event, holder);
            registerEnergyCap(event, holder);
        }
    }

    private static <T extends LimaBlockEntity & ItemHolderBlockEntity & EnergyHolderBlockEntity & FluidHolderBlockEntity> void registerItemEnergyFluidCaps(RegisterCapabilitiesEvent event, Collection<? extends Supplier<? extends BlockEntityType<? extends T>>> types)
    {
        for (Supplier<? extends BlockEntityType<? extends T>> holder : types)
        {
            registerItemCap(event, holder);
            registerEnergyCap(event, holder);
            registerFluidCap(event, holder);
        }
    }

    private static void registerItemCap(RegisterCapabilitiesEvent event, Supplier<? extends BlockEntityType<? extends ItemHolderBlockEntity>> holder)
    {
        event.registerBlockEntity(Capabilities.Item.BLOCK, holder.get(), ItemHolderBlockEntity::createExternalItems);
    }

    private static void registerEnergyCap(RegisterCapabilitiesEvent event, Supplier<? extends BlockEntityType<? extends EnergyHolderBlockEntity>> holder)
    {
        event.registerBlockEntity(Capabilities.Energy.BLOCK, holder.get(), EnergyHolderBlockEntity::createExternalEnergy);
    }

    private static void registerFluidCap(RegisterCapabilitiesEvent event, Supplier<? extends BlockEntityType<? extends FluidHolderBlockEntity>> holder)
    {
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, holder.get(), FluidHolderBlockEntity::createExternalFluids);
    }

    //#region Sided Access Rules
    private static final IORules STANDARD_PUSH_PULL = IORules.builder().permits(IOAccessSets.ALL_ALLOWED).withDefaultIOAccess(IOAccess.INPUT_ONLY).allowsAutoInput().allowsAutoOutput().build();
    private static final IORules STANDARD_PUSH_ONLY = IORules.builder().permits(IOAccessSets.ALL_ALLOWED).withDefaultIOAccess(IOAccess.INPUT_ONLY).allowsAutoOutput().build();
    private static final IORules INPUT_ONLY_NO_PULL = IORules.builder().permits(IOAccessSets.INPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).build();
    private static final IORules INPUT_ONLY_PULL = IORules.builder().permits(IOAccessSets.INPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).allowsAutoInput().build();
    private static final IORules OUTPUT_ONLY_PUSH = IORules.builder().permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.OUTPUT_ONLY).allowsAutoOutput().build();
    private static final IORules OUTPUT_ONLY_AUTO_PUSH = IORules.builder().permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.OUTPUT_ONLY).autoOutputByDefault().build();
    private static final IORules STORAGE_RULES = IORules.builder().permits(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).allowsAutoInput().allowsAutoOutput().build();

    private static final Set<RelativeHorizontalSide> FABRICATOR_VALID_SIDES = EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT);
    private static final IORules FABRICATOR_ITEM_RULES = IORules.builder()
            .forSides(FABRICATOR_VALID_SIDES)
            .permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.OUTPUT_ONLY)
            .allowsAutoOutput().build();
    private static final IORules FABRICATOR_ENERGY_RULES = IORules.builder().forSides(FABRICATOR_VALID_SIDES).permits(IOAccessSets.INPUT_ONLY_OR_DISABLED).withDefaultIOAccess(IOAccess.INPUT_ONLY).build();

    private static final Set<RelativeHorizontalSide> DOUBLE_BLOCK_VALID_SIDES = EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT, RelativeHorizontalSide.RIGHT);
    private static final IORules DOUBLE_BLOCK_STANDARD_PUSH_ONLY = IORules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.ALL_ALLOWED)
            .withDefaultIOAccess(IOAccess.INPUT_ONLY)
            .allowsAutoOutput().build();
    private static final IORules DOUBLE_BLOCK_INPUT_NO_PULL = IORules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.INPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.INPUT_ONLY).build();
    private static final IORules DOUBLE_BLOCK_INPUT_PULL = IORules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.INPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.INPUT_ONLY)
            .allowsAutoInput().build();
    private static final IORules TURRET_ITEM_RULES = IORules.builder()
            .forSides(DOUBLE_BLOCK_VALID_SIDES)
            .permits(IOAccessSets.OUTPUT_ONLY_OR_DISABLED)
            .withDefaultIOAccess(IOAccess.OUTPUT_ONLY)
            .allowsAutoOutput().build();
    //#endregion

    //#region Registrations
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<UpgradeStationBlockEntity>> UPGRADE_STATION = register(LTXIIdentifiers.ID_UPGRADE_STATION, UpgradeStationBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.UPGRADE_STATION).hasMenu(LTXIMenus.UPGRADE_STATION));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<EnergyCellArrayBlockEntity>> ENERGY_CELL_ARRAY = registerItemEnergyMachine(LTXIIdentifiers.ID_ENERGY_CELL_ARRAY, EnergyCellArrayBlockEntity::new, STANDARD_PUSH_ONLY, STORAGE_RULES, builder -> builder
            .withBlock(LTXIBlocks.ENERGY_CELL_ARRAY)
            .hasMenu(LTXIMenus.ENERGY_CELL_ARRAY));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<InfiniteECABlockEntity>> INFINITE_ENERGY_CELL_ARRAY = registerItemEnergyMachine(LTXIIdentifiers.ID_INFINITE_ENERGY_CELL_ARRAY, InfiniteECABlockEntity::new, STANDARD_PUSH_ONLY, OUTPUT_ONLY_AUTO_PUSH, builder -> builder
            .withBlock(LTXIBlocks.INFINITE_ENERGY_CELL_ARRAY)
            .hasMenu(LTXIMenus.ENERGY_CELL_ARRAY));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<PortableTankBlockEntity>> PORTABLE_TANK = registerSided(LTXIIdentifiers.ID_PORTABLE_TANK, PortableTankBlockEntity::new,
            rules -> rules.fluids(STORAGE_RULES),
            builder -> builder.withBlock(LTXIBlocks.PORTABLE_TANK).hasMenu(LTXIMenus.PORTABLE_TANK));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<SpecialInfiniteTankBlockEntity>> INFINITE_WATER_TANK = registerSided(LTXIIdentifiers.ID_INFINITE_WATER_TANK, SpecialInfiniteTankBlockEntity::createWaterTank,
            rules -> rules.fluids(OUTPUT_ONLY_AUTO_PUSH),
            builder -> builder.withBlock(LTXIBlocks.INFINITE_WATER_TANK).hasMenu(LTXIMenus.SPECIAL_INFINITE_TANK));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<SpecialInfiniteTankBlockEntity>> INFINITE_LAVA_TANK = registerSided(LTXIIdentifiers.ID_INFINITE_LAVA_TANK, SpecialInfiniteTankBlockEntity::createLavaTank,
            rules -> rules.fluids(OUTPUT_ONLY_AUTO_PUSH),
            builder -> builder.withBlock(LTXIBlocks.INFINITE_LAVA_TANK).hasMenu(LTXIMenus.SPECIAL_INFINITE_TANK));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<DigitalFurnaceBlockEntity>> DIGITAL_FURNACE = registerItemEnergyMachine(LTXIIdentifiers.ID_DIGITAL_FURNACE, DigitalFurnaceBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, builder -> builder.withBlock(LTXIBlocks.DIGITAL_FURNACE).hasMenu(LTXIMenus.DIGITAL_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<DigitalSmokerBlockEntity>> DIGITAL_SMOKER = registerItemEnergyMachine(LTXIIdentifiers.ID_DIGITAL_SMOKER, DigitalSmokerBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, builder -> builder.withBlock(LTXIBlocks.DIGITAL_SMOKER).hasMenu(LTXIMenus.DIGITAL_SMOKER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<DigitalBlastFurnaceBlockEntity>> DIGITAL_BLAST_FURNACE = registerItemEnergyMachine(LTXIIdentifiers.ID_DIGITAL_BLAST_FURNACE, DigitalBlastFurnaceBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, builder -> builder.withBlock(LTXIBlocks.DIGITAL_BLAST_FURNACE).hasMenu(LTXIMenus.DIGITAL_BLAST_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<GrinderBlockEntity>> GRINDER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_GRINDER, GrinderBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, OUTPUT_ONLY_PUSH,
            builder -> builder.withBlock(LTXIBlocks.GRINDER).hasMenu(LTXIMenus.GRINDER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MaterialPressBlockEntity>> MATERIAL_PRESS = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_MATERIAL_PRESS, MaterialPressBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, INPUT_ONLY_PULL,
            builder -> builder.withBlock(LTXIBlocks.MATERIAL_PRESS).hasMenu(LTXIMenus.MATERIAL_PRESS));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<ArcFurnaceBlockEntity>> ARC_FURNACE = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_ARC_FURNACE, ArcFurnaceBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, INPUT_ONLY_PULL,
            builder -> builder.withBlock(LTXIBlocks.ARC_FURNACE).hasMenu(LTXIMenus.ARC_FURNACE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<HydroSieveBlockEntity>> HYDROSIEVE = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_HYDROSIEVE, HydroSieveBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, INPUT_ONLY_PULL,
            builder -> builder.withBlock(LTXIBlocks.HYDROSIEVE).hasMenu(LTXIMenus.HYDROSIEVE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<ElectroCentrifugeBlockEntity>> ELECTROCENTRIFUGE = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_ELECTROCENTRIFUGE, ElectroCentrifugeBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, STANDARD_PUSH_PULL,
            builder -> builder.withBlock(LTXIBlocks.ELECTROCENTRIFUGE).hasMenu(LTXIMenus.ELECTROCENTRIFUGE));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MixerBlockEntity>> MIXER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_MIXER, MixerBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, STANDARD_PUSH_PULL,
            builder -> builder.withBlock(LTXIBlocks.MIXER).hasMenu(LTXIMenus.MIXER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<VoltaicInjectorBlockEntity>> VOLTAIC_INJECTOR = registerItemEnergyMachine(LTXIIdentifiers.ID_VOLTAIC_INJECTOR, VoltaicInjectorBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, builder -> builder.withBlock(LTXIBlocks.VOLTAIC_INJECTOR).hasMenu(LTXIMenus.VOLTAIC_INJECTOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<ChemLabBlockEntity>> CHEM_LAB = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_CHEM_LAB, ChemLabBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, STANDARD_PUSH_PULL,
            builder -> builder.withBlock(LTXIBlocks.CHEM_LAB).hasMenu(LTXIMenus.CHEM_LAB));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<AssemblerBlockEntity>> ASSEMBLER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_ASSEMBLER, AssemblerBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, INPUT_ONLY_PULL, builder -> builder.withBlock(LTXIBlocks.ASSEMBLER).hasMenu(LTXIMenus.ASSEMBLER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<GeoSynthesizerBlockEntity>> GEO_SYNTHESIZER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_GEO_SYNTHESIZER, GeoSynthesizerBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, INPUT_ONLY_PULL, builder -> builder.withBlock(LTXIBlocks.GEO_SYNTHESIZER).hasMenu(LTXIMenus.GEO_SYNTHESIZER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<FabricatorBlockEntity>> FABRICATOR = registerItemEnergyMachine(LTXIIdentifiers.ID_FABRICATOR, FabricatorBlockEntity::new, FABRICATOR_ITEM_RULES, FABRICATOR_ENERGY_RULES, builder -> builder.withBlock(LTXIBlocks.FABRICATOR).hasMenu(LTXIMenus.FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<AutoFabricatorBlockEntity>> AUTO_FABRICATOR = registerItemEnergyMachine(LTXIIdentifiers.ID_AUTO_FABRICATOR, AutoFabricatorBlockEntity::new, STANDARD_PUSH_ONLY, INPUT_ONLY_NO_PULL, builder -> builder.withBlock(LTXIBlocks.AUTO_FABRICATOR).hasMenu(LTXIMenus.AUTO_FABRICATOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<AirScrubberBlockEntity>> ATMOSPHERIC_SCRUBBER = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_ATMOSPHERIC_SCRUBBER, AirScrubberBlockEntity::new, OUTPUT_ONLY_PUSH, INPUT_ONLY_NO_PULL, OUTPUT_ONLY_PUSH,
            builder -> builder.withBlock(LTXIBlocks.ATMOSPHERIC_SCRUBBER).hasMenu(LTXIMenus.ATMOSPHERIC_SCRUBBER));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<DigitalGardenBlockEntity>> DIGITAL_GARDEN = registerItemEnergyFluidMachine(LTXIIdentifiers.ID_DIGITAL_GARDEN, DigitalGardenBlockEntity::new, DOUBLE_BLOCK_STANDARD_PUSH_ONLY, DOUBLE_BLOCK_INPUT_NO_PULL, DOUBLE_BLOCK_INPUT_PULL, builder -> builder.withBlock(LTXIBlocks.DIGITAL_GARDEN).hasMenu(LTXIMenus.DIGITAL_GARDEN));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<PortableGeneratorBlockEntity>> PORTABLE_GENERATOR = registerItemEnergyMachine(LTXIIdentifiers.ID_PORTABLE_GENERATOR, PortableGeneratorBlockEntity::new, INPUT_ONLY_NO_PULL, OUTPUT_ONLY_AUTO_PUSH, builder -> builder
            .withBlock(LTXIBlocks.PORTABLE_GENERATOR).hasMenu(LTXIMenus.PORTABLE_GENERATOR));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL = registerSided(LTXIIdentifiers.ID_SOLAR_PANEL, SolarPanelBlockEntity::new,
            rules -> rules.energy(OUTPUT_ONLY_AUTO_PUSH),
            builder -> builder.withBlock(LTXIBlocks.SOLAR_PANEL).hasMenu(LTXIMenus.SOLAR_PANEL));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RepairStationBlockEntity>> REPAIR_STATION = registerItemEnergyMachine(LTXIIdentifiers.ID_REPAIR_STATION, RepairStationBlockEntity::new, STANDARD_PUSH_PULL, INPUT_ONLY_NO_PULL, builder -> builder.withBlock(LTXIBlocks.REPAIR_STATION).hasMenu(LTXIMenus.REPAIR_STATION));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<ArcTurretBlockEntity>> ARC_TURRET = registerTurret(LTXIIdentifiers.ID_ARC_TURRET, ArcTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.ARC_TURRET).hasMenu(LTXIMenus.ARC_TURRET));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RocketTurretBlockEntity>> ROCKET_TURRET = registerTurret(LTXIIdentifiers.ID_ROCKET_TURRET, RocketTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.ROCKET_TURRET).hasMenu(LTXIMenus.ROCKET_TURRET));
    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<RailgunTurretBlockEntity>> RAILGUN_TURRET = registerTurret(LTXIIdentifiers.ID_RAILGUN_TURRET, RailgunTurretBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.RAILGUN_TURRET).hasMenu(LTXIMenus.RAILGUN_TURRET));

    public static final DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<MeshBlockEntity>> MESH_BLOCK = register("mesh_block", MeshBlockEntity::new, builder -> builder.withBlock(LTXIBlocks.MESH_BLOCK));
    //#endregion

    // Helpers
    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<BE>> register(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<LimaBlockEntityType.Builder<BE>> op)
    {
        return TYPES.register(name, id -> op.apply(LimaBlockEntityType.builder(id, factory)).build());
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<BE>> registerSided(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<IORuleSet.Builder> rulesOp, UnaryOperator<LimaBlockEntityType.Builder<BE>> op)
    {
        return register(name, factory, builder -> op.apply(builder).component(LTXIDataComponents.IO_RULES, rulesOp.apply(IORuleSet.builder()).build()));
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<BE>> registerItemEnergyMachine(String name, BlockEntityType.BlockEntitySupplier<BE> factory, IORules itemRules, IORules energyRules, UnaryOperator<LimaBlockEntityType.Builder<BE>> op)
    {
        return registerSided(name, factory, rules -> rules.items(itemRules).energy(energyRules), op);
    }

    private static <BE extends LimaBlockEntity> DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<BE>> registerItemEnergyFluidMachine(String name, BlockEntityType.BlockEntitySupplier<BE> factory, IORules itemRules, IORules energyRules, IORules fluidRules, UnaryOperator<LimaBlockEntityType.Builder<BE>> op)
    {
        return registerSided(name, factory, rules -> rules.items(itemRules).energy(energyRules).fluids(fluidRules), op);
    }

    private static <BE extends TurretBlockEntity> DeferredHolder<BlockEntityType<?>, LimaBlockEntityType<BE>> registerTurret(String name, BlockEntityType.BlockEntitySupplier<BE> factory, UnaryOperator<LimaBlockEntityType.Builder<BE>> op)
    {
        return registerItemEnergyMachine(name, factory, TURRET_ITEM_RULES, DOUBLE_BLOCK_INPUT_NO_PULL, op);
    }
}