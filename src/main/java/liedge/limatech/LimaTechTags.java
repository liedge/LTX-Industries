package liedge.limatech;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static liedge.limacore.lib.ModResources.COMMON_NAMESPACE;
import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechTags
{
    private LimaTechTags() {}

    public static final class Blocks
    {
        private Blocks() {}

        public static final TagKey<Block> TITANIUM_ORES = COMMON_NAMESPACE.blockTag("ores/titanium");
        public static final TagKey<Block> NIOBIUM_ORES = COMMON_NAMESPACE.blockTag("ores/niobium");
        public static final TagKey<Block> RAW_TITANIUM_STORAGE_BLOCKS = COMMON_NAMESPACE.blockTag("storage_blocks/raw_titanium");
        public static final TagKey<Block> RAW_NIOBIUM_STORAGE_BLOCKS = COMMON_NAMESPACE.blockTag("storage_blocks/raw_niobium");
        public static final TagKey<Block> TITANIUM_STORAGE_BLOCKS = COMMON_NAMESPACE.blockTag("storage_blocks/titanium");
        public static final TagKey<Block> NIOBIUM_STORAGE_BLOCKS = COMMON_NAMESPACE.blockTag("storage_blocks/niobium");
        public static final TagKey<Block> SLATE_ALLOY_STORAGE_BLOCKS = COMMON_NAMESPACE.blockTag("storage_blocks/slate_alloy");
        public static final TagKey<Block> DEEPSLATE_GRINDABLES = RESOURCES.blockTag("deepslate_grindables");
    }

    public static final class Items
    {
        private Items() {}

        // Block tags
        public static final TagKey<Item> TITANIUM_ORES = copy(Blocks.TITANIUM_ORES);
        public static final TagKey<Item> NIOBIUM_ORES = copy(Blocks.NIOBIUM_ORES);
        public static final TagKey<Item> RAW_TITANIUM_STORAGE_BLOCKS = copy(Blocks.RAW_TITANIUM_STORAGE_BLOCKS);
        public static final TagKey<Item> RAW_NIOBIUM_STORAGE_BLOCKS = copy(Blocks.RAW_NIOBIUM_STORAGE_BLOCKS);
        public static final TagKey<Item> TITANIUM_STORAGE_BLOCKS = copy(Blocks.TITANIUM_STORAGE_BLOCKS);
        public static final TagKey<Item> NIOBIUM_STORAGE_BLOCKS = copy(Blocks.NIOBIUM_STORAGE_BLOCKS);
        public static final TagKey<Item> SLATE_ALLOY_STORAGE_BLOCKS = copy(Blocks.SLATE_ALLOY_STORAGE_BLOCKS);
        public static final TagKey<Item> DEEPSLATE_GRINDABLES = copy(Blocks.DEEPSLATE_GRINDABLES);

        // Raw materials
        public static final TagKey<Item> RAW_TITANIUM_MATERIALS = COMMON_NAMESPACE.itemTag("raw_materials/titanium");
        public static final TagKey<Item> RAW_NIOBIUM_MATERIALS = COMMON_NAMESPACE.itemTag("raw_materials/niobium");

        // Ingots
        public static final TagKey<Item> TITANIUM_INGOTS = COMMON_NAMESPACE.itemTag("ingots/titanium");
        public static final TagKey<Item> NIOBIUM_INGOTS = COMMON_NAMESPACE.itemTag("ingots/niobium");
        public static final TagKey<Item> SLATE_ALLOY_INGOTS = COMMON_NAMESPACE.itemTag("ingots/slate_alloy");

        // Nuggets
        public static final TagKey<Item> TITANIUM_NUGGETS = COMMON_NAMESPACE.itemTag("nuggets/titanium");
        public static final TagKey<Item> NIOBIUM_NUGGETS = COMMON_NAMESPACE.itemTag("nuggets/niobium");
        public static final TagKey<Item> SLATE_ALLOY_NUGGETS = COMMON_NAMESPACE.itemTag("nuggets/slate_alloy");

        // Dusts
        public static final TagKey<Item> DEEPSLATE_DUSTS = COMMON_NAMESPACE.itemTag("dusts/deepslate");

        private static TagKey<Item> copy(TagKey<Block> blockTag)
        {
            return ItemTags.create(blockTag.location());
        }
    }

    public static final class EntityTypes
    {
        private EntityTypes() {}

        public static final TagKey<EntityType<?>> FLYING_MOBS = tag("flying_mobs");

        public static final TagKey<EntityType<?>> ELITE_MOBS = tag("elite_mobs");
        public static final TagKey<EntityType<?>> BOSS_MOBS = tag("boss_mobs");

        private static TagKey<EntityType<?>> tag(String name)
        {
            return RESOURCES.entityTypeTag(name);
        }
    }
}