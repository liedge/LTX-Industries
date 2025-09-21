package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.LTXITags.Blocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.Items.*;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
import static net.minecraft.tags.ItemTags.*;
import static net.minecraft.world.item.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class ItemTagsGen extends LimaTagsProvider.ItemTags
{
    ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper)
    {
        super(output, LTXIndustries.MODID, blockTags, lookupProvider, helper);
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

        copyTag(Blocks.NEON_LIGHTS, NEON_LIGHTS);
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

        buildTag(SWORDS).add(LTX_SWORD);
        buildTag(SHOVELS).add(LTX_SHOVEL);
        buildTag(AXES).add(LTX_AXE);
        buildTag(HOES).add(LTX_HOE);
        buildTag(TOOLS_SHEAR).add(LTX_SHEARS);
        buildTag(TOOLS_BRUSH).add(LTX_BRUSH);
        buildTag(TOOLS_FISHING_ROD).add(LTX_FISHING_ROD);
        buildTag(TOOLS_WRENCH).add(LTX_WRENCH);
        buildTag(TOOLS_IGNITER).add(LTX_LIGHTER);
        buildTag(CREEPER_IGNITERS).add(LTX_LIGHTER);

        buildTag(BEACON_PAYMENT_ITEMS).add(TITANIUM_INGOT, NIOBIUM_INGOT, SLATESTEEL_INGOT); // Only add this mod's ingots

        buildTag(DEEPSLATE_DUSTS).add(DEEPSLATE_DUST);
        buildTag(DUSTS).addTag(DEEPSLATE_DUSTS);

        buildTag(NEON_LIGHT_MATERIALS).add(GLOWSTONE, GLOW_INK_SAC, GLOW_BERRIES);

        buildTag(LIGHTWEIGHT_WEAPONS).add(SUBMACHINE_GUN);
        buildTag(SPECIALIST_WEAPONS).add(SHOTGUN, LINEAR_FUSION_RIFLE);
        buildTag(EXPLOSIVE_WEAPONS).add(GRENADE_LAUNCHER, ROCKET_LAUNCHER);
        buildTag(HEAVY_WEAPONS).add(HEAVY_PISTOL);
        buildTag(ENERGY_PROJECTILE_WEAPONS).addTags(LIGHTWEIGHT_WEAPONS, SPECIALIST_WEAPONS, EXPLOSIVE_WEAPONS, HEAVY_WEAPONS);
        buildTag(MELEE_WEAPONS).add(LTX_SWORD, LTX_AXE);
        buildTag(ALL_WEAPONS).addTags(MELEE_WEAPONS, ENERGY_PROJECTILE_WEAPONS);

        buildTag(MINING_TOOLS).add(LTX_DRILL, LTX_SHOVEL, LTX_AXE, LTX_HOE, LTX_WRENCH);
        buildTag(ALL_TOOLS).add(LTX_SHEARS, LTX_BRUSH, LTX_FISHING_ROD, LTX_LIGHTER).addTags(MINING_TOOLS, MELEE_WEAPONS);
    }
}