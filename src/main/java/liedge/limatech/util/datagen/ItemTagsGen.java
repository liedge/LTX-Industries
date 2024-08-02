package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.TagBuilderHelper;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechTags.Blocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.Items.*;
import static liedge.limatech.registry.LimaTechItems.*;
import static net.minecraft.tags.ItemTags.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class ItemTagsGen extends ItemTagsProvider implements TagBuilderHelper<Item>
{
    ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper)
    {
        super(output, registries, blockTags, LimaTech.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        copy(Blocks.TITANIUM_ORES, TITANIUM_ORES);
        copy(Blocks.NIOBIUM_ORES, NIOBIUM_ORES);

        copy(Blocks.RAW_TITANIUM_STORAGE_BLOCKS, RAW_TITANIUM_STORAGE_BLOCKS);
        copy(Blocks.RAW_NIOBIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS);
        copy(Blocks.TITANIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS);
        copy(Blocks.NIOBIUM_STORAGE_BLOCKS, NIOBIUM_STORAGE_BLOCKS);
        copy(Blocks.SLATE_ALLOY_STORAGE_BLOCKS, SLATE_ALLOY_STORAGE_BLOCKS);

        copy(Blocks.DEEPSLATE_GRINDABLES, DEEPSLATE_GRINDABLES);

        copy(Tags.Blocks.ORE_RATES_SINGULAR, ORE_RATES_SINGULAR);
        copy(Tags.Blocks.ORES_IN_GROUND_STONE, ORES_IN_GROUND_STONE);
        copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, ORES_IN_GROUND_DEEPSLATE);

        buildTag(RAW_TITANIUM_MATERIALS).add(RAW_TITANIUM);
        buildTag(RAW_NIOBIUM_MATERIALS).add(RAW_NIOBIUM);
        buildTag(RAW_MATERIALS).addTags(RAW_TITANIUM_MATERIALS, RAW_NIOBIUM_MATERIALS);

        buildTag(TITANIUM_INGOTS).add(TITANIUM_INGOT);
        buildTag(NIOBIUM_INGOTS).add(NIOBIUM_INGOT);
        buildTag(SLATE_ALLOY_INGOTS).add(SLATE_ALLOY_INGOT);
        buildTag(INGOTS).addTags(TITANIUM_INGOTS, NIOBIUM_INGOTS, SLATE_ALLOY_INGOTS);

        buildTag(TITANIUM_NUGGETS).add(TITANIUM_NUGGET);
        buildTag(NIOBIUM_NUGGETS).add(NIOBIUM_NUGGET);
        buildTag(SLATE_ALLOY_NUGGETS).add(SLATE_ALLOY_NUGGET);
        buildTag(NUGGETS).addTags(TITANIUM_NUGGETS, NIOBIUM_NUGGETS, SLATE_ALLOY_NUGGETS);

        buildTag(SWORDS).add(TITANIUM_SWORD);
        buildTag(SHOVELS).add(TITANIUM_SHOVEL);
        buildTag(PICKAXES).add(TITANIUM_PICKAXE);
        buildTag(AXES).add(TITANIUM_AXE);
        buildTag(HOES).add(TITANIUM_HOE);
        buildTag(TOOLS_SHEAR).add(TITANIUM_SHEARS);

        buildTag(BEACON_PAYMENT_ITEMS).addTags(TITANIUM_INGOTS, NIOBIUM_INGOTS, SLATE_ALLOY_INGOTS);

        buildTag(DEEPSLATE_DUSTS).add(DEEPSLATE_POWDER);
        buildTag(DUSTS).addTag(DEEPSLATE_DUSTS);
    }

    @Override
    public @Nullable Registry<Item> getTagRegistry()
    {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public TagBuilder getOrCreateRawBuilder(TagKey<Item> tagKey)
    {
        return super.getOrCreateRawBuilder(tagKey);
    }
}