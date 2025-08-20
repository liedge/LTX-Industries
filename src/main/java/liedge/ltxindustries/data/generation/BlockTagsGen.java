package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.game.LTXIBlocks;
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
        buildTag(MINEABLE_WITH_PICKAXE).add(TITANIUM_PANEL, TITANIUM_GLASS, SLATE_GLASS);

        buildTag(NEEDS_STONE_TOOL).add(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM_BLOCK, TITANIUM_BLOCK, MESH_BLOCK).copyTo(MINEABLE_WITH_PICKAXE);
        buildTag(NEEDS_DIAMOND_TOOL).add(NIOBIUM_ORE, RAW_NIOBIUM_BLOCK, NIOBIUM_BLOCK, SLATESTEEL_BLOCK).copyTo(MINEABLE_WITH_PICKAXE);

        buildTag(BEACON_BASE_BLOCKS).add(TITANIUM_BLOCK, NIOBIUM_BLOCK, SLATESTEEL_BLOCK);

        buildTag(TITANIUM_ORES).add(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE);
        buildTag(NIOBIUM_ORES).add(NIOBIUM_ORE);
        buildTag(ORES).addTags(TITANIUM_ORES, NIOBIUM_ORES);

        buildTag(ORES_IN_GROUND_STONE).add(TITANIUM_ORE);
        buildTag(ORES_IN_GROUND_DEEPSLATE).add(DEEPSLATE_TITANIUM_ORE);
        buildTag(ORE_RATES_SINGULAR).addTags(TITANIUM_ORES, NIOBIUM_ORES);

        buildTag(RAW_TITANIUM_STORAGE_BLOCKS).add(RAW_TITANIUM_BLOCK);
        buildTag(RAW_NIOBIUM_STORAGE_BLOCKS).add(RAW_NIOBIUM_BLOCK);
        buildTag(TITANIUM_STORAGE_BLOCKS).add(TITANIUM_BLOCK);
        buildTag(NIOBIUM_STORAGE_BLOCKS).add(NIOBIUM_BLOCK);
        buildTag(SLATESTEEL_STORAGE_BLOCKS).add(SLATESTEEL_BLOCK);
        buildTag(SHEARS_HARVESTABLE).add(COBWEB, SHORT_GRASS, FERN, DEAD_BUSH, HANGING_ROOTS, VINE, TRIPWIRE).add(BILEVINE, BILEVINE_PLANT).addTags(WOOL, LEAVES);

        buildTag(IMPERMEABLE).add(TITANIUM_PANEL, TITANIUM_GLASS, SLATE_GLASS).addTag(LTXITags.Blocks.NEON_LIGHTS);
        buildTag(WITHER_IMMUNE).add(SLATE_GLASS);
        buildTag(DRAGON_IMMUNE).add(SLATE_GLASS);

        buildTag(LTXITags.Blocks.NEON_LIGHTS)
                .addHolders(List.copyOf(LTXIBlocks.NEON_LIGHTS.values()))
                .copyTo(MINEABLE_WITH_PICKAXE);
        buildTag(DEEPSLATE_GRINDABLES).add(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
        buildTag(WRENCH_BREAKABLE).add(DIGITAL_FURNACE,
                DIGITAL_SMOKER,
                DIGITAL_BLAST_FURNACE,
                GRINDER,
                MATERIAL_FUSING_CHAMBER,
                ELECTROCENTRIFUGE,
                MIXER,
                VOLTAIC_INJECTOR,
                CHEM_LAB,
                FABRICATOR,
                AUTO_FABRICATOR,
                EQUIPMENT_UPGRADE_STATION,
                MOLECULAR_RECONSTRUCTOR,
                        ENERGY_CELL_ARRAY,
                        INFINITE_ENERGY_CELL_ARRAY,
                ROCKET_TURRET,
                RAILGUN_TURRET)
                .copyTo(MINEABLE_WITH_PICKAXE, NEEDS_STONE_TOOL);
        buildTag(LTX_HOE_BOOSTABLE).addTag(CROPS);

        buildTag(STORAGE_BLOCKS).addTags(RAW_TITANIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS, NIOBIUM_STORAGE_BLOCKS, SLATESTEEL_STORAGE_BLOCKS);
    }
}