package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.Blocks.*;
import static liedge.limatech.registry.game.LimaTechBlocks.*;
import static net.minecraft.tags.BlockTags.*;
import static net.neoforged.neoforge.common.Tags.Blocks.*;

class BlockTagsGen extends LimaTagsProvider.RegistryTags<Block>
{
    BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(output, BuiltInRegistries.BLOCK, LimaTech.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        buildTag(MINEABLE_WITH_PICKAXE).addHolders(List.copyOf(GLOW_BLOCKS.values()));

        buildTag(NEEDS_STONE_TOOL).add(TITANIUM_ORE, DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM_BLOCK, TITANIUM_BLOCK, ENERGY_STORAGE_ARRAY, INFINITE_ENERGY_STORAGE_ARRAY, DIGITAL_FURNACE, GRINDER, RECOMPOSER, MATERIAL_FUSING_CHAMBER, FABRICATOR, EQUIPMENT_UPGRADE_STATION,
                ROCKET_TURRET, RAILGUN_TURRET).copyTo(MINEABLE_WITH_PICKAXE);
        buildTag(NEEDS_DIAMOND_TOOL).add(NIOBIUM_ORE, RAW_NIOBIUM_BLOCK, NIOBIUM_BLOCK, SLATE_ALLOY_BLOCK).copyTo(MINEABLE_WITH_PICKAXE);

        buildTag(BEACON_BASE_BLOCKS).add(TITANIUM_BLOCK, NIOBIUM_BLOCK, SLATE_ALLOY_BLOCK);

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
        buildTag(SLATE_ALLOY_STORAGE_BLOCKS).add(SLATE_ALLOY_BLOCK);

        buildTag(DEEPSLATE_GRINDABLES).add(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);

        buildTag(STORAGE_BLOCKS).addTags(RAW_TITANIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS, NIOBIUM_STORAGE_BLOCKS, SLATE_ALLOY_STORAGE_BLOCKS);
    }
}