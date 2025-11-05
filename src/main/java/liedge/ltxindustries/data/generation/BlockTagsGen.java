package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.Blocks.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static net.minecraft.tags.BlockTags.*;
import static net.minecraft.world.level.block.Blocks.*;
import static net.neoforged.neoforge.common.Tags.Blocks.*;

class BlockTagsGen extends LimaTagsProvider.RegistryTags<Block>
{
    BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(output, BuiltInRegistries.BLOCK, LTXIndustries.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        buildTag(WRENCH_BREAKABLE).add(
                DIGITAL_FURNACE,
                DIGITAL_SMOKER,
                DIGITAL_BLAST_FURNACE,
                GRINDER,
                MATERIAL_FUSING_CHAMBER,
                ELECTROCENTRIFUGE,
                MIXER,
                VOLTAIC_INJECTOR,
                CHEM_LAB,
                ASSEMBLER,
                FABRICATOR,
                AUTO_FABRICATOR,
                DIGITAL_GARDEN,
                EQUIPMENT_UPGRADE_STATION,
                MOLECULAR_RECONSTRUCTOR,
                ENERGY_CELL_ARRAY,
                INFINITE_ENERGY_CELL_ARRAY,
                ROCKET_TURRET,
                RAILGUN_TURRET,
                MESH_BLOCK);
        buildTag(NEON_LIGHT_BLOCKS).addHolders(List.copyOf(NEON_LIGHTS.values()));

        List<Holder<Block>> stonePickaxeBreakable = List.of(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM_BLOCK, RAW_TITANIUM_CLUSTER, TITANIUM_BLOCK, MESH_BLOCK);
        List<Holder<Block>> diamondPickaxeBreakable = List.of(NIOBIUM_ORE, RAW_NIOBIUM_BLOCK, RAW_NIOBIUM_CLUSTER, NIOBIUM_BLOCK, SLATESTEEL_BLOCK);
        buildTag(MINEABLE_WITH_PICKAXE).add(TITANIUM_PANEL, SMOOTH_TITANIUM_PANEL, TITANIUM_GLASS, SLATESTEEL_PANEL, SMOOTH_SLATESTEEL_PANEL).addTags(WRENCH_BREAKABLE, NEON_LIGHT_BLOCKS).addHolders(stonePickaxeBreakable).addHolders(diamondPickaxeBreakable);
        buildTag(NEEDS_STONE_TOOL).addHolders(stonePickaxeBreakable).addTags(WRENCH_BREAKABLE);
        buildTag(NEEDS_DIAMOND_TOOL).addHolders(diamondPickaxeBreakable);

        buildTag(BEACON_BASE_BLOCKS).add(TITANIUM_BLOCK, NIOBIUM_BLOCK, SLATESTEEL_BLOCK);

        buildTag(TITANIUM_ORES).add(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE);
        buildTag(NIOBIUM_ORES).add(NIOBIUM_ORE);
        buildTag(ORES).addTags(TITANIUM_ORES, NIOBIUM_ORES);
        buildTag(ORE_CLUSTERS).add(RAW_TITANIUM_CLUSTER, RAW_NIOBIUM_CLUSTER);

        buildTag(ORES_IN_GROUND_STONE).add(TITANIUM_ORE);
        buildTag(ORES_IN_GROUND_DEEPSLATE).add(DEEPSLATE_TITANIUM_ORE);
        buildTag(ORE_RATES_SINGULAR).addTags(TITANIUM_ORES, NIOBIUM_ORES);
        buildTag(ORE_RATES_DENSE).addTags(ORE_CLUSTERS);

        buildTag(RAW_TITANIUM_STORAGE_BLOCKS).add(RAW_TITANIUM_BLOCK);
        buildTag(RAW_NIOBIUM_STORAGE_BLOCKS).add(RAW_NIOBIUM_BLOCK);
        buildTag(TITANIUM_STORAGE_BLOCKS).add(TITANIUM_BLOCK);
        buildTag(NIOBIUM_STORAGE_BLOCKS).add(NIOBIUM_BLOCK);
        buildTag(SLATESTEEL_STORAGE_BLOCKS).add(SLATESTEEL_BLOCK);
        buildTag(SHEARS_HARVESTABLE).add(COBWEB, SHORT_GRASS, FERN, DEAD_BUSH, HANGING_ROOTS, VINE, TRIPWIRE).add(BILEVINE, BILEVINE_PLANT).addTags(WOOL, LEAVES);

        buildTag(IMPERMEABLE).add(TITANIUM_PANEL, SMOOTH_TITANIUM_PANEL, TITANIUM_GLASS, SLATESTEEL_PANEL, SMOOTH_SLATESTEEL_PANEL).addTag(NEON_LIGHT_BLOCKS);
        buildTag(DEEPSLATE_GRINDABLES).add(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
        buildTag(LTX_HOE_BOOSTABLE).addTag(CROPS);
        buildTag(STORAGE_BLOCKS).addTags(RAW_TITANIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS, NIOBIUM_STORAGE_BLOCKS, SLATESTEEL_STORAGE_BLOCKS);
    }
}