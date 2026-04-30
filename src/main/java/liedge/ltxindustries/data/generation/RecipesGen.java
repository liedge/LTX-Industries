package liedge.ltxindustries.data.generation;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import liedge.limacore.data.generation.LimaRecipeProvider;
import liedge.limacore.data.generation.recipe.LimaCustomRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapedRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.RecipeResult;
import liedge.limacore.recipe.result.ResultCount;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.NeonLightColor;
import liedge.ltxindustries.integration.guideme.GuideMEIntegration;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.MutableUpgrades;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.recipe.*;
import liedge.ltxindustries.registry.bootstrap.LTXIRecipeModes;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIFluids;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static liedge.ltxindustries.LTXITags.Fluids.HYDROGEN_FLUIDS;
import static liedge.ltxindustries.LTXITags.Fluids.OXYGEN_FLUIDS;
import static liedge.ltxindustries.LTXITags.Items.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIUpgrades.*;
import static liedge.ltxindustries.registry.game.LTXIFluids.VIRIDIC_ACID;
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

    // Common holder sources
    private final HolderGetter<Item> items;
    private final HolderGetter<Fluid> fluids;

    private RecipesGen(HolderLookup.Provider registries, RecipeOutput output)
    {
        super(registries, output, LTXIndustries.RESOURCES);
        this.items = registries.lookupOrThrow(Registries.ITEM);
        this.fluids = registries.lookupOrThrow(Registries.FLUID);
    }

    @Override
    protected void buildRecipes()
    {
        //#region Crafting table recipes
        nineStorageRecipes(output, RAW_TITANIUM, RAW_TITANIUM_BLOCK);
        nineStorageRecipes(output, RAW_NIOBIUM, RAW_NIOBIUM_BLOCK);

        nuggetIngotBlockRecipes(output, "titanium", TITANIUM_NUGGET, TITANIUM_INGOT, TITANIUM_BLOCK);
        nuggetIngotBlockRecipes(output, "niobium", NIOBIUM_NUGGET, NIOBIUM_INGOT, NIOBIUM_BLOCK);
        nuggetIngotBlockRecipes(output, "slatesteel", SLATESTEEL_NUGGET, SLATESTEEL_INGOT, SLATESTEEL_BLOCK);

        shaped(TITANIUM_PANEL, 32).input('t', TITANIUM_INGOT).input('f', POLYMER_INGOT).patterns("tft", "f f", "tft").category(CraftingBookCategory.BUILDING).save(output);
        shaped(SMOOTH_TITANIUM_PANEL, 32).input('t', TITANIUM_INGOT).input('f', POLYMER_INGOT).patterns("ftf", "t t", "ftf").category(CraftingBookCategory.BUILDING).save(output);
        shaped(TILED_TITANIUM_PANEL, 4).input('p', TITANIUM_PANEL).patterns("pp", "pp").category(CraftingBookCategory.BUILDING).save(output);
        shaped(SLATESTEEL_PANEL, 32).input('s', SLATESTEEL_INGOT).input('f', POLYMER_INGOT).patterns("sfs", "f f", "sfs").category(CraftingBookCategory.BUILDING).save(output);
        shaped(SMOOTH_SLATESTEEL_PANEL, 32).input('s', SLATESTEEL_INGOT).input('f', POLYMER_INGOT).patterns("fsf", "s s", "fsf").category(CraftingBookCategory.BUILDING).save(output);
        shaped(TILED_SLATESTEEL_PANEL, 4).input('p', SLATESTEEL_PANEL).patterns("pp", "pp").category(CraftingBookCategory.BUILDING).save(output);

        shaped(TITANIUM_GEAR).input('i', TITANIUM_INGOT).input('n', items, NUGGETS_IRON).patterns("ini", "n n", "ini").save(output);
        shaped(SLATESTEEL_GEAR).input('i', SLATESTEEL_INGOT).input('n', items, NUGGETS_IRON).patterns("ini", "n n", "ini").save(output);
        shaped(T1_CIRCUIT).input('c', STONE_PRESSURE_PLATE).input('m', COPPER_INGOT).input('r', REDSTONE).input('t', TITANIUM_INGOT).patterns("tmt", "mcm", "rmr").save(output);
        shaped(T2_CIRCUIT).input('c', T1_CIRCUIT).input('m', GOLD_INGOT).input('r', REPEATER).input('t', TITANIUM_INGOT).input('b', COPPER_INGOT).patterns(" r ", "mcm", "tbt").save(output);
        shaped(OPTICAL_TECH_PART).input('c', T2_CIRCUIT).input('g', TINTED_GLASS).input('t', TITANIUM_INGOT).input('m', ELECTRIC_CHEMICAL).patterns("ggg", "tmt", "ctc").save(output);

        shaped(EMPTY_UPGRADE_MODULE).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('g', items, GLASS_BLOCKS_CHEAP).patterns("ggg", "gcg", "ttt").save(output);
        shaped(EMPTY_FABRICATION_BLUEPRINT, 2).input('l', items, DYES_LIME).input('p', PAPER).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).patterns("lll", "ppp", "tct").save(output);
        shaped(ITEMS_IO_CONFIG_CARD).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('m', CHEST).patterns(" m ", "tct", " t ").save(output);
        shaped(ENERGY_IO_CONFIG_CARD).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('m', ELECTRIC_CHEMICAL).patterns(" m ", "tct", " t ").save(output);
        shaped(FLUIDS_IO_CONFIG_CARD).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('m', BUCKET).patterns(" m ", "tct", " t ").save(output);

        shapeless(GUIDE_TABLET).condition(new ModLoadedCondition(GuideMEIntegration.MODID)).input(BOOK).input(TITANIUM_INGOT).input(items, DYES_LIME).save(output);
        shaped(defaultUpgradableItem(EPSILON_WRENCH, registries)).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).patterns("t t", " c ", " t ").save(output);

        // Machine recipes
        shaped(ENERGY_CELL_ARRAY).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('e', ELECTRIC_CHEMICAL).input('b', COPPER_BLOCK).patterns("tct", "ebe", "tct").save(output);
        shaped(DIGITAL_FURNACE).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('a', FURNACE).input('s', items, GLASS_BLOCKS_CHEAP).patterns("tct", "sas", "gcg").save(output);
        shaped(DIGITAL_SMOKER).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('a', SMOKER).input('s', items, GLASS_BLOCKS_CHEAP).patterns("tct", "sas", "gcg").save(output);
        shaped(DIGITAL_BLAST_FURNACE).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('a', BLAST_FURNACE).input('s', items, GLASS_BLOCKS_CHEAP).patterns("tct", "sas", "gcg").save(output);
        shaped(GRINDER).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('s', IRON_INGOT).patterns("tct", "sgs", "gcg").save(output);
        shaped(MATERIAL_FUSING_CHAMBER).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('a', BLAST_FURNACE).input('s', OBSIDIAN).patterns("tct", "sas", "gcg").save(output);
        shaped(ELECTROCENTRIFUGE).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T2_CIRCUIT).input('a', CAULDRON).input('s', TITANIUM_GLASS).patterns("gcg", "sas", "tct").save(output);
        shaped(MIXER).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T2_CIRCUIT).input('a', CAULDRON).input('s', TITANIUM_GLASS).patterns("tct", "sas", "gcg").save(output);
        shaped(VOLTAIC_INJECTOR).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('a', LIGHTNING_ROD).input('s', COPPER_INGOT).patterns("tct", "sas", "gcg").save(output);
        shaped(CHEM_LAB).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T2_CIRCUIT).input('a', SLATESTEEL_GEAR).input('s', TITANIUM_GLASS).patterns("tct", "sas", "gcg").save(output);
        shaped(ASSEMBLER).input('t', TITANIUM_INGOT).input('g', SLATESTEEL_GEAR).input('c', T2_CIRCUIT).input('a', CRAFTER).input('s', POLYMER_INGOT).input('o', OPTICAL_TECH_PART)
                .patterns("tot", "sas", "gcg").save(output);
        shaped(GEO_SYNTHESIZER).input('t', TITANIUM_INGOT).input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).input('a', COBBLESTONE).input('s', BUCKET).patterns("tct", "sas", "gcg").save(output);
        shaped(FABRICATOR).input('t', TITANIUM_INGOT).input('c', T3_CIRCUIT).input('a', CRAFTING_TABLE).input('g', SLATESTEEL_GEAR).input('o', OPTICAL_TECH_PART).patterns("tot", "cac", "gtg").save(output);
        shaped(AUTO_FABRICATOR).input('p', POLYMER_INGOT).input('c', T3_CIRCUIT).input('g', SLATESTEEL_GEAR).input('s', TITANIUM_GLASS).input('a', CRAFTER).input('o', OPTICAL_TECH_PART)
                .patterns("pop", "sas", "gcg").save(output);
        shaped(UPGRADE_STATION).input('t', TITANIUM_INGOT).input('b', TITANIUM_BLOCK).input('a', CRAFTING_TABLE).input('l', items, DYES_LIME).patterns("ttt", "lal", "tbt").save(output);

        // Standard machine systems
        upgradeShaped(output, registries, STANDARD_MACHINE_SYSTEMS, 1, builder -> builder
                .input('r', REDSTONE).input('c', T1_CIRCUIT).patterns(" r ", "rmr", " c "));
        upgradeShaped(output, registries, STANDARD_MACHINE_SYSTEMS, 2, builder -> builder
                .input('r', REDSTONE).input('c', T1_CIRCUIT).patterns(" r ", "rmr", " c "));
        upgradeShaped(output, registries, STANDARD_MACHINE_SYSTEMS, 3, builder -> builder
                .input('r', REDSTONE).input('c', T2_CIRCUIT).patterns(" r ", "rmr", " c "));
        upgradeShaped(output, registries, STANDARD_MACHINE_SYSTEMS, 4, builder -> builder
                .input('r', REDSTONE).input('c', T2_CIRCUIT).patterns(" r ", "rmr", " c "));
        upgradeShaped(output, registries, STANDARD_MACHINE_SYSTEMS, 5, builder -> builder
                .input('r', REDSTONE).input('c', T3_CIRCUIT).patterns(" r ", "rmr", " c "));
        upgradeShaped(output, registries, STANDARD_MACHINE_SYSTEMS, 6, builder -> builder
                .input('r', REDSTONE).input('c', T3_CIRCUIT).patterns(" r ", "rmr", " c "));

        upgradeShaped(output, registries, GPM_PARALLEL, 1, builder -> builder
                .input('g', TITANIUM_GEAR).input('c', T1_CIRCUIT).patterns(" c ", "gmg", " c "));
        upgradeShaped(output, registries, GPM_PARALLEL, 2, builder -> builder
                .input('g', TITANIUM_GEAR).input('s', SLATESTEEL_GEAR).input('c', T2_CIRCUIT).patterns("gcg", "sms", "gcg"));

        upgradeShaped(output, registries, GEO_SYNTHESIZER_PARALLEL, 1, builder -> builder
                .input('w', WATER_BUCKET).input('l', LAVA_BUCKET).input('c', T1_CIRCUIT).input('t', TITANIUM_INGOT).patterns("twt", "cmc", "tlt"));
        upgradeShaped(output, registries, GEO_SYNTHESIZER_PARALLEL, 2, builder -> builder
                .input('w', PACKED_ICE).input('l', MAGMA_BLOCK).input('c', T2_CIRCUIT).input('g', TITANIUM_GEAR).patterns("wgl", "cmc", "lgw"));
        upgradeShaped(output, registries, GEO_SYNTHESIZER_PARALLEL, 3, builder -> builder
                .input('w', BLUE_ICE).input('l', MAGMA_BLOCK).input('c', T3_CIRCUIT).input('g', SLATESTEEL_GEAR).patterns("wgl", "cmc", "lgw"));

        NEON_LIGHTS.forEach((color, holder) -> shaped(holder, 4).input('d', neonLightDye(color)).input('g', GLOWSTONE).patterns("dg", "gd").save(output));
        //#endregion

        // Smelting/cooking recipes
        oreSmeltBlast(output, "smelt_raw_titanium", RAW_TITANIUM, stackTemplate(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_stone_titanium", TITANIUM_ORE, stackTemplate(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_deepslate_titanium", DEEPSLATE_TITANIUM_ORE, stackTemplate(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_raw_niobium", RAW_NIOBIUM, stackTemplate(NIOBIUM_INGOT));
        oreSmeltBlast(output, "smelt_niobium_ore", NIOBIUM_ORE, stackTemplate(NIOBIUM_INGOT));

        orePebblesCooking(COAL_ORE_PEBBLES, COAL, 2);
        orePebblesCooking(COPPER_ORE_PEBBLES, COPPER_INGOT, 1);
        orePebblesCooking(IRON_ORE_PEBBLES, IRON_INGOT, 1);
        orePebblesCooking(LAPIS_ORE_PEBBLES, LAPIS_LAZULI, 6);
        orePebblesCooking(REDSTONE_ORE_PEBBLES, REDSTONE, 8);
        orePebblesCooking(GOLD_ORE_PEBBLES, GOLD_INGOT, 1);
        orePebblesCooking(DIAMOND_ORE_PEBBLES, DIAMOND, 1);
        orePebblesCooking(EMERALD_ORE_PEBBLES, EMERALD, 1);
        orePebblesCooking(QUARTZ_ORE_PEBBLES, QUARTZ, 4);
        orePebblesCooking(NETHERITE_ORE_PEBBLES, NETHERITE_SCRAP, 1);
        orePebblesCooking(TITANIUM_ORE_PEBBLES, TITANIUM_INGOT, 1);
        orePebblesCooking(NIOBIUM_ORE_PEBBLES, NIOBIUM_INGOT, 1);

        fabricatingRecipes();
        grindingRecipes();
        mfcRecipes();
        electroCentrifugingRecipes();
        mixingRecipes();
        energizingRecipes();
        chemLabRecipes();
        assemblingRecipes();
        geoSynthesisRecipes();
        gardenSimRecipes();
    }

    private void fabricatingRecipes()
    {
        // Default modules
        defaultModuleFabricating(output, registries, EPSILON_SHOVEL_DEFAULT, EPSILON_SHOVEL);
        defaultModuleFabricating(output, registries, EPSILON_WRENCH_DEFAULT, EPSILON_WRENCH);
        defaultModuleFabricating(output, registries, EPSILON_MELEE_DEFAULT, EPSILON_SWORD, EPSILON_AXE);
        defaultModuleFabricating(output, registries, WAYFINDER_DEFAULT, WAYFINDER);
        defaultModuleFabricating(output, registries, SERENITY_DEFAULT, SERENITY);
        defaultModuleFabricating(output, registries, MIRAGE_DEFAULT, MIRAGE);
        defaultModuleFabricating(output, registries, AURORA_DEFAULT, AURORA);
        defaultModuleFabricating(output, registries, STARGAZER_DEFAULT, STARGAZER);
        defaultModuleFabricating(output, registries, NOVA_DEFAULT, NOVA);
        defaultModuleFabricating(output, registries, HEAD_DEFAULT, WONDERLAND_HEAD);
        defaultModuleFabricating(output, registries, BODY_DEFAULT, WONDERLAND_BODY);
        defaultModuleFabricating(output, registries, LEGS_DEFAULT, WONDERLAND_LEGS);
        defaultModuleFabricating(output, registries, FEET_DEFAULT, WONDERLAND_FEET);

        fabricating(20_000_000)
                .input(CIRCUIT_BOARD)
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 12)
                .input(POLYMER_INGOT, 16)
                .input(REDSTONE, 32)
                .input(NIOBIUM_INGOT, 8)
                .input(DIAMOND, 8)
                .input(CHORUS_CHEMICAL, 4)
                .output(ItemResult.of(T4_CIRCUIT))
                .group("circuits")
                .save(output);
        fabricating(100_000_000)
                .input(CIRCUIT_BOARD)
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 16)
                .input(SLATESTEEL_INGOT, 24)
                .input(POLYMER_INGOT, 48)
                .input(NIOBIUM_INGOT, 16)
                .input(REDSTONE, 48)
                .input(AMETHYST_SHARD, 12)
                .input(SCULK_CHEMICAL, 8)
                .output(ItemResult.of(T5_CIRCUIT))
                .group("circuits")
                .save(output);
        fabricating(1_000_000)
                .input(T4_CIRCUIT)
                .input(TITANIUM_INGOT, 8)
                .input(POLYMER_INGOT, 8)
                .input(TITANIUM_GEAR, 2)
                .input(SLATESTEEL_GEAR)
                .input(TITANIUM_GLASS, 16)
                .output(ItemResult.of(DIGITAL_GARDEN))
                .group("machines")
                .save(output);

        fabricating(2_500_000)
                .input(T3_CIRCUIT)
                .input(OPTICAL_TECH_PART)
                .input(TITANIUM_INGOT, 20)
                .input(TITANIUM_GEAR, 4)
                .input(ELECTRIC_CHEMICAL, 32)
                .output(ItemResult.of(ARC_TURRET))
                .group("turrets")
                .save(output);
        fabricating(5_000_000)
                .input(T3_CIRCUIT, 2)
                .input(OPTICAL_TECH_PART, 2)
                .input(IMPULSE_TECH_PART, 2)
                .input(TITANIUM_INGOT, 24)
                .input(TITANIUM_GEAR, 6)
                .input(SLATESTEEL_GEAR, 2)
                .output(ItemResult.of(ROCKET_TURRET))
                .group("turrets").save(output);
        fabricating(20_000_000)
                .input(T4_CIRCUIT, 1)
                .input(OPTICAL_TECH_PART, 4)
                .input(TITANIUM_INGOT, 32)
                .input(POLYMER_INGOT, 12)
                .input(SLATESTEEL_INGOT, 8)
                .input(TITANIUM_GEAR, 8)
                .input(SLATESTEEL_GEAR, 4)
                .output(ItemResult.of(RAILGUN_TURRET))
                .group("turrets").save(output);

        // Tools fabricating
        final String toolFabGroup = "ltx/tool";
        equipmentFabricating(output, registries, EPSILON_DRILL, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 6)
                .input(SLATESTEEL_GEAR, 3)
                .input(LTX_LIME_PIGMENT, 6));
        equipmentFabricating(output, registries, EPSILON_SWORD, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 16)
                .input(SLATESTEEL_INGOT, 2)
                .input(LTX_LIME_PIGMENT, 4));
        equipmentFabricating(output, registries, EPSILON_SHOVEL, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 8)
                .input(POLYMER_INGOT, 2)
                .input(SLATESTEEL_GEAR)
                .input(LTX_LIME_PIGMENT, 2));
        equipmentFabricating(output, registries, EPSILON_AXE, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 6)
                .input(SLATESTEEL_GEAR, 3)
                .input(LTX_LIME_PIGMENT, 6));
        equipmentFabricating(output, registries, EPSILON_HOE, toolFabGroup, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 16)
                .input(POLYMER_INGOT, 4)
                .input(SLATESTEEL_GEAR, 2)
                .input(LTX_LIME_PIGMENT, 4));
        equipmentFabricating(output, registries, EPSILON_SHEARS, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(SLATESTEEL_INGOT, 2)
                .input(LTX_LIME_PIGMENT, 2));
        equipmentFabricating(output, registries, EPSILON_BRUSH, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 3)
                .input(SLATESTEEL_INGOT)
                .input(FEATHER, 3)
                .input(LTX_LIME_PIGMENT, 2));
        equipmentFabricating(output, registries, EPSILON_FISHING_ROD, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(SLATESTEEL_INGOT, 2)
                .input(STRING, 2)
                .input(LTX_LIME_PIGMENT, 4));
        equipmentFabricating(output, registries, EPSILON_LIGHTER, toolFabGroup, 500_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 3)
                .input(SLATESTEEL_INGOT)
                .input(FLINT)
                .input(LTX_LIME_PIGMENT, 2));

        // Weapons fabrication
        String weaponFabGroup = "ltx/weapon";
        equipmentFabricating(output, registries, WAYFINDER, weaponFabGroup + ".05", 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(items, DYES_LIME, 3)
                .input(GLOWSTONE_DUST, 8));
        equipmentFabricating(output, registries, SERENITY, weaponFabGroup + ".11", 500_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 24)
                .input(LTX_LIME_PIGMENT, 4));
        equipmentFabricating(output, registries, MIRAGE, weaponFabGroup + ".13", 750_000, builder -> builder
                .input(T1_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 32)
                .input(LTX_LIME_PIGMENT, 6));
        equipmentFabricating(output, registries, AURORA, weaponFabGroup + ".21", 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 16)
                .input(LTX_LIME_PIGMENT, 8));
        equipmentFabricating(output, registries, HANABI, weaponFabGroup + ".33", 20_000_000, builder -> builder
                .input(T3_CIRCUIT, 1)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 24)
                .input(SLATESTEEL_INGOT, 8)
                .input(LTX_LIME_PIGMENT, 12)
                .input(IMPULSE_TECH_PART, 2)
                .input(TITANIUM_GLASS, 6));
        equipmentFabricating(output, registries, STARGAZER, weaponFabGroup + ".37", 25_000_000, builder -> builder
                .input(T2_CIRCUIT, 6)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 32)
                .input(LTX_LIME_PIGMENT, 12)
                .input(OPTICAL_TECH_PART, 3));
        equipmentFabricating(output, registries, DAYBREAK, weaponFabGroup + ".41", 30_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 48)
                .input(SLATESTEEL_INGOT, 24)
                .input(LTX_LIME_PIGMENT, 16)
                .input(IMPULSE_TECH_PART, 3)
                .input(OPTICAL_TECH_PART, 1));
        equipmentFabricating(output, registries, NOVA, weaponFabGroup + ".77", 75_000_000, builder -> builder
                .input(T4_CIRCUIT, 1)
                .input(TITANIUM_INGOT, 32)
                .input(POLYMER_INGOT, 24)
                .input(SLATESTEEL_INGOT, 16)
                .input(LTX_LIME_PIGMENT, 8));

        // Bodysuit fabrication
        final String armorFabGroup = "ltx/armor";
        equipmentFabricating(output, registries, WONDERLAND_HEAD, armorFabGroup + ".1", 5_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 15)
                .input(LTX_LIME_PIGMENT, 5)
                .input(SLATESTEEL_INGOT, 10)
                .input(POLYMER_INGOT, 5));
        equipmentFabricating(output, registries, WONDERLAND_BODY, armorFabGroup + ".2", 8_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 24)
                .input(LTX_LIME_PIGMENT, 8)
                .input(SLATESTEEL_INGOT, 16)
                .input(POLYMER_INGOT, 8));
        equipmentFabricating(output, registries, WONDERLAND_LEGS, armorFabGroup + ".3", 7_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 21)
                .input(LTX_LIME_PIGMENT, 7)
                .input(SLATESTEEL_INGOT, 14)
                .input(POLYMER_INGOT, 7));
        equipmentFabricating(output, registries, WONDERLAND_FEET, armorFabGroup + ".4", 4_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 12)
                .input(LTX_LIME_PIGMENT, 4)
                .input(SLATESTEEL_INGOT, 8)
                .input(POLYMER_INGOT, 4));

        final String toolEUMGroup = "eum/tool";
        upgradeFabricating(output, registries, toolEUMGroup, EQUIPMENT_ENERGY_UPGRADE, 1, 100_000, builder -> builder
                .input(T1_CIRCUIT)
                .input(TITANIUM_INGOT, 2)
                .input(POLYMER_INGOT, 4)
                .input(ELECTRIC_CHEMICAL, 4));
        upgradeFabricating(output, registries, toolEUMGroup, EQUIPMENT_ENERGY_UPGRADE, 2, 250_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 4)
                .input(POLYMER_INGOT, 8)
                .input(ELECTRIC_CHEMICAL, 8));
        upgradeFabricating(output, registries, toolEUMGroup, EQUIPMENT_ENERGY_UPGRADE, 3, 500_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 8)
                .input(POLYMER_INGOT, 16)
                .input(GOLD_INGOT, 4)
                .input(ELECTRIC_CHEMICAL, 16));
        upgradeFabricating(output, registries, toolEUMGroup, EQUIPMENT_ENERGY_UPGRADE, 4, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(POLYMER_INGOT, 24)
                .input(NIOBIUM_INGOT, 2)
                .input(ELECTRIC_CHEMICAL, 32));
        upgradeFabricating(output, registries, toolEUMGroup, EPSILON_FISHING_LURE, 1, 100_000, builder -> builder
                .input(T1_CIRCUIT)
                .input(STRING, 4)
                .input(COD, 2));
        upgradeFabricating(output, registries, toolEUMGroup, EPSILON_FISHING_LURE, 2, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(STRING, 8)
                .input(COD, 4)
                .input(SALMON, 2));
        upgradeFabricating(output, registries, toolEUMGroup, EPSILON_FISHING_LURE, 3, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 2)
                .input(STRING, 8)
                .input(PUFFERFISH, 2));
        upgradeFabricating(output, registries, toolEUMGroup, EPSILON_FISHING_LURE, 4, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(SLATESTEEL_INGOT, 2)
                .input(STRING, 4)
                .input(CARBON_DUST, 12)
                .input(LTX_LIME_PIGMENT, 6)
                .input(PRISMARINE_CRYSTALS, 2));
        upgradeFabricating(output, registries, toolEUMGroup, EPSILON_FISHING_LURE, 5, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(SLATESTEEL_INGOT, 4)
                .input(POLYMER_INGOT, 8)
                .input(STRING, 8)
                .input(CARBON_DUST, 24)
                .input(LTX_LIME_PIGMENT, 12)
                .input(HEART_OF_THE_SEA));
        upgradeFabricating(output, registries, toolEUMGroup, TOOL_NETHERITE_LEVEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(DIAMOND, 3)
                .input(NETHERITE_INGOT)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 4));
        upgradeFabricating(output, registries, toolEUMGroup, EPSILON_OMNI_DRILL, 1, 20_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_INGOT, 32)
                .input(POLYMER_INGOT, 16)
                .input(SLATESTEEL_GEAR, 2)
                .input(LTX_LIME_PIGMENT, 8));
        upgradeFabricating(output, registries, toolEUMGroup, TREE_VEIN_MINE, 1, 250_00, builder -> builder
                .input(T1_CIRCUIT)
                .input(TITANIUM_GEAR, 2));
        upgradeFabricating(output, registries, toolEUMGroup, TOOL_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(POLYMER_INGOT, 8)
                .input(items, ItemTags.WOOL, 8)
                .input(SCULK_CHEMICAL, 4));

        UnaryOperator<FabricatingBuilder> directDrops = builder -> builder
                .input(T3_CIRCUIT, 3)
                .input(TITANIUM_INGOT, 16)
                .input(SLATESTEEL_INGOT, 8)
                .input(CHORUS_CHEMICAL, 8)
                .input(ENDER_PEARL, 8);
        upgradeFabricating(output, registries, toolEUMGroup, EQUIPMENT_BLOCK_DROPS_CAPTURE, 1, 15_000_000, directDrops);
        upgradeFabricating(output, registries, "combat", NO_ANGER_ATTACKS, 1, 1_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(PHANTOM_MEMBRANE, 4)
                .input(CHORUS_CHEMICAL, 12)
                .input(DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY), POTION)));
        upgradeFabricating(output, registries, "combat", MOB_DROPS_CAPTURE, 1, 15_000_000, directDrops);

        upgradeFabricating(output, registries, "eum/weapon", WEAPON_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 16)
                .input(POLYMER_INGOT, 16)
                .input(items, ItemTags.WOOL, 16)
                .input(SCULK_CHEMICAL, 8));

        upgradeFabricating(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(REDSTONE, 4));
        upgradeFabricating(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(T1_CIRCUIT, 4)
                .input(REDSTONE, 8));
        upgradeFabricating(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 3, 750_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(REDSTONE, 8)
                .input(BLAZE_POWDER, 4)
                .input(TITANIUM_INGOT, 4));
        upgradeFabricating(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 4, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(REDSTONE, 8)
                .input(BLAZE_POWDER, 8)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 4)
                .input(POLYMER_INGOT, 8));
        upgradeFabricating(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 5, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(REDSTONE, 8)
                .input(BLAZE_POWDER, 12)
                .input(TITANIUM_INGOT, 12)
                .input(SLATESTEEL_INGOT, 8)
                .input(POLYMER_INGOT, 16));
        upgradeFabricating(output, registries, "eum/enchant", SILK_TOUCH_ENCHANTMENT, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(EMERALD, 1)
                .input(SLIME_BALL, 8)
                .input(TITANIUM_GLASS, 4));
        UnaryOperator<FabricatingBuilder> multi1 = builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(IRON_INGOT, 4)
                .input(RABBIT_FOOT)
                .input(LAPIS_LAZULI, 4);
        UnaryOperator<FabricatingBuilder> multi2 = builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 6)
                .input(RABBIT_FOOT, 2)
                .input(LAPIS_LAZULI, 8);
        UnaryOperator<FabricatingBuilder> multi3 = builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(EMERALD, 2)
                .input(DIAMOND, 2)
                .input(LAPIS_BLOCK, 2);
        UnaryOperator<FabricatingBuilder> multi4 = builder -> builder
                .input(T3_CIRCUIT, 8)
                .input(EMERALD, 6)
                .input(DIAMOND, 6)
                .input(AMETHYST_BLOCK, 3)
                .input(NETHER_STAR, 1)
                .input(LAPIS_BLOCK, 4);
        UnaryOperator<FabricatingBuilder> multi5 = builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(EMERALD_BLOCK, 1)
                .input(DIAMOND_BLOCK, 1)
                .input(AMETHYST_BLOCK, 6)
                .input(NETHER_STAR, 2)
                .input(LAPIS_BLOCK, 8);
        upgradeFabricating(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 1, 125_000, multi1);
        upgradeFabricating(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 1, 125_000, multi1);
        upgradeFabricating(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 2, 250_000, multi2);
        upgradeFabricating(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 2, 250_000, multi2);
        upgradeFabricating(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 3, 500_000, multi3);
        upgradeFabricating(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 3, 500_000, multi3);
        upgradeFabricating(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 4, 1_000_000, multi4);
        upgradeFabricating(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 4, 1_000_000, multi4);
        upgradeFabricating(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 5, 10_000_000, multi5);
        upgradeFabricating(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 5, 10_000_000, multi5);

        upgradeFabricating(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(ZOMBIE_HEAD)
                .input(CREEPER_HEAD)
                .input(SKELETON_SKULL));
        upgradeFabricating(output, registries,"eum/enchant", RAZOR_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(DIAMOND, 2)
                .input(ZOMBIE_HEAD, 2)
                .input(CREEPER_HEAD, 2)
                .input(SKELETON_SKULL, 2));
        upgradeFabricating(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 3, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(ZOMBIE_HEAD, 4)
                .input(SKELETON_SKULL, 4)
                .input(CREEPER_HEAD, 4));
        upgradeFabricating(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 4, 2_00_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(WITHER_SKELETON_SKULL, 6)
                .input(PIGLIN_HEAD, 6));
        upgradeFabricating(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 5, 4_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(ZOMBIE_HEAD, 8)
                .input(CREEPER_HEAD, 8)
                .input(SKELETON_SKULL, 8)
                .input(WITHER_SKELETON_SKULL, 8)
                .input(PIGLIN_HEAD, 8)
                .input(DRAGON_HEAD, 1));

        upgradeFabricating(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 1, 300_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(GUNPOWDER, 8)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 2));
        upgradeFabricating(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 2, 600_000, builder -> builder
                .input(T2_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 12)
                .input(LAPIS_LAZULI, 16)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 4));
        upgradeFabricating(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 3, 900_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 16)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 8)
                .input(SPECIALIST_WEAPON_ENERGY, 2));
        upgradeFabricating(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 4, 1_200_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(SPECIALIST_WEAPON_ENERGY, 4)
                .input(EXPLOSIVES_WEAPON_ENERGY, 2));
        upgradeFabricating(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 5, 1_500_000, builder -> builder
                .input(T4_CIRCUIT, 4)
                .input(SLATESTEEL_INGOT, 2)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 16)
                .input(SPECIALIST_WEAPON_ENERGY, 8)
                .input(EXPLOSIVES_WEAPON_ENERGY, 4)
                .input(HEAVY_WEAPON_ENERGY, 2));

        upgradeFabricating(output, registries, "eum/weapon/gl", FLAME_GRENADE_CORE, 1, 2_500_000, builder -> builder
                .input(IMPULSE_TECH_PART, 1)
                .input(TITANIUM_INGOT, 4)
                .input(TITANIUM_GLASS, 8)
                .input(BLAZE_POWDER, 16));
        upgradeFabricating(output, registries, "eum/weapon/gl", CRYO_GRENADE_CORE, 1, 2_500_000, builder -> builder
                .input(IMPULSE_TECH_PART)
                .input(TITANIUM_INGOT, 4)
                .input(TITANIUM_GLASS, 8)
                .input(ICE, 16));
        upgradeFabricating(output, registries, "eum/weapon/gl", ELECTRIC_GRENADE_CORE, 1, 5_000_000, builder -> builder
                .input(IMPULSE_TECH_PART)
                .input(TITANIUM_GLASS, 16)
                .input(SLATESTEEL_INGOT, 8)
                .input(POLYMER_INGOT, 12)
                .input(ELECTRIC_CHEMICAL, 32));
        upgradeFabricating(output, registries, "eum/weapon/gl", ACID_GRENADE_CORE, 1, 25_000_000, builder -> builder
                .input(IMPULSE_TECH_PART, 2)
                .input(TITANIUM_GLASS, 32)
                .input(SLATESTEEL_INGOT, 16)
                .input(POLYMER_INGOT, 24)
                .input(VIRIDIC_WEAPON_CHEMICAL, 16));
        upgradeFabricating(output, registries, "eum/weapon/gl", NEURO_GRENADE_CORE, 1, 50_000_000, builder -> builder
                .input(IMPULSE_TECH_PART, 4)
                .input(TITANIUM_GLASS, 32)
                .input(SLATESTEEL_INGOT, 16)
                .input(POLYMER_INGOT, 24)
                .input(NEURO_CHEMICAL, 8));
        upgradeFabricating(output, registries, "eum/weapon", HANABI_SPEED_BOOST, 1, 750_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(IMPULSE_TECH_PART, 2)
                .input(PHANTOM_MEMBRANE, 2));
        upgradeFabricating(output, registries, "eum/weapon", HANABI_SPEED_BOOST, 2, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(IMPULSE_TECH_PART, 4)
                .input(PHANTOM_MEMBRANE, 4));

        final String armorEUMGroup = "eum/armor";
        upgradeFabricating(output, registries, armorEUMGroup, PASSIVE_NIGHT_VISION, 1, 250_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(OPTICAL_TECH_PART, 2)
                .input(GLOWSTONE_DUST, 8)
                .input(GOLDEN_CARROT, 2));

        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_PASSIVE_SHIELD, 1, 5_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 4)
                .input(ELECTRIC_CHEMICAL, 8));
        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_PASSIVE_SHIELD, 2, 10_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 8)
                .input(TITANIUM_GEAR, 1)
                .input(ELECTRIC_CHEMICAL, 16));
        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_PASSIVE_SHIELD, 3, 50_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_INGOT, 16)
                .input(TITANIUM_GEAR, 4)
                .input(SLATESTEEL_GEAR, 4)
                .input(ELECTRIC_CHEMICAL, 32));
        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_PASSIVE_SHIELD, 4, 100_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 32)
                .input(TITANIUM_GEAR, 8)
                .input(SLATESTEEL_GEAR, 8)
                .input(ELECTRIC_CHEMICAL, 64));

        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_DEFENSE, 1, 250_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 4)
                .input(SLATESTEEL_INGOT, 4));
        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_DEFENSE, 2, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 8)
                .input(POLYMER_INGOT, 4));
        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_DEFENSE, 3, 10_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(TITANIUM_INGOT, 16)
                .input(SLATESTEEL_INGOT, 16)
                .input(POLYMER_INGOT, 8));
        upgradeFabricating(output, registries, armorEUMGroup, ARMOR_DEFENSE, 4, 50_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 24)
                .input(SLATESTEEL_INGOT, 24)
                .input(POLYMER_INGOT, 12));

        upgradeFabricating(output, registries, armorEUMGroup, BREATHING_UNIT, 1, 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(ELECTRIC_CHEMICAL, 4)
                .input(POLYMER_INGOT, 4)
                .input(TITANIUM_GLASS, 4));

        upgradeFabricating(output, registries, armorEUMGroup, PASSIVE_SATURATION, 1, 100_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(SCULK_CHEMICAL, 16)
                .input(GOLDEN_APPLE, 32)
                .input(GOLDEN_CARROT, 32)
                .input(GLISTERING_MELON_SLICE, 32));

        upgradeFabricating(output, registries, armorEUMGroup, CREATIVE_FLIGHT, 1, 150_000_000, builder -> builder
                .input(T5_CIRCUIT)
                .input(CHORUS_CHEMICAL, 32)
                .input(PHANTOM_MEMBRANE, 16));

        upgradeFabricating(output, registries, "mum/gpm", ULTIMATE_MACHINE_SYSTEMS, 1, 250_000_000, false, builder -> builder
                .input(moduleIngredient(registries, STANDARD_MACHINE_SYSTEMS, 6))
                .input(T5_CIRCUIT, 2)
                .input(REDSTONE_BLOCK, 16)
                .input(SLATESTEEL_INGOT, 32));

        upgradeFabricating(output, registries, "mum/gpm", GPM_PARALLEL, 3, 50_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_GEAR, 3)
                .input(SLATESTEEL_GEAR, 3)
                .input(POLYMER_INGOT, 3)
                .input(CHORUS_CHEMICAL, 4));
        upgradeFabricating(output, registries, "mum/gpm", GPM_PARALLEL, 4, 100_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_GEAR, 4)
                .input(SLATESTEEL_GEAR, 4)
                .input(POLYMER_INGOT, 8)
                .input(SCULK_CHEMICAL, 4));

        upgradeFabricating(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 4)
                .input(ELECTRIC_CHEMICAL, 4)
                .input(COPPER_INGOT, 4));
        upgradeFabricating(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 2, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 4)
                .input(ELECTRIC_CHEMICAL, 8)
                .input(COPPER_INGOT, 8));
        upgradeFabricating(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 3, 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(ELECTRIC_CHEMICAL, 16)
                .input(GOLD_INGOT, 8));
        upgradeFabricating(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 4, 5_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 12)
                .input(SLATESTEEL_INGOT, 4)
                .input(ELECTRIC_CHEMICAL, 24)
                .input(NIOBIUM_INGOT, 4));
        upgradeFabricating(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 5, 20_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 16)
                .input(SLATESTEEL_INGOT, 8)
                .input(POLYMER_INGOT, 12)
                .input(ELECTRIC_CHEMICAL, 48)
                .input(NIOBIUM_INGOT, 8)
                .input(CHORUS_CHEMICAL, 8));

        final String fabricatorMUMGroup = "mum/fabricator";
        upgradeFabricating(output, registries, fabricatorMUMGroup, FABRICATOR_UPGRADE, 1, 500_000, builder -> builder
                .input(T2_CIRCUIT)
                .input(OPTICAL_TECH_PART, 2)
                .input(DIAMOND, 2)
                .input(ELECTRIC_CHEMICAL, 4));
        upgradeFabricating(output, registries, fabricatorMUMGroup, FABRICATOR_UPGRADE, 2, 1_000_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(OPTICAL_TECH_PART, 2)
                .input(DIAMOND, 4)
                .input(ELECTRIC_CHEMICAL, 8));
        upgradeFabricating(output, registries, fabricatorMUMGroup, FABRICATOR_UPGRADE, 3, 5_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(OPTICAL_TECH_PART, 4)
                .input(AMETHYST_SHARD, 4)
                .input(ELECTRIC_CHEMICAL, 16));
        upgradeFabricating(output, registries, fabricatorMUMGroup, FABRICATOR_UPGRADE, 4, 10_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(OPTICAL_TECH_PART, 4)
                .input(AMETHYST_BLOCK, 4)
                .input(ELECTRIC_CHEMICAL, 32));

        final String turretMUMGroup = "mum/turret";
        upgradeFabricating(output, registries, turretMUMGroup, TURRET_LOOTING, 1, 125_000, multi1);
        upgradeFabricating(output, registries, turretMUMGroup, TURRET_LOOTING, 2, 250_000, multi2);
        upgradeFabricating(output, registries, turretMUMGroup, TURRET_LOOTING, 3, 500_000, multi3);

        UnaryOperator<FabricatingBuilder> targetPredicates = builder -> builder
                .input(T3_CIRCUIT)
                .input(OPTICAL_TECH_PART)
                .input(PHANTOM_MEMBRANE);
        upgradeFabricating(output, registries, "targeting", ALL_ENTITIES_TARGETING, 1, 1_000_000, targetPredicates);
        upgradeFabricating(output, registries, "targeting", NEUTRAL_ENEMY_TARGETING, 1, 500_000, targetPredicates);
        upgradeFabricating(output, registries, "targeting", HOSTILE_TARGETING, 1, 500_000, targetPredicates);
    }

    private void grindingRecipes()
    {
        // Resource things
        grinding().input(STONE).output(ItemResult.of(COBBLESTONE)).save(output);
        grinding().input(items, COBBLESTONES_NORMAL)
                .output(ItemResult.of(GRAVEL))
                .output(ItemResult.of(FLINT, ResultCount.exactlyRandom(1, 0.25f), RecipeResult.NO_GROUP, false))
                .save(output);
        grinding().input(items, Tags.Items.GRAVELS).output(ItemResult.of(SAND)).save(output);
        grinding().input(items, CROPS_SUGAR_CANE).output(ItemResult.of(RESINOUS_BIOMASS)).output(ItemResult.of(SUGAR, 2)).save(output, "grind_sugar_cane");
        grinding().input(BAMBOO).output(ItemResult.of(RESINOUS_BIOMASS)).save(output, "grind_bamboo");
        grinding().input(LTXIItems.SPARK_FRUIT).output(ItemResult.of(ELECTRIC_CHEMICAL)).save(output);
        grinding().input(VITRIOL_BERRIES).output(ItemResult.of(ACIDIC_BIOMASS)).save(output);
        grinding().input(items, CARBON_SOURCES).output(ItemResult.of(CARBON_DUST)).save(output);
        grinding().input(items, LTXITags.Items.DEEPSLATE_GRINDABLES).output(ItemResult.of(DEEPSLATE_DUST)).save(output, "grind_deepslate");

        // Dyes
        Holder<RecipeMode> dyes = registries.holderOrThrow(LTXIRecipeModes.DYE_EXTRACTION);
        grinding()
                .input(items, GREEN_GROUP_DYE_SOURCES, 4)
                .output(ItemResult.of(GREEN_DYE, ResultCount.exactlyRandom(1, 0.8f)))
                .output(ItemResult.of(LIME_DYE, ResultCount.exactlyRandom(1, 0.5f)))
                .needsMode(dyes)
                .time(120)
                .save(output, "extract_green_group_dyes");
        grinding().input(SEA_PICKLE).output(ItemResult.of(LIME_DYE, 2)).needsMode(dyes).time(120).save(output);

        orePebbleGrinding(COAL_ORE_PEBBLES, Tags.Items.ORES_COAL, null, "coal", output);
        orePebbleGrinding(COPPER_ORE_PEBBLES, Tags.Items.ORES_COPPER, Tags.Items.RAW_MATERIALS_COPPER, "copper", output);
        orePebbleGrinding(IRON_ORE_PEBBLES, Tags.Items.ORES_IRON, Tags.Items.RAW_MATERIALS_IRON, "iron", output);
        orePebbleGrinding(LAPIS_ORE_PEBBLES, Tags.Items.ORES_LAPIS, null, "lapis", output);
        orePebbleGrinding(REDSTONE_ORE_PEBBLES, Tags.Items.ORES_REDSTONE, null, "redstone", output);
        orePebbleGrinding(GOLD_ORE_PEBBLES, Tags.Items.ORES_GOLD, Tags.Items.RAW_MATERIALS_GOLD, "gold", output);
        orePebbleGrinding(DIAMOND_ORE_PEBBLES, Tags.Items.ORES_DIAMOND, null, "diamond", output);
        orePebbleGrinding(EMERALD_ORE_PEBBLES, Tags.Items.ORES_EMERALD, null, "emerald", output);
        orePebbleGrinding(QUARTZ_ORE_PEBBLES, Tags.Items.ORES_QUARTZ, null, "quartz", output);
        grinding().input(items, ORES_NETHERITE_SCRAP).output(ItemResult.of(NETHERITE_ORE_PEBBLES, 2)).save(output, "grind_debris");
        orePebbleGrinding(TITANIUM_ORE_PEBBLES, LTXITags.Items.TITANIUM_ORES, LTXITags.Items.RAW_TITANIUM_MATERIALS, "titanium", output);
        orePebbleGrinding(NIOBIUM_ORE_PEBBLES, LTXITags.Items.NIOBIUM_ORES, LTXITags.Items.RAW_NIOBIUM_MATERIALS, "niobium", output);
        orePebbleGrinding(TIN_ORE_PEBBLES, ModResources.COMMON.itemTag("ores/tin"), ModResources.COMMON.itemTag("raw_materials/tin"), "tin", output, true);
        orePebbleGrinding(OSMIUM_ORE_PEBBLES, ModResources.COMMON.itemTag("ores/osmium"), ModResources.COMMON.itemTag("raw_materials/osmium"), "osmium", output, true);
        orePebbleGrinding(NICKEL_ORE_PEBBLES, ModResources.COMMON.itemTag("ores/nickel"), ModResources.COMMON.itemTag("raw_materials/nickel"), "nickel", output, true);
        orePebbleGrinding(LEAD_ORE_PEBBLES, ModResources.COMMON.itemTag("ores/lead"), ModResources.COMMON.itemTag("raw_materials/lead"), "lead", output, true);
        orePebbleGrinding(SILVER_ORE_PEBBLES, ModResources.COMMON.itemTag("ores/silver"), ModResources.COMMON.itemTag("raw_materials/silver"), "silver", output, true);
        orePebbleGrinding(URANIUM_ORE_PEBBLES, ModResources.COMMON.itemTag("ores/uranium"), ModResources.COMMON.itemTag("raw_materials/uranium"), "uranium", output, true);
        grinding().input(RAW_TITANIUM_CLUSTER).output(ItemResult.of(RAW_TITANIUM, 5)).save(output, "grind_titanium_clusters");
        grinding().input(RAW_NIOBIUM_CLUSTER).output(ItemResult.of(RAW_NIOBIUM, 5)).save(output, "grind_niobium_clusters");
        grinding().input(LTXIItems.GLOOM_SHROOM).output(ItemResult.of(NEURO_BLUE_PIGMENT, 2)).time(120).save(output, "shrooms_to_dye");
    }

    private void mfcRecipes()
    {
        fusing().input(NETHERITE_ORE_PEBBLES, 2).input(GOLD_INGOT).output(ItemResult.of(NETHERITE_INGOT)).save(output, "pebble_netherite");
        fusing().input(NETHERITE_SCRAP, 4).input(GOLD_INGOT, 1).output(ItemResult.of(NETHERITE_INGOT)).save(output, "scrap_netherite");
        NEON_LIGHTS.forEach((color, holder) -> fusing().input(items, NEON_LIGHT_MATERIALS, 2).input(neonLightDye(color)).time(80).output(ItemResult.of(holder, 8)).save(output));
        fusing().input(IRON_INGOT).input(CARBON_DUST).input(DEEPSLATE_DUST, 4).fluidInput(fluids, OXYGEN_FLUIDS, 250).time(400).output(ItemResult.of(SLATESTEEL_INGOT)).save(output);
        fusing().input(TITANIUM_INGOT).input(items, GEMS_QUARTZ, 3).output(ItemResult.of(TITANIUM_GLASS, 2)).save(output);
        fusing().input(AMETHYST_SHARD).input(SCULK_CHEMICAL, 4).output(ItemResult.of(ECHO_SHARD)).time(400).save(output);
        fusing().randomInput(SCULK_CATALYST, 1, 0f).randomInput(SCULK_CHEMICAL, 1, 0.5f).input(DIRT).output(ItemResult.of(SCULK)).save(output);
    }

    private void electroCentrifugingRecipes()
    {
        // Modes
        Holder<RecipeMode> electrolyze = registries.holderOrThrow(LTXIRecipeModes.ECF_ELECTROLYZE);
        Holder<RecipeMode> dyes = registries.holderOrThrow(LTXIRecipeModes.DYE_EXTRACTION);
        Holder<RecipeMode> dissolution = registries.holderOrThrow(LTXIRecipeModes.CHEM_DISSOLUTION);

        // Dyes
        electroCentrifuging().input(items, DYES_LIME).output(ItemResult.of(LTX_LIME_PIGMENT)).time(120).needsMode(dyes).save(output);
        electroCentrifuging().input(VITRIOL_BERRIES).output(ItemResult.of(VIRIDIC_GREEN_PIGMENT, 2)).time(120).needsMode(dyes).save(output);
        electroCentrifuging().input(LTXIItems.SPARK_FRUIT).output(ItemResult.of(ELECTRIC_CHARTREUSE_PIGMENT, 2)).time(120).needsMode(dyes).save(output);
        electroCentrifuging().input(LTXIItems.GLOOM_SHROOM).output(ItemResult.of(NEURO_BLUE_PIGMENT, 2)).time(120).needsMode(dyes).save(output);

        // Recipes
        electroCentrifuging()
                .input(MUD)
                .output(ItemResult.of(DIRT))
                .output(ItemResult.of(CLAY_BALL, ResultCount.between(1, 3)))
                .output(ItemResult.of(MANGROVE_PROPAGULE, ResultCount.exactlyRandom(1, 0.05f), RecipeResult.NO_GROUP, false))
                .fluidOutput(FluidResult.of(Fluids.WATER, 1000))
                .time(120)
                .save(output, "split_mud");

        electroCentrifuging()
                .needsMode(electrolyze)
                .fluidInput(Fluids.WATER, 1000)
                .fluidOutput(FluidResult.of(LTXIFluids.HYDROGEN, 1000))
                .fluidOutput(FluidResult.of(LTXIFluids.OXYGEN, 500))
                .time(300)
                .save(output, "water_electrolyzing");

        electroCentrifuging()
                .needsMode(dissolution)
                .fluidInput(VIRIDIC_ACID, 250)
                .input(CHORUS_FRUIT, 2)
                .output(ItemResult.of(CHORUS_CHEMICAL))
                .time(300)
                .save(output, "chorus_fruit_extraction");

        electroCentrifuging()
                .needsMode(dissolution)
                .fluidInput(VIRIDIC_ACID, 2000)
                .input(LTXIItems.GLOOM_SHROOM)
                .output(ItemResult.of(SCULK_CHEMICAL))
                .output(ItemResult.of(NEURO_CHEMICAL, ResultCount.exactlyRandom(1, 0.05f)))
                .time(400)
                .save(output, "gloom_shroom_extraction");
    }

    private void mixingRecipes()
    {
        mixing().input(DIRT).fluidInput(Fluids.WATER, 1000).output(ItemResult.of(MUD)).time(120).save(output);
        mixing().input(ACIDIC_BIOMASS, 4).fluidInput(Fluids.WATER, 1000).fluidOutput(FluidResult.of(VIRIDIC_ACID, 1000)).save(output);
        mixing().input(RESINOUS_BIOMASS, 2).fluidInput(VIRIDIC_ACID, 250).output(ItemResult.of(MONOMER_CHEMICAL)).save(output);
    }


    private void energizingRecipes()
    {
        Holder<RecipeMode> dyes = registries.holderOrThrow(LTXIRecipeModes.DYE_EXTRACTION);
        energizing().input(items, DYES_LIGHT_BLUE).output(ItemResult.of(ENERGY_BLUE_PIGMENT)).needsMode(dyes).time(120).save(output, "energize_light_blue_dyes");
        energizing().input(items, DYES_BLUE).output(ItemResult.of(ENERGY_BLUE_PIGMENT)).needsMode(dyes).time(120).save(output, "energize_blue_dyes");
        energizing().input(TITANIUM_GLASS).output(ItemResult.of(GLACIA_GLASS)).time(100).save(output);
    }


    private void chemLabRecipes()
    {
        chemLab().input(MONOMER_CHEMICAL).fluidInput(fluids, OXYGEN_FLUIDS, 125).output(ItemResult.of(POLYMER_INGOT)).save(output);
        chemLab().input(POLYMER_INGOT).input(COPPER_INGOT, 2).fluidInput(VIRIDIC_ACID, 125).output(ItemResult.of(CIRCUIT_BOARD)).save(output);
        chemLab()
                .input(ELECTRIC_CHEMICAL, 2)
                .fluidInput(VIRIDIC_ACID, 8000)
                .fluidInput(fluids, HYDROGEN_FLUIDS, 2000)
                .output(ItemResult.of(VIRIDIC_WEAPON_CHEMICAL))
                .time(900)
                .save(output);
    }

    private void assemblingRecipes()
    {
        assembling()
                .input(CIRCUIT_BOARD)
                .input(TITANIUM_INGOT, 2)
                .input(REDSTONE, 4)
                .input(COPPER_INGOT, 2)
                .output(ItemResult.of(T1_CIRCUIT, 2))
                .time(200)
                .save(output);

        assembling()
                .input(CIRCUIT_BOARD)
                .input(TITANIUM_INGOT, 4)
                .input(REDSTONE, 8)
                .input(GOLD_INGOT, 2)
                .output(ItemResult.of(T2_CIRCUIT, 2))
                .time(300)
                .save(output);

        assembling()
                .input(CIRCUIT_BOARD)
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(REDSTONE, 12)
                .input(QUARTZ, 8)
                .input(ELECTRIC_CHEMICAL, 4)
                .output(ItemResult.of(T3_CIRCUIT))
                .save(output);

        assembling()
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 16)
                .input(SLATESTEEL_INGOT, 8)
                .input(SLATESTEEL_GEAR)
                .input(ELECTRIC_CHEMICAL, 8)
                .fluidInput(fluids, HYDROGEN_FLUIDS, 16_000)
                .output(ItemResult.of(IMPULSE_TECH_PART))
                .save(output);

        assembling()
                .input(T2_CIRCUIT)
                .input(TITANIUM_GLASS, 2)
                .input(ELECTRIC_CHEMICAL, 2)
                .output(ItemResult.of(OPTICAL_TECH_PART))
                .time(200)
                .save(output);
    }

    private void geoSynthesisRecipes()
    {
        geoSynthesis().randomInput(COBBLESTONE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(COBBLESTONE)).save(output);
        geoSynthesis().randomInput(STONE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(STONE)).save(output);
        geoSynthesis().randomInput(COBBLED_DEEPSLATE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(COBBLED_DEEPSLATE)).save(output);
        geoSynthesis().randomInput(DEEPSLATE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(DEEPSLATE)).save(output);
        geoSynthesis().randomInput(GRANITE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(GRANITE)).save(output);
        geoSynthesis().randomInput(DIORITE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(DIORITE)).save(output);
        geoSynthesis().randomInput(ANDESITE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(ANDESITE)).save(output);
        geoSynthesis().randomInput(DRIPSTONE_BLOCK, 1, 0f).randomFluidInput(VIRIDIC_ACID, 1000, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).output(ItemResult.of(DRIPSTONE_BLOCK)).save(output);
        geoSynthesis().randomInput(BASALT, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(BASALT)).save(output);
        geoSynthesis().randomInput(BLACKSTONE, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).randomFluidInput(Fluids.LAVA, 1000, 0f).output(ItemResult.of(BLACKSTONE)).save(output);
        geoSynthesis().randomInput(OBSIDIAN, 1, 0f).randomFluidInput(Fluids.WATER, 1000, 0f).fluidInput(Fluids.LAVA, 1000).time(120).output(ItemResult.of(OBSIDIAN)).save(output);
    }

    private void gardenSimRecipes()
    {
        // Modes
        Holder<RecipeMode> farming = registries.holderOrThrow(LTXIRecipeModes.GS_FARMING);
        Holder<RecipeMode> woods = registries.holderOrThrow(LTXIRecipeModes.GS_WOODS);
        Holder<RecipeMode> orchard = registries.holderOrThrow(LTXIRecipeModes.GS_ORCHARD);
        Holder<RecipeMode> foliage = registries.holderOrThrow(LTXIRecipeModes.GS_FOLIAGE);

        // Crops
        garden().needsMode(farming).growSeed(WHEAT_SEEDS, WHEAT, 1).water(250).save(output);
        garden().needsMode(farming).reproduce(POTATO, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(CARROT, 2).water(250).save(output);
        garden().needsMode(farming).growSeed(BEETROOT_SEEDS, BEETROOT, 2).water(250).save(output);
        garden().needsMode(farming).reproduce(SWEET_BERRIES).water(250).save(output);
        garden().needsMode(farming).reproduce(COCOA_BEANS, 2).water(500).save(output);
        garden().needsMode(farming).growSeed(PUMPKIN_SEEDS, PUMPKIN, 1).water(1000).save(output);
        garden().needsMode(farming).growSeed(MELON_SEEDS, MELON, 1).water(1000).save(output);
        garden().needsMode(farming).reproduce(GLOW_BERRIES).water(250).save(output);
        garden().needsMode(farming).reproduce(BAMBOO).water(500).save(output);
        garden().needsMode(farming).reproduce(SUGAR_CANE).water(500).save(output);
        garden().needsMode(farming).reproduce(CACTUS).water(125).save(output);
        garden().needsMode(farming).reproduce(KELP).water(1000).save(output);
        garden().needsMode(farming).reproduce(SEA_PICKLE, 2).water(1000).save(output);
        garden().needsMode(farming).reproduce(NETHER_WART).water(250).save(output);
        garden().needsMode(farming).growSeed(CHORUS_FLOWER, CHORUS_FRUIT, 2).water(1000).save(output);

        // Flowers
        garden().reproduce(DANDELION).water(125).time(300).save(output);
        garden().reproduce(POPPY).water(125).time(300).save(output);
        garden().reproduce(BLUE_ORCHID).water(125).time(300).save(output);
        garden().reproduce(ALLIUM).water(125).time(300).save(output);
        garden().reproduce(AZURE_BLUET).water(125).time(300).save(output);
        garden().reproduce(RED_TULIP).water(125).time(300).save(output);
        garden().reproduce(ORANGE_TULIP).water(125).time(300).save(output);
        garden().reproduce(WHITE_TULIP).water(125).time(300).save(output);
        garden().reproduce(PINK_TULIP).water(125).time(300).save(output);
        garden().reproduce(OXEYE_DAISY).water(125).time(300).save(output);
        garden().reproduce(CORNFLOWER).water(125).time(300).save(output);
        garden().reproduce(LILY_OF_THE_VALLEY).water(125).time(300).save(output);
        garden().reproduce(WITHER_ROSE).water(500).save(output);
        garden().growSeed(TORCHFLOWER_SEEDS, TORCHFLOWER, 1).water(500).save(output);
        garden().reproduce(SUNFLOWER).water(250).time(300).save(output);
        garden().reproduce(LILAC).water(250).time(300).save(output);
        garden().reproduce(PEONY).water(250).time(300).save(output);
        garden().reproduce(ROSE_BUSH).water(250).time(300).save(output);
        garden().growSeed(PITCHER_POD, PITCHER_PLANT, 1).water(500).save(output);

        // Shrooms
        garden().needsMode(farming).reproduce(RED_MUSHROOM).water(250).save(output);
        garden().needsMode(farming).reproduce(BROWN_MUSHROOM).water(250).save(output);

        // Saplings
        garden().needsMode(farming).reproduce(OAK_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(BIRCH_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(SPRUCE_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(JUNGLE_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(DARK_OAK_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(ACACIA_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(CHERRY_SAPLING).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(MANGROVE_PROPAGULE).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(AZALEA).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(FLOWERING_AZALEA).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(CRIMSON_FUNGUS).water(250).time(300).save(output);
        garden().needsMode(farming).reproduce(WARPED_FUNGUS).water(250).time(300).save(output);

        // Woods
        garden().needsMode(woods).growSeed(OAK_SAPLING, OAK_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(BIRCH_SAPLING, BIRCH_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(SPRUCE_SAPLING, SPRUCE_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(JUNGLE_SAPLING, JUNGLE_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(DARK_OAK_SAPLING, DARK_OAK_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(ACACIA_SAPLING, ACACIA_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(CHERRY_SAPLING, CHERRY_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(MANGROVE_PROPAGULE, MANGROVE_LOG, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(CRIMSON_FUNGUS, CRIMSON_STEM, 4).water(1000).save(output);
        garden().needsMode(woods).growSeed(WARPED_FUNGUS, WARPED_STEM, 4).water(1000).save(output);

        // Orchard
        garden().needsMode(orchard).growSeed(items, APPLE_SAPLINGS, APPLE, 3).water(1000).save(output);

        // Foliage
        garden().needsMode(foliage).growSeed(OAK_SAPLING, OAK_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(BIRCH_SAPLING, BIRCH_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(SPRUCE_SAPLING, SPRUCE_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(JUNGLE_SAPLING, JUNGLE_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(DARK_OAK_SAPLING, DARK_OAK_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(ACACIA_SAPLING, ACACIA_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(CHERRY_SAPLING, CHERRY_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(AZALEA, AZALEA_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(FLOWERING_AZALEA, FLOWERING_AZALEA_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(MANGROVE_PROPAGULE, MANGROVE_LEAVES, 8).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(CRIMSON_FUNGUS, NETHER_WART_BLOCK, 2).water(1500).time(300).save(output);
        garden().needsMode(foliage).growSeed(WARPED_FUNGUS, WARPED_WART_BLOCK, 2).water(1500).time(300).save(output);

        // LTXI
        garden().reproduce(LTXIItems.SPARK_FRUIT).water(500).save(output);
        garden().reproduce(LTXIItems.VITRIOL_BERRIES).water(1000).save(output);
        garden().reproduce(LTXIItems.GLOOM_SHROOM).water(10_000).time(1200).save(output);
    }

    // Helpers
    private void orePebblesCooking(ItemLike orePebble, ItemLike resultItem, int resultCount)
    {
        String name = getItemName(orePebble);
        smelting(stackTemplate(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "smelt_" + name);
        blasting(stackTemplate(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "blast_" + name);
    }

    private LTXIBuilder<GrindingRecipe> grinding()
    {
        return new LTXIBuilder<>(resources, GrindingRecipe::new);
    }

    private void orePebbleGrinding(Holder<Item> orePebble, TagKey<Item> oreTag, @Nullable TagKey<Item> rawOreTag, String name, RecipeOutput output, boolean optional)
    {
        // Ore block recipe
        LTXIBuilder<?> oreRecipe = grinding().input(items, oreTag).output(ItemResult.of(orePebble, 3));
        if (optional) oreRecipe.condition(new NotCondition(new TagEmptyCondition<>(oreTag)));
        oreRecipe.save(output, "grind_" + name + "_ores");

        // Raw material recipe
        if (rawOreTag != null)
        {
            LTXIBuilder<?> rawMatRecipe = grinding().input(items, rawOreTag).output(ItemResult.of(orePebble, 2));
            if (optional) rawMatRecipe.condition(new NotCondition(new TagEmptyCondition<>(rawOreTag)));
            rawMatRecipe.save(output, "grind_raw_" + name + "_materials");
        }
    }

    private void orePebbleGrinding(Holder<Item> orePebble, TagKey<Item> oreTag, @Nullable TagKey<Item> rawOreTag, String name, RecipeOutput output)
    {
        orePebbleGrinding(orePebble, oreTag, rawOreTag, name, output, false);
    }

    private LTXIBuilder<MaterialFusingRecipe> fusing()
    {
        return new LTXIBuilder<>(resources, MaterialFusingRecipe::new);
    }

    private LTXIBuilder<ElectroCentrifugingRecipe> electroCentrifuging()
    {
        return new LTXIBuilder<>(resources, ElectroCentrifugingRecipe::new);
    }

    private LTXIBuilder<MixingRecipe> mixing()
    {
        return new LTXIBuilder<>(resources, MixingRecipe::new);
    }

    private LTXIBuilder<EnergizingRecipe> energizing()
    {
        return new LTXIBuilder<>(resources, EnergizingRecipe::new);
    }

    private LTXIBuilder<ChemicalReactingRecipe> chemLab()
    {
        return new LTXIBuilder<>(resources, ChemicalReactingRecipe::new);
    }

    private LTXIBuilder<AssemblingRecipe> assembling()
    {
        return new LTXIBuilder<>(resources, 400, AssemblingRecipe::new);
    }

    private LTXIBuilder<GeoSynthesisRecipe> geoSynthesis()
    {
        return new LTXIBuilder<>(resources, 60, GeoSynthesisRecipe::new);
    }

    private GardenBuilder garden()
    {
        return new GardenBuilder(resources);
    }

    private FabricatingBuilder fabricating(int energyRequired)
    {
        return new FabricatingBuilder(resources, energyRequired);
    }

    private Ingredient moduleIngredient(HolderLookup.Provider registries, ResourceKey<Upgrade> upgradeKey, int upgradeRank)
    {
        Holder<Upgrade> holder = registries.holderOrThrow(upgradeKey);
        return DataComponentIngredient.of(true, LTXIDataComponents.UPGRADE_ENTRY, new UpgradeEntry(holder, upgradeRank), UPGRADE_MODULE);
    }

    private ItemStackTemplate moduleTemplate(HolderLookup.Provider registries, ResourceKey<Upgrade> upgradeKey, int upgradeRank)
    {
        DataComponentPatch components = DataComponentPatch.builder().set(LTXIDataComponents.UPGRADE_ENTRY.get(), new UpgradeEntry(registries.holderOrThrow(upgradeKey), upgradeRank)).build();
        return new ItemStackTemplate(UPGRADE_MODULE, components);
    }

    private void upgradeShaped(RecipeOutput output, HolderLookup.Provider registries, ResourceKey<Upgrade> upgradeKey, int upgradeRank, UnaryOperator<LimaShapedRecipeBuilder> op)
    {
        Ingredient module = upgradeRank == 1 ? Ingredient.of(EMPTY_UPGRADE_MODULE) : moduleIngredient(registries, upgradeKey, upgradeRank - 1);
        LimaShapedRecipeBuilder builder = shaped(moduleTemplate(registries, upgradeKey, upgradeRank)).input('m', module);
        op.apply(builder).save(output, upgradeKey.identifier().getPath() + "_" + upgradeRank);
    }

    private void upgradeFabricating(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<Upgrade> upgradeKey, int upgradeRank, int energyRequired, boolean addBaseModuleInput, UnaryOperator<FabricatingBuilder> op)
    {
        ItemStackTemplate stackTemplate = moduleTemplate(registries, upgradeKey, upgradeRank);
        FabricatingBuilder builder = fabricating(energyRequired).group(group).output(ItemResult.fromVanilla(stackTemplate));

        if (addBaseModuleInput)
        {
            Ingredient moduleInput = upgradeRank == 1 ? Ingredient.of(EMPTY_UPGRADE_MODULE) : moduleIngredient(registries, upgradeKey, upgradeRank - 1);
            builder.input(moduleInput);
        }

        String name = upgradeKey.registry().getPath() + "s/" + upgradeKey.identifier().getPath() + "_" + upgradeRank;
        op.apply(builder).save(output, name);
    }

    private void upgradeFabricating(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<Upgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, true, op);
    }

    private void defaultModuleFabricating(RecipeOutput output, HolderLookup.Provider registries, ResourceKey<Upgrade> upgradeKey, ItemLike... equipmentItems)
    {
        upgradeFabricating(output, registries, "eum/defaults", upgradeKey, 1, 50_000, builder ->
                builder.randomInput(Ingredient.of(equipmentItems), 0f));
    }

    private void equipmentFabricating(RecipeOutput output, HolderLookup.Provider registries, Supplier<? extends UpgradableEquipmentItem> itemSupplier, String group, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        ItemStackTemplate stackTemplate = defaultUpgradableItem(itemSupplier, registries);
        FabricatingBuilder builder = fabricating(energyRequired).group(group).output(ItemResult.fromVanilla(stackTemplate));
        op.apply(builder).save(output);
    }

    private ItemStackTemplate defaultUpgradableItem(Supplier<? extends UpgradableEquipmentItem> itemSupplier, HolderLookup.Provider registries)
    {
        UpgradableEquipmentItem item = itemSupplier.get();
        ResourceKey<Upgrade> defaultKey = item.getDefaultUpgradeKey();
        DataComponentPatch components = DataComponentPatch.EMPTY;

        if (defaultKey != null)
        {
            Holder<Upgrade> upgrade = registries.holderOrThrow(defaultKey);
            components = DataComponentPatch.builder().set(LTXIDataComponents.UPGRADES.get(), MutableUpgrades.create().set(upgrade).build()).build();
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
            case VIRIDIC_GREEN -> Either.left(VIRIDIC_GREEN_PIGMENT);
            case NEURO_BLUE -> Either.left(NEURO_BLUE_PIGMENT);
            default -> Either.right(Objects.requireNonNull(color.getDyeColor()).getTag());
        };
        return either.map(Ingredient::of, tagKey -> Ingredient.of(items.getOrThrow(tagKey)));
    }

    // Builder classes
    private static class FabricatingBuilder extends LimaCustomRecipeBuilder<FabricatingRecipe, FabricatingBuilder>
    {
        private final int energyRequired;

        FabricatingBuilder(ModResources resources, int energyRequired)
        {
            super(resources);
            this.energyRequired = energyRequired;
        }

        @Override
        protected FabricatingRecipe buildRecipe()
        {
            Preconditions.checkState(fluidInputs.isEmpty(), "Fabricating recipes do not support fluid inputs.");
            Preconditions.checkState(fluidResults.isEmpty(), "Fabricating recipes do not support fluid outputs.");
            Preconditions.checkState(itemResults.size() == 1, "Fabricating recipe must have only 1 output");
            ItemResult result = itemResults.getFirst();

            return new FabricatingRecipe(itemInputs, result, energyRequired, getGroupOrBlank());
        }
    }

    private static class LTXIBuilder<R extends LTXIRecipe> extends LimaCustomRecipeBuilder<R, LTXIBuilder<R>>
    {
        private final int defaultTime;
        private final LTXIRecipeSupplier<R> factory;

        private int craftTime = -1;
        @Nullable
        private Holder<RecipeMode> mode;

        LTXIBuilder(ModResources resources, int defaultTime, LTXIRecipeSupplier<R> factory)
        {
            super(resources);
            this.defaultTime = defaultTime;
            this.factory = factory;
        }

        LTXIBuilder(ModResources resources, LTXIRecipeSupplier<R> factory)
        {
            this(resources, LTXIRecipe.DEFAULT_CRAFTING_TIME, factory);
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

        LTXIBuilder<R> needsMode(HolderGetter<RecipeMode> holders, ResourceKey<RecipeMode> key)
        {
            return needsMode(holders.getOrThrow(key));
        }

        @Override
        protected R buildRecipe()
        {
            int time = craftTime > 0 ? craftTime : defaultTime;
            return factory.apply(itemInputs, fluidInputs, itemResults, fluidResults, time, Optional.ofNullable(mode));
        }
    }

    private static class GardenBuilder extends LTXIBuilder<GardenSimulatingRecipe>
    {
        GardenBuilder(ModResources modResources)
        {
            super(modResources, 600, GardenSimulatingRecipe::new);
        }

        @Override
        GardenBuilder needsMode(Holder<RecipeMode> mode)
        {
            return (GardenBuilder) super.needsMode(mode);
        }

        @Override
        GardenBuilder needsMode(HolderGetter<RecipeMode> holders, ResourceKey<RecipeMode> key)
        {
            return (GardenBuilder) super.needsMode(holders, key);
        }

        GardenBuilder water(int amount)
        {
            fluidInput(Fluids.WATER, amount);
            return this;
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

        GardenBuilder growSeed(HolderGetter<Item> holders, TagKey<Item> seedTag, ItemLike produce, int outputCount)
        {
            randomInput(holders, seedTag, 1, 0).output(ItemResult.of(LimaRegistryUtil.builtInHolder(produce.asItem()), outputCount));
            return this;
        }
    }
}