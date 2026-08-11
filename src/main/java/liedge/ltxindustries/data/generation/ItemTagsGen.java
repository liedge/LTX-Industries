package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.LTXITags.Blocks;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.Items.*;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
import static net.minecraft.tags.ItemTags.*;
import static net.minecraft.world.item.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class ItemTagsGen extends LimaTagsProvider.ItemTags
{
    ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags)
    {
        super(output, LTXIndustries.MODID, blockTags, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        copyTag(Tags.Blocks.STONES, STONES);
        copyTag(BlockTags.STAIRS, STAIRS);
        copyTag(BlockTags.SLABS, SLABS);
        copyTag(BlockTags.WALLS, WALLS);
        copyTag(Blocks.TITANIUM_ORES, TITANIUM_ORES);
        copyTag(Blocks.SILVER_ORES, SILVER_ORES);
        copyTag(Blocks.NIOBIUM_ORES, NIOBIUM_ORES);

        copyTag(Blocks.RAW_TITANIUM_STORAGE_BLOCKS, RAW_TITANIUM_STORAGE_BLOCKS);
        copyTag(Blocks.RAW_SILVER_STORAGE_BLOCKS, RAW_SILVER_STORAGE_BLOCKS);
        copyTag(Blocks.RAW_NIOBIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS);
        copyTag(Blocks.TITANIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS);
        copyTag(Blocks.SILVER_STORAGE_BLOCKS, SILVER_STORAGE_BLOCKS);
        copyTag(Blocks.NIOBIUM_STORAGE_BLOCKS, NIOBIUM_STORAGE_BLOCKS);
        copyTag(Blocks.SLATESTEEL_STORAGE_BLOCKS, SLATESTEEL_STORAGE_BLOCKS);

        copyTag(Blocks.NEON_LIGHT_BLOCKS, NEON_LIGHT_BLOCKS);
        copyTag(Blocks.DEEPSLATE_GRINDABLES, DEEPSLATE_GRINDABLES);
        copyTag(Blocks.WRENCH_BREAKABLE, WRENCH_BREAKABLE);

        copyTag(Tags.Blocks.ORES, ORES);
        copyTag(Blocks.ORE_CLUSTERS, ORE_CLUSTERS);
        copyTag(Tags.Blocks.ORE_RATES_SINGULAR, ORE_RATES_SINGULAR);
        copyTag(Tags.Blocks.ORE_RATES_DENSE, ORE_RATES_DENSE);
        copyTag(Tags.Blocks.ORES_IN_GROUND_STONE, ORES_IN_GROUND_STONE);
        copyTag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, ORES_IN_GROUND_DEEPSLATE);

        buildTag(RAW_TITANIUM_MATERIALS).add(RAW_TITANIUM).copyTo(RAW_MATERIALS);
        buildTag(RAW_SILVER_MATERIALS).add(RAW_SILVER).copyTo(RAW_MATERIALS);
        buildTag(RAW_OLIVINE_MATERIALS).add(RAW_OLIVINE).copyTo(RAW_MATERIALS);
        buildTag(RAW_FLUORITE_MATERIALS).add(RAW_FLUORITE).copyTo(RAW_MATERIALS);
        buildTag(RAW_NIOBIUM_MATERIALS).add(RAW_NIOBIUM).copyTo(RAW_MATERIALS);

        buildTag(TITANIUM_INGOTS).add(TITANIUM_INGOT).copyTo(INGOTS);
        buildTag(SILVER_INGOTS).add(SILVER_INGOT).copyTo(INGOTS);
        buildTag(NIOBIUM_INGOTS).add(NIOBIUM_INGOT).copyTo(INGOTS);
        buildTag(SILICON_INGOTS).add(SILICON_INGOT).copyTo(INGOTS);
        buildTag(INGOTS).add(SLATESTEEL_INGOT, TUNGSTEN_SLATESTEEL_INGOT, RHENIUM_INGOT);

        buildTag(TITANIUM_NUGGETS).add(TITANIUM_NUGGET).copyTo(NUGGETS);
        buildTag(SILVER_NUGGETS).add(SILVER_NUGGET).copyTo(NUGGETS);
        buildTag(NIOBIUM_NUGGETS).add(NIOBIUM_NUGGET).copyTo(NUGGETS);
        buildTag(SLATESTEEL_NUGGETS).add(SLATESTEEL_NUGGET).copyTo(NUGGETS);

        buildTag(OLIVINE_GEMS).add(OLIVINE).copyTo(GEMS);
        buildTag(FLUORITE_GEMS).add(FLUORITE).copyTo(GEMS);
        buildTag(GEMS).add(PYROXENE);

        TagKey<Item> plateTag = ModResources.COMMON.itemTag("plates");
        buildTag(COPPER_PLATES).add(COPPER_PLATE).copyTo(plateTag);
        buildTag(GOLD_PLATES).add(GOLD_PLATE).copyTo(plateTag);
        buildTag(TITANIUM_PLATES).add(TITANIUM_PLATE).copyTo(plateTag);
        buildTag(SILVER_PLATES).add(SILVER_PLATE).copyTo(plateTag);
        buildTag(plateTag).add(NIOBIUM_PLATE, RHENIUM_PLATE, SILICON_PLATE, SLATESTEEL_PLATE, TUNGSTEN_SLATESTEEL_PLATE, POLYMER_PLATE, FLUOROPOLYMER_PLATE);

        final TagKey<Item> gearsTag = ModResources.COMMON.itemTag("gears");
        buildTag(TITANIUM_GEARS).add(TITANIUM_GEAR).copyTo(gearsTag);
        buildTag(gearsTag).add(SLATESTEEL_GEAR);

        buildTag(SWORDS).add(EPSILON_SWORD);
        buildTag(SHOVELS).add(EPSILON_SHOVEL);
        buildTag(AXES).add(EPSILON_AXE);
        buildTag(HOES).add(EPSILON_HOE);
        buildTag(TOOLS_SHEAR).add(EPSILON_SHEARS);
        buildTag(TOOLS_BRUSH).add(EPSILON_BRUSH);
        buildTag(TOOLS_FISHING_ROD).add(EPSILON_FISHING_ROD);
        buildTag(TOOLS_WRENCH).add(EPSILON_WRENCH);
        buildTag(TOOLS_IGNITER).add(EPSILON_LIGHTER);
        buildTag(CREEPER_IGNITERS).add(EPSILON_LIGHTER);

        buildTag(BEACON_PAYMENT_ITEMS).add(TITANIUM_INGOT, SILVER_INGOT, NIOBIUM_INGOT); // Only add this mod's ingots

        buildTag(TITANIUM_DUSTS).add(TITANIUM_DUST).copyTo(DUSTS);
        buildTag(SILVER_DUSTS).add(SILVER_DUST).copyTo(DUSTS);
        buildTag(NIOBIUM_DUSTS).add(NIOBIUM_DUST).copyTo(DUSTS);
        buildTag(SODIUM_DUSTS).add(SODIUM_DUST).copyTo(DUSTS);
        buildTag(SILICON_DUSTS).add(SILICON_DUST).copyTo(DUSTS);
        buildTag(SULFUR_DUSTS).add(SULFUR_DUST).copyTo(DUSTS);
        buildTag(DEEPSLATE_DUSTS).add(DEEPSLATE_DUST).copyTo(DUSTS);
        buildTag(DUSTS).add(CARBON_DUST, PHOSPHORUS_DUST, TUNGSTEN_SLATESTEEL_DUST, RHENIUM_DUST, PERIDOTITE_DUST);

        buildTag(CRUSHED_ORE_ITEMS).addHolders(List.copyOf(CRUSHED_ORES.values()));
        buildTag(WASHED_ORE_ITEMS).addHolders(List.copyOf(WASHED_ORES.values()));
        buildTag(ORE_CHUNK_ITEMS).addHolders(List.copyOf(ORE_CHUNKS.values()));
        buildTag(ORE_SOLUTION_ITEMS).addHolders(List.copyOf(ORE_SOLUTIONS.values()));
        buildTag(ORE_CRYSTAL_ITEMS).addHolders(List.copyOf(ORE_CRYSTALS.values()));

        buildTag(GREEN_GROUP_DYE_SOURCES).add(SHORT_GRASS, TALL_GRASS, FERN, LARGE_FERN).addTags(LEAVES, SAPLINGS);
        buildTag(APPLE_SAPLINGS).add(OAK_SAPLING, DARK_OAK_SAPLING);

        // Weapon equipment definitions
        buildTag(LIGHTWEIGHT_WEAPONS).add(WAYFINDER, SERENITY, MIRAGE);
        buildTag(SPECIALIST_WEAPONS).add(AURORA, STARGAZER);
        buildTag(EXPLOSIVE_WEAPONS).add(HANABI, DAYBREAK);
        buildTag(HEAVY_WEAPONS).add(NOVA);
        buildTag(ENERGY_PROJECTILE_WEAPONS).addTags(LIGHTWEIGHT_WEAPONS, SPECIALIST_WEAPONS, EXPLOSIVE_WEAPONS, HEAVY_WEAPONS);
        buildTag(MELEE_WEAPONS).add(EPSILON_SWORD, EPSILON_AXE);
        buildTag(WEAPON_EQUIPMENT).addTags(MELEE_WEAPONS, ENERGY_PROJECTILE_WEAPONS);

        // Tool equipment definitions
        buildTag(TOOL_EQUIPMENT).add(
                EPSILON_DRILL,
                EPSILON_SWORD,
                EPSILON_SHOVEL,
                EPSILON_AXE,
                EPSILON_HOE,
                EPSILON_WRENCH,
                EPSILON_SHEARS,
                EPSILON_BRUSH,
                EPSILON_FISHING_ROD,
                EPSILON_LIGHTER);
        buildTag(MODULAR_MINING_TOOLS).add(EPSILON_DRILL, EPSILON_SHOVEL, EPSILON_AXE, EPSILON_HOE);
        buildTag(MINING_TOOLS).addTag(MODULAR_MINING_TOOLS).add(EPSILON_SHEARS, EPSILON_WRENCH);

        // Armor definitions
        buildTag(WONDERLAND_ARMOR).add(WONDERLAND_HEAD, WONDERLAND_BODY, WONDERLAND_LEGS, WONDERLAND_FEET);

        // Global equipment
        buildTag(EQUIPMENT_ITEMS).addTags(TOOL_EQUIPMENT, WONDERLAND_ARMOR, WEAPON_EQUIPMENT);

        // Equipment-related tags
        buildTag(FREEZE_IMMUNE_WEARABLES).addTag(WONDERLAND_ARMOR);
        buildTag(ENERGY_UPGRADABLE_EQUIPMENT).addTags(WONDERLAND_ARMOR, TOOL_EQUIPMENT);
    }
}