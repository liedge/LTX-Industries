package liedge.ltxindustries.data.generation;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import liedge.limacore.data.generation.LimaRecipeProvider;
import liedge.limacore.data.generation.recipe.LimaCustomRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapedRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.ResultCount;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.NeonLightColor;
import liedge.ltxindustries.integration.guideme.GuideMEIntegration;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.BuiltInOres;
import liedge.ltxindustries.lib.MachineLocation;
import liedge.ltxindustries.lib.upgrades.MutableUpgrades;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.recipe.*;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.bootstrap.LTXIRecipeModes;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static liedge.ltxindustries.LTXITags.Fluids.HYDROGEN_FLUIDS;
import static liedge.ltxindustries.LTXITags.Fluids.OXYGEN_FLUIDS;
import static liedge.ltxindustries.LTXITags.Items.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIUpgrades.*;
import static liedge.ltxindustries.registry.game.LTXIFluids.*;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
import static net.minecraft.world.item.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class RecipesGen extends LimaRecipeProvider
{
    static final class Runner extends RecipeProvider.Runner
    {
        Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output)
        {
            return new RecipesGen(registries, output);
        }

        @Override
        public String getName()
        {
            return "LTXI Recipes";
        }
    }

    // Patterns
    private final String[] stairsPattern = {"#  ", "## ", "###"};
    private final String[] slabPattern = {"###"};
    private final String[] wallPattern = {"###", "###"};

    private RecipesGen(HolderLookup.Provider registries, RecipeOutput output)
    {
        super(registries, output, LTXIndustries.RESOURCES);
    }

    @Override
    protected void buildRecipes()
    {
        craftingTableRecipes();
        cookingRecipes();
        stonecuttingRecipes();
        oreProcessingRecipes();
        fabricatingRecipes();
        grindingRecipes();
        pressingRecipes();
        arcSmeltingRecipes();
        sievingRecipes();
        electroCentrifugingRecipes();
        mixingRecipes();
        energizingRecipes();
        chemLabRecipes();
        assemblingRecipes();
        geoSynthesisRecipes();
        scrubbingRecipes();
        gardenRecipes();
    }

    private void craftingTableRecipes()
    {
        shaped(PERIDOTITE_STAIRS, 4).input('#', PERIDOTITE).patterns(stairsPattern).category(CraftingBookCategory.BUILDING).save(output);
        shaped(PERIDOTITE_SLAB, 6).input('#', PERIDOTITE).patterns(slabPattern).category(CraftingBookCategory.BUILDING).save(output);
        shaped(PERIDOTITE_WALL, 6).input('#', PERIDOTITE).patterns(wallPattern).save(output);
        shaped(POLISHED_PERIDOTITE, 4).input('#', PERIDOTITE).patterns("##", "##").save(output);
        shaped(POLISHED_PERIDOTITE_STAIRS, 4).input('#', POLISHED_PERIDOTITE).patterns(stairsPattern).category(CraftingBookCategory.BUILDING).save(output);
        shaped(POLISHED_PERIDOTITE_SLAB, 6).input('#', POLISHED_PERIDOTITE).patterns(slabPattern).category(CraftingBookCategory.BUILDING).save(output);
        shaped(POLISHED_PERIDOTITE_WALL, 6).input('#', POLISHED_PERIDOTITE).patterns(wallPattern).save(output);

        nineStorageRecipes(RAW_TITANIUM, RAW_TITANIUM_BLOCK);
        nineStorageRecipes(RAW_SILVER, RAW_SILVER_BLOCK);
        nineStorageRecipes(RAW_NIOBIUM, RAW_NIOBIUM_BLOCK);

        nuggetIngotBlockRecipes("titanium", TITANIUM_NUGGET, TITANIUM_INGOT, TITANIUM_BLOCK);
        nuggetIngotBlockRecipes("silver", SILVER_NUGGET, SILVER_INGOT, SILVER_BLOCK);
        nuggetIngotBlockRecipes("niobium", NIOBIUM_NUGGET, NIOBIUM_INGOT, NIOBIUM_BLOCK);
        nuggetIngotBlockRecipes("slatesteel", SLATESTEEL_NUGGET, SLATESTEEL_INGOT, SLATESTEEL_BLOCK);

        shaped(TITANIUM_PANEL, 16).input('m', TITANIUM_INGOTS).input('p', POLYMER).patterns("mpm", "p p", "mpm").category(CraftingBookCategory.BUILDING).save(output, "titanium_panel_p");
        shaped(TITANIUM_PANEL, 32).input('m', TITANIUM_INGOTS).input('p', FLUOROPOLYMER).patterns("mpm", "p p", "mpm").category(CraftingBookCategory.BUILDING).save(output, "titanium_panel_fp");
        shaped(SLATESTEEL_PANEL, 16).input('m', SLATESTEEL_INGOTS).input('p', POLYMER).patterns("mpm", "p p", "mpm").category(CraftingBookCategory.BUILDING).save(output, "slatesteel_panel_p");
        shaped(SLATESTEEL_PANEL, 32).input('m', SLATESTEEL_INGOTS).input('p', FLUOROPOLYMER).patterns("mpm", "p p", "mpm").category(CraftingBookCategory.BUILDING).save(output, "slatesteel_panel_fp");

        shaped(TITANIUM_GEAR).input('i', TITANIUM_INGOTS).input('n', NUGGETS_IRON).patterns("ini", "n n", "ini").save(output);
        shaped(SLATESTEEL_GEAR).input('i', SLATESTEEL_INGOTS).input('n', NUGGETS_IRON).patterns("ini", "n n", "ini").save(output);
        shaped(MACHINE_HOUSING).input('i', INGOTS_IRON).input('g', TITANIUM_GEARS).input('l', LEVER).patterns("igi", "glg", "igi").save(output);
        shaped(SMALL_VOLTAIC_CELL).input('t', TITANIUM_INGOTS).input('s', SILVER_INGOTS).input('c', STORAGE_BLOCKS_COPPER).input('g', GLASS_BLOCKS_CHEAP).patterns("tst", "gcg", "gsg").save(output);
        shaped(CIRCUIT_BOARD).input('p', PAPER).input('r', REPEATER).input('c', INGOTS_COPPER).input('g', GOLD_NUGGET).patterns("gcg", "crc", "ppp").save(output);
        shaped(T1_CIRCUIT).input('b', CIRCUIT_BOARD).input('e', SMALL_VOLTAIC_CELL).input('c', INGOTS_COPPER).input('t', TITANIUM_INGOTS).patterns("ece", "cbc", "tct").save(output);

        shaped(EMPTY_UPGRADE_MODULE, 2).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('g', TITANIUM_GLASS).input('s', SLATESTEEL_INGOTS).patterns("gsg", "tct").save(output);
        shaped(EMPTY_FABRICATION_BLUEPRINT, 2).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('g', TITANIUM_GLASS).input('s', PAPER).patterns("gsg", "tct").save(output);
        shaped(ITEMS_IO_CONFIG_CARD).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('m', CHESTS_WOODEN).patterns(" m ", "tct", " t ").save(output);
        shaped(ENERGY_IO_CONFIG_CARD).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('m', SODIUM_DUSTS).patterns(" m ", "tct", " t ").save(output);
        shaped(FLUIDS_IO_CONFIG_CARD).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('m', BUCKET).patterns(" m ", "tct", " t ").save(output);

        shapeless(GUIDE_TABLET).condition(new ModLoadedCondition(GuideMEIntegration.MODID)).input(BOOK).input(SMALL_VOLTAIC_CELL).save(output);
        shaped(defaultUpgradableItem(EPSILON_WRENCH)).input('t', TITANIUM_INGOTS).input('c', SMALL_VOLTAIC_CELL).patterns("t t", " c ", " t ").save(output);

        // Machine recipes
        shaped(ENERGY_CELL_ARRAY).input('h', MACHINE_HOUSING).input('t', INGOTS_COPPER).input('c', T1_CIRCUIT).input('e', SMALL_VOLTAIC_CELL).patterns("ttt", "ehe", "ece").save(output);
        shaped(PORTABLE_TANK).input('t', TITANIUM_INGOTS).input('b', BUCKET).input('g', GLASS_BLOCKS_CHEAP).input('G', TITANIUM_GEARS).patterns(" t ", "gbg", " G ").save(output);
        shaped(DIGITAL_FURNACE).input('h', MACHINE_HOUSING).input('c', T1_CIRCUIT).input('b', Items.BRICKS).input('0', INGOTS_COPPER).input('k', FURNACE).patterns(" k ", "bhb", "0c0").save(output);
        shaped(DIGITAL_SMOKER).input('h', MACHINE_HOUSING).input('c', T1_CIRCUIT).input('b', Items.BRICKS).input('0', INGOTS_COPPER).input('k', SMOKER).patterns(" k ", "bhb", "0c0").save(output);
        shaped(DIGITAL_BLAST_FURNACE).input('h', MACHINE_HOUSING).input('c', T1_CIRCUIT).input('b', Items.BRICKS).input('0', INGOTS_COPPER).input('k', BLAST_FURNACE).patterns(" k ", "bhb", "0c0").save(output);
        shaped(GRINDER).input('h', MACHINE_HOUSING).input('t', TITANIUM_INGOTS).input('g', TITANIUM_GEARS).input('c', T1_CIRCUIT).input('i', INGOTS_IRON)
                .patterns("t t", "gig", "chc").save(output);
        shaped(MATERIAL_PRESS).input('h', MACHINE_HOUSING).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('s', SLATESTEEL_INGOTS).input('g', SLATESTEEL_GEARS)
                .patterns("gtg", " s ", "chc").save(output);
        shaped(ARC_FURNACE).input('h', MACHINE_HOUSING).input('c', T1_CIRCUIT).input('a', BLAST_FURNACE).input('o', OBSIDIANS_NORMAL).input('0', CARBON_DUSTS).patterns("000", "oao", "chc").save(output);
        shaped(HYDROSIEVE).input('h', MACHINE_HOUSING).input('t', TITANIUM_INGOTS).input('g', TITANIUM_GEARS).input('c', T1_CIRCUIT).input('b', IRON_BARS).patterns("bbb", "tgt", "chc").save(output);
        shaped(ELECTROCENTRIFUGE).input('h', MACHINE_HOUSING).input('g', TITANIUM_GEARS).input('c', T1_CIRCUIT).input('0', TITANIUM_GLASS).input('e', SMALL_VOLTAIC_CELL).patterns("0 0", "ege", "chc").save(output);
        shaped(MIXER).input('h', MACHINE_HOUSING).input('t', TITANIUM_INGOTS).input('g', TITANIUM_GEARS).input('c', T1_CIRCUIT).input('0', TITANIUM_GLASS).patterns("ttt", "0g0", "chc").save(output);
        shaped(VOLTAIC_INJECTOR).input('h', MACHINE_HOUSING).input('s', SLATESTEEL_PLATES).input('c', T1_CIRCUIT).input('o', OLIVINE_GEMS).input('e', SMALL_VOLTAIC_CELL).patterns(" o ", "ese", "chc").save(output);
        shaped(CHEM_LAB).input('h', MACHINE_HOUSING).input('t', TITANIUM_INGOTS).input('c', T1_CIRCUIT).input('a', emptyPortableTank()).input('0', TITANIUM_GLASS).patterns("ttt", "0a0", "chc").save(output);
        shaped(ASSEMBLER).input('h', MACHINE_HOUSING).input('g', SLATESTEEL_GEARS).input('c', T1_CIRCUIT).input('e', SMALL_VOLTAIC_CELL).input('0', TITANIUM_GLASS)
                .patterns("e e", "0g0", "chc").save(output);
        shaped(GEO_SYNTHESIZER).input('h', MACHINE_HOUSING).input('c', T1_CIRCUIT).input('a', DIAMOND_PICKAXE).input('s', BUCKET).patterns("sas", "chc").save(output);
        shaped(FABRICATOR).input('h', MACHINE_HOUSING).input('g', TITANIUM_GEARS).input('s', SLATESTEEL_GEARS).input('c', T2_CIRCUIT).input('o', GEMS_DIAMOND).input('e', MEDIUM_VOLTAIC_CELL)
                .patterns("gog", "ese", "chc").save(output);
        shaped(AUTO_FABRICATOR).input('h', MACHINE_HOUSING).input('g', TITANIUM_GEARS).input('s', SLATESTEEL_GEARS).input('c', T3_CIRCUIT).input('o', OPTICAL_TECH_PART).input('e', MEDIUM_VOLTAIC_CELL)
                .patterns("gog", "ese", "chc").save(output);
        shaped(UPGRADE_STATION).input('t', TITANIUM_INGOTS).input('g', TITANIUM_GEARS).input('s', SLATESTEEL_PLATES).input('p', TITANIUM_PLATES).input('0', POLYMERS)
                .patterns("psp", "0g0", "ttt").save(output);
        shaped(REPAIR_STATION).input('h', MACHINE_HOUSING).input('t', TITANIUM_PLATES).input('s', SLATESTEEL_STORAGE_BLOCKS).input('c', T2_CIRCUIT).input('g', SLATESTEEL_GEARS).input('e', LARGE_VOLTAIC_CELL)
                .patterns("tst", "geg", "chc").save(output);

        // Generators
        shaped(PORTABLE_GENERATOR).input('h', MACHINE_HOUSING).input('c', T1_CIRCUIT).input('e', SMALL_VOLTAIC_CELL).input('0', FURNACE).input('b', IRON_BARS).patterns("000", "bhb", "ece").save(output);
        shaped(SOLAR_PANEL).input('h', MACHINE_HOUSING).input('s', SILICON_PLATES).input('c', T1_CIRCUIT).input('e', SMALL_VOLTAIC_CELL).input('g', TITANIUM_GLASS).patterns("sss", "ghg", "ece").save(output);

        // Upgrade modules
        upgradeShaped(POWER_TIERS, 1, builder -> builder
                .input('c', T1_CIRCUIT).input('s', SLATESTEEL_PLATES).input('g', SLATESTEEL_GEARS).input('e', SMALL_VOLTAIC_CELL).patterns("ese", "cmc", "gsg"));
        upgradeShaped(POWER_TIERS, 2, builder -> builder
                .input('c', T2_CIRCUIT).input('t', TITANIUM_GEARS).input('g', SLATESTEEL_GEARS).input('e', MEDIUM_VOLTAIC_CELL).patterns("ege", "cmc", "gtg"));
        upgradeShaped(GEO_SYNTHESIZER_PARALLEL, 1, builder -> builder
                .input('w', WATER_BUCKET).input('l', LAVA_BUCKET).input('c', T1_CIRCUIT).input('t', TITANIUM_INGOTS).patterns("twt", "cmc", "tlt"));
        upgradeShaped(GEO_SYNTHESIZER_PARALLEL, 2, builder -> builder
                .input('w', PACKED_ICE).input('l', MAGMA_BLOCK).input('c', T2_CIRCUIT).input('g', TITANIUM_GEARS).patterns("wgl", "cmc", "lgw"));
        upgradeShaped(GEO_SYNTHESIZER_PARALLEL, 3, builder -> builder
                .input('w', BLUE_ICE).input('l', MAGMA_BLOCK).input('c', T3_CIRCUIT).input('g', SLATESTEEL_GEARS).patterns("wgl", "cmc", "lgw"));

        upgradeShaped(ECA_CAPACITY_UPGRADE, 1, builder -> builder.input('c', T1_CIRCUIT).input('e', SMALL_VOLTAIC_CELL).patterns(" c ", "eme", " c "));
        upgradeShaped(ECA_CAPACITY_UPGRADE, 2, builder -> builder.input('c', T2_CIRCUIT).input('e', SMALL_VOLTAIC_CELL).patterns(" c ", "eme", " c "));

        upgradeShaped(PORTABLE_TANK_UPGRADE, 1, builder -> builder.input('g', TITANIUM_GLASS).input('b', BUCKET).patterns(" g ", "gmg", " b "));
        upgradeShaped(PORTABLE_TANK_UPGRADE, 2, builder -> builder.input('g', TITANIUM_GLASS).input('t', TITANIUM_INGOTS).patterns("tgt", "gmg", "tgt"));

        upgradeShaped(ORE_PROCESS_2, 1, builder -> builder.input('s', SLATESTEEL_PLATES).input('g', SLATESTEEL_GEARS).input('b', IRON_BARS).patterns("sbs", "bmb", "gbg"));

        NEON_LIGHTS.forEach((color, holder) -> shaped(holder, 4).input('d', neonLightDye(color)).input('g', GLOWSTONE).patterns("dg", "gd").save(output));
    }

    private void cookingRecipes()
    {
        oreSmeltBlast("raw_titanium_materials", Ingredient.of(items.getOrThrow(RAW_TITANIUM_MATERIALS)), TITANIUM_INGOT);
        oreSmeltBlast("titanium_ores", Ingredient.of(items.getOrThrow(TITANIUM_ORES)), TITANIUM_INGOT);

        oreSmeltBlast("raw_silver_materials", Ingredient.of(items.getOrThrow(RAW_SILVER_MATERIALS)), SILVER_INGOT);
        oreSmeltBlast("silver_ores", Ingredient.of(items.getOrThrow(SILVER_ORES)), SILVER_INGOT);

        oreSmeltBlast("raw_olivine_materials", Ingredient.of(items.getOrThrow(RAW_OLIVINE_MATERIALS)), OLIVINE);
        oreSmeltBlast("raw_fluorite_materials", Ingredient.of(items.getOrThrow(RAW_FLUORITE_MATERIALS)), FLUORITE);

        oreSmeltBlast("raw_niobium_materials", Ingredient.of(items.getOrThrow(RAW_NIOBIUM_MATERIALS)), NIOBIUM_INGOT);
        oreSmeltBlast("niobium_ores", Ingredient.of(items.getOrThrow(NIOBIUM_ORES)), NIOBIUM_INGOT);

        // Dust smelting
        smelting(TITANIUM_INGOT).input(TITANIUM_DUSTS).save(output, "smelt_titanium_dust");
        smelting(SILVER_INGOT).input(SILVER_DUSTS).save(output, "smelt_silver_dust");
        smelting(NIOBIUM_INGOT).input(NIOBIUM_DUSTS).save(output, "smelt_niobium_dust");
        smelting(SLATESTEEL_INGOT).input(SLATESTEEL_DUSTS).save(output, "smelt_slatesteel_dust");
    }

    private void stonecuttingRecipes()
    {
        stonecuttingInterchange(List.of(TITANIUM_PANEL, SMOOTH_TITANIUM_PANEL, TILED_TITANIUM_PANEL));
        stonecuttingInterchange(List.of(SLATESTEEL_PANEL, SMOOTH_SLATESTEEL_PANEL, TILED_SLATESTEEL_PANEL));

        stonecutting(PERIDOTITE_STAIRS).input(PERIDOTITE).category(CraftingBookCategory.BUILDING).save(output);
        stonecutting(PERIDOTITE_SLAB, 2).input(PERIDOTITE).category(CraftingBookCategory.BUILDING).save(output);
        stonecutting(PERIDOTITE_WALL).input(PERIDOTITE).save(output);
        stonecutting(POLISHED_PERIDOTITE).input(PERIDOTITE).category(CraftingBookCategory.BUILDING).save(output);
        stonecutting(POLISHED_PERIDOTITE_STAIRS).input(POLISHED_PERIDOTITE).category(CraftingBookCategory.BUILDING).save(output);
        stonecutting(POLISHED_PERIDOTITE_SLAB, 2).input(POLISHED_PERIDOTITE).category(CraftingBookCategory.BUILDING).save(output);
        stonecutting(POLISHED_PERIDOTITE_WALL).input(POLISHED_PERIDOTITE).save(output);
    }

    private void oreProcessingRecipes()
    {
        oreProcessCooking(BuiltInOres.COAL, COAL, 2);
        oreProcessCooking(BuiltInOres.COPPER, COPPER_INGOT, 1);
        oreProcessCooking(BuiltInOres.IRON, IRON_INGOT, 1);
        oreProcessCooking(BuiltInOres.LAPIS, LAPIS_LAZULI, 4);
        oreProcessCooking(BuiltInOres.REDSTONE, REDSTONE, 6);
        oreProcessCooking(BuiltInOres.GOLD, GOLD_INGOT, 1);
        oreProcessCooking(BuiltInOres.DIAMOND, DIAMOND, 1);
        oreProcessCooking(BuiltInOres.EMERALD, EMERALD, 1);
        oreProcessCooking(BuiltInOres.QUARTZ, QUARTZ, 2);
        oreProcessCooking(BuiltInOres.TITANIUM, TITANIUM_INGOT, 1);
        oreProcessCooking(BuiltInOres.SILVER, SILVER_INGOT, 1);
        oreProcessCooking(BuiltInOres.OLIVINE, OLIVINE, 1);
        oreProcessCooking(BuiltInOres.FLUORITE, FLUORITE, 2);
        oreProcessCooking(BuiltInOres.NIOBIUM, NIOBIUM_INGOT, 1);

        Holder<RecipeMode> mode = registries.holderOrThrow(LTXIRecipeModes.ORE_PROCESSING);

        for (BuiltInOres ore : BuiltInOres.values())
        {
            Holder<Item> crushedOre = CRUSHED_ORES.get(ore);
            Holder<Item> washedOre = WASHED_ORES.get(ore);
            Holder<Item> oreChunk = ORE_CHUNKS.get(ore);
            Holder<Item> oreSolution = ORE_SOLUTIONS.get(ore);
            Holder<Item> oreCrystal = ORE_CRYSTALS.get(ore);

            ItemResult s3Byproduct = switch (ore)
            {
                case TITANIUM -> ItemResult.of(TUNGSTEN_TRIOXIDE, ResultCount.exactlyRandom(1, 0.1f));
                case OLIVINE -> ItemResult.of(PYROXENE, ResultCount.exactlyRandom(1, 0.075f));
                default -> null;
            };
            ItemResult s5Byproduct = switch (ore)
            {
                case COPPER -> ItemResult.of(RHENIUM_7_OXIDE, ResultCount.exactlyRandom(1, 0.05f));
                case OLIVINE -> ItemResult.of(PYROXENE, ResultCount.exactlyRandom(1, 0.05f));
                default -> null;
            };

            sieving()
                    .needsMode(mode)
                    .input(crushedOre.value())
                    .water(1000)
                    .output(ItemResult.of(washedOre))
                    .output(ItemResult.of(washedOre, ResultCount.exactlyRandom(1, 0.5f)))
                    .save(output);

            energizing()
                    .needsMode(mode)
                    .input(washedOre.value())
                    .output(ItemResult.of(oreChunk))
                    .output(ItemResult.of(oreChunk, ResultCount.exactlyRandom(1, 0.5f)))
                    .tryOutput(s3Byproduct)
                    .save(output);

            chemLab()
                    .needsMode(mode)
                    .input(oreChunk.value())
                    .fluidInput(SULFURIC_ACID, 125)
                    .output(ItemResult.of(oreSolution))
                    .output(ItemResult.of(oreSolution, ResultCount.exactlyRandom(1, 0.5f)))
                    .save(output);

            electroCentrifuging()
                    .needsMode(mode)
                    .input(oreSolution.value())
                    .fluidInput(HYDROCHLORIC_ACID, 125)
                    .output(ItemResult.of(oreCrystal))
                    .output(ItemResult.of(oreCrystal, ResultCount.exactlyRandom(1, 0.5f)))
                    .tryOutput(s5Byproduct)
                    .save(output);
        }
    }

    private void fabricatingRecipes()
    {
        // Default modules
        defaultModuleFabricating(EPSILON_SHOVEL_DEFAULT, EPSILON_SHOVEL);
        defaultModuleFabricating(EPSILON_WRENCH_DEFAULT, EPSILON_WRENCH);
        defaultModuleFabricating(EPSILON_MELEE_DEFAULT, EPSILON_SWORD, EPSILON_AXE);
        defaultModuleFabricating(TREE_VEIN_MINE, EPSILON_AXE);
        defaultModuleFabricating(SERENITY_DEFAULT, SERENITY);
        defaultModuleFabricating(MIRAGE_DEFAULT, MIRAGE);
        defaultModuleFabricating(AURORA_DEFAULT, AURORA);
        defaultModuleFabricating(STARGAZER_DEFAULT, STARGAZER);
        defaultModuleFabricating(NOVA_DEFAULT, NOVA);
        defaultModuleFabricating(HEAD_DEFAULT, WONDERLAND_HEAD);
        defaultModuleFabricating(BODY_DEFAULT, WONDERLAND_BODY);
        defaultModuleFabricating(LEGS_DEFAULT, WONDERLAND_LEGS);
        defaultModuleFabricating(FEET_DEFAULT, WONDERLAND_FEET);
        defaultModuleFabricating(ARMOR_DEFENSE, WONDERLAND_HEAD, WONDERLAND_BODY, WONDERLAND_LEGS, WONDERLAND_FEET);
        defaultModuleFabricating(ARMOR_PASSIVE_SHIELD, WONDERLAND_HEAD, WONDERLAND_BODY, WONDERLAND_LEGS, WONDERLAND_FEET);

        fabricating(100_000_000)
                .input(TITANIUM_PLATES, 16)
                .input(RHENIUM_INGOTS, 8)
                .input(ELITE_CIRCUIT_BOARD)
                .input(T4_CIRCUIT, 4)
                .input(LARGE_VOLTAIC_CELL, 3)
                .input(SCULK_CHEMICAL, 24)
                .output(ItemResult.of(T5_CIRCUIT))
                .group("0/circuit")
                .save(output);

        String machineGroup = "0/machine";
        fabricating(5_000_000)
                .input(MACHINE_HOUSING)
                .input(POLYMERS, 8)
                .input(T3_CIRCUIT, 2)
                .input(emptyPortableTank())
                .input(SLATESTEEL_GEARS, 2)
                .input(IRON_BARS, 4)
                .output(ItemResult.of(ATMOSPHERIC_SCRUBBER))
                .group(machineGroup)
                .save(output);
        fabricating(2_500_000)
                .input(MACHINE_HOUSING, 2)
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_GLASS, 8)
                .input(emptyPortableTank())
                .input(TITANIUM_GEARS, 2)
                .output(ItemResult.of(DIGITAL_GARDEN))
                .group(machineGroup)
                .save(output);

        String turretGroup = "0/turret";
        fabricating(2_500_000)
                .input(MACHINE_HOUSING, 2)
                .input(T3_CIRCUIT, 2)
                .input(OPTICAL_TECH_PART, 2)
                .input(MEDIUM_VOLTAIC_CELL, 8)
                .input(SODIUM_DUSTS, 32)
                .input(TITANIUM_GEARS, 4)
                .input(SLATESTEEL_GEARS, 2)
                .output(ItemResult.of(ARC_TURRET))
                .group(turretGroup)
                .save(output);
        fabricating(5_000_000)
                .input(MACHINE_HOUSING, 2)
                .input(T3_CIRCUIT, 2)
                .input(OPTICAL_TECH_PART, 2)
                .input(IMPULSE_TECH_PART, 2)
                .input(PHOSPHORUS_DUSTS, 16)
                .input(TITANIUM_GEARS, 4)
                .input(SLATESTEEL_GEARS, 2)
                .output(ItemResult.of(ROCKET_TURRET))
                .group(turretGroup)
                .save(output);
        fabricating(20_000_000)
                .input(MACHINE_HOUSING, 2)
                .input(TUNGSTEN_SLATESTEEL_INGOTS, 8)
                .input(T4_CIRCUIT)
                .input(OPTICAL_TECH_PART, 2)
                .input(LASER_TECH_PART, 3)
                .input(TITANIUM_GEARS, 4)
                .input(SLATESTEEL_GEARS, 2)
                .output(ItemResult.of(RAILGUN_TURRET))
                .group(turretGroup)
                .save(output);

        // Tools fabricating
        final String toolFabGroup = "1/tools";
        equipmentFabricating(EPSILON_DRILL, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL)
                .input(TITANIUM_INGOTS, 24)
                .input(OLIVINE_GEMS, 4)
                .input(SLATESTEEL_INGOTS, 8)
                .input(SLATESTEEL_GEARS, 2)
                .input(TITANIUM_GEARS));
        equipmentFabricating(EPSILON_SWORD, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL)
                .input(TITANIUM_INGOTS, 16)
                .input(OLIVINE_GEMS, 8)
                .input(SLATESTEEL_GEARS, 2)
                .input(TITANIUM_GEARS));
        equipmentFabricating(EPSILON_SHOVEL, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL)
                .input(TITANIUM_INGOTS, 16)
                .input(OLIVINE_GEMS, 4)
                .input(SLATESTEEL_GEARS, 2)
                .input(TITANIUM_GEARS));
        equipmentFabricating(EPSILON_AXE, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL)
                .input(TITANIUM_INGOTS, 24)
                .input(OLIVINE_GEMS, 8)
                .input(SLATESTEEL_GEARS, 2)
                .input(TITANIUM_GEARS));
        equipmentFabricating(EPSILON_HOE, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL)
                .input(TITANIUM_INGOTS, 16)
                .input(OLIVINE_GEMS, 4)
                .input(SLATESTEEL_GEARS, 2)
                .input(TITANIUM_GEARS));
        equipmentFabricating(EPSILON_SHEARS, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL)
                .input(TITANIUM_INGOTS, 12)
                .input(SLATESTEEL_PLATES, 2)
                .input(TITANIUM_GEARS));
        equipmentFabricating(EPSILON_BRUSH, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 12)
                .input(SLATESTEEL_GEARS, 2)
                .input(TITANIUM_GEARS)
                .input(FEATHER, 4));
        equipmentFabricating(EPSILON_FISHING_ROD, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 12)
                .input(TITANIUM_GEARS, 2)
                .input(SLATESTEEL_GEARS)
                .input(STRINGS, 4));
        equipmentFabricating(EPSILON_LIGHTER, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 12)
                .input(SLATESTEEL_PLATES, 4)
                .input(PHOSPHORUS_DUSTS, 4));

        // Weapons fabrication
        String weaponFabGroup = "1/weapon";
        equipmentFabricating(WAYFINDER, weaponFabGroup + ".05", 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 8)
                .input(TITANIUM_GEARS)
                .input(MEDIUM_VOLTAIC_CELL));
        equipmentFabricating(SERENITY, weaponFabGroup + ".11", 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 16)
                .input(POLYMERS, 16)
                .input(OLIVINE_GEMS, 24));
        equipmentFabricating(MIRAGE, weaponFabGroup + ".13", 2_500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 24)
                .input(POLYMERS, 24)
                .input(SLATESTEEL_INGOTS, 8)
                .input(OLIVINE_GEMS, 48));
        equipmentFabricating(AURORA, weaponFabGroup + ".21", 10_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 32)
                .input(FLUOROPOLYMER, 24)
                .input(SLATESTEEL_INGOTS, 16)
                .input(LASER_TECH_PART, 2));
        equipmentFabricating(HANABI, weaponFabGroup + ".33", 100_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 24)
                .input(TUNGSTEN_SLATESTEEL_INGOTS, 16)
                .input(IMPULSE_TECH_PART, 4)
                .input(TITANIUM_GLASS, 20));
        equipmentFabricating(STARGAZER, weaponFabGroup + ".37", 50_000_000, builder -> builder
                .input(T3_CIRCUIT, 3)
                .input(OPTICAL_TECH_PART, 4)
                .input(TITANIUM_INGOTS, 32)
                .input(FLUOROPOLYMER, 20)
                .input(SLATESTEEL_INGOTS, 8)
                .input(LASER_TECH_PART, 3));
        equipmentFabricating(DAYBREAK, weaponFabGroup + ".41", 100_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(OPTICAL_TECH_PART, 2)
                .input(TITANIUM_INGOTS, 48)
                .input(TUNGSTEN_SLATESTEEL_INGOTS, 20)
                .input(IMPULSE_TECH_PART, 4));
        equipmentFabricating(NOVA, weaponFabGroup + ".77", 250_000_000, builder -> builder
                .input(T5_CIRCUIT)
                .input(OPTICAL_TECH_PART)
                .input(TITANIUM_INGOTS, 32)
                .input(SLATESTEEL_INGOTS, 24)
                .input(RHENIUM_INGOTS, 8)
                .input(LASER_TECH_PART, 8));

        // Bodysuit fabrication
        final String armorFabGroup = "1/armor";
        UnaryOperator<FabricatingBuilder> armorBase = builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 32)
                .input(SLATESTEEL_PLATES, 16)
                .input(FLUOROPOLYMER_SHEET, 24)
                .input(SILICONE_RUBBER, 8)
                .input(MEDIUM_VOLTAIC_CELL, 2);
        equipmentFabricating(WONDERLAND_HEAD, armorFabGroup + ".1", 20_000_000, builder -> armorBase.apply(builder).input(OPTICAL_TECH_PART, 2));
        equipmentFabricating(WONDERLAND_BODY, armorFabGroup + ".2", 20_000_000, armorBase);
        equipmentFabricating(WONDERLAND_LEGS, armorFabGroup + ".3", 20_000_000, armorBase);
        equipmentFabricating(WONDERLAND_FEET, armorFabGroup + ".4", 20_000_000, armorBase);

        final String upgradeGroup = "upgrade/tool";
        upgradeFabricating(upgradeGroup, EQUIPMENT_ENERGY_UPGRADE, 1, 100_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_PLATES, 4)
                .input(POLYMERS, 4)
                .input(MEDIUM_VOLTAIC_CELL, 2));
        upgradeFabricating(upgradeGroup, EQUIPMENT_ENERGY_UPGRADE, 2, 250_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 8)
                .input(FLUOROPOLYMER, 8)
                .input(SILICONE_RUBBER, 4)
                .input(MEDIUM_VOLTAIC_CELL, 4));
        upgradeFabricating(upgradeGroup, EQUIPMENT_ENERGY_UPGRADE, 3, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 12)
                .input(FLUOROPOLYMER, 12)
                .input(SILICONE_RUBBER, 8)
                .input(LARGE_VOLTAIC_CELL));
        upgradeFabricating(upgradeGroup, EQUIPMENT_ENERGY_UPGRADE, 4, 1_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_PLATES, 16)
                .input(FLUOROPOLYMER, 16)
                .input(SILICONE_RUBBER, 12)
                .input(LARGE_VOLTAIC_CELL, 2));
        upgradeFabricating(upgradeGroup, EPSILON_FISHING_LURE, 1, 100_000, builder -> builder
                .input(T1_CIRCUIT)
                .input(STRINGS, 4)
                .input(COD, 2));
        upgradeFabricating(upgradeGroup, EPSILON_FISHING_LURE, 2, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(STRINGS, 8)
                .input(COD, 4)
                .input(SALMON, 2));
        upgradeFabricating(upgradeGroup, EPSILON_FISHING_LURE, 3, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOTS, 2)
                .input(STRINGS, 8)
                .input(PUFFERFISH, 2));
        upgradeFabricating(upgradeGroup, EPSILON_FISHING_LURE, 4, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(SLATESTEEL_INGOTS, 2)
                .input(STRINGS, 4)
                .input(CARBON_DUSTS, 12)
                .input(LTX_LIME_PIGMENT, 6)
                .input(GEMS_PRISMARINE, 2));
        upgradeFabricating(upgradeGroup, EPSILON_FISHING_LURE, 5, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(SLATESTEEL_INGOTS, 4)
                .input(POLYMER, 8)
                .input(STRINGS, 8)
                .input(CARBON_DUSTS, 24)
                .input(LTX_LIME_PIGMENT, 12)
                .input(HEART_OF_THE_SEA));
        upgradeFabricating(upgradeGroup, EPSILON_OMNI_DRILL, 1, 20_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_PLATES, 32)
                .input(SLATESTEEL_GEARS, 4)
                .input(TUNGSTEN_SLATESTEEL_INGOTS, 16)
                .input(LASER_TECH_PART));
        upgradeFabricating(upgradeGroup, ORE_VEIN_MINE, 1, 50_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(OPTICAL_TECH_PART)
                .input(TITANIUM_GEARS, 2)
                .input(SLATESTEEL_GEARS));
        upgradeFabricating(upgradeGroup, TOOL_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(FLUOROPOLYMER, 4)
                .input(SILICONE_RUBBER, 8)
                .input(SCULK_CHEMICAL, 4));

        UnaryOperator<FabricatingBuilder> directDrops = builder -> builder
                .input(T3_CIRCUIT, 3)
                .input(TITANIUM_INGOTS, 16)
                .input(SLATESTEEL_INGOTS, 8)
                .input(CHORUS_CHEMICAL, 8)
                .input(ENDER_PEARLS, 8);
        upgradeFabricating(upgradeGroup, EQUIPMENT_BLOCK_DROPS_CAPTURE, 1, 15_000_000, directDrops);
        upgradeFabricating("combat", NO_ANGER_ATTACKS, 1, 1_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(NANO_LOGIC_CORE)
                .input(CHORUS_CHEMICAL, 16)
                .input(SCULK_CHEMICAL, 8)
                .input(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY), POTION)));
        upgradeFabricating("combat", MOB_DROPS_CAPTURE, 1, 15_000_000, directDrops);

        upgradeFabricating("eum/weapon", WEAPON_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(FLUOROPOLYMER, 6)
                .input(SILICONE_RUBBER, 16)
                .input(SCULK_CHEMICAL, 8));

        final String enchantGroup = "upgrade/enchant";
        upgradeFabricating(enchantGroup, SILK_TOUCH_ENCHANTMENT, 1, 500_000, builder -> builder
                .input(POLYMERS, 12)
                .input(T3_CIRCUIT)
                .input(GOLD_PLATES, 8)
                .input(GEMS_EMERALD, 4));
        upgradeFabricating(enchantGroup, LOOTING_ENCHANTMENT, 1, 200_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_PLATES, 4)
                .input(OLIVINE_GEMS, 3));
        upgradeFabricating(enchantGroup, LOOTING_ENCHANTMENT, 2, 400_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 8)
                .input(SLATESTEEL_PLATES, 4)
                .input(OLIVINE_GEMS, 6));
        upgradeFabricating(enchantGroup, LOOTING_ENCHANTMENT, 3, 600_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 12)
                .input(SLATESTEEL_PLATES, 8)
                .input(OLIVINE_GEMS, 9));
        upgradeFabricating(enchantGroup, LOOTING_ENCHANTMENT, 4, 1_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_PLATES, 16)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 8)
                .input(OLIVINE_GEMS, 12)
                .input(CORROSIVE_WEAPON_CHEMICAL, 2));
        upgradeFabricating(enchantGroup, LOOTING_ENCHANTMENT, 5, 2_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 32)
                .input(RHENIUM_PLATES, 4)
                .input(OLIVINE_GEMS, 24)
                .input(CORROSIVE_WEAPON_CHEMICAL, 4));

        upgradeFabricating(enchantGroup, FORTUNE_ENCHANTMENT, 1, 200_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_GEARS)
                .input(SILVER_INGOTS, 4)
                .input(GEMS_DIAMOND));
        upgradeFabricating(enchantGroup, FORTUNE_ENCHANTMENT, 2, 400_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_GEARS, 2)
                .input(SLATESTEEL_GEARS)
                .input(INGOTS_GOLD, 4)
                .input(GEMS_DIAMOND, 2));
        upgradeFabricating(enchantGroup, FORTUNE_ENCHANTMENT, 3, 600_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_GEARS, 4)
                .input(SLATESTEEL_GEARS, 2)
                .input(INGOTS_GOLD, 8)
                .input(GEMS_DIAMOND, 4)
                .input(GEMS_EMERALD, 2));
        upgradeFabricating(enchantGroup, FORTUNE_ENCHANTMENT, 4, 1_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_GEARS, 8)
                .input(SLATESTEEL_GEARS, 4)
                .input(TUNGSTEN_SLATESTEEL_INGOTS, 8)
                .input(LOGIC_CORE, 4)
                .input(PYROXENE, 4));
        upgradeFabricating(enchantGroup, FORTUNE_ENCHANTMENT, 5, 2_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_GEARS, 12)
                .input(SLATESTEEL_GEARS, 6)
                .input(RHENIUM_INGOTS, 4)
                .input(LOGIC_CORE, 6)
                .input(PYROXENE, 8));

        upgradeFabricating(enchantGroup, RAZOR_ENCHANTMENT, 1, 250_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 4)
                .input(SLATESTEEL_PLATES, 8)
                .input(OLIVINE_GEMS, 8));
        upgradeFabricating(enchantGroup, RAZOR_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(T2_CIRCUIT, 4)
                .input(TITANIUM_PLATES, 8)
                .input(SLATESTEEL_PLATES, 12)
                .input(OLIVINE_GEMS, 12));
        upgradeFabricating(enchantGroup, RAZOR_ENCHANTMENT, 3, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 12)
                .input(SLATESTEEL_PLATES, 16)
                .input(OLIVINE_GEMS, 16));
        upgradeFabricating(enchantGroup, RAZOR_ENCHANTMENT, 4, 2_00_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_PLATES, 24)
                .input(SLATESTEEL_PLATES, 32)
                .input(LASER_TECH_PART));
        upgradeFabricating(enchantGroup, RAZOR_ENCHANTMENT, 5, 4_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_PLATES, 32)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 12)
                .input(LASER_TECH_PART, 2));

        final String hanabiGroup = "upgrade/hanabi";
        upgradeFabricating(hanabiGroup + ".gc1", FLAME_GRENADE_CORE, 1, 2_500_000, builder -> builder
                .input(IMPULSE_TECH_PART, 2)
                .input(TITANIUM_PLATES, 24)
                .input(SLATESTEEL_PLATES, 8)
                .input(PHOSPHORUS_DUSTS, 32));
        upgradeFabricating(hanabiGroup + ".gc2", CRYO_GRENADE_CORE, 1, 2_500_000, builder -> builder
                .input(IMPULSE_TECH_PART)
                .input(TITANIUM_PLATES, 24)
                .input(ICE, 16));
        upgradeFabricating(hanabiGroup + ".gc3", ELECTRIC_GRENADE_CORE, 1, 5_000_000, builder -> builder
                .input(IMPULSE_TECH_PART, 2)
                .input(FLUOROPOLYMER_SHEET, 24)
                .input(LARGE_VOLTAIC_CELL)
                .input(SODIUM_DUSTS, 32));
        upgradeFabricating(hanabiGroup + ".gc4", ACID_GRENADE_CORE, 1, 25_000_000, builder -> builder
                .input(IMPULSE_TECH_PART, 2)
                .input(FLUOROPOLYMER_SHEET, 24)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 4)
                .input(CORROSIVE_WEAPON_CHEMICAL, 16));
        upgradeFabricating(hanabiGroup + ".gc5", GLOOM_GAS_GRENADE_CORE, 1, 50_000_000, builder -> builder
                .input(IMPULSE_TECH_PART, 2)
                .input(FLUOROPOLYMER_SHEET, 32)
                .input(TITANIUM_GLASS, 24)
                .input(GLOOM_WEAPON_CHEMICAL, 8));
        upgradeFabricating(hanabiGroup + ".sb", HANABI_SPEED_BOOST, 1, 750_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(IMPULSE_TECH_PART, 2)
                .input(CHORUS_CHEMICAL, 4));
        upgradeFabricating(hanabiGroup + ".sb", HANABI_SPEED_BOOST, 2, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(IMPULSE_TECH_PART, 4)
                .input(CHORUS_CHEMICAL, 8));

        final String armorGroup = "upgrade/armor";
        upgradeFabricating(armorGroup, PASSIVE_NIGHT_VISION, 1, 250_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(OPTICAL_TECH_PART, 2)
                .input(GLOWSTONE_DUST, 8)
                .input(GOLDEN_CARROT, 2));

        upgradeFabricating(armorGroup, ARMOR_PASSIVE_SHIELD, 2, 10_000_000, builder -> builder
                .input(T3_CIRCUIT, 3)
                .input(FLUOROPOLYMER_SHEET, 8)
                .input(MEDIUM_VOLTAIC_CELL, 2)
                .input(SODIUM_DUSTS, 16));
        upgradeFabricating(armorGroup, ARMOR_PASSIVE_SHIELD, 3, 50_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(FLUOROPOLYMER_SHEET, 16)
                .input(LARGE_VOLTAIC_CELL)
                .input(CHORUS_CHEMICAL, 8));
        upgradeFabricating(armorGroup, ARMOR_PASSIVE_SHIELD, 4, 100_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(FLUOROPOLYMER_SHEET, 24)
                .input(LARGE_VOLTAIC_CELL, 2)
                .input(SCULK_CHEMICAL, 8));

        upgradeFabricating(armorGroup, ARMOR_DEFENSE, 2, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 16)
                .input(SLATESTEEL_PLATES, 8)
                .input(FLUOROPOLYMER_SHEET, 4));
        upgradeFabricating(armorGroup, ARMOR_DEFENSE, 3, 10_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_PLATES, 20)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 8)
                .input(FLUOROPOLYMER_SHEET, 8));
        upgradeFabricating(armorGroup, ARMOR_DEFENSE, 4, 50_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_PLATES, 32)
                .input(RHENIUM_PLATES, 4)
                .input(FLUOROPOLYMER_SHEET, 12));

        upgradeFabricating(armorGroup, HEAD_EXPERIENCE_CAPTURE, 1, 10_000_000, builder -> builder
                .input(T3_CIRCUIT, 6)
                .input(OPTICAL_TECH_PART, 2));

        upgradeFabricating(armorGroup, BREATHING_UNIT, 1, 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_GEARS, 2)
                .input(TITANIUM_GLASS, 8)
                .input(SODIUM_DUSTS, 16));

        upgradeFabricating(armorGroup, PASSIVE_SATURATION, 1, 100_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(SCULK_CHEMICAL, 16)
                .input(GOLDEN_APPLE, 32)
                .input(GOLDEN_CARROT, 32)
                .input(GLISTERING_MELON_SLICE, 32));

        upgradeFabricating(armorGroup, CREATIVE_FLIGHT, 1, 150_000_000, builder -> builder
                .input(T5_CIRCUIT)
                .input(LARGE_VOLTAIC_CELL, 4)
                .input(IMPULSE_TECH_PART, 8)
                .input(CHORUS_CHEMICAL, 32)
                .input(SCULK_CHEMICAL, 16));

        final String powerTiersCategory = "machine/power_tiers";
        upgradeFabricating(powerTiersCategory, POWER_TIERS, 3, 10_000_000, builder -> builder
                .input(TITANIUM_PLATES, 4)
                .input(POLYMER_SHEETS, 4)
                .input(T3_CIRCUIT, 2)
                .input(MEDIUM_VOLTAIC_CELL, 4)
                .input(TITANIUM_GEARS, 2)
                .input(SLATESTEEL_GEARS, 4));
        upgradeFabricating(powerTiersCategory, POWER_TIERS, 4, 25_000_000, builder -> builder
                .input(TITANIUM_PLATES, 8)
                .input(FLUOROPOLYMER_SHEET, 6)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 4)
                .input(T4_CIRCUIT)
                .input(LARGE_VOLTAIC_CELL, 2)
                .input(TITANIUM_GEARS, 3)
                .input(SLATESTEEL_GEARS, 6));
        upgradeFabricating(powerTiersCategory, POWER_TIERS, 5, 50_000_000, builder -> builder
                .input(TITANIUM_PLATES, 16)
                .input(FLUOROPOLYMER_SHEET, 8)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 8)
                .input(T5_CIRCUIT)
                .input(LARGE_VOLTAIC_CELL, 3)
                .input(TITANIUM_GEARS, 4)
                .input(SLATESTEEL_GEARS, 8));

        final String oreProcessCategory = "machines/ores";
        upgradeFabricating(oreProcessCategory, ORE_PROCESS_3, 1, 12_500_000, builder -> builder
                .input(SLATESTEEL_PLATES, 8)
                .input(T3_CIRCUIT, 2)
                .input(SLATESTEEL_GEARS, 4)
                .input(MEDIUM_VOLTAIC_CELL, 4)
                .input(OLIVINE_GEMS, 32));
        upgradeFabricating(oreProcessCategory, ORE_PROCESS_4, 1, 25_000_000, builder -> builder
                .input(TITANIUM_PLATES, 16)
                .input(FLUOROPOLYMER, 8)
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_GEARS, 4)
                .input(TITANIUM_GLASS, 8));
        upgradeFabricating(oreProcessCategory, ORE_PROCESS_5, 1, 50_000_000, builder -> builder
                .input(TITANIUM_GLASS, 24)
                .input(T4_CIRCUIT)
                .input(TITANIUM_GEARS, 8)
                .input(SLATESTEEL_GEARS, 4)
                .input(LARGE_VOLTAIC_CELL)
                .input(SODIUM_DUSTS, 32));

        final String storageUpgrades = "upgrade/storage";
        upgradeFabricating(storageUpgrades, ECA_CAPACITY_UPGRADE, 3, 10_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(POLYMERS, 8)
                .input(MEDIUM_VOLTAIC_CELL, 4));
        upgradeFabricating(storageUpgrades, ECA_CAPACITY_UPGRADE, 4, 20_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(FLUOROPOLYMER, 16)
                .input(SILICONE_RUBBER, 8)
                .input(LARGE_VOLTAIC_CELL));
        upgradeFabricating(storageUpgrades, ECA_CAPACITY_UPGRADE, 5, 30_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(FLUOROPOLYMER, 24)
                .input(SILICONE_RUBBER, 16)
                .input(LARGE_VOLTAIC_CELL, 2));

        upgradeFabricating(storageUpgrades, PORTABLE_TANK_UPGRADE, 3, 10_000_000, builder -> builder
                .input(TITANIUM_PLATES, 8)
                .input(SLATESTEEL_PLATES, 4)
                .input(SILICONE_RUBBER, 4)
                .input(TITANIUM_GLASS, 8));
        upgradeFabricating(storageUpgrades, PORTABLE_TANK_UPGRADE, 4, 20_000_000, builder -> builder
                .input(TITANIUM_PLATES, 16)
                .input(SLATESTEEL_PLATES, 8)
                .input(SILICONE_RUBBER, 8)
                .input(TITANIUM_GLASS, 16));
        upgradeFabricating(storageUpgrades, PORTABLE_TANK_UPGRADE, 5, 30_000_000, builder -> builder
                .input(TITANIUM_PLATES, 32)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 8)
                .input(SILICONE_RUBBER, 16)
                .input(TITANIUM_GLASS, 32));

        final String fabricatorUG = "upgrade/fabricator";
        upgradeFabricating(fabricatorUG, FABRICATOR_UPGRADE, 1, 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(SLATESTEEL_PLATES, 4)
                .input(OPTICAL_TECH_PART)
                .input(MEDIUM_VOLTAIC_CELL, 4)
                .input(OLIVINE_GEMS, 4)
                .input(GEMS_DIAMOND));
        upgradeFabricating(fabricatorUG, FABRICATOR_UPGRADE, 2, 2_500_000, builder -> builder
                .input(T2_CIRCUIT, 4)
                .input(SLATESTEEL_PLATES, 8)
                .input(OPTICAL_TECH_PART, 2)
                .input(MEDIUM_VOLTAIC_CELL, 4)
                .input(OLIVINE_GEMS, 8));
        upgradeFabricating(fabricatorUG, FABRICATOR_UPGRADE, 3, 5_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 4)
                .input(OPTICAL_TECH_PART, 2)
                .input(LARGE_VOLTAIC_CELL)
                .input(LASER_TECH_PART));
        upgradeFabricating(fabricatorUG, FABRICATOR_UPGRADE, 4, 10_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 8)
                .input(OPTICAL_TECH_PART, 2)
                .input(LARGE_VOLTAIC_CELL, 2)
                .input(LASER_TECH_PART, 2));

        UnaryOperator<FabricatingBuilder> targetPredicates = builder -> builder
                .input(T3_CIRCUIT)
                .input(OPTICAL_TECH_PART)
                .input(LOGIC_CORE);
        upgradeFabricating("targeting", ALL_ENTITIES_TARGETING, 1, 1_000_000, targetPredicates);
        upgradeFabricating("targeting", NEUTRAL_ENEMY_TARGETING, 1, 500_000, targetPredicates);
        upgradeFabricating("targeting", HOSTILE_TARGETING, 1, 500_000, targetPredicates);
    }

    private void grindingRecipes()
    {
        // Modes
        Holder<RecipeMode> elements = registries.holderOrThrow(LTXIRecipeModes.ELEMENT_EXTRACTION);
        Holder<RecipeMode> dyes = registries.holderOrThrow(LTXIRecipeModes.DYE_EXTRACTION);

        // Element/base compounds
        grinding().needsMode(elements).input(SPARK_FRUIT).output(ItemResult.of(SODIUM_DUST)).time(300).save(output);
        grinding().needsMode(elements).input(VITRIOL_BERRIES).output(ItemResult.of(ACIDIC_BIOMASS)).save(output);
        grinding().needsMode(elements).input(COAL).output(ItemResult.of(CARBON_DUST)).output(ItemResult.of(SULFUR_DUST, ResultCount.exactlyRandom(1, 0.25f))).save(output, "coal_carbon");
        grinding().needsMode(elements).input(CHARCOAL).output(ItemResult.of(CARBON_DUST)).save(output, "charcoal_carbon");
        grinding().needsMode(elements).input(Ingredient.of(WARPED_FUNGUS, CRIMSON_FUNGUS)).fluidOutput(FluidResult.of(AMMONIA, 200)).time(300).save(output);

        // Dusts
        grinding().input(TITANIUM_INGOTS).output(ItemResult.of(TITANIUM_DUST)).save(output);
        grinding().input(SILVER_INGOTS).output(ItemResult.of(SILVER_DUST)).save(output);
        grinding().input(NIOBIUM_INGOTS).output(ItemResult.of(NIOBIUM_DUST)).save(output);
        grinding().input(SLATESTEEL_INGOTS).output(ItemResult.of(SLATESTEEL_DUST)).save(output);
        grinding().input(DEEPSLATE_GRINDABLES).output(ItemResult.of(DEEPSLATE_DUST)).save(output);

        // Resource things
        grinding().input(STONE).output(ItemResult.of(COBBLESTONE)).save(output);
        grinding().input(COBBLESTONES_NORMAL).output(ItemResult.of(GRAVEL)).save(output);
        grinding().input(GRAVELS).output(ItemResult.of(SAND)).save(output);
        grinding().input(CROPS_SUGAR_CANE).output(ItemResult.of(RESINOUS_BIOMASS)).output(ItemResult.of(SUGAR, 2)).save(output, "grind_sugar_cane");
        grinding().input(BAMBOO).output(ItemResult.of(RESINOUS_BIOMASS)).save(output, "grind_bamboo");
        grinding().input(Ingredient.of(PERIDOTITE, POLISHED_PERIDOTITE)).output(ItemResult.of(PERIDOTITE_DUST)).save(output);
        grinding().input(BONES).output(ItemResult.of(BONE_MEAL, ResultCount.between(4, 6))).save(output);
        grinding().input(RODS_BLAZE).output(ItemResult.of(BLAZE_POWDER, ResultCount.between(4, 6))).save(output);
        grinding().input(MAGMA_BLOCK).output(ItemResult.of(MAGMA_CREAM, 4)).save(output);

        // Dyes
        grinding()
                .needsMode(dyes)
                .input(GREEN_GROUP_DYE_SOURCES, 4)
                .output(ItemResult.of(GREEN_DYE, ResultCount.exactlyRandom(1, 0.8f)))
                .output(ItemResult.of(LIME_DYE, ResultCount.exactlyRandom(1, 0.5f)))
                .time(120)
                .save(output, "extract_green_group_dyes");
        grinding().needsMode(dyes).input(SEA_PICKLE).output(ItemResult.of(LIME_DYE, 2)).time(120).save(output);
        grinding().needsMode(dyes).input(SPARK_FRUIT).output(ItemResult.of(ELECTRIC_CHARTREUSE_PIGMENT, 2)).time(120).save(output);
        grinding().needsMode(dyes).input(VITRIOL_BERRIES).output(ItemResult.of(CORROSIVE_GREEN_PIGMENT, 2)).time(120).save(output);
        grinding().needsMode(dyes).input(GLOOM_SHROOM).output(ItemResult.of(GLOOM_BLUE_PIGMENT, 2)).time(120).save(output);

        // Ore processing
        oreProcessCrushing(BuiltInOres.COAL, ORES_COAL, null);
        oreProcessCrushing(BuiltInOres.COPPER, ORES_COPPER, RAW_MATERIALS_COPPER);
        oreProcessCrushing(BuiltInOres.IRON, ORES_IRON, RAW_MATERIALS_IRON);
        oreProcessCrushing(BuiltInOres.LAPIS, ORES_LAPIS, null);
        oreProcessCrushing(BuiltInOres.REDSTONE, ORES_REDSTONE, null);
        oreProcessCrushing(BuiltInOres.GOLD, ORES_GOLD, RAW_MATERIALS_GOLD);
        oreProcessCrushing(BuiltInOres.DIAMOND, ORES_DIAMOND, null);
        oreProcessCrushing(BuiltInOres.EMERALD, ORES_EMERALD, null);
        oreProcessCrushing(BuiltInOres.QUARTZ, ORES_QUARTZ, null);
        oreProcessCrushing(BuiltInOres.TITANIUM, TITANIUM_ORES, RAW_TITANIUM_MATERIALS);
        oreProcessCrushing(BuiltInOres.SILVER, SILVER_ORES, RAW_SILVER_MATERIALS);
        oreProcessCrushing(BuiltInOres.OLIVINE, null, RAW_OLIVINE_MATERIALS);
        oreProcessCrushing(BuiltInOres.FLUORITE, null, RAW_FLUORITE_MATERIALS);
        oreProcessCrushing(BuiltInOres.NIOBIUM, NIOBIUM_ORES, RAW_NIOBIUM_MATERIALS);

        // Ore clusters
        grinding().input(RAW_TITANIUM_CLUSTER).output(ItemResult.of(RAW_TITANIUM, 5)).save(output, "grind_titanium_clusters");
        grinding().input(RAW_SILVER_CLUSTER).output(ItemResult.of(RAW_SILVER, 5)).save(output, "grind_silver_clusters");
        grinding().input(RAW_NIOBIUM_CLUSTER).output(ItemResult.of(RAW_NIOBIUM, 5)).save(output, "grind_niobium_clusters");
    }

    private void pressingRecipes()
    {
        // Modes
        Holder<RecipeMode> plates = registries.holderOrThrow(LTXIRecipeModes.PLATE_PRESSING);
        Holder<RecipeMode> gears = registries.holderOrThrow(LTXIRecipeModes.GEAR_PRESSING);

        // Plates
        pressing().needsMode(plates).input(INGOTS_COPPER).output(ItemResult.of(COPPER_PLATE)).save(output);
        pressing().needsMode(plates).input(INGOTS_GOLD).output(ItemResult.of(GOLD_PLATE)).save(output);
        pressing().needsMode(plates).input(TITANIUM_INGOTS).output(ItemResult.of(TITANIUM_PLATE)).save(output);
        pressing().needsMode(plates).input(SILVER_INGOTS).output(ItemResult.of(SILVER_PLATE)).save(output);
        pressing().needsMode(plates).input(NIOBIUM_INGOTS).output(ItemResult.of(NIOBIUM_PLATE)).save(output);
        pressing().needsMode(plates).input(RHENIUM_INGOTS).output(ItemResult.of(RHENIUM_PLATE)).save(output);
        pressing().needsMode(plates).input(SILICON_INGOTS).output(ItemResult.of(SILICON_PLATE)).save(output);
        pressing().needsMode(plates).input(SLATESTEEL_INGOTS).output(ItemResult.of(SLATESTEEL_PLATE)).save(output);
        pressing().needsMode(plates).input(TUNGSTEN_SLATESTEEL_INGOTS).output(ItemResult.of(TUNGSTEN_SLATESTEEL_PLATE)).save(output);
        pressing().needsMode(plates).input(POLYMER).output(ItemResult.of(POLYMER_SHEET)).save(output);
        pressing().needsMode(plates).input(FLUOROPOLYMER).output(ItemResult.of(FLUOROPOLYMER_SHEET)).save(output);

        pressing().needsMode(gears).input(TITANIUM_INGOTS, 4).output(ItemResult.of(TITANIUM_GEAR)).save(output);
        pressing().needsMode(gears).input(SLATESTEEL_INGOTS, 4).output(ItemResult.of(SLATESTEEL_GEAR)).save(output);
    }

    private void arcSmeltingRecipes()
    {
        // Modes
        Holder<RecipeMode> unshielded = registries.holderOrThrow(LTXIRecipeModes.UNSHIELDED_SMELTING);
        Holder<RecipeMode> inertGas = registries.holderOrThrow(LTXIRecipeModes.INERT_SMELTING);

        // Unshielded mode smelting
        arcSmelting().needsMode(unshielded).input(INGOTS_IRON).input(CARBON_DUSTS).input(DEEPSLATE_DUSTS, 2).fluidInput(OXYGEN_FLUIDS, 250).output(ItemResult.of(SLATESTEEL_INGOT)).time(300).save(output);
        arcSmelting().needsMode(unshielded).input(SILICON_DUSTS, 2).output(ItemResult.of(SILICON_INGOT)).time(600).save(output);

        // Inert gas smelting
        arcSmelting().needsMode(inertGas).input(SILICON_DUSTS).fluidInput(NITROGEN, 125).output(ItemResult.of(SILICON_INGOT)).save(output, "silicon_ingot_gas");
        arcSmelting().needsMode(inertGas).input(TUNGSTEN_SLATESTEEL_DUSTS).fluidInput(NITROGEN, 500).output(ItemResult.of(TUNGSTEN_SLATESTEEL_INGOT)).time(1200).save(output);
        arcSmelting().needsMode(inertGas)
                .input(SILICON_DUSTS, 6)
                .input(PHOSPHORUS_DUSTS, 6)
                .input(OLIVINE_GEMS, 8)
                .fluidInput(ARGON, 1000)
                .output(ItemResult.of(LOGIC_CORE))
                .time(600)
                .save(output);
        arcSmelting().needsMode(inertGas)
                .input(LOGIC_CORE, 2)
                .input(PYROXENE, 8)
                .input(NIOBIUM_DUSTS, 4)
                .fluidInput(ARGON, 4000)
                .output(ItemResult.of(NANO_LOGIC_CORE))
                .time(1200)
                .save(output);
        arcSmelting().needsMode(inertGas).input(RHENIUM_DUSTS).fluidInput(ARGON, 2000).output(ItemResult.of(RHENIUM_INGOT)).time(1800).save(output);

        // Misc
        NEON_LIGHTS.forEach((color, holder) -> arcSmelting().input(PHOSPHORUS_DUSTS, 2).input(neonLightDye(color)).time(120).output(ItemResult.of(holder, 16)).save(output));
        arcSmelting().input(NETHERITE_SCRAP, 4).input(INGOTS_GOLD).output(ItemResult.of(NETHERITE_INGOT)).save(output);
        arcSmelting().input(TITANIUM_INGOTS).input(GEMS_QUARTZ, 3).output(ItemResult.of(TITANIUM_GLASS, 2)).save(output);
        arcSmelting().input(TITANIUM_DUSTS).fluidInput(OXYGEN_FLUIDS, 1000).output(ItemResult.of(WHITE_DYE, 8)).save(output);
    }

    private void sievingRecipes()
    {
        sieving().water(125).input(GRAVELS).output(ItemResult.of(FLINT)).output(ItemResult.of(FLINT, ResultCount.exactlyRandom(1, 0.5f))).save(output);
        sieving().water(1000).input(PERIDOTITE_DUSTS).output(ItemResult.of(RAW_OLIVINE, ResultCount.exactlyRandom(1, 0.025f))).save(output);
        sieving().water(1000).input(DEEPSLATE_DUSTS).output(ItemResult.of(RAW_FLUORITE, ResultCount.exactlyRandom(1, 0.025f))).save(output);
    }

    private void electroCentrifugingRecipes()
    {
        // Modes
        Holder<RecipeMode> elements = registries.holderOrThrow(LTXIRecipeModes.ELEMENT_EXTRACTION);

        // Elemental extraction
        electroCentrifuging()
                .needsMode(elements)
                .input(SANDS, 1)
                .output(ItemResult.of(SILICON_DUST, ResultCount.exactlyRandom(1, 0.5f)))
                .fluidOutput(FluidResult.of(OXYGEN, 250))
                .time(400)
                .save(output, "electrolyze_sand");
        electroCentrifuging()
                .needsMode(elements)
                .input(FLINT)
                .output(ItemResult.of(SILICON_DUST, ResultCount.exactlyRandom(1, 0.8f)))
                .fluidOutput(FluidResult.of(OXYGEN, 250))
                .time(300)
                .save(output, "electrolyze_flint");
        electroCentrifuging()
                .needsMode(elements)
                .input(GEMS_QUARTZ)
                .output(ItemResult.of(SILICON_DUST, 2))
                .fluidOutput(FluidResult.of(OXYGEN, 1000))
                .save(output, "electrolyze_quartz");
        electroCentrifuging()
                .needsMode(elements)
                .input(PHOSPHORUS_SOURCES, 2)
                .output(ItemResult.of(PHOSPHORUS_DUST))
                .time(300)
                .save(output, "phosphorus_sources");
        electroCentrifuging()
                .needsMode(elements)
                .water(1000)
                .fluidOutput(FluidResult.of(HYDROGEN, 1000))
                .fluidOutput(FluidResult.of(OXYGEN, 500))
                .time(1200)
                .save(output, "electrolyze_water");
        electroCentrifuging()
                .needsMode(elements)
                .input(KELP, 2)
                .fluidOutput(FluidResult.of(CHLORINE, 250))
                .time(600)
                .save(output, "kelp_chlorine");
        electroCentrifuging()
                .needsMode(elements)
                .fluidInput(SEA_WATER, 1000)
                .fluidOutput(FluidResult.of(CHLORINE, 500))
                .time(400)
                .save(output, "sea_water_chlorine");

        // Splitting
        electroCentrifuging()
                .input(MUD)
                .output(ItemResult.of(DIRT))
                .output(ItemResult.of(CLAY_BALL, ResultCount.between(1, 3)))
                .output(ItemResult.of(MANGROVE_PROPAGULE, ResultCount.exactlyRandom(1, 0.05f), false))
                .fluidOutput(FluidResult.of(Fluids.WATER, 1000))
                .time(120)
                .save(output, "split_mud");
        electroCentrifuging()
                .input(MAGMA_CREAM)
                .output(ItemResult.of(SLIME_BALL))
                .output(ItemResult.of(BLAZE_POWDER))
                .save(output, "split_magma_cream");
    }

    private void mixingRecipes()
    {
        mixing().input(DIRT).water(1000).output(ItemResult.of(MUD)).time(120).save(output);
        mixing().input(ACIDIC_BIOMASS, 4).water(1000).fluidOutput(FluidResult.of(SULFURIC_ACID, 1000)).save(output);
        mixing().input(RESINOUS_BIOMASS, 4).water(2000).fluidInput(SULFURIC_ACID, 1000).output(ItemResult.of(POLYMER, 2)).time(600).save(output);
        mixing().input(CHORUS_FRUIT, 2).fluidInput(SULFURIC_ACID, 1000).output(ItemResult.of(CHORUS_CHEMICAL)).time(600).save(output);
        mixing().input(GLOOM_SHROOM, 2).fluidInput(SULFURIC_ACID, 1000).output(ItemResult.of(SCULK_CHEMICAL)).time(600).save(output);

        // Concretes
        for (DyeColor color : DyeColor.values())
        {
            String colorName = color.getSerializedName();
            Holder<Item> concretePowder = items.getOrThrow(ModResources.MC.resourceKey(Registries.ITEM, colorName + "_concrete_powder"));
            Holder<Item> concrete = items.getOrThrow(ModResources.MC.resourceKey(Registries.ITEM, colorName + "_concrete"));

            mixing()
                    .time(40)
                    .input(concretePowder.value())
                    .water(125)
                    .output(ItemResult.of(concrete))
                    .save(output, "concretes/hydrate_" + colorName);

            mixing()
                    .time(100)
                    .input(GRAVELS, 4)
                    .input(SANDS, 4)
                    .input(color.getTag())
                    .water(1000)
                    .output(ItemResult.of(concrete, 8))
                    .save(output, "concretes/mix_" + colorName);
        }
    }

    private void energizingRecipes()
    {
        Holder<RecipeMode> dyes = registries.holderOrThrow(LTXIRecipeModes.DYE_EXTRACTION);

        // Dyes
        energizing().needsMode(dyes).input(DYES_LIME).output(ItemResult.of(LTX_LIME_PIGMENT)).time(120).save(output, "energize_lime_dyes");
        energizing().needsMode(dyes).input(DYES_LIGHT_BLUE).output(ItemResult.of(ENERGY_BLUE_PIGMENT)).time(120).save(output, "energize_light_blue_dyes");
        energizing().needsMode(dyes).input(DYES_BLUE).output(ItemResult.of(ENERGY_BLUE_PIGMENT)).time(120).save(output, "energize_blue_dyes");

        // Misc
        energizing().input(TITANIUM_GLASS).output(ItemResult.of(GLACIA_GLASS)).time(100).save(output);
    }

    private void chemLabRecipes()
    {
        chemLab().fluidInput(HYDROGEN_FLUIDS, 1000).fluidInput(CHLORINE, 1000).fluidOutput(FluidResult.of(HYDROCHLORIC_ACID, 1000)).save(output);
        chemLab().fluidInput(NITROGEN, 1000).fluidInput(HYDROGEN_FLUIDS, 3000).fluidOutput(FluidResult.of(AMMONIA, 1000)).save(output);
        chemLab().input(SULFUR_DUSTS, 4).fluidInput(OXYGEN_FLUIDS, 1000).fluidOutput(FluidResult.of(SULPHURINE, 1000)).time(400).save(output, "sulfur_to_sulphurine");
        chemLab().fluidInput(SULPHURINE, 1000).water(1000).fluidOutput(FluidResult.of(SULFURIC_ACID, 1000)).time(900).save(output);
        chemLab().input(FLUORITE_GEMS, 4).fluidInput(SULFURIC_ACID, 1000).fluidOutput(FluidResult.of(HYDROFLUORIC_ACID, 1000)).save(output);

        chemLab()
                .input(SILICON_DUSTS, 4)
                .fluidInput(METHANE, 2000)
                .fluidInput(CHLORINE, 2000)
                .fluidOutput(FluidResult.of(SILICONE_OIL, 1000))
                .fluidOutput(FluidResult.of(HYDROCHLORIC_ACID, 1000))
                .save(output);
        chemLab().fluidInput(SILICONE_OIL, 1000).fluidInput(SULPHURINE, 500).output(ItemResult.of(SILICONE_RUBBER, 2)).save(output);
        chemLab()
                .randomInput(TITANIUM_DUSTS, 1, 0f)
                .fluidInput(HYDROFLUORIC_ACID, 1000)
                .fluidInput(METHANE, 2000)
                .fluidInput(CHLORINE, 2000)
                .output(ItemResult.of(FLUOROPOLYMER, 2))
                .fluidOutput(FluidResult.of(HYDROCHLORIC_ACID, 1000))
                .save(output);

        chemLab().input(POLYMER_SHEET, 2).input(COPPER_PLATES).fluidInput(SULFURIC_ACID, 500).output(ItemResult.of(CIRCUIT_BOARD)).save(output);
        chemLab().input(FLUOROPOLYMER_SHEET, 2).input(SILVER_PLATES).fluidInput(SULFURIC_ACID, 1000).output(ItemResult.of(ELITE_CIRCUIT_BOARD)).save(output);
        chemLab()
                .input(SODIUM_DUSTS, 8)
                .fluidInput(SULFURIC_ACID, 8000)
                .fluidInput(CHLORINE, 4000)
                .output(ItemResult.of(CORROSIVE_WEAPON_CHEMICAL))
                .time(900)
                .save(output);
        chemLab()
                .input(SCULK_CHEMICAL, 32)
                .fluidInput(AMMONIA, 4000)
                .output(ItemResult.of(GLOOM_WEAPON_CHEMICAL))
                .time(2400)
                .save(output);

        chemLab()
                .input(SLATESTEEL_DUSTS)
                .input(TUNGSTEN_TRIOXIDE, 2)
                .fluidInput(HYDROGEN_FLUIDS, 4000)
                .output(ItemResult.of(TUNGSTEN_SLATESTEEL_DUST))
                .time(600)
                .save(output);
        chemLab()
                .input(RHENIUM_7_OXIDE)
                .fluidInput(AMMONIA, 1000)
                .output(ItemResult.of(AMMONIUM_PERRHENATE))
                .time(1200)
                .save(output);
        chemLab()
                .input(AMMONIUM_PERRHENATE, 2)
                .fluidInput(HYDROGEN_FLUIDS, 4000)
                .output(ItemResult.of(RHENIUM_DUST))
                .time(1200)
                .save(output);
    }

    private void assemblingRecipes()
    {
        assembling().input(TITANIUM_PLATES, 8).input(POLYMER, 4).output(ItemResult.of(MACHINE_HOUSING)).save(output, "machine_housing_p");
        assembling().input(TITANIUM_PLATES, 4).input(FLUOROPOLYMER, 2).output(ItemResult.of(MACHINE_HOUSING)).save(output, "machine_housing_fp");
        assembling()
                .input(POLYMER, 4)
                .input(COPPER_PLATES, 2)
                .input(SODIUM_DUSTS, 4)
                .output(ItemResult.of(SMALL_VOLTAIC_CELL, 2))
                .save(output, "small_voltaic_cell_p");
        assembling()
                .input(FLUOROPOLYMER, 2)
                .input(COPPER_PLATES, 2)
                .input(SODIUM_DUSTS, 4)
                .output(ItemResult.of(SMALL_VOLTAIC_CELL, 3))
                .save(output, "small_voltaic_cell_fp");
        assembling()
                .input(POLYMER, 6)
                .input(SILVER_PLATES, 3)
                .input(SODIUM_DUSTS, 8)
                .output(ItemResult.of(MEDIUM_VOLTAIC_CELL))
                .time(600)
                .save(output, "medium_voltaic_cell_p");
        assembling()
                .input(FLUOROPOLYMER, 4)
                .input(SILVER_PLATES, 3)
                .input(SODIUM_DUSTS, 8)
                .output(ItemResult.of(MEDIUM_VOLTAIC_CELL, 2))
                .time(600)
                .save(output, "medium_voltaic_cell_fp");
        assembling()
                .input(FLUOROPOLYMER, 8)
                .input(GOLD_PLATES, 3)
                .input(NIOBIUM_PLATES, 3)
                .input(SODIUM_DUSTS, 16)
                .output(ItemResult.of(LARGE_VOLTAIC_CELL))
                .time(900)
                .save(output);
        assembling()
                .input(CIRCUIT_BOARD)
                .input(SILICON_PLATES, 4)
                .input(COPPER_PLATES, 3)
                .input(SMALL_VOLTAIC_CELL, 2)
                .input(TITANIUM_PLATES, 2)
                .output(ItemResult.of(T1_CIRCUIT, 2))
                .save(output, "t1_circuits_basic");
        assembling()
                .input(ELITE_CIRCUIT_BOARD)
                .input(COPPER_PLATES)
                .input(LOGIC_CORE)
                .output(ItemResult.of(T1_CIRCUIT, 4))
                .save(output, "t1_circuits_elite");
        assembling()
                .input(T1_CIRCUIT, 2)
                .input(SILICON_PLATES, 6)
                .input(SILVER_PLATES, 3)
                .input(SMALL_VOLTAIC_CELL, 3)
                .input(TITANIUM_PLATES, 4)
                .output(ItemResult.of(T2_CIRCUIT))
                .time(600)
                .save(output, "t2_circuits_basic");
        assembling()
                .input(ELITE_CIRCUIT_BOARD)
                .input(SILVER_PLATES)
                .input(LOGIC_CORE)
                .output(ItemResult.of(T2_CIRCUIT, 2))
                .time(600)
                .save(output, "t2_circuits_elite");
        assembling()
                .input(T2_CIRCUIT, 2)
                .input(SILICON_PLATES, 8)
                .input(GOLD_PLATES, 3)
                .input(MEDIUM_VOLTAIC_CELL, 2)
                .input(TITANIUM_PLATES, 6)
                .output(ItemResult.of(T3_CIRCUIT))
                .time(800)
                .save(output, "t3_circuits_basic");
        assembling()
                .input(ELITE_CIRCUIT_BOARD)
                .input(GOLD_PLATES)
                .input(LOGIC_CORE, 2)
                .output(ItemResult.of(T3_CIRCUIT, 2))
                .time(800)
                .save(output, "t3_circuits_elite");
        assembling()
                .input(ELITE_CIRCUIT_BOARD, 2)
                .input(T3_CIRCUIT, 2)
                .input(NANO_LOGIC_CORE)
                .input(LARGE_VOLTAIC_CELL)
                .input(CHORUS_CHEMICAL, 8)
                .input(TITANIUM_PLATES, 4)
                .output(ItemResult.of(T4_CIRCUIT))
                .time(1800)
                .save(output);

        assembling()
                .input(TITANIUM_PLATES, 6)
                .input(T2_CIRCUIT)
                .input(SMALL_VOLTAIC_CELL, 2)
                .input(TITANIUM_GLASS, 4)
                .output(ItemResult.of(OPTICAL_TECH_PART))
                .save(output);
        assembling()
                .input(TITANIUM_PLATES, 12)
                .input(SLATESTEEL_PLATES, 8)
                .input(T2_CIRCUIT)
                .input(MEDIUM_VOLTAIC_CELL, 2)
                .input(SODIUM_DUSTS, 32)
                .fluidInput(HYDROGEN_FLUIDS, 32_000)
                .output(ItemResult.of(IMPULSE_TECH_PART))
                .save(output);
        assembling()
                .input(FLUOROPOLYMER_SHEET, 12)
                .input(SLATESTEEL_PLATES, 8)
                .input(T3_CIRCUIT)
                .input(SILICONE_RUBBER, 4)
                .input(LARGE_VOLTAIC_CELL)
                .input(OLIVINE_GEMS, 24)
                .output(ItemResult.of(LASER_TECH_PART))
                .save(output);

        assembling()
                .input(emptyPortableTank())
                .input(T4_CIRCUIT)
                .input(SLATESTEEL_PLATES, 32)
                .input(SLATESTEEL_GEARS, 4)
                .input(FLUOROPOLYMER_SHEET, 16)
                .input(SILICONE_RUBBER, 32)
                .water(2_500_000)
                .output(ItemResult.of(INFINITE_WATER_TANK))
                .time(1200)
                .save(output);
        assembling()
                .input(emptyPortableTank())
                .input(T5_CIRCUIT)
                .input(TUNGSTEN_SLATESTEEL_PLATES, 32)
                .input(RHENIUM_PLATES, 4)
                .input(SLATESTEEL_GEARS, 8)
                .fluidInput(FluidTags.LAVA, 10_000_000)
                .output(ItemResult.of(INFINITE_LAVA_TANK))
                .time(3600)
                .save(output);
    }

    private void geoSynthesisRecipes()
    {
        geoSynthWaterLava(COBBLESTONE);
        geoSynthWaterLava(STONE);
        geoSynthWaterLava(COBBLED_DEEPSLATE);
        geoSynthWaterLava(DEEPSLATE);
        geoSynthWaterLava(GRANITE);
        geoSynthWaterLava(DIORITE);
        geoSynthWaterLava(ANDESITE);
        geoSynthWaterLava(PERIDOTITE);
        geoSynthWaterLava(DRIPSTONE_BLOCK);
        geoSynthWaterLava(BASALT);
        geoSynthWaterLava(BLACKSTONE);

        geoSynthesis().randomInput(OBSIDIAN, 1, 0f).water(1000, 0f).fluidInput(FluidTags.LAVA, 1000).time(120).output(ItemResult.of(OBSIDIAN)).save(output);
    }

    private void scrubbingRecipes()
    {
        Holder<RecipeMode> ambientFluids = registries.holderOrThrow(LTXIRecipeModes.AMBIENT_FLUIDS);
        Holder<RecipeMode> ambientGases = registries.holderOrThrow(LTXIRecipeModes.AMBIENT_GASES);
        Holder<RecipeMode> localizedFluids = registries.holderOrThrow(LTXIRecipeModes.LOCALIZED_FLUIDS);
        Holder<RecipeMode> localizedGases = registries.holderOrThrow(LTXIRecipeModes.LOCALIZED_GASES);

        scrubbing(ambientFluids).inDimension(Level.OVERWORLD).fluidOutput(FluidResult.of(Fluids.WATER, 1000)).save(output, "overworld_fluids");
        scrubbing(ambientGases).inDimension(Level.OVERWORLD)
                .output(ItemResult.of(CARBON_DUST, ResultCount.exactlyRandom(1, 0.025f), false))
                .fluidOutput(FluidResult.of(NITROGEN, 500))
                .save(output, "overworld_gases");
        scrubbing(localizedFluids).inDimension(Level.OVERWORLD).inBiomes(Tags.Biomes.IS_OCEAN).requireWaterlog().fluidOutput(FluidResult.of(SEA_WATER, 1000)).save(output);
        scrubbing(localizedGases).inDimension(Level.OVERWORLD).inBiomes(Tags.Biomes.IS_SWAMP).fluidOutput(FluidResult.of(METHANE, 250)).save(output);

        scrubbing(ambientFluids).inDimension(Level.NETHER).fluidOutput(FluidResult.of(Fluids.LAVA, 1000)).save(output, "nether_fluids");
        scrubbing(ambientGases).inDimension(Level.NETHER).fluidOutput(FluidResult.of(SULPHURINE, 500)).save(output, "nether_gases");

        scrubbing(ambientGases).inDimension(Level.END)
                .output(ItemResult.of(CHORUS_CHEMICAL, ResultCount.exactlyRandom(1, 0.0125f), false))
                .fluidOutput(FluidResult.of(ARGON, 250))
                .save(output, "end_gases");
    }

    private void gardenRecipes()
    {
        // Modes
        Holder<RecipeMode> farming = registries.holderOrThrow(LTXIRecipeModes.GS_FARMING);
        Holder<RecipeMode> woods = registries.holderOrThrow(LTXIRecipeModes.GS_WOODS);
        Holder<RecipeMode> orchard = registries.holderOrThrow(LTXIRecipeModes.GS_ORCHARD);
        Holder<RecipeMode> foliage = registries.holderOrThrow(LTXIRecipeModes.GS_FOLIAGE);

        // Crops
        garden().needsMode(farming).growSeed(WHEAT_SEEDS, WHEAT, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(POTATO, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(CARROT, 2).water(250).save(output);
        garden().needsMode(farming).growSeed(BEETROOT_SEEDS, BEETROOT, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(SWEET_BERRIES, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(COCOA_BEANS, 2).water(500).save(output);
        garden().needsMode(farming).growSeed(PUMPKIN_SEEDS, PUMPKIN, 1).water(1000).save(output);
        garden().needsMode(farming).growSeed(MELON_SEEDS, MELON, 1).water(1000).save(output);
        garden().needsMode(farming).reproduce(GLOW_BERRIES, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(BAMBOO, 2).water(500).save(output);
        garden().needsMode(farming).reproduce(SUGAR_CANE, 2).water(500).save(output);
        garden().needsMode(farming).reproduce(CACTUS, 2).water(125).save(output);
        garden().needsMode(farming).reproduce(KELP, 2).water(1000).save(output);
        garden().needsMode(farming).reproduce(SEA_PICKLE, 2).water(1000).save(output);
        garden().needsMode(farming).reproduce(NETHER_WART, 2).water(250).save(output);
        garden().needsMode(farming).growSeed(CHORUS_FLOWER, CHORUS_FRUIT, 2).water(1000).save(output);

        // Flowers
        garden().reproduce(DANDELION, 2).water(250).time(300).save(output);
        garden().reproduce(POPPY, 2).water(250).time(300).save(output);
        garden().reproduce(BLUE_ORCHID, 2).water(250).time(300).save(output);
        garden().reproduce(ALLIUM, 2).water(250).time(300).save(output);
        garden().reproduce(AZURE_BLUET, 2).water(250).time(300).save(output);
        garden().reproduce(RED_TULIP, 2).water(250).time(300).save(output);
        garden().reproduce(ORANGE_TULIP, 2).water(250).time(300).save(output);
        garden().reproduce(WHITE_TULIP, 2).water(250).time(300).save(output);
        garden().reproduce(PINK_TULIP, 2).water(250).time(300).save(output);
        garden().reproduce(OXEYE_DAISY, 2).water(250).time(300).save(output);
        garden().reproduce(CORNFLOWER, 2).water(250).time(300).save(output);
        garden().reproduce(LILY_OF_THE_VALLEY, 2).water(250).time(300).save(output);
        garden().reproduce(WITHER_ROSE).water(1000).save(output);
        garden().growSeed(TORCHFLOWER_SEEDS, TORCHFLOWER, 2).water(500).save(output);
        garden().reproduce(SUNFLOWER, 2).water(250).time(300).save(output);
        garden().reproduce(LILAC, 2).water(250).time(300).save(output);
        garden().reproduce(PEONY, 2).water(250).time(300).save(output);
        garden().reproduce(ROSE_BUSH, 2).water(250).time(300).save(output);
        garden().growSeed(PITCHER_POD, PITCHER_PLANT, 2).water(500).save(output);

        // Shrooms
        garden().needsMode(farming).reproduce(RED_MUSHROOM, 2).water(500).save(output);
        garden().needsMode(farming).reproduce(BROWN_MUSHROOM, 2).water(500).save(output);

        // Saplings
        garden().needsMode(farming).reproduce(OAK_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(BIRCH_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(SPRUCE_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(JUNGLE_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(DARK_OAK_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(ACACIA_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(CHERRY_SAPLING,  2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(MANGROVE_PROPAGULE, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(PALE_OAK_SAPLING, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(AZALEA, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(FLOWERING_AZALEA, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(CRIMSON_FUNGUS, 2).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(WARPED_FUNGUS, 2).water(250).time(300).save(output);

        // Woods
        garden().needsMode(woods).growSeed(OAK_SAPLING, OAK_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(BIRCH_SAPLING, BIRCH_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(SPRUCE_SAPLING, SPRUCE_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(JUNGLE_SAPLING, JUNGLE_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(DARK_OAK_SAPLING, DARK_OAK_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(ACACIA_SAPLING, ACACIA_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(CHERRY_SAPLING, CHERRY_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(MANGROVE_PROPAGULE, MANGROVE_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(PALE_OAK_SAPLING, PALE_OAK_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(CRIMSON_FUNGUS, CRIMSON_STEM, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(WARPED_FUNGUS, WARPED_STEM, 4).water(1000).save(output);

        // Orchard
        garden().needsMode(orchard).growSeed(APPLE_SAPLINGS, APPLE, 3).water(1000).save(output);

        // Foliage
        garden().needsMode(foliage).growSeed(OAK_SAPLING, OAK_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(BIRCH_SAPLING, BIRCH_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(SPRUCE_SAPLING, SPRUCE_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(JUNGLE_SAPLING, JUNGLE_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(DARK_OAK_SAPLING, DARK_OAK_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(ACACIA_SAPLING, ACACIA_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(CHERRY_SAPLING, CHERRY_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(MANGROVE_PROPAGULE, MANGROVE_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(PALE_OAK_SAPLING, PALE_OAK_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(AZALEA, AZALEA_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(FLOWERING_AZALEA, FLOWERING_AZALEA_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(CRIMSON_FUNGUS, NETHER_WART_BLOCK, 2).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(WARPED_FUNGUS, WARPED_WART_BLOCK, 2).water(1500).time(300).save(output);

        // LTXI
        garden().reproduce(SPARK_FRUIT, 3).water(1000).save(output);
        garden().reproduce(VITRIOL_BERRIES, 2).randomFluidInput(SULPHURINE, 16000, 0f).save(output);
        garden().reproduce(GLOOM_SHROOM).fluidInput(AMMONIA, 1000).save(output);
    }

    //#region Ore processing

    private void oreProcessCooking(BuiltInOres ore, ItemLike resultItem, int resultCount)
    {
        String name = ore.getSerializedName() + "_materials";
        Ingredient ingredient = Ingredient.of(CRUSHED_ORES.get(ore), WASHED_ORES.get(ore), ORE_CHUNKS.get(ore), ORE_SOLUTIONS.get(ore), ORE_CRYSTALS.get(ore));

        smelting(resultItem, resultCount).input(ingredient).xp(0.5f).save(output, name);
        blasting(resultItem, resultCount).input(ingredient).xp(0.5f).save(output, name);
    }

    private void oreProcessCrushing(BuiltInOres ore, @Nullable TagKey<Item> oreTag, @Nullable TagKey<Item> rawOreTag)
    {
        List<Ingredient> baseMaterials = Stream.of(oreTag, rawOreTag).filter(Objects::nonNull).map(tag -> Ingredient.of(items.getOrThrow(tag))).toList();
        Ingredient ingredient = baseMaterials.size() == 1 ? baseMaterials.getFirst() : new CompoundIngredient(baseMaterials).toVanilla();

        grinding().input(ingredient).output(ItemResult.of(CRUSHED_ORES.get(ore), 2)).save(output, "grind_" + ore.getSerializedName() + "_ores");
    }

    //#endregion

    // Helpers

    private void stonecuttingInterchange(List<ItemLike> variants)
    {
        for (ItemLike variant : variants)
        {
            stonecutting(variant).input(Ingredient.of(variants.stream().filter(o -> o != variant))).save(output);
        }
    }

    private LTXIBuilder<GrindingRecipe> grinding()
    {
        return new LTXIBuilder<>(resources, registries, GrindingRecipe::new);
    }

    private LTXIBuilder<PressingRecipe> pressing()
    {
        return new LTXIBuilder<>(resources, registries, PressingRecipe::new);
    }

    private LTXIBuilder<ArcSmeltingRecipe> arcSmelting()
    {
        return new LTXIBuilder<>(resources, registries, ArcSmeltingRecipe::new);
    }

    private LTXIBuilder<SievingRecipe> sieving()
    {
        return new LTXIBuilder<>(resources, registries, SievingRecipe::new);
    }

    private LTXIBuilder<ElectroCentrifugingRecipe> electroCentrifuging()
    {
        return new LTXIBuilder<>(resources, registries, ElectroCentrifugingRecipe::new);
    }

    private LTXIBuilder<MixingRecipe> mixing()
    {
        return new LTXIBuilder<>(resources, registries, MixingRecipe::new);
    }

    private LTXIBuilder<EnergizingRecipe> energizing()
    {
        return new LTXIBuilder<>(resources, registries, EnergizingRecipe::new);
    }

    private LTXIBuilder<ChemicalReactingRecipe> chemLab()
    {
        return new LTXIBuilder<>(resources, registries, ChemicalReactingRecipe::new);
    }

    private LTXIBuilder<AssemblingRecipe> assembling()
    {
        return new LTXIBuilder<>(resources, registries, AssemblingRecipe::new, 400);
    }

    private LTXIBuilder<GeoSynthesisRecipe> geoSynthesis()
    {
        return new LTXIBuilder<>(resources, registries, GeoSynthesisRecipe::new, 100);
    }

    private void geoSynthWaterLava(ItemLike key)
    {
        geoSynthesis().randomInput(key, 1, 0f).water(1000, 0f).randomFluidInput(FluidTags.LAVA, 1000, 0f).output(ItemResult.of(key.asItem())).save(output);
    }

    private ScrubbingBuilder scrubbing(Holder<RecipeMode> mode)
    {
        return new ScrubbingBuilder(resources, registries, mode);
    }

    private GardenBuilder garden()
    {
        return new GardenBuilder(resources, registries);
    }

    private FabricatingBuilder fabricating(int energyRequired)
    {
        return new FabricatingBuilder(resources, registries, energyRequired);
    }

    private Ingredient emptyPortableTank()
    {
        return DataComponentIngredient.of(false, LimaCoreDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY, PORTABLE_TANK);
    }

    private Ingredient moduleIngredient(ResourceKey<Upgrade> upgradeKey, int upgradeRank)
    {
        Holder<Upgrade> holder = registries.holderOrThrow(upgradeKey);
        return DataComponentIngredient.of(false, LTXIDataComponents.UPGRADE_ENTRY, new UpgradeEntry(holder, upgradeRank), UPGRADE_MODULE);
    }

    private Ingredient autoModuleIngredient(ResourceKey<Upgrade> upgradeKey, int upgradeRank)
    {
        return upgradeRank == 1 ? Ingredient.of(EMPTY_UPGRADE_MODULE) : moduleIngredient(upgradeKey, upgradeRank - 1);
    }

    private ItemStackTemplate moduleTemplate(ResourceKey<Upgrade> upgradeKey, int upgradeRank)
    {
        DataComponentPatch components = DataComponentPatch.builder().set(LTXIDataComponents.UPGRADE_ENTRY.get(), new UpgradeEntry(registries.holderOrThrow(upgradeKey), upgradeRank)).build();
        return new ItemStackTemplate(UPGRADE_MODULE, components);
    }

    private void upgradeShaped(ResourceKey<Upgrade> upgradeKey, int upgradeRank, UnaryOperator<LimaShapedRecipeBuilder> op)
    {
        LimaShapedRecipeBuilder builder = shaped(moduleTemplate(upgradeKey, upgradeRank)).input('m', autoModuleIngredient(upgradeKey, upgradeRank));
        op.apply(builder).save(output, "upgrades/" + upgradeKey.identifier().getPath() + "_" + upgradeRank);
    }

    private <T extends LimaCustomRecipeBuilder<?, ?>> void upgradeCustomCrafting(ResourceKey<Upgrade> upgradeKey, int upgradeRank, boolean useBaseModule, T instance, UnaryOperator<T> modifier)
    {
        instance.output(ItemResult.copyOf(moduleTemplate(upgradeKey, upgradeRank)));
        if (useBaseModule) instance.input(autoModuleIngredient(upgradeKey, upgradeRank));
        modifier.apply(instance).save(output, "upgrades/" + upgradeKey.identifier().getPath() + "_" + upgradeRank);
    }

    private void upgradeFabricating(String group, ResourceKey<Upgrade> upgradeKey, int upgradeRank, int energyRequired, boolean useBaseModule, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeCustomCrafting(upgradeKey, upgradeRank, useBaseModule, fabricating(energyRequired).group(group), op);
    }

    private void upgradeFabricating(String group, ResourceKey<Upgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(group, upgradeKey, upgradeRank, energyRequired, true, op);
    }

    private void defaultModuleFabricating(ResourceKey<Upgrade> upgradeKey, ItemLike... equipmentItems)
    {
        upgradeFabricating("eum/defaults", upgradeKey, 1, 50_000, builder ->
                builder.randomInput(Ingredient.of(equipmentItems), 0f));
    }

    private void equipmentFabricating(Supplier<? extends UpgradableEquipmentItem> itemSupplier, String group, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        ItemStackTemplate stackTemplate = defaultUpgradableItem(itemSupplier);
        FabricatingBuilder builder = fabricating(energyRequired).group(group).output(ItemResult.copyOf(stackTemplate));
        op.apply(builder).save(output);
    }

    private ItemStackTemplate defaultUpgradableItem(Supplier<? extends UpgradableEquipmentItem> itemSupplier)
    {
        UpgradableEquipmentItem item = itemSupplier.get();
        DataComponentPatch components = DataComponentPatch.EMPTY;

        Upgrades upgrades = MutableUpgrades.create().setAll(registries.lookupOrThrow(LTXIRegistries.Keys.UPGRADES), item.getDefaultUpgrades()).build();
        if (!upgrades.isEmpty())
        {
            components = DataComponentPatch.builder().set(LTXIDataComponents.UPGRADES.get(), upgrades).build();
        }

        return new ItemStackTemplate(item.asItem(), components);
    }

    private Ingredient neonLightDye(NeonLightColor color)
    {
        Either<ItemLike, TagKey<Item>> either = switch (color)
        {
            case LTX_LIME -> Either.left(LTX_LIME_PIGMENT);
            case ENERGY_BLUE -> Either.left(ENERGY_BLUE_PIGMENT);
            case ELECTRIC_CHARTREUSE -> Either.left(ELECTRIC_CHARTREUSE_PIGMENT);
            case CORROSIVE_GREEN -> Either.left(CORROSIVE_GREEN_PIGMENT);
            case GLOOM_BLUE -> Either.left(GLOOM_BLUE_PIGMENT);
            default -> Either.right(Objects.requireNonNull(color.getDyeColor()).getTag());
        };
        return either.map(Ingredient::of, tagKey -> Ingredient.of(items.getOrThrow(tagKey)));
    }

    // Builder classes
    private static class FabricatingBuilder extends LimaCustomRecipeBuilder<FabricatingRecipe, FabricatingBuilder>
    {
        private final int energyRequired;

        FabricatingBuilder(ModResources resources, HolderLookup.Provider registries, int energyRequired)
        {
            super(resources, registries);
            this.energyRequired = energyRequired;
        }

        @Override
        protected FabricatingRecipe buildRecipe()
        {
            Preconditions.checkState(fluidInputs.isEmpty(), "Fabricating recipes do not support fluid inputs.");
            Preconditions.checkState(fluidResults.isEmpty(), "Fabricating recipes do not support fluid outputs.");
            Preconditions.checkState(itemResults.size() == 1, "Fabricating recipe must have only 1 output");
            ItemResult result = itemResults.getFirst();

            return new FabricatingRecipe(itemInputs, result, energyRequired, getGroup());
        }
    }

    private static class LTXIBuilder<R extends LTXIRecipe> extends LimaCustomRecipeBuilder<R, LTXIBuilder<R>>
    {
        private final int defaultTime;
        private final LTXIRecipeSupplier<R> factory;

        private int craftTime = -1;
        private @Nullable Holder<RecipeMode> mode;

        LTXIBuilder(ModResources resources, HolderLookup.Provider registries, LTXIRecipeSupplier<R> factory, int defaultTime)
        {
            super(resources, registries);
            this.defaultTime = defaultTime;
            this.factory = factory;
        }

        LTXIBuilder(ModResources resources, HolderLookup.Provider registries, LTXIRecipeSupplier<R> factory)
        {
            this(resources, registries, factory, LTXIRecipe.DEFAULT_CRAFTING_TIME);
        }

        LTXIBuilder<R> time(int craftTime)
        {
            this.craftTime = craftTime;
            return this;
        }

        LTXIBuilder<R> needsMode(Holder<RecipeMode> mode)
        {
            this.mode = mode;
            return this;
        }

        LTXIBuilder<R> needsMode(ResourceKey<RecipeMode> key)
        {
            return needsMode(registries.holderOrThrow(key));
        }

        LTXIBuilder<R> tryOutput(@Nullable ItemResult result)
        {
            return result != null ? output(result) : this;
        }

        // Commonly used inputs
        LTXIBuilder<R> water(int amount)
        {
            return fluidInput(FluidTags.WATER, amount);
        }

        LTXIBuilder<R> water(int amount, float consumeChance)
        {
            return randomFluidInput(FluidTags.WATER, amount, consumeChance);
        }

        @Override
        protected R buildRecipe()
        {
            int time = craftTime > 0 ? craftTime : defaultTime;
            return factory.apply(itemInputs, fluidInputs, itemResults, fluidResults, time, Optional.ofNullable(mode));
        }
    }

    private static class ScrubbingBuilder extends LimaCustomRecipeBuilder<AirScrubbingRecipe, ScrubbingBuilder>
    {
        private final Holder<RecipeMode> mode;

        private @Nullable ResourceKey<Level> dimension;
        private @Nullable HolderSet<Biome> biomes;
        private boolean needsWaterlog = false;

        ScrubbingBuilder(ModResources resources, HolderLookup.Provider registries, Holder<RecipeMode> mode)
        {
            super(resources, registries);
            this.mode = mode;
        }

        ScrubbingBuilder inDimension(ResourceKey<Level> dimension)
        {
            this.dimension = dimension;
            return this;
        }

        ScrubbingBuilder inBiomes(HolderSet<Biome> biomes)
        {
            this.biomes = biomes;
            return this;
        }

        ScrubbingBuilder inBiomes(TagKey<Biome> tagKey)
        {
            return inBiomes(registries.getOrThrow(tagKey));
        }

        ScrubbingBuilder requireWaterlog()
        {
            this.needsWaterlog = true;
            return this;
        }

        @Override
        protected AirScrubbingRecipe buildRecipe()
        {
            return new AirScrubbingRecipe(mode, new MachineLocation(Optional.ofNullable(dimension), Optional.ofNullable(biomes), needsWaterlog), itemResults, fluidResults);
        }
    }

    private static class GardenBuilder extends LTXIBuilder<GardenSimulatingRecipe>
    {
        GardenBuilder(ModResources resources, HolderLookup.Provider registries)
        {
            super(resources, registries, GardenSimulatingRecipe::new, 600);
        }

        @Override
        GardenBuilder needsMode(Holder<RecipeMode> mode)
        {
            return (GardenBuilder) super.needsMode(mode);
        }

        @Override
        GardenBuilder needsMode(ResourceKey<RecipeMode> key)
        {
            return (GardenBuilder) super.needsMode(key);
        }

        GardenBuilder reproduce(ItemLike cropItem, int outputCount)
        {
            randomInput(cropItem, 1, 0).output(ItemResult.of(LimaRegistryUtil.builtInHolder(cropItem.asItem()), outputCount));
            return this;
        }

        GardenBuilder reproduce(ItemLike cropItem)
        {
            return reproduce(cropItem, 1);
        }

        GardenBuilder growSeed(ItemLike seeds, ItemLike produce, int outputCount)
        {
            randomInput(seeds, 1, 0).output(ItemResult.of(LimaRegistryUtil.builtInHolder(produce.asItem()), outputCount));
            return this;
        }

        GardenBuilder growSeed(TagKey<Item> seedTag, ItemLike produce, int outputCount)
        {
            randomInput(seedTag, 1, 0).output(ItemResult.of(LimaRegistryUtil.builtInHolder(produce.asItem()), outputCount));
            return this;
        }
    }
}