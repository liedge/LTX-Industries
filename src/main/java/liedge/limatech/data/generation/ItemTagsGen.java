package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechTags.Blocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.Items.*;
import static liedge.limatech.registry.LimaTechItems.*;
import static net.minecraft.tags.ItemTags.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class ItemTagsGen extends LimaTagsProvider.ItemTags
{
    ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper)
    {
        super(output, LimaTech.MODID, blockTags, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        copyTag(Blocks.TITANIUM_ORES, TITANIUM_ORES);
        copyTag(Blocks.NIOBIUM_ORES, NIOBIUM_ORES);

        copyTag(Blocks.RAW_TITANIUM_STORAGE_BLOCKS, RAW_TITANIUM_STORAGE_BLOCKS);
        copyTag(Blocks.RAW_NIOBIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS);
        copyTag(Blocks.TITANIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS);
        copyTag(Blocks.NIOBIUM_STORAGE_BLOCKS, NIOBIUM_STORAGE_BLOCKS);
        copyTag(Blocks.SLATE_ALLOY_STORAGE_BLOCKS, SLATE_ALLOY_STORAGE_BLOCKS);

        copyTag(Blocks.DEEPSLATE_GRINDABLES, DEEPSLATE_GRINDABLES);

        copyTag(Tags.Blocks.ORE_RATES_SINGULAR, ORE_RATES_SINGULAR);
        copyTag(Tags.Blocks.ORES_IN_GROUND_STONE, ORES_IN_GROUND_STONE);
        copyTag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, ORES_IN_GROUND_DEEPSLATE);

        buildTag(RAW_TITANIUM_MATERIALS).add(RAW_TITANIUM);
        buildTag(RAW_NIOBIUM_MATERIALS).add(RAW_NIOBIUM);
        buildTag(RAW_MATERIALS).addTags(RAW_TITANIUM_MATERIALS, RAW_NIOBIUM_MATERIALS);

        buildTag(TITANIUM_INGOTS).add(TITANIUM_INGOT);
        buildTag(NIOBIUM_INGOTS).add(NIOBIUM_INGOT);
        buildTag(SLATE_ALLOY_INGOTS).add(SLATE_ALLOY_INGOT);
        buildTag(INGOTS).add(BEDROCK_ALLOY_INGOT).addTags(TITANIUM_INGOTS, NIOBIUM_INGOTS, SLATE_ALLOY_INGOTS);

        buildTag(TITANIUM_NUGGETS).add(TITANIUM_NUGGET);
        buildTag(NIOBIUM_NUGGETS).add(NIOBIUM_NUGGET);
        buildTag(SLATE_ALLOY_NUGGETS).add(SLATE_ALLOY_NUGGET);
        buildTag(NUGGETS).addTags(TITANIUM_NUGGETS, NIOBIUM_NUGGETS, SLATE_ALLOY_NUGGETS);

        buildTag(DYES_WHITE).add(WHITE_PIGMENT);
        buildTag(DYES_LIME).add(LIME_PIGMENT);

        buildTag(SWORDS).add(TITANIUM_SWORD);
        buildTag(SHOVELS).add(TITANIUM_SHOVEL);
        buildTag(PICKAXES).add(TITANIUM_PICKAXE);
        buildTag(AXES).add(TITANIUM_AXE);
        buildTag(HOES).add(TITANIUM_HOE);
        buildTag(TOOLS_SHEAR).add(TITANIUM_SHEARS);

        buildTag(BEACON_PAYMENT_ITEMS).addTags(TITANIUM_INGOTS, NIOBIUM_INGOTS, SLATE_ALLOY_INGOTS);

        buildTag(DEEPSLATE_DUSTS).add(DEEPSLATE_POWDER);
        buildTag(DUSTS).addTag(DEEPSLATE_DUSTS);

        buildTag(LTX_WEAPONS).add(SUBMACHINE_GUN, SHOTGUN, GRENADE_LAUNCHER, ROCKET_LAUNCHER, MAGNUM);
    }
}