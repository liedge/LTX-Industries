package liedge.ltxindustries;

import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import static liedge.limacore.lib.ModResources.COMMON;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXITags
{
    private LTXITags() {}

    public static final class Blocks
    {
        private Blocks() {}

        public static final TagKey<Block> TITANIUM_ORES = COMMON.blockTag("ores/titanium");
        public static final TagKey<Block> NIOBIUM_ORES = COMMON.blockTag("ores/niobium");
        public static final TagKey<Block> RAW_TITANIUM_STORAGE_BLOCKS = COMMON.blockTag("storage_blocks/raw_titanium");
        public static final TagKey<Block> RAW_NIOBIUM_STORAGE_BLOCKS = COMMON.blockTag("storage_blocks/raw_niobium");
        public static final TagKey<Block> TITANIUM_STORAGE_BLOCKS = COMMON.blockTag("storage_blocks/titanium");
        public static final TagKey<Block> NIOBIUM_STORAGE_BLOCKS = COMMON.blockTag("storage_blocks/niobium");
        public static final TagKey<Block> SLATESTEEL_STORAGE_BLOCKS = COMMON.blockTag("storage_blocks/slatesteel");
        public static final TagKey<Block> SHEARS_HARVESTABLE = COMMON.blockTag("shears_harvestable");

        // Mod Tags
        public static final TagKey<Block> ORE_CLUSTERS = RESOURCES.blockTag("ore_clusters");
        public static final TagKey<Block> NEON_LIGHT_BLOCKS = RESOURCES.blockTag("neon_lights");
        public static final TagKey<Block> DEEPSLATE_GRINDABLES = RESOURCES.blockTag("deepslate_grindables");
        public static final TagKey<Block> WRENCH_BREAKABLE = RESOURCES.blockTag("wrench_breakable");
        public static final TagKey<Block> LTX_HOE_BOOSTABLE = RESOURCES.blockTag("ltx_hoe_boostable");
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
        public static final TagKey<Item> SLATESTEEL_STORAGE_BLOCKS = copy(Blocks.SLATESTEEL_STORAGE_BLOCKS);
        public static final TagKey<Item> ORE_CLUSTERS = copy(Blocks.ORE_CLUSTERS);
        public static final TagKey<Item> NEON_LIGHT_BLOCKS = copy(Blocks.NEON_LIGHT_BLOCKS);
        public static final TagKey<Item> DEEPSLATE_GRINDABLES = copy(Blocks.DEEPSLATE_GRINDABLES);
        public static final TagKey<Item> WRENCH_BREAKABLE = copy(Blocks.WRENCH_BREAKABLE);

        // Raw materials
        public static final TagKey<Item> RAW_TITANIUM_MATERIALS = COMMON.itemTag("raw_materials/titanium");
        public static final TagKey<Item> RAW_NIOBIUM_MATERIALS = COMMON.itemTag("raw_materials/niobium");

        // Ingots
        public static final TagKey<Item> TITANIUM_INGOTS = COMMON.itemTag("ingots/titanium");
        public static final TagKey<Item> NIOBIUM_INGOTS = COMMON.itemTag("ingots/niobium");
        public static final TagKey<Item> SLATESTEEL_INGOTS = COMMON.itemTag("ingots/slatesteel");

        // Nuggets
        public static final TagKey<Item> TITANIUM_NUGGETS = COMMON.itemTag("nuggets/titanium");
        public static final TagKey<Item> NIOBIUM_NUGGETS = COMMON.itemTag("nuggets/niobium");
        public static final TagKey<Item> SLATESTEEL_NUGGETS = COMMON.itemTag("nuggets/slatesteel");

        // Dusts
        public static final TagKey<Item> DEEPSLATE_DUSTS = COMMON.itemTag("dusts/deepslate");

        // Mod tags
        public static final TagKey<Item> NEON_LIGHT_MATERIALS = RESOURCES.itemTag("neon_light_materials");
        public static final TagKey<Item> APPLE_SAPLINGS = RESOURCES.itemTag("apple_saplings");
        public static final TagKey<Item> REPAIR_BLACKLIST = RESOURCES.itemTag("repair_blacklist");

        public static final TagKey<Item> ALL_WEAPONS = RESOURCES.itemTag("weapons");
        public static final TagKey<Item> ENERGY_PROJECTILE_WEAPONS = RESOURCES.itemTag("weapons/energy_projectile");
        public static final TagKey<Item> MELEE_WEAPONS = RESOURCES.itemTag("weapons/melee");
        public static final TagKey<Item> LIGHTWEIGHT_WEAPONS = RESOURCES.itemTag("weapons/lightweight");
        public static final TagKey<Item> SPECIALIST_WEAPONS = RESOURCES.itemTag("weapons/specialist");
        public static final TagKey<Item> EXPLOSIVE_WEAPONS = RESOURCES.itemTag("weapons/explosive");
        public static final TagKey<Item> HEAVY_WEAPONS = RESOURCES.itemTag("weapons/heavy");

        public static final TagKey<Item> ALL_TOOLS = RESOURCES.itemTag("tools");
        public static final TagKey<Item> MINING_TOOLS = RESOURCES.itemTag("tools/mining");

        private static TagKey<Item> copy(TagKey<Block> blockTag)
        {
            return ItemTags.create(blockTag.location());
        }
    }

    public static final class Fluids
    {
        private Fluids() {}

        public static final TagKey<Fluid> HYDROGEN_FLUIDS = COMMON.fluidTag("hydrogen");
        public static final TagKey<Fluid> OXYGEN_FLUIDS = COMMON.fluidTag("oxygen");
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
        public static final TagKey<BlockEntityType<?>> STANDARD_UPGRADABLE_MACHINES = tag("upgradable/standard");
        public static final TagKey<BlockEntityType<?>> ULTIMATE_UPGRADABLE_MACHINES = tag("upgradable/ultimate");
        public static final TagKey<BlockEntityType<?>> TURRETS = tag("turrets");

        private static TagKey<BlockEntityType<?>> tag(String name)
        {
            return RESOURCES.blockEntityTag(name);
        }
    }

    public static final class EquipmentUpgrades
    {
        private EquipmentUpgrades() {}

        public static final TagKey<EquipmentUpgrade> MINING_LEVEL_UPGRADES = tag("mining_levels");
        public static final TagKey<EquipmentUpgrade> MINING_DROPS_MODIFIERS = tag("mining_drops_modifiers");
        public static final TagKey<EquipmentUpgrade> AMMO_SOURCE_MODIFIERS = tag("ammo_source_modifiers");

        private static TagKey<EquipmentUpgrade> tag(String name)
        {
            return RESOURCES.tagKey(LTXIRegistries.Keys.EQUIPMENT_UPGRADES, name);
        }
    }

    public static final class MachineUpgrades
    {
        private MachineUpgrades() {}

        public static final TagKey<MachineUpgrade> MACHINE_TIER = tag("machine_tier");

        private static TagKey<MachineUpgrade> tag(String name)
        {
            return RESOURCES.tagKey(LTXIRegistries.Keys.MACHINE_UPGRADES, name);
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