package liedge.limatech;

import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
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

        // Mod tags
        public static final TagKey<Item> LTX_WEAPONS = RESOURCES.itemTag("ltx_weapons");

        private static TagKey<Item> copy(TagKey<Block> blockTag)
        {
            return ItemTags.create(blockTag.location());
        }
    }

    public static final class EntityTypes
    {
        private EntityTypes() {}

        public static final TagKey<EntityType<?>> IMMUNE_TO_LTX_WEAPONS = tag("immune_to_ltx_weapons");

        public static final TagKey<EntityType<?>> WEAK_TO_FLAME = tag("weak_to_flame");
        public static final TagKey<EntityType<?>> WEAK_TO_CRYO = tag("weak_to_cryo");
        public static final TagKey<EntityType<?>> WEAK_TO_ELECTRIC = tag("weak_to_electric");

        public static final TagKey<EntityType<?>> ROCKET_TURRET_TARGETS = tag("rocket_turret_targets");

        public static final TagKey<EntityType<?>> MEDIUM_THREAT_LEVEL = tag("medium_threat");
        public static final TagKey<EntityType<?>> HIGH_THREAT_LEVEL = tag("high_threat");

        private static TagKey<EntityType<?>> tag(String name)
        {
            return RESOURCES.entityTypeTag(name);
        }
    }

    public static final class EquipmentUpgrades
    {
        private EquipmentUpgrades() {}

        public static final TagKey<EquipmentUpgrade> AMMO_SOURCE_MODIFIERS = key("ammo_source_modifiers");

        private static TagKey<EquipmentUpgrade> key(String name)
        {
            return RESOURCES.tagKey(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, name);
        }
    }
}