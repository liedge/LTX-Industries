package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.Blocks.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static net.minecraft.tags.BlockTags.*;
import static net.minecraft.world.level.block.Blocks.*;
import static net.neoforged.neoforge.common.Tags.Blocks.*;

class BlockTagsGen extends LimaTagsProvider.RegistryTags<Block>
{
    BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, BuiltInRegistries.BLOCK, LTXIndustries.MODID, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        buildTag(WRENCH_BREAKABLE).add(
                DIGITAL_FURNACE,
                DIGITAL_SMOKER,
                DIGITAL_BLAST_FURNACE,
                GRINDER,
                MATERIAL_PRESS,
                ARC_FURNACE,
                HYDROSIEVE,
                ELECTROCENTRIFUGE,
                MIXER,
                VOLTAIC_INJECTOR,
                CHEM_LAB,
                ASSEMBLER,
                GEO_SYNTHESIZER,
                FABRICATOR,
                AUTO_FABRICATOR,
                ATMOSPHERIC_SCRUBBER,
                DIGITAL_GARDEN,
                PORTABLE_GENERATOR,
                SOLAR_PANEL,
                UPGRADE_STATION,
                ENERGY_CELL_ARRAY,
                INFINITE_ENERGY_CELL_ARRAY,
                PORTABLE_TANK,
                INFINITE_WATER_TANK,
                INFINITE_LAVA_TANK,
                REPAIR_STATION,
                ARC_TURRET,
                ROCKET_TURRET,
                RAILGUN_TURRET,
                MESH_BLOCK);
        buildTag(NEON_LIGHT_BLOCKS).addHolders(List.copyOf(NEON_LIGHTS.values()));

        List<Holder<Block>> ironPickaxeBreakable = List.of(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM_BLOCK, RAW_TITANIUM_CLUSTER, TITANIUM_BLOCK,
                SILVER_ORE, DEEPSLATE_SILVER_ORE, RAW_SILVER_BLOCK, RAW_SILVER_CLUSTER, SILVER_BLOCK,
                SLATESTEEL_BLOCK);
        List<Holder<Block>> diamondPickaxeBreakable = List.of(NIOBIUM_ORE, RAW_NIOBIUM_BLOCK, RAW_NIOBIUM_CLUSTER, NIOBIUM_BLOCK);
        buildTag(MINEABLE_WITH_PICKAXE).add(
                PERIDOTITE,
                PERIDOTITE_STAIRS,
                PERIDOTITE_SLAB,
                PERIDOTITE_WALL,
                POLISHED_PERIDOTITE,
                POLISHED_PERIDOTITE_STAIRS,
                POLISHED_PERIDOTITE_SLAB,
                POLISHED_PERIDOTITE_WALL,
                TITANIUM_PANEL,
                SMOOTH_TITANIUM_PANEL,
                TILED_TITANIUM_PANEL,
                TITANIUM_GLASS,
                GLACIA_GLASS,
                SLATESTEEL_PANEL,
                SMOOTH_SLATESTEEL_PANEL,
                TILED_SLATESTEEL_PANEL)
                .addTags(WRENCH_BREAKABLE, NEON_LIGHT_BLOCKS)
                .addHolders(ironPickaxeBreakable)
                .addHolders(diamondPickaxeBreakable);
        buildTag(NEEDS_IRON_TOOL).addHolders(ironPickaxeBreakable);
        buildTag(NEEDS_DIAMOND_TOOL).addHolders(diamondPickaxeBreakable);

        buildTag(BEACON_BASE_BLOCKS).add(TITANIUM_BLOCK, SILVER_BLOCK, NIOBIUM_BLOCK, SLATESTEEL_BLOCK);

        buildTag(STONES).add(PERIDOTITE);
        buildTag(STAIRS).add(PERIDOTITE_STAIRS, POLISHED_PERIDOTITE_STAIRS);
        buildTag(SLABS).add(PERIDOTITE_SLAB, POLISHED_PERIDOTITE_SLAB);
        buildTag(WALLS).add(PERIDOTITE_WALL, POLISHED_PERIDOTITE_WALL);

        buildTag(TITANIUM_ORES).add(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE).copyTo(ORES, ORE_RATES_SINGULAR);
        buildTag(SILVER_ORES).add(SILVER_ORE, DEEPSLATE_SILVER_ORE).copyTo(ORES, ORE_RATES_SINGULAR);
        buildTag(NIOBIUM_ORES).add(NIOBIUM_ORE).copyTo(ORES, ORE_RATES_SINGULAR);
        buildTag(ORE_CLUSTERS).add(RAW_TITANIUM_CLUSTER, RAW_SILVER_CLUSTER, RAW_NIOBIUM_CLUSTER);

        buildTag(ORES_IN_GROUND_STONE).add(TITANIUM_ORE, SILVER_ORE);
        buildTag(ORES_IN_GROUND_DEEPSLATE).add(DEEPSLATE_TITANIUM_ORE, DEEPSLATE_SILVER_ORE);
        buildTag(ORE_RATES_DENSE).addTags(ORE_CLUSTERS);

        buildTag(RAW_TITANIUM_STORAGE_BLOCKS).add(RAW_TITANIUM_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(RAW_SILVER_STORAGE_BLOCKS).add(RAW_SILVER_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(RAW_NIOBIUM_STORAGE_BLOCKS).add(RAW_NIOBIUM_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(TITANIUM_STORAGE_BLOCKS).add(TITANIUM_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(SILVER_STORAGE_BLOCKS).add(SILVER_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(NIOBIUM_STORAGE_BLOCKS).add(NIOBIUM_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(SLATESTEEL_STORAGE_BLOCKS).add(SLATESTEEL_BLOCK).copyTo(STORAGE_BLOCKS);
        buildTag(SHEARS_HARVESTABLE).add(COBWEB, SHORT_GRASS, FERN, DEAD_BUSH, HANGING_ROOTS, VINE, TRIPWIRE).add(BILEVINE, BILEVINE_PLANT).addTags(WOOL, LEAVES);

        buildTag(IMPERMEABLE).add(TITANIUM_PANEL, SMOOTH_TITANIUM_PANEL, TILED_TITANIUM_PANEL, TITANIUM_GLASS, GLACIA_GLASS, SLATESTEEL_PANEL, SMOOTH_SLATESTEEL_PANEL, TILED_SLATESTEEL_PANEL).addTag(NEON_LIGHT_BLOCKS);
        buildTag(DEEPSLATE_GRINDABLES).add(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
        buildTag(EPSILON_HOE_BOOSTABLE).addTag(CROPS);
    }
}