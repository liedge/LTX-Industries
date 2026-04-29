package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXITags.Blocks;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

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
        copyTag(Blocks.TITANIUM_ORES, TITANIUM_ORES);
        copyTag(Blocks.NIOBIUM_ORES, NIOBIUM_ORES);

        copyTag(Blocks.RAW_TITANIUM_STORAGE_BLOCKS, RAW_TITANIUM_STORAGE_BLOCKS);
        copyTag(Blocks.RAW_NIOBIUM_STORAGE_BLOCKS, RAW_NIOBIUM_STORAGE_BLOCKS);
        copyTag(Blocks.TITANIUM_STORAGE_BLOCKS, TITANIUM_STORAGE_BLOCKS);
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

        buildTag(RAW_TITANIUM_MATERIALS).add(RAW_TITANIUM);
        buildTag(RAW_NIOBIUM_MATERIALS).add(RAW_NIOBIUM);
        buildTag(RAW_MATERIALS).addTags(RAW_TITANIUM_MATERIALS, RAW_NIOBIUM_MATERIALS);

        buildTag(TITANIUM_INGOTS).add(TITANIUM_INGOT);
        buildTag(NIOBIUM_INGOTS).add(NIOBIUM_INGOT);
        buildTag(SLATESTEEL_INGOTS).add(SLATESTEEL_INGOT);
        buildTag(INGOTS).addTags(TITANIUM_INGOTS, NIOBIUM_INGOTS, SLATESTEEL_INGOTS);

        buildTag(TITANIUM_NUGGETS).add(TITANIUM_NUGGET);
        buildTag(NIOBIUM_NUGGETS).add(NIOBIUM_NUGGET);
        buildTag(SLATESTEEL_NUGGETS).add(SLATESTEEL_NUGGET);
        buildTag(NUGGETS).addTags(TITANIUM_NUGGETS, NIOBIUM_NUGGETS, SLATESTEEL_NUGGETS);

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

        buildTag(BEACON_PAYMENT_ITEMS).add(TITANIUM_INGOT, NIOBIUM_INGOT, SLATESTEEL_INGOT); // Only add this mod's ingots

        buildTag(DEEPSLATE_DUSTS).add(DEEPSLATE_DUST);
        buildTag(DUSTS).add(CARBON_DUST).addTag(DEEPSLATE_DUSTS);

        buildTag(GREEN_GROUP_DYE_SOURCES).add(SHORT_GRASS, TALL_GRASS, FERN, LARGE_FERN).addTags(LEAVES, SAPLINGS);
        buildTag(CARBON_SOURCES).add(CHARCOAL).addTag(COALS);
        buildTag(NEON_LIGHT_MATERIALS).add(GLOWSTONE, GLOW_INK_SAC, GLOW_BERRIES);
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