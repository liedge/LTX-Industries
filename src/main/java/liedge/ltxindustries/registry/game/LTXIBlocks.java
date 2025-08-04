package liedge.ltxindustries.registry.game;

import liedge.limacore.registry.DeferredBlockWithItem;
import liedge.limacore.registry.LimaDeferredBlocksWithItems;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.LTXICommonIds;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.*;
import liedge.ltxindustries.item.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, $) -> EnergyHolderItem.createEnergyAccess(stack), ENERGY_STORAGE_ARRAY, INFINITE_ENERGY_STORAGE_ARRAY);
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

    // Ingot storage blocks
    public static final DeferredBlockWithItem<Block, BlockItem> TITANIUM_BLOCK = BLOCKS.registerSimpleBlockAndItem("titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> NIOBIUM_BLOCK = BLOCKS.registerSimpleBlockAndItem("niobium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(6f, 9f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlockWithItem<Block, BlockItem> SLATE_ALLOY_BLOCK = BLOCKS.registerSimpleBlockAndItem("slate_alloy_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 12f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    public static final Map<DyeColor, DeferredBlock<Block>> STANDARD_NEON_LIGHTS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(DyeColor.class, color -> BLOCKS.registerSimpleBlockAndItem(color.getSerializedName() + "_neon_light", neonLightProperties().mapColor(color)));
    public static final DeferredBlockWithItem<Block, BlockItem> LTX_LIME_NEON_LIGHT = BLOCKS.registerSimpleBlockAndItem("ltx_lime_neon_light", neonLightProperties().mapColor(DyeColor.LIME));
    public static final DeferredBlockWithItem<Block, BlockItem> ENERGY_BLUE_NEON_LIGHT = BLOCKS.registerSimpleBlockAndItem("energy_blue_neon_light", neonLightProperties().mapColor(MapColor.ICE));
    public static final DeferredBlockWithItem<Block, BlockItem> ELECTRIC_CHARTREUSE_NEON_LIGHT = BLOCKS.registerSimpleBlockAndItem("electric_chartreuse_neon_light", neonLightProperties().mapColor(DyeColor.LIME));
    public static final DeferredBlockWithItem<Block, BlockItem> ACID_GREEN_NEON_LIGHT = BLOCKS.registerSimpleBlockAndItem("acid_green_neon_light", neonLightProperties().mapColor(MapColor.PLANT));
    public static final DeferredBlockWithItem<Block, BlockItem> NEURO_BLUE_NEON_LIGHT = BLOCKS.registerSimpleBlockAndItem("neuro_blue_neon_light", neonLightProperties().mapColor(MapColor.WATER));
    public static final DeferredBlockWithItem<Block, BlockItem> TITANIUM_GLASS = BLOCKS.registerBlockAndSimpleItem("titanium_glass", () -> new TransparentBlock(quartzGlassProperties()), new Item.Properties());
    public static final DeferredBlockWithItem<Block, BlockItem> SLATE_GLASS = BLOCKS.registerBlockAndSimpleItem("slate_glass", () -> new TransparentBlock(quartzGlassProperties()), new Item.Properties());

    // Plants
    public static final DeferredBlock<BerryVinesBlock> BILEVINE = BLOCKS.registerBlock("bilevine", BerryVinesBlock::new, berryVinesProperties());
    public static final DeferredBlock<BerryVinesPlantBlock> BILEVINE_PLANT = BLOCKS.registerBlock("bilevine_plant", BerryVinesPlantBlock::new, berryVinesProperties());

    // Machinery
    public static final DeferredBlockWithItem<EnergyStorageArrayBlock, ESABlockItem> ENERGY_STORAGE_ARRAY = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_ENERGY_STORAGE_ARRAY, () -> new EnergyStorageArrayBlock(machineProperties().noOcclusion(), false), block -> new ESABlockItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<EnergyStorageArrayBlock, InfiniteESABlockItem> INFINITE_ENERGY_STORAGE_ARRAY = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_INFINITE_ENERGY_STORAGE_ARRAY, () -> new EnergyStorageArrayBlock(machineProperties().noOcclusion(), true), block -> new InfiniteESABlockItem(block, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredBlockWithItem<BasicMachineBlock, BlockItem> DIGITAL_FURNACE = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_DIGITAL_FURNACE, () -> new BasicMachineBlock(machineProperties()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<BasicMachineBlock, BlockItem> DIGITAL_SMOKER = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_DIGITAL_SMOKER, () -> new BasicMachineBlock(machineProperties()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<BasicMachineBlock, BlockItem> DIGITAL_BLAST_FURNACE = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_DIGITAL_BLAST_FURNACE, () -> new BasicMachineBlock(machineProperties()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<BasicMachineBlock, BlockItem> GRINDER = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_GRINDER, () -> new BasicMachineBlock(machineProperties()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<BasicMachineBlock, BlockItem> MATERIAL_FUSING_CHAMBER = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_MATERIAL_FUSING_CHAMBER, () -> new BasicMachineBlock(machineProperties()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<FabricatorBlock, BlockItem> FABRICATOR = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_FABRICATOR, () -> new FabricatorBlock(machineProperties().noOcclusion()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));
    public static final DeferredBlockWithItem<FabricatorBlock, BlockItem> AUTO_FABRICATOR = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_AUTO_FABRICATOR, () -> new FabricatorBlock(machineProperties().noOcclusion()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));

    public static final DeferredBlockWithItem<MolecularReconstructorBlock, BlockItem> MOLECULAR_RECONSTRUCTOR = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_MOLECULAR_RECONSTRUCTOR, () -> new MolecularReconstructorBlock(machineProperties().noOcclusion()), block -> ContentsTooltipBlockItem.energyTooltipItem(block, new Item.Properties().stacksTo(1)));

    public static final DeferredBlockWithItem<EquipmentUpgradeStationBlock, BlockItem> EQUIPMENT_UPGRADE_STATION = BLOCKS.registerBlockAndSimpleItem(LTXICommonIds.ID_EQUIPMENT_UPGRADE_STATION, () -> new EquipmentUpgradeStationBlock(machineProperties().noOcclusion()), new Item.Properties().stacksTo(1));

    public static final DeferredBlockWithItem<TurretBlock, BlockItem> ROCKET_TURRET = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_ROCKET_TURRET, () -> new TurretBlock(machineProperties().noOcclusion()), block -> ContentsTooltipBlockItem.energyOwnerTooltipItem(block, new Item.Properties().stacksTo(1).rarity(LTXIItemRarities.ltxGearRarity())));
    public static final DeferredBlockWithItem<TurretBlock, BlockItem> RAILGUN_TURRET = BLOCKS.registerBlockAndItem(LTXICommonIds.ID_RAILGUN_TURRET, () -> new TurretBlock(machineProperties().noOcclusion()), block -> ContentsTooltipBlockItem.energyOwnerTooltipItem(block, new Item.Properties().stacksTo(1).rarity(LTXIItemRarities.ltxGearRarity())));

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
}