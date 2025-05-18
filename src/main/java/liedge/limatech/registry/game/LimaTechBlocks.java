package liedge.limatech.registry.game;

import liedge.limacore.capability.energy.ItemEnergyProperties;
import liedge.limacore.registry.DeferredBlockWithItem;
import liedge.limacore.registry.LimaDeferredBlocksWithItems;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.block.*;
import liedge.limatech.item.ContentsTooltipBlockItem;
import liedge.limatech.item.ESABlockItem;
import liedge.limatech.item.EnergyHolderItem;
import liedge.limatech.item.LimaTechRarities;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

public final class LimaTechBlocks
{
    private LimaTechBlocks() {}

    private static final LimaDeferredBlocksWithItems BLOCKS = LimaTech.RESOURCES.deferredBlocksWithItems();

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
        bus.addListener(RegisterCapabilitiesEvent.class, LimaTechBlocks::registerCapabilities);
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

    // Decoration blocks
    public static final Map<DyeColor, DeferredBlock<Block>> GLOW_BLOCKS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(DyeColor.class, color -> BLOCKS.registerSimpleBlockAndItem(color.getSerializedName() + "_glow_block", of().mapColor(color).sound(SoundType.GLASS).strength(2f).lightLevel(state -> 15)));

    // Machinery
    public static final DeferredBlockWithItem<EnergyStorageArrayBlock, ESABlockItem> ENERGY_STORAGE_ARRAY = BLOCKS.registerBlockAndItem("energy_storage_array", () -> new EnergyStorageArrayBlock(machineProperties().noOcclusion(), false), block -> new ESABlockItem(block, new Item.Properties().stacksTo(1), false));
    public static final DeferredBlockWithItem<EnergyStorageArrayBlock, ESABlockItem> INFINITE_ENERGY_STORAGE_ARRAY = BLOCKS.registerBlockAndItem("infinite_energy_storage_array", () -> new EnergyStorageArrayBlock(machineProperties().noOcclusion(), true), block -> new ESABlockItem(block, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(LimaCoreDataComponents.ENERGY_PROPERTIES, ItemEnergyProperties.INFINITE), true));
    public static final DeferredBlockWithItem<BasicHorizontalMachineBlock, ContentsTooltipBlockItem> DIGITAL_FURNACE = BLOCKS.registerBlockAndItem("digital_furnace", () -> new BasicHorizontalMachineBlock(machineProperties()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), true, true));
    public static final DeferredBlockWithItem<BasicHorizontalMachineBlock, ContentsTooltipBlockItem> GRINDER = BLOCKS.registerBlockAndItem("grinder", () -> new BasicHorizontalMachineBlock(machineProperties()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), true, true));
    public static final DeferredBlockWithItem<BasicHorizontalMachineBlock, ContentsTooltipBlockItem> RECOMPOSER = BLOCKS.registerBlockAndItem("recomposer", () -> new BasicHorizontalMachineBlock(machineProperties()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), true, true));
    public static final DeferredBlockWithItem<BasicHorizontalMachineBlock, ContentsTooltipBlockItem> MATERIAL_FUSING_CHAMBER = BLOCKS.registerBlockAndItem("material_fusing_chamber", () -> new BasicHorizontalMachineBlock(machineProperties()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), true, true));
    public static final DeferredBlockWithItem<FabricatorBlock, ContentsTooltipBlockItem> FABRICATOR = BLOCKS.registerBlockAndItem("fabricator", () -> new FabricatorBlock(machineProperties().noOcclusion()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), true, true));
    public static final DeferredBlockWithItem<FabricatorBlock, ContentsTooltipBlockItem> AUTO_FABRICATOR = BLOCKS.registerBlockAndItem("auto_fabricator", () -> new FabricatorBlock(machineProperties().noOcclusion()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), true, false));
    public static final DeferredBlockWithItem<EquipmentUpgradeStationBlock, ContentsTooltipBlockItem> EQUIPMENT_UPGRADE_STATION = BLOCKS.registerBlockAndItem("equipment_upgrade_station", () -> new EquipmentUpgradeStationBlock(machineProperties().noOcclusion()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1), false, true));

    public static final DeferredBlockWithItem<TurretBlock, BlockItem> ROCKET_TURRET = BLOCKS.registerBlockAndItem("rocket_turret", () -> new TurretBlock(machineProperties().noOcclusion()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1).rarity(LimaTechRarities.ltxGearRarity()), true, false, true));
    public static final DeferredBlockWithItem<TurretBlock, BlockItem> RAILGUN_TURRET = BLOCKS.registerBlockAndItem("railgun_turret", () -> new TurretBlock(machineProperties().noOcclusion()), block -> new ContentsTooltipBlockItem(block, new Item.Properties().stacksTo(1).rarity(LimaTechRarities.ltxGearRarity()), true, false, true));

    // Helpers & initializers
    private static BlockBehaviour.Properties machineProperties()
    {
        return of().mapColor(MapColor.SNOW).pushReaction(PushReaction.IGNORE).strength(6f, 24f).requiresCorrectToolForDrops();
    }
}