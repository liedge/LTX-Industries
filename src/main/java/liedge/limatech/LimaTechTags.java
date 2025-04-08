package liedge.limatech;

import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;

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
        public static final TagKey<Block> SHEARS_HARVESTABLE = COMMON_NAMESPACE.blockTag("shears_harvestable");

        public static final TagKey<Block> DEEPSLATE_GRINDABLES = RESOURCES.blockTag("deepslate_grindables");
        public static final TagKey<Block> WRENCH_BREAKABLE = RESOURCES.blockTag("wrench_breakable");
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
        public static final TagKey<Item> WRENCH_BREAKABLE = copy(Blocks.WRENCH_BREAKABLE);

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
        public static final TagKey<Item> GLOW_BLOCK_MATERIALS = RESOURCES.itemTag("glow_block_materials");

        public static final TagKey<Item> LTX_PROJECTILE_WEAPONS = RESOURCES.itemTag("ltx_weapons/energy");
        public static final TagKey<Item> LTX_MELEE_WEAPONS = RESOURCES.itemTag("ltx_weapons/melee");
        public static final TagKey<Item> LTX_ALL_WEAPONS = RESOURCES.itemTag("ltx_weapons/all");

        public static final TagKey<Item> LTX_MINING_TOOLS = RESOURCES.itemTag("ltx_tools/mining");
        public static final TagKey<Item> LTX_ALL_TOOLS = RESOURCES.itemTag("ltx_tools/all");

        private static TagKey<Item> copy(TagKey<Block> blockTag)
        {
            return ItemTags.create(blockTag.location());
        }
    }

    public static final class EntityTypes
    {
        private EntityTypes() {}

        public static final TagKey<EntityType<?>> INVALID_TARGETS = tag("targets/invalid");
        public static final TagKey<EntityType<?>> MEDIUM_THREAT_TARGETS = tag("targets/medium_threat");
        public static final TagKey<EntityType<?>> HIGH_THREAT_TARGETS = tag("targets/high_threat");
        public static final TagKey<EntityType<?>> FLYING_TARGETS = tag("targets/flying");
        public static final TagKey<EntityType<?>> AQUATIC_TARGETS = tag("targets/aquatic");

        public static final TagKey<EntityType<?>> WEAK_TO_FLAME = tag("weak_to_flame");
        public static final TagKey<EntityType<?>> WEAK_TO_CRYO = tag("weak_to_cryo");
        public static final TagKey<EntityType<?>> WEAK_TO_ELECTRIC = tag("weak_to_electric");

        private static TagKey<EntityType<?>> tag(String name)
        {
            return RESOURCES.entityTypeTag(name);
        }
    }

    public static final class BlockEntities
    {
        private BlockEntities() {}

        public static final TagKey<BlockEntityType<?>> GENERAL_PROCESSING_MACHINES = tag("machines/general_processing");
        public static final TagKey<BlockEntityType<?>> TURRETS = tag("turrets");

        private static TagKey<BlockEntityType<?>> tag(String name)
        {
            return RESOURCES.blockEntityTag(name);
        }
    }

    public static final class EquipmentUpgrades
    {
        private EquipmentUpgrades() {}

        public static final TagKey<EquipmentUpgrade> TOOL_PROFILE_OVERRIDES = tag("tool_profile_overrides");
        public static final TagKey<EquipmentUpgrade> MINING_DROPS_MODIFIERS = tag("mining_drops_modifiers");
        public static final TagKey<EquipmentUpgrade> AMMO_SOURCE_MODIFIERS = tag("ammo_source_modifiers");

        private static TagKey<EquipmentUpgrade> tag(String name)
        {
            return RESOURCES.tagKey(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, name);
        }
    }

    public static final class MachineUpgrades
    {
        private MachineUpgrades() {}

        public static final TagKey<MachineUpgrade> MACHINE_TIER = tag("machine_tier");

        private static TagKey<MachineUpgrade> tag(String name)
        {
            return RESOURCES.tagKey(LimaTechRegistries.Keys.MACHINE_UPGRADES, name);
        }
    }

    public static final class GameEvents
    {
        private GameEvents() {}

        public static final TagKey<GameEvent> WEAPON_VIBRATIONS = tag("weapon_vibrations");
        public static final TagKey<GameEvent> HANDHELD_EQUIPMENT = tag("handheld_equipment");

        private static TagKey<GameEvent> tag(String name)
        {
            return RESOURCES.tagKey(Registries.GAME_EVENT, name);
        }
    }

    public static final class DamageTypes
    {
        private DamageTypes() {}

        public static final TagKey<DamageType> WEAPON_DAMAGE = tag("weapon_damage");
        public static final TagKey<DamageType> BYPASS_SURVIVAL_DEFENSES = tag("bypass_survival_defenses");

        private static TagKey<DamageType> tag(String name)
        {
            return RESOURCES.tagKey(Registries.DAMAGE_TYPE, name);
        }
    }
}