package liedge.ltxindustries.registry.game;

import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.*;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.of;

public final class LTXIBlocks
{
    private LTXIBlocks() {}

    private static final DeferredRegister.Blocks BLOCKS = LTXIndustries.RESOURCES.deferredBlocks();

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
    }

    // State Predicates
    private static final BlockBehaviour.StatePredicate NEVER = (state, level, pos) -> false;

    // Ores
    public static final DeferredBlock<DropExperienceBlock> TITANIUM_ORE = BLOCKS.registerBlock("titanium_ore", properties -> new DropExperienceBlock(ConstantInt.of(0), properties), of().mapColor(MapColor.STONE).strength(3f).requiresCorrectToolForDrops());
    public static final DeferredBlock<DropExperienceBlock> DEEPSLATE_TITANIUM_ORE = BLOCKS.registerBlock("deepslate_titanium_ore", properties -> new DropExperienceBlock(ConstantInt.of(0), properties), of().mapColor(MapColor.DEEPSLATE).strength(4.5f, 3f).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());
    public static final DeferredBlock<DropExperienceBlock> NIOBIUM_ORE = BLOCKS.registerBlock("niobium_ore", properties -> new DropExperienceBlock(UniformInt.of(1, 4), properties), of().mapColor(MapColor.SAND).strength(3.2f, 9f).requiresCorrectToolForDrops());

    // Raw ore blocks
    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = BLOCKS.registerSimpleBlock("raw_titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> RAW_NIOBIUM_BLOCK = BLOCKS.registerSimpleBlock("raw_niobium_block", of().mapColor(MapColor.COLOR_PURPLE).strength(5f, 9f).requiresCorrectToolForDrops());
    public static final DeferredBlock<SurfaceStickingBlock> RAW_TITANIUM_CLUSTER = BLOCKS.registerBlock("raw_titanium_cluster", properties -> new SurfaceStickingBlock(properties, LTXIBlockShapes.RAW_ORE_CLUSTER, false), of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).requiresCorrectToolForDrops().noOcclusion());
    public static final DeferredBlock<SurfaceStickingBlock> RAW_NIOBIUM_CLUSTER = BLOCKS.registerBlock("raw_niobium_cluster", properties -> new SurfaceStickingBlock(properties, LTXIBlockShapes.RAW_ORE_CLUSTER, false), of().mapColor(MapColor.COLOR_PURPLE).strength(5f, 9f).requiresCorrectToolForDrops().noOcclusion());

    // Ingot storage blocks
    public static final DeferredBlock<Block> TITANIUM_BLOCK = BLOCKS.registerSimpleBlock("titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> NIOBIUM_BLOCK = BLOCKS.registerSimpleBlock("niobium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(6f, 9f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLATESTEEL_BLOCK = BLOCKS.registerSimpleBlock("slatesteel_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 12f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    // Building blocks
    public static final Map<NeonLightColor, DeferredBlock<Block>> NEON_LIGHTS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(NeonLightColor.class, color -> BLOCKS.registerSimpleBlock(color.toString() + "_neon_light", neonLightProperties().mapColor(color.getMapColor())));
    public static final DeferredBlock<Block> TITANIUM_PANEL = BLOCKS.registerSimpleBlock("titanium_panel", of().mapColor(DyeColor.WHITE).strength(3.5f, 36f).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_TITANIUM_PANEL = BLOCKS.registerSimpleBlock("smooth_titanium_panel", of().mapColor(DyeColor.WHITE).strength(3.5f, 36f).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> TILED_TITANIUM_PANEL = BLOCKS.registerSimpleBlock("tiled_titanium_panel", of().mapColor(DyeColor.WHITE).strength(3.5f, 36f).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlock<TransparentBlock> TITANIUM_GLASS = BLOCKS.registerBlock("titanium_glass", TransparentBlock::new, quartzGlassProperties());
    public static final DeferredBlock<Block> SLATESTEEL_PANEL = BLOCKS.registerSimpleBlock("slatesteel_panel", of().mapColor(DyeColor.GRAY).strength(3.5f, 36f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_SLATESTEEL_PANEL = BLOCKS.registerSimpleBlock("smooth_slatesteel_panel", of().mapColor(DyeColor.GRAY).strength(3.5f, 36f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> TILED_SLATESTEEL_PANEL = BLOCKS.registerSimpleBlock("tiled_slatesteel_panel", of().mapColor(DyeColor.GRAY).strength(3.5f, 36f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    // Plants
    public static final DeferredBlock<SparkFruitBlock> SPARK_FRUIT = BLOCKS.registerBlock("spark_fruit", SparkFruitBlock::new, of().mapColor(MapColor.PLANT).randomTicks().strength(0.2f, 3.0f).sound(SoundType.WET_GRASS).noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel(state -> state.getValue(BlockStateProperties.AGE_2)== 2 ? 7 : 0));
    public static final DeferredBlock<BerryVinesBlock> BILEVINE = BLOCKS.registerBlock("bilevine", BerryVinesBlock::new, berryVinesProperties());
    public static final DeferredBlock<BerryVinesPlantBlock> BILEVINE_PLANT = BLOCKS.registerBlock("bilevine_plant", BerryVinesPlantBlock::new, berryVinesProperties());
    public static final DeferredBlock<GloomShroomBlock> GLOOM_SHROOM = BLOCKS.registerBlock("gloom_shroom", GloomShroomBlock::new, of().mapColor(MapColor.COLOR_BLUE).instabreak().noCollission().sound(SoundType.FUNGUS).pushReaction(PushReaction.DESTROY));

    // Helper stations
    public static final DeferredBlock<EquipmentUpgradeStationBlock> EQUIPMENT_UPGRADE_STATION = BLOCKS.registerBlock(LTXIIdentifiers.ID_EQUIPMENT_UPGRADE_STATION, EquipmentUpgradeStationBlock::new, machineProperties());

    // Machinery
    public static final DeferredBlock<EnergyCellArrayBlock> ENERGY_CELL_ARRAY = BLOCKS.registerBlock(LTXIIdentifiers.ID_ENERGY_CELL_ARRAY, EnergyCellArrayBlock::new, machineProperties());
    public static final DeferredBlock<EnergyCellArrayBlock> INFINITE_ENERGY_CELL_ARRAY = BLOCKS.registerBlock(LTXIIdentifiers.ID_INFINITE_ENERGY_CELL_ARRAY, EnergyCellArrayBlock::new, machineProperties());
    public static final DeferredBlock<StateMachineBlock> DIGITAL_FURNACE = BLOCKS.registerBlock(LTXIIdentifiers.ID_DIGITAL_FURNACE, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.COOKING_MACHINE, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> DIGITAL_SMOKER = BLOCKS.registerBlock(LTXIIdentifiers.ID_DIGITAL_SMOKER, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.COOKING_MACHINE, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> DIGITAL_BLAST_FURNACE = BLOCKS.registerBlock(LTXIIdentifiers.ID_DIGITAL_BLAST_FURNACE, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.COOKING_MACHINE, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> GRINDER = BLOCKS.registerBlock(LTXIIdentifiers.ID_GRINDER, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.COOKING_MACHINE, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> MATERIAL_FUSING_CHAMBER = BLOCKS.registerBlock(LTXIIdentifiers.ID_MATERIAL_FUSING_CHAMBER, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.COOKING_MACHINE, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> ELECTROCENTRIFUGE = BLOCKS.registerBlock(LTXIIdentifiers.ID_ELECTROCENTRIFUGE, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.ELECTROCENTRIFUGE, true), machineProperties());
    public static final DeferredBlock<StateMachineBlock> MIXER = BLOCKS.registerBlock(LTXIIdentifiers.ID_MIXER, properties -> StateMachineBlock.rotatingShape(properties, LTXIBlockShapes.MIXER, true), machineProperties());
    public static final DeferredBlock<StateMachineBlock> VOLTAIC_INJECTOR = BLOCKS.registerBlock(LTXIIdentifiers.ID_VOLTAIC_INJECTOR, properties -> StateMachineBlock.rotatingShape(properties, LTXIBlockShapes.VOLTAIC_INJECTOR, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> CHEM_LAB = BLOCKS.registerBlock(LTXIIdentifiers.ID_CHEM_LAB, properties -> StateMachineBlock.rotatingShape(properties, LTXIBlockShapes.CHEM_LAB, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> ASSEMBLER = BLOCKS.registerBlock(LTXIIdentifiers.ID_ASSEMBLER, properties -> StateMachineBlock.staticShape(properties, LTXIBlockShapes.ASSEMBLER, false), machineProperties());
    public static final DeferredBlock<StateMachineBlock> GEO_SYNTHESIZER = BLOCKS.registerBlock(LTXIIdentifiers.ID_GEO_SYNTHESIZER, properties -> StateMachineBlock.rotatingShape(properties, LTXIBlockShapes.GEO_SYNTHESIZER, false), machineProperties());
    public static final DeferredBlock<PrimaryMeshBlock> FABRICATOR = BLOCKS.registerBlock(LTXIIdentifiers.ID_FABRICATOR, properties -> PrimaryMeshBlock.create(properties, LTXIBlockMeshes.WIDE_STATION, LTXIBlockShapes.FABRICATOR, true), machineProperties());
    public static final DeferredBlock<FabricatorBlock> AUTO_FABRICATOR = BLOCKS.registerBlock(LTXIIdentifiers.ID_AUTO_FABRICATOR, FabricatorBlock::new, machineProperties());
    public static final DeferredBlock<PrimaryMeshBlock> MOLECULAR_RECONSTRUCTOR = BLOCKS.registerBlock(LTXIIdentifiers.ID_MOLECULAR_RECONSTRUCTOR, properties -> PrimaryMeshBlock.create(properties, LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.MOLECULAR_RECONSTRUCTOR, false), machineProperties());
    public static final DeferredBlock<PrimaryMeshBlock> DIGITAL_GARDEN = BLOCKS.registerBlock(LTXIIdentifiers.ID_DIGITAL_GARDEN, properties -> PrimaryMeshBlock.create(properties, LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.DIGITAL_GARDEN, false), machineProperties());
    public static final DeferredBlock<PrimaryMeshBlock> ROCKET_TURRET = BLOCKS.registerBlock(LTXIIdentifiers.ID_ROCKET_TURRET, properties -> PrimaryMeshBlock.create(properties, LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.GENERAL_TURRET, true), machineProperties());
    public static final DeferredBlock<PrimaryMeshBlock> RAILGUN_TURRET = BLOCKS.registerBlock(LTXIIdentifiers.ID_RAILGUN_TURRET, properties -> PrimaryMeshBlock.create(properties, LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.GENERAL_TURRET, true), machineProperties());

    // Fluid blocks
    public static final DeferredBlock<LiquidBlock> VIRIDIC_ACID_BLOCK = BLOCKS.register("viridic_acid", () -> new LiquidBlock(LTXIFluids.VIRIDIC_ACID.get(), of()
            .mapColor(MapColor.PLANT)
            .replaceable()
            .noCollission()
            .strength(100f)
            .pushReaction(PushReaction.DESTROY)
            .noLootTable()
            .liquid()
            .lightLevel(state -> LTXIFluids.VIRIDIC_ACID_LIGHT)
            .sound(SoundType.EMPTY)));

    // Technical blocks
    public static final DeferredBlock<SurfaceStickingBlock> GLOWSTICK = BLOCKS.registerBlock("glowstick", properties -> new SurfaceStickingBlock(properties, LTXIBlockShapes.GLOWSTICK, true), of().noCollission().instabreak().pushReaction(PushReaction.DESTROY).lightLevel(state -> 15).noLootTable());
    public static final DeferredBlock<MeshBlock> MESH_BLOCK = BLOCKS.registerBlock("mesh_block", MeshBlock::new, machineProperties().dynamicShape().noOcclusion().noLootTable());

    // Helpers & initializers
    private static BlockBehaviour.Properties neonLightProperties()
    {
        return of()
                .sound(SoundType.GLASS)
                .strength(2f)
                .lightLevel(state -> 15);
    }

    private static BlockBehaviour.Properties machineProperties()
    {
        return of()
                .mapColor(MapColor.SNOW)
                .pushReaction(PushReaction.BLOCK)
                .strength(6f, 32f)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    private static BlockBehaviour.Properties berryVinesProperties()
    {
        return of()
                .mapColor(MapColor.WARPED_NYLIUM)
                .randomTicks()
                .noCollission()
                .lightLevel(CaveVines.emission(14))
                .instabreak()
                .sound(SoundType.CAVE_VINES)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties quartzGlassProperties()
    {
        return of().instrument(NoteBlockInstrument.HAT)
                .noOcclusion()
                .sound(SoundType.GLASS)
                .strength(1f, 1200f)
                .isValidSpawn(Blocks::never)
                .isRedstoneConductor(NEVER)
                .isSuffocating(NEVER)
                .isViewBlocking(NEVER)
                .requiresCorrectToolForDrops();
    }
}