package liedge.limatech.registry;

import liedge.limacore.registry.LimaBlockDeferredRegister;
import liedge.limacore.registry.LimaDeferredBlock;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.block.BasicMachineBlock;
import liedge.limatech.block.FabricatorBlock;
import liedge.limatech.block.RocketTurretBlock;
import liedge.limatech.item.MachineBlockItem;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collection;
import java.util.Map;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.of;

public final class LimaTechBlocks
{
    private LimaTechBlocks() {}

    private static final LimaBlockDeferredRegister BLOCKS = LimaTech.RESOURCES.deferredBlocks();

    public static void initRegister(IEventBus bus)
    {
        BLOCKS.registerToBus(bus);
    }

    static Collection<LimaDeferredBlock<?, ?>> getRegisteredBlocks()
    {
        return BLOCKS.getRegistryEntries();
    }

    // Ores
    public static final LimaDeferredBlock<DropExperienceBlock, BlockItem> TITANIUM_ORE = BLOCKS.registerBlockAndSimpleItem("titanium_ore", () -> new DropExperienceBlock(ConstantInt.of(0), of().mapColor(MapColor.STONE).strength(3f).requiresCorrectToolForDrops()));
    public static final LimaDeferredBlock<DropExperienceBlock, BlockItem> DEEPSLATE_TITANIUM_ORE = BLOCKS.registerBlockAndSimpleItem("deepslate_titanium_ore", () -> new DropExperienceBlock(ConstantInt.of(0), of().mapColor(MapColor.DEEPSLATE).strength(4.5f, 3f).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops()));
    public static final LimaDeferredBlock<DropExperienceBlock, BlockItem> NIOBIUM_ORE = BLOCKS.registerBlockAndSimpleItem("niobium_ore", () -> new DropExperienceBlock(UniformInt.of(1, 4), of().mapColor(MapColor.SAND).strength(3.2f, 9f).requiresCorrectToolForDrops()));

    // Raw ore blocks
    public static final LimaDeferredBlock<Block, BlockItem> RAW_TITANIUM_BLOCK = BLOCKS.registerSimpleBlockWithItem("raw_titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).requiresCorrectToolForDrops());
    public static final LimaDeferredBlock<Block, BlockItem> RAW_NIOBIUM_BLOCK = BLOCKS.registerSimpleBlockWithItem("raw_niobium_block", of().mapColor(MapColor.COLOR_PURPLE).strength(5f, 9f).requiresCorrectToolForDrops());

    // Ingot storage blocks
    public static final LimaDeferredBlock<Block, BlockItem> TITANIUM_BLOCK = BLOCKS.registerSimpleBlockWithItem("titanium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 6f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final LimaDeferredBlock<Block, BlockItem> NIOBIUM_BLOCK = BLOCKS.registerSimpleBlockWithItem("niobium_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(6f, 9f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final LimaDeferredBlock<Block, BlockItem> SLATE_ALLOY_BLOCK = BLOCKS.registerSimpleBlockWithItem("slate_alloy_block", of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5f, 12f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    // Decoration blocks
    public static final Map<DyeColor, DeferredBlock<Block>> GLOW_BLOCKS = LimaCollectionsUtil.immutableEnumMapFor(DyeColor.class, color -> BLOCKS.registerSimpleBlockWithItem(color.getSerializedName() + "_glow_block", of().mapColor(color).sound(SoundType.GLASS).strength(2f).lightLevel(state -> 15)));

    // Machinery
    public static final LimaDeferredBlock<BasicMachineBlock, MachineBlockItem> GRINDER = BLOCKS.registerBlockAndItem("grinder", () -> new BasicMachineBlock(machineProperties()), block -> new MachineBlockItem(block, new Item.Properties().stacksTo(1)));
    public static final LimaDeferredBlock<BasicMachineBlock, MachineBlockItem> MATERIAL_FUSING_CHAMBER = BLOCKS.registerBlockAndItem("material_fusing_chamber", () -> new BasicMachineBlock(machineProperties()), block -> new MachineBlockItem(block, new Item.Properties().stacksTo(1)));
    public static final LimaDeferredBlock<FabricatorBlock, MachineBlockItem> FABRICATOR = BLOCKS.registerBlockAndItem("fabricator", () -> new FabricatorBlock(machineProperties().noOcclusion()), block -> new MachineBlockItem(block, new Item.Properties().stacksTo(1)));

    public static final LimaDeferredBlock<RocketTurretBlock, BlockItem> ROCKET_TURRET = BLOCKS.registerBlockAndSimpleItem("rocket_turret", () -> new RocketTurretBlock(machineProperties().noOcclusion()));

    // Helpers & initializers
    private static BlockBehaviour.Properties machineProperties()
    {
        return of().mapColor(MapColor.SNOW).pushReaction(PushReaction.IGNORE).strength(6f, 24f).requiresCorrectToolForDrops();
    }
}