package liedge.ltxindustries.registry.game;

import liedge.limacore.registry.DeferredBlockWithItem;
import liedge.limacore.registry.LimaDeferredBlocksWithItems;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.LTXICommonIds;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.*;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import liedge.ltxindustries.item.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.of;

public final class LTXIBlocks
{
    private LTXIBlocks() {}

    private static final LimaDeferredBlocksWithItems BLOCKS = LTXIndustries.RESOURCES.deferredBlocksWithItems();

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
        bus.addListener(RegisterCapabilitiesEvent.class, LTXIBlocks::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        // Energy container block items
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, $) -> EnergyHolderItem.createEnergyAccess(stack), ENERGY_CELL_ARRAY, INFINITE_ENERGY_CELL_ARRAY);
    }

    static Collection<DeferredHolder<Block, ? extends Block>> getRegisteredBlocks()
    {
        return BLOCKS.getEntriesWithItemsOnly();
    }

    // State Predicates
    private static final BlockBehaviour.StatePredicate NEVER = (state, level, pos) -> false;

    // Ores
    public static final DeferredBlockWithItem<DropExperienceBlock, BlockItem> TITANIUM_ORE = BLOCKS.registerBlockAndSimpleItem("titanium_ore", () -> new DropExperienceBlock(ConstantInt.of(0), of().mapColor(MapColor.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlockWithItem<DropExperienceBlock, BlockItem> DEEPSLATE_TITANIUM_ORE = BLOCKS.registerBlockAndSimpleItem("deepslate_titanium_ore", () -> new DropExperienceBlock(ConstantInt.of(0), of().mapColor(MapColor.DEEPSLATE).strength(4.5f, 3f).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops()));
    public static final DeferredBlockWithItem<DropExperienceBlock, BlockItem> NIOBIUM_ORE = BLOCKS.registerBlockAndSimpleItem("niobium_ore", () -> new DropExperienceBlock(UniformInt.of(1, 4), of().mapColor(MapColor.SAND).strength(3.2f, 9f).requiresCorrectToolForDrops()));

    // Raw ore blocks
    public static final DeferredBlockWithItem<Block, BlockItem> RAW_TITANIUM_BLOCK = BLOCKS.registerSimpleBlockAndItem("raw_titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> RAW_NIOBIUM_BLOCK = BLOCKS.registerSimpleBlockAndItem("raw_niobium_block", of().mapColor(MapColor.COLOR_PURPLE).strength(5f, 9f).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<SurfaceStickingBlock, BlockItem> RAW_TITANIUM_CLUSTER = BLOCKS.registerBlockAndSimpleItem("raw_titanium_cluster", () -> new SurfaceStickingBlock(of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).requiresCorrectToolForDrops().noOcclusion(), LTXIBlockShapes.RAW_ORE_CLUSTER, false));
    public static final DeferredBlockWithItem<SurfaceStickingBlock, BlockItem> RAW_NIOBIUM_CLUSTER = BLOCKS.registerBlockAndSimpleItem("raw_niobium_cluster", () -> new SurfaceStickingBlock(of().mapColor(MapColor.COLOR_PURPLE).strength(5f, 9f).requiresCorrectToolForDrops().noOcclusion(), LTXIBlockShapes.RAW_ORE_CLUSTER, false));

    // Ingot storage blocks
    public static final DeferredBlockWithItem<Block, BlockItem> TITANIUM_BLOCK = BLOCKS.registerSimpleBlockAndItem("titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> NIOBIUM_BLOCK = BLOCKS.registerSimpleBlockAndItem("niobium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(6f, 9f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> SLATESTEEL_BLOCK = BLOCKS.registerSimpleBlockAndItem("slatesteel_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 12f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    // Building blocks
    public static final Map<NeonLightColor, DeferredBlock<Block>> NEON_LIGHTS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(NeonLightColor.class, color -> BLOCKS.registerSimpleBlockAndItem(color.toString() + "_neon_light", neonLightProperties().mapColor(color.getMapColor())));
    public static final DeferredBlockWithItem<Block, BlockItem> TITANIUM_PANEL = BLOCKS.registerSimpleBlockAndItem("titanium_panel", of().mapColor(DyeColor.WHITE).strength(3.5f, 36f).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> SMOOTH_TITANIUM_PANEL = BLOCKS.registerSimpleBlockAndItem("smooth_titanium_panel", of().mapColor(DyeColor.WHITE).strength(3.5f, 36f).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> TILED_TITANIUM_PANEL = BLOCKS.registerSimpleBlockAndItem("tiled_titanium_panel", of().mapColor(DyeColor.WHITE).strength(3.5f, 36f).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> TITANIUM_GLASS = BLOCKS.registerBlockAndSimpleItem("titanium_glass", () -> new TransparentBlock(quartzGlassProperties()), new Item.Properties());
    public static final DeferredBlockWithItem<Block, BlockItem> SLATESTEEL_PANEL = BLOCKS.registerSimpleBlockAndItem("slatesteel_panel", of().mapColor(DyeColor.GRAY).strength(3.5f, 36f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> SMOOTH_SLATESTEEL_PANEL = BLOCKS.registerSimpleBlockAndItem("smooth_slatesteel_panel", of().mapColor(DyeColor.GRAY).strength(3.5f, 36f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> TILED_SLATESTEEL_PANEL = BLOCKS.registerSimpleBlockAndItem("tiled_slatesteel_panel", of().mapColor(DyeColor.GRAY).strength(3.5f, 36f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    // Plants
    public static final DeferredBlock<SparkFruitBlock> SPARK_FRUIT = BLOCKS.registerBlock("spark_fruit", SparkFruitBlock::new, of().mapColor(MapColor.PLANT).randomTicks().strength(0.2f, 3.0f).sound(SoundType.WET_GRASS).noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel(state -> state.getValue(BlockStateProperties.AGE_2)== 2 ? 7 : 0));
    public static final DeferredBlock<BerryVinesBlock> BILEVINE = BLOCKS.registerBlock("bilevine", BerryVinesBlock::new, berryVinesProperties());
    public static final DeferredBlock<BerryVinesPlantBlock> BILEVINE_PLANT = BLOCKS.registerBlock("bilevine_plant", BerryVinesPlantBlock::new, berryVinesProperties());
    public static final DeferredBlock<GloomShroomBlock> GLOOM_SHROOM = BLOCKS.registerBlock("gloom_shroom", GloomShroomBlock::new, of().mapColor(MapColor.COLOR_BLUE).instabreak().noCollission().sound(SoundType.FUNGUS).pushReaction(PushReaction.DESTROY));

    // Helper stations
    public static final DeferredBlockWithItem<EquipmentUpgradeStationBlock, BlockItem> EQUIPMENT_UPGRADE_STATION = BLOCKS.registerBlockAndSimpleItem(LTXICommonIds.ID_EQUIPMENT_UPGRADE_STATION, () -> new EquipmentUpgradeStationBlock(machineProperties().noOcclusion()), new Item.Properties().stacksTo(1));

    // Machinery
    public static final DeferredBlockWithItem<EnergyCellArrayBlock, ECABlockItem> ENERGY_CELL_ARRAY = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_ENERGY_CELL_ARRAY, () -> new EnergyCellArrayBlock(machineProperties().noOcclusion()), block -> new ECABlockItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<EnergyCellArrayBlock, InfiniteECABlockItem> INFINITE_ENERGY_CELL_ARRAY = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_INFINITE_ENERGY_CELL_ARRAY, () -> new EnergyCellArrayBlock(machineProperties().noOcclusion()), block -> new InfiniteECABlockItem(block, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> DIGITAL_FURNACE = registerWithEnergyTooltip(LTXICommonIds.ID_DIGITAL_FURNACE, () -> StateMachineBlock.staticShape(machineProperties(), LTXIBlockShapes.COOKING_MACHINE, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> DIGITAL_SMOKER = registerWithEnergyTooltip(LTXICommonIds.ID_DIGITAL_SMOKER, () -> StateMachineBlock.staticShape(machineProperties(), LTXIBlockShapes.COOKING_MACHINE, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> DIGITAL_BLAST_FURNACE = registerWithEnergyTooltip(LTXICommonIds.ID_DIGITAL_BLAST_FURNACE, () -> StateMachineBlock.staticShape(machineProperties(), LTXIBlockShapes.COOKING_MACHINE, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> GRINDER = registerWithEnergyTooltip(LTXICommonIds.ID_GRINDER, () -> StateMachineBlock.staticShape(machineProperties(), LTXIBlockShapes.COOKING_MACHINE, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> MATERIAL_FUSING_CHAMBER = registerWithEnergyTooltip(LTXICommonIds.ID_MATERIAL_FUSING_CHAMBER, () -> StateMachineBlock.staticShape(machineProperties(), LTXIBlockShapes.COOKING_MACHINE, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> ELECTROCENTRIFUGE = registerWithEnergyTooltip(LTXICommonIds.ID_ELECTROCENTRIFUGE, () -> StateMachineBlock.staticShape(machineProperties().noOcclusion(), LTXIBlockShapes.ELECTROCENTRIFUGE, true));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> MIXER = registerWithEnergyTooltip(LTXICommonIds.ID_MIXER, () -> StateMachineBlock.rotatingShape(machineProperties().noOcclusion(), LTXIBlockShapes.MIXER, true));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> VOLTAIC_INJECTOR = registerWithEnergyTooltip(LTXICommonIds.ID_VOLTAIC_INJECTOR, () -> StateMachineBlock.rotatingShape(machineProperties().noOcclusion(), LTXIBlockShapes.VOLTAIC_INJECTOR, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> CHEM_LAB = registerWithEnergyTooltip(LTXICommonIds.ID_CHEM_LAB, () -> StateMachineBlock.rotatingShape(machineProperties().noOcclusion(), LTXIBlockShapes.CHEM_LAB, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> ASSEMBLER = registerWithEnergyTooltip(LTXICommonIds.ID_ASSEMBLER, () -> StateMachineBlock.staticShape(machineProperties().noOcclusion(), LTXIBlockShapes.ASSEMBLER, false));
    public static final DeferredBlockWithItem<StateMachineBlock, BlockItem> GEO_SYNTHESIZER = registerWithEnergyTooltip(LTXICommonIds.ID_GEO_SYNTHESIZER, () -> StateMachineBlock.rotatingShape(machineProperties().noOcclusion(), LTXIBlockShapes.GEO_SYNTHESIZER, false));
    public static final DeferredBlockWithItem<PrimaryMeshBlock, BlockItem> FABRICATOR = registerWithEnergyTooltip(LTXICommonIds.ID_FABRICATOR, () -> PrimaryMeshBlock.create(machineProperties().noOcclusion(), LTXIBlockMeshes.WIDE_STATION, LTXIBlockShapes.FABRICATOR, true));
    public static final DeferredBlockWithItem<FabricatorBlock, BlockItem> AUTO_FABRICATOR = registerWithEnergyTooltip(LTXICommonIds.ID_AUTO_FABRICATOR, () -> new FabricatorBlock(machineProperties().noOcclusion()));
    public static final DeferredBlockWithItem<PrimaryMeshBlock, BlockItem> MOLECULAR_RECONSTRUCTOR = registerWithEnergyTooltip(LTXICommonIds.ID_MOLECULAR_RECONSTRUCTOR, () -> PrimaryMeshBlock.create(machineProperties().noOcclusion(), LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.MOLECULAR_RECONSTRUCTOR, false));
    public static final DeferredBlockWithItem<PrimaryMeshBlock, BlockItem> DIGITAL_GARDEN = registerWithEnergyTooltip(LTXICommonIds.ID_DIGITAL_GARDEN, () -> PrimaryMeshBlock.create(machineProperties().noCollission(), LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.DIGITAL_GARDEN, false));
    public static final DeferredBlockWithItem<PrimaryMeshBlock, BlockItem> ROCKET_TURRET = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_ROCKET_TURRET, () -> PrimaryMeshBlock.create(machineProperties().noOcclusion(), LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.GENERAL_TURRET, true), block -> ContentsTooltipBlockItem.energyOwnerTooltipItem(block, new Item.Properties().stacksTo(1).rarity(LTXIItemRarities.ltxGearRarity())));
    public static final DeferredBlockWithItem<PrimaryMeshBlock, BlockItem> RAILGUN_TURRET = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_RAILGUN_TURRET, () -> PrimaryMeshBlock.create(machineProperties().noOcclusion(), LTXIBlockMeshes.DOUBLE_VERTICAL, LTXIBlockShapes.GENERAL_TURRET, true), block -> ContentsTooltipBlockItem.energyOwnerTooltipItem(block, new Item.Properties().stacksTo(1).rarity(LTXIItemRarities.ltxGearRarity())));

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
                .requiresCorrectToolForDrops();
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

    private static <T extends Block> DeferredBlockWithItem<T, BlockItem> registerWithEnergyTooltip(String name, Supplier<T> supplier)
    {
        return BLOCKS.registerBlockAndItem(name, supplier, block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    }
}