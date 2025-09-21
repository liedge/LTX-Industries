package liedge.ltxindustries.data.generation;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import liedge.limacore.data.generation.LimaRecipeProvider;
import liedge.limacore.data.generation.recipe.LimaCustomRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.function.ObjectIntFunction;
import liedge.limacore.recipe.ItemResult;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.NeonLightColor;
import liedge.ltxindustries.integration.guideme.GuideMEIntegration;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseEntry;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgradeEntry;
import liedge.ltxindustries.recipe.*;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIFluids;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static liedge.ltxindustries.LTXITags.Items.NEON_LIGHT_MATERIALS;
import static liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIMachineUpgrades.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
import static net.minecraft.world.item.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class RecipesGen extends LimaRecipeProvider
{
    RecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries, LTXIndustries.RESOURCES);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries)
    {
        //#region Crafting table recipes
        SpecialRecipeBuilder.special(DefaultUpgradeModuleRecipe::new).save(output, modResources.location("default_upgrade_module"));

        nineStorageRecipes(output, RAW_TITANIUM, RAW_TITANIUM_BLOCK);
        nineStorageRecipes(output, RAW_NIOBIUM, RAW_NIOBIUM_BLOCK);

        nuggetIngotBlockRecipes(output, "titanium", TITANIUM_NUGGET, TITANIUM_INGOT, TITANIUM_BLOCK);
        nuggetIngotBlockRecipes(output, "niobium", NIOBIUM_NUGGET, NIOBIUM_INGOT, NIOBIUM_BLOCK);
        nuggetIngotBlockRecipes(output, "slatesteel", SLATESTEEL_NUGGET, SLATESTEEL_INGOT, SLATESTEEL_BLOCK);

        shaped(TITANIUM_PANEL, 32).input('t', TITANIUM_INGOT).input('f', POLYMER_INGOT).patterns("tft", "f f", "tft").bookCategory(CraftingBookCategory.BUILDING).save(output);
        shaped(SMOOTH_TITANIUM_PANEL, 32).input('t', TITANIUM_INGOT).input('f', POLYMER_INGOT).patterns("ftf", "t t", "ftf").bookCategory(CraftingBookCategory.BUILDING).save(output);

        shaped(T1_CIRCUIT).input('c', STONE_PRESSURE_PLATE).input('m', COPPER_INGOT).input('r', REDSTONE).input('t', TITANIUM_INGOT).patterns("tmt", "mcm", "rmr").save(output);
        shaped(T2_CIRCUIT).input('c', T1_CIRCUIT).input('m', GOLD_INGOT).input('r', REPEATER).input('t', TITANIUM_INGOT).input('b', COPPER_INGOT).patterns(" r ", "mcm", "tbt").save(output);
        shaped(T3_CIRCUIT).input('c', T2_CIRCUIT).input('r', COMPARATOR).input('t', TITANIUM_INGOT).input('b', COPPER_INGOT).patterns(" r ", "rcr", "tbt").save(output);

        shaped(EMPTY_UPGRADE_MODULE).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('g', GLASS_BLOCKS_CHEAP).patterns("ggg", "gcg", "ttt").save(output);
        shaped(EMPTY_FABRICATION_BLUEPRINT, 2).input('l', DYES_LIME).input('p', PAPER).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).patterns("lll", "ppp", "tct").save(output);

        shaped(defaultUpgradableItem(LTX_WRENCH, registries)).input('t', TITANIUM_INGOT).input('l', DYES_LIME).patterns("t t", " l ", " t ").save(output);

        shapeless(GuideMEIntegration.createGuideTabletItem())
                .condition(new ModLoadedCondition("guideme"))
                .input(BOOK)
                .input(TITANIUM_INGOT)
                .input(DYES_LIME)
                .save(output, "guide_tablet");

        // Machine recipes
        shaped(ENERGY_CELL_ARRAY).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('r', LIGHTNING_ROD).input('b', COPPER_BLOCK).patterns("trt", "cbc", "trt").save(output);
        shaped(DIGITAL_FURNACE).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('a', FURNACE).input('g', GLASS_BLOCKS_CHEAP).patterns("tct", "gag", "tct").save(output);
        shaped(DIGITAL_SMOKER).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('a', SMOKER).input('g', GLASS_BLOCKS_CHEAP).patterns("tct", "gag", "tct").save(output);
        shaped(DIGITAL_BLAST_FURNACE).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('a', BLAST_FURNACE).input('g', GLASS_BLOCKS_CHEAP).patterns("tct", "gag", "tct").save(output);
        shaped(GRINDER).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('a', GRINDSTONE).input('i', IRON_INGOT).patterns("tct", "iai", "tct").save(output);
        shaped(MATERIAL_FUSING_CHAMBER).input('t', TITANIUM_INGOT).input('c', T2_CIRCUIT).input('a', BLAST_FURNACE).input('o', OBSIDIAN).patterns("tct", "oao", "tct").save(output);
        shaped(ELECTROCENTRIFUGE).input('t', TITANIUM_INGOT).input('c', T2_CIRCUIT).input('a', CAULDRON).input('g', TITANIUM_GLASS).patterns("gcg", "gag", "tct").save(output);
        shaped(MIXER).input('t', TITANIUM_INGOT).input('c', T2_CIRCUIT).input('a', CAULDRON).input('g', TITANIUM_GLASS).patterns("tct", "gag", "tct").save(output);
        shaped(VOLTAIC_INJECTOR).input('t', TITANIUM_INGOT).input('c', T1_CIRCUIT).input('a', STONE_PRESSURE_PLATE).input('r', LIGHTNING_ROD).patterns("tct", "rar", "tct").save(output);
        shaped(CHEM_LAB).input('t', TITANIUM_INGOT).input('c', T3_CIRCUIT).input('a', BREWING_STAND).input('g', TITANIUM_GLASS).patterns("tct", "gag", "tct").save(output);
        shaped(FABRICATOR).input('p', POLYMER_INGOT).input('t', TITANIUM_INGOT).input('c', T3_CIRCUIT).input('a', CRAFTING_TABLE).patterns("pcp", "cac", "ttt").save(output);
        shaped(AUTO_FABRICATOR).input('p', POLYMER_INGOT).input('t', TITANIUM_INGOT).input('c', T3_CIRCUIT).input('s', SLATESTEEL_INGOT).input('g', TITANIUM_GLASS).input('a', CRAFTER)
                .patterns("pcp", "gag", "tst").save(output);
        shaped(EQUIPMENT_UPGRADE_STATION).input('t', TITANIUM_INGOT).input('a', ANVIL).input('l', DYES_LIME).patterns("ttt",  "lal", "ttt").save(output);

        // Standard machine systems
        final String smsRecipeName = STANDARD_MACHINE_SYSTEMS.location().getPath();
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 1)).input('m', EMPTY_UPGRADE_MODULE).input('r', REDSTONE).input('c', T1_CIRCUIT)
                .patterns(" r ", "rmr", " c ").save(output, smsRecipeName + "_1");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 2)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 1)).input('r', REDSTONE).input('c', T1_CIRCUIT)
                .patterns(" r ", "rmr", " c ").save(output, smsRecipeName + "_2");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 3)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 2)).input('r', REDSTONE).input('c', T2_CIRCUIT)
                .patterns(" r ", "rmr", " c ").save(output, smsRecipeName + "_3");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 4)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 3)).input('r', REDSTONE).input('c', T2_CIRCUIT)
                .patterns(" r ", "rmr", " c ").save(output, smsRecipeName + "_4");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 5)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 4)).input('r', REDSTONE).input('c', T3_CIRCUIT)
                .patterns(" r ", "rmr", " c ").save(output, smsRecipeName + "_5");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 6)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 5)).input('r', REDSTONE).input('c', T3_CIRCUIT)
                .patterns(" r ", "rmr", " c ").save(output, smsRecipeName + "_6");

        NEON_LIGHTS.forEach((color, holder) -> shaped(holder, 4).input('d', neonLightDye(color)).input('g', GLOWSTONE).patterns("dg", "gd").save(output));
        //#endregion

        // Smelting/cooking recipes
        oreSmeltBlast(output, "smelt_raw_titanium", RAW_TITANIUM, stackOf(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_stone_titanium", TITANIUM_ORE, stackOf(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_deepslate_titanium", DEEPSLATE_TITANIUM_ORE, stackOf(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_raw_niobium", RAW_NIOBIUM, stackOf(NIOBIUM_INGOT));
        oreSmeltBlast(output, "smelt_niobium_ore", NIOBIUM_ORE, stackOf(NIOBIUM_INGOT));

        orePebblesCooking(COAL_ORE_PEBBLES, COAL, 2, output);
        orePebblesCooking(COPPER_ORE_PEBBLES, COPPER_INGOT, 1, output);
        orePebblesCooking(IRON_ORE_PEBBLES, IRON_INGOT, 1, output);
        orePebblesCooking(LAPIS_ORE_PEBBLES, LAPIS_LAZULI, 6, output);
        orePebblesCooking(REDSTONE_ORE_PEBBLES, REDSTONE, 8, output);
        orePebblesCooking(GOLD_ORE_PEBBLES, GOLD_INGOT, 1, output);
        orePebblesCooking(DIAMOND_ORE_PEBBLES, DIAMOND, 1, output);
        orePebblesCooking(EMERALD_ORE_PEBBLES, EMERALD, 1, output);
        orePebblesCooking(QUARTZ_ORE_PEBBLES, QUARTZ, 4, output);
        orePebblesCooking(NETHERITE_ORE_PEBBLES, NETHERITE_SCRAP, 1, output);
        orePebblesCooking(TITANIUM_ORE_PEBBLES, TITANIUM_INGOT, 1, output);
        orePebblesCooking(NIOBIUM_ORE_PEBBLES, NIOBIUM_INGOT, 1, output);

        grindingRecipes(output);
        mfcRecipes(output);
        electroCentrifugingRecipes(output);
        mixingRecipes(output);
        energizingRecipes(output);
        chemLabRecipes(output);

        // Fabricating recipes
        fabricating(250_000)
                .input(CIRCUIT_BOARD)
                .input(REDSTONE, 4)
                .input(COPPER_INGOT, 2)
                .input(TITANIUM_INGOT, 2)
                .output(T1_CIRCUIT, 2)
                .group("circuits")
                .save(output);
        fabricating(500_000)
                .input(CIRCUIT_BOARD)
                .input(REDSTONE, 6)
                .input(GOLD_INGOT, 4)
                .input(TITANIUM_INGOT, 4)
                .output(T2_CIRCUIT, 2)
                .group("circuits")
                .save(output);
        fabricating(1_000_000)
                .input(CIRCUIT_BOARD)
                .input(REDSTONE, 8)
                .input(GOLD_INGOT, 2)
                .input(QUARTZ, 8)
                .input(TITANIUM_INGOT, 8)
                .output(T3_CIRCUIT, 2)
                .group("circuits")
                .save(output);
        fabricating(20_000_000)
                .input(CIRCUIT_BOARD)
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 12)
                .input(NIOBIUM_INGOT, 8)
                .input(POLYMER_INGOT, 16)
                .input(REDSTONE, 32)
                .input(DIAMOND, 8)
                .output(T4_CIRCUIT)
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
                .input(ECHO_SHARD, 8)
                .input(AMETHYST_SHARD, 16)
                .output(T5_CIRCUIT)
                .group("circuits")
                .save(output);
        fabricating(2_000_000)
                .input(ANVIL)
                .input(TITANIUM_INGOT, 20)
                .input(SLATESTEEL_BLOCK)
                .input(T4_CIRCUIT)
                .output(MOLECULAR_RECONSTRUCTOR)
                .group("machines")
                .save(output);

        fabricating(500_000)
                .input(GUNPOWDER, 8)
                .input(TITANIUM_INGOT, 4)
                .input(T2_CIRCUIT)
                .output(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .group("tech_parts")
                .requiresAdvancement()
                .unlockedBy("visited_fortress", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.holderOrThrow(BuiltinStructures.FORTRESS))))
                .save(output);
        fabricating(2_500_000)
                .input(TARGETING_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 32)
                .input(SLATESTEEL_INGOT, 16)
                .input(T3_CIRCUIT, 2)
                .output(ROCKET_TURRET)
                .group("turrets").save(output);
        fabricating(5_000_000)
                .input(TARGETING_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 32)
                .input(SLATESTEEL_INGOT, 16)
                .input(T4_CIRCUIT, 2)
                .output(RAILGUN_TURRET)
                .group("turrets").save(output);

        // Tools fabricating
        upgradeableItemFabricating(LTX_DRILL, registries, 1_000_000)
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 9)
                .input(SLATESTEEL_INGOT, 3)
                .input(LTX_LIME_PIGMENT, 6)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_SWORD, registries, 1_000_000)
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(SLATESTEEL_INGOT, 2)
                .input(LTX_LIME_PIGMENT, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_SHOVEL, registries, 1_000_000)
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 3)
                .input(SLATESTEEL_INGOT, 1)
                .input(LTX_LIME_PIGMENT, 2)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_AXE, registries, 1_000_000)
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 9)
                .input(SLATESTEEL_INGOT, 3)
                .input(LTX_LIME_PIGMENT, 6)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_HOE, registries, 1_000_000)
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(SLATESTEEL_INGOT, 2)
                .input(LTX_LIME_PIGMENT, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_SHEARS, registries, 500_000)
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(SLATESTEEL_INGOT, 2)
                .input(LTX_LIME_PIGMENT, 2)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_BRUSH, registries, 500_000)
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 3)
                .input(SLATESTEEL_INGOT)
                .input(FEATHER, 3)
                .input(LTX_LIME_PIGMENT, 2)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_FISHING_ROD, registries, 500_000)
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 6)
                .input(SLATESTEEL_INGOT, 2)
                .input(STRING, 2)
                .input(LTX_LIME_PIGMENT, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_LIGHTER, registries, 500_000)
                .input(T2_CIRCUIT)
                .input(TITANIUM_INGOT, 3)
                .input(SLATESTEEL_INGOT)
                .input(FLINT)
                .input(LTX_LIME_PIGMENT, 2)
                .group("ltx/tool").save(output);

        // Weapons fabrication
        upgradeableItemFabricating(SUBMACHINE_GUN, registries, 500_000)
                .input(TITANIUM_INGOT, 24)
                .input(LTX_LIME_PIGMENT, 4)
                .input(T1_CIRCUIT, 2)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(SHOTGUN, registries, 1_000_000)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 16)
                .input(LTX_LIME_PIGMENT, 8)
                .input(T2_CIRCUIT, 4)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(GRENADE_LAUNCHER, registries, 20_000_000)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 24)
                .input(SLATESTEEL_INGOT, 8)
                .input(LTX_LIME_PIGMENT, 12)
                .input(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .input(TITANIUM_GLASS, 6)
                .input(T2_CIRCUIT, 6)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(LINEAR_FUSION_RIFLE, registries, 25_000_000)
                .input(TITANIUM_INGOT, 24)
                .input(POLYMER_INGOT, 32)
                .input(LTX_LIME_PIGMENT, 12)
                .input(TITANIUM_GLASS, 8)
                .input(AMETHYST_SHARD, 2)
                .input(T2_CIRCUIT, 6)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(ROCKET_LAUNCHER, registries, 30_000_000)
                .input(TITANIUM_INGOT, 48)
                .input(SLATESTEEL_INGOT, 24)
                .input(LTX_LIME_PIGMENT, 16)
                .input(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .input(T3_CIRCUIT, 8)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(HEAVY_PISTOL, registries, 50_000_000)
                .input(TITANIUM_INGOT, 16)
                .input(POLYMER_INGOT, 24)
                .input(SLATESTEEL_INGOT, 8)
                .input(LTX_LIME_PIGMENT, 8)
                .input(T4_CIRCUIT, 1)
                .requiresAdvancement()
                .unlockedBy("kill_boss", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(LTXITags.EntityTypes.HIGH_THREAT_TARGETS)))
                .group("ltx/weapon").save(output);

        final String eumTools = "eum/tool";
        equipmentModuleFab(output, registries, eumTools, EPSILON_FISHING_LURE, 1, 100_000, builder -> builder
                .input(T1_CIRCUIT)
                .input(STRING, 4)
                .input(COD, 2));
        equipmentModuleFab(output, registries, eumTools, EPSILON_FISHING_LURE, 2, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(STRING, 8)
                .input(COD, 4)
                .input(SALMON, 2));
        equipmentModuleFab(output, registries, eumTools, EPSILON_FISHING_LURE, 3, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 2)
                .input(STRING, 8)
                .input(PUFFERFISH, 2));
        equipmentModuleFab(output, registries, eumTools, EPSILON_FISHING_LURE, 4, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(SLATESTEEL_INGOT, 2)
                .input(STRING, 4)
                .input(CARBON_DUST, 12)
                .input(LTX_LIME_PIGMENT, 6)
                .input(PRISMARINE_CRYSTALS, 2));
        equipmentModuleFab(output, registries, eumTools, EPSILON_FISHING_LURE, 5, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(SLATESTEEL_INGOT, 4)
                .input(POLYMER_INGOT, 8)
                .input(STRING, 8)
                .input(CARBON_DUST, 24)
                .input(LTX_LIME_PIGMENT, 12)
                .input(HEART_OF_THE_SEA));
        equipmentModuleFab(output, registries, eumTools, TOOL_NETHERITE_LEVEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(DIAMOND, 3)
                .input(NETHERITE_INGOT)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 4));
        equipmentModuleFab(output, registries, eumTools, EPSILON_OMNI_DRILL, 1, 20_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(LTX_LIME_PIGMENT, 16));
        equipmentModuleFab(output, registries, eumTools, TOOL_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT)
                .input(TITANIUM_INGOT, 4)
                .input(SCULK_SENSOR, 1)
                .input(ItemTags.WOOL, 8));

        UnaryOperator<FabricatingBuilder> directDrops = builder -> builder
                .input(T4_CIRCUIT)
                .input(NETHER_STAR)
                .input(ENDER_CHEST)
                .input(ENDER_PEARL, 8)
                .input(ECHO_SHARD);
        equipmentModuleFab(output, registries, "eum/tool", TOOL_DIRECT_DROPS, 1, 15_000_000, directDrops);
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_DIRECT_DROPS, 1, 15_000_000, directDrops);

        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_ARMOR_PIERCE, 1, 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(OBSIDIAN, 8)
                .input(TITANIUM_INGOT, 8));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_ARMOR_PIERCE, 2, 2_500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(OBSIDIAN, 32)
                .input(TITANIUM_INGOT, 32));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_ARMOR_PIERCE, 3, 5_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(OBSIDIAN, 64)
                .input(SLATESTEEL_INGOT, 4));

        equipmentModuleFab(output, registries, "eum/weapon", HIGH_IMPACT_ROUNDS, 1, 1_000_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TNT, 8)
                .input(PISTON, 2));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 16)
                .input(ECHO_SHARD, 2)
                .input(ItemTags.WOOL, 16));
        equipmentModuleFab(output, registries, "eum/weapon", UNIVERSAL_STEALTH_DAMAGE, 1, 750_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(PHANTOM_MEMBRANE, 12)
                .input(ENDER_PEARL, 8)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY)).build(), POTION)));

        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_SHIELD_REGEN, 1, 1_500_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(GOLDEN_APPLE, 1)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.FIRE_RESISTANCE)).build(), POTION))
                .input(SHIELD)
                .input(DIAMOND, 4));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_SHIELD_REGEN, 2, 3_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(GOLDEN_APPLE, 2)
                .input(DIAMOND, 8)
                .input(AMETHYST_SHARD, 2));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_SHIELD_REGEN, 3, 5_000_000, builder -> builder
                .input(T4_CIRCUIT, 1)
                .input(AMETHYST_SHARD, 8)
                .input(ENCHANTED_GOLDEN_APPLE));

        equipmentModuleFab(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(REDSTONE, 4));
        equipmentModuleFab(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(T1_CIRCUIT, 4)
                .input(REDSTONE, 8));
        equipmentModuleFab(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 3, 750_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(REDSTONE, 8)
                .input(BLAZE_POWDER, 4)
                .input(TITANIUM_INGOT, 4));
        equipmentModuleFab(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 4, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(REDSTONE, 8)
                .input(BLAZE_POWDER, 8)
                .input(TITANIUM_INGOT, 8)
                .input(SLATESTEEL_INGOT, 4)
                .input(POLYMER_INGOT, 8));
        equipmentModuleFab(output, registries, "eum/enchant", EFFICIENCY_ENCHANTMENT, 5, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(REDSTONE, 8)
                .input(BLAZE_POWDER, 12)
                .input(TITANIUM_INGOT, 12)
                .input(SLATESTEEL_INGOT, 8)
                .input(POLYMER_INGOT, 16));
        equipmentModuleFab(output, registries, "eum/enchant", SILK_TOUCH_ENCHANTMENT, 1, 500_000, builder -> builder
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
        equipmentModuleFab(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 1, 125_000, multi1);
        equipmentModuleFab(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 1, 125_000, multi1);
        equipmentModuleFab(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 2, 250_000, multi2);
        equipmentModuleFab(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 2, 250_000, multi2);
        equipmentModuleFab(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 3, 500_000, multi3);
        equipmentModuleFab(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 3, 500_000, multi3);
        equipmentModuleFab(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 4, 1_000_000, multi4);
        equipmentModuleFab(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 4, 1_000_000, multi4);
        equipmentModuleFab(output, registries, "eum/enchant", LOOTING_ENCHANTMENT, 5, 10_000_000, multi5);
        equipmentModuleFab(output, registries, "eum/enchant", FORTUNE_ENCHANTMENT, 5, 10_000_000, multi5);

        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(ZOMBIE_HEAD)
                .input(CREEPER_HEAD)
                .input(SKELETON_SKULL));
        equipmentModuleFab(output, registries,"eum/enchant", RAZOR_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(DIAMOND, 2)
                .input(ZOMBIE_HEAD, 2)
                .input(CREEPER_HEAD, 2)
                .input(SKELETON_SKULL, 2));
        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 3, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 2)
                .input(ZOMBIE_HEAD, 4)
                .input(SKELETON_SKULL, 4)
                .input(CREEPER_HEAD, 4));
        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 4, 2_00_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(WITHER_SKELETON_SKULL, 6)
                .input(PIGLIN_HEAD, 6));
        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 5, 4_000_000, builder -> builder
                .input(T4_CIRCUIT)
                .input(ZOMBIE_HEAD, 8)
                .input(CREEPER_HEAD, 8)
                .input(SKELETON_SKULL, 8)
                .input(WITHER_SKELETON_SKULL, 8)
                .input(PIGLIN_HEAD, 8)
                .input(DRAGON_HEAD, 1));

        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 1, 300_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(GUNPOWDER, 8)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 2));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 2, 600_000, builder -> builder
                .input(T2_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 12)
                .input(LAPIS_LAZULI, 16)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 4));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 3, 900_000, builder -> builder
                .input(T3_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 16)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 8)
                .input(SPECIALIST_WEAPON_ENERGY, 2));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 4, 1_200_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(SPECIALIST_WEAPON_ENERGY, 4)
                .input(EXPLOSIVES_WEAPON_ENERGY, 2));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 5, 1_500_000, builder -> builder
                .input(T4_CIRCUIT, 4)
                .input(SLATESTEEL_INGOT, 2)
                .input(LIGHTWEIGHT_WEAPON_ENERGY, 16)
                .input(SPECIALIST_WEAPON_ENERGY, 8)
                .input(EXPLOSIVES_WEAPON_ENERGY, 4)
                .input(HEAVY_WEAPON_ENERGY, 2));

        equipmentModuleFab(output, registries, "eum/weapon/gl", FLAME_GRENADE_CORE, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(FIRE_CHARGE, 21));
        equipmentModuleFab(output, registries, "eum/weapon/gl", CRYO_GRENADE_CORE, 1, 250_000, builder -> builder
                .input(T1_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(SNOW_BLOCK, 16)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_SLOWNESS)).build(), POTION)));
        equipmentModuleFab(output, registries, "eum/weapon/gl", ELECTRIC_GRENADE_CORE, 1, 500_000, builder -> builder
                .input(T2_CIRCUIT, 6)
                .input(TITANIUM_INGOT, 8)
                .input(LIGHTNING_ROD, 4));
        equipmentModuleFab(output, registries, "eum/weapon/gl", ACID_GRENADE_CORE, 1, 1_000_000, builder -> builder
                .input(T3_CIRCUIT, 8)
                .input(TITANIUM_INGOT, 16)
                .requiresAdvancement()
                .unlockedBy("kill_bogged", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.BOGGED))));
        equipmentModuleFab(output, registries, "eum/weapon/gl", NEURO_GRENADE_CORE, 1, 5_000_000, builder -> builder
                .input(T4_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 32)
                .input(NETHER_STAR)
                .requiresAdvancement()
                .unlockedBy("kill_warden", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.WARDEN))));
        equipmentModuleFab(output, registries, "eum/weapon", GRENADE_LAUNCHER_PROJECTILE_SPEED, 1, 750_000, builder -> builder
                .input(T2_CIRCUIT, 2)
                .input(FIREWORK_ROCKET, 9)
                .input(PHANTOM_MEMBRANE, 2));
        equipmentModuleFab(output, registries, "eum/weapon", GRENADE_LAUNCHER_PROJECTILE_SPEED, 2, 2_000_000, builder -> builder
                .input(T3_CIRCUIT, 3)
                .input(FIREWORK_ROCKET, 36)
                .input(PHANTOM_MEMBRANE, 8));
        equipmentModuleFab(output, registries, "eum/weapon", HEAVY_PISTOL_GOD_ROUNDS, 1, 500_000_000, builder -> builder
                .input(T5_CIRCUIT, 4)
                .input(SLATESTEEL_BLOCK, 4)
                .requiresAdvancement()
                .unlockedBy(HEAVY_PISTOL));

        machineModuleFab(output, registries, "mum/gpm", ULTIMATE_MACHINE_SYSTEMS, 1, 250_000_000, false, null, builder -> builder
                .input(mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 6))
                .input(T5_CIRCUIT, 2)
                .input(REDSTONE_BLOCK, 16)
                .input(SLATESTEEL_INGOT, 32));

        machineModuleFab(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 1, 250_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(COPPER_INGOT, 6));
        machineModuleFab(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 2, 500_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(COPPER_INGOT, 6)
                .input(T2_CIRCUIT, 2));
        machineModuleFab(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 3, 1_000_000, builder -> builder
                .input(LIGHTNING_ROD, 4)
                .input(GOLD_INGOT, 12)
                .input(T2_CIRCUIT, 2));
        machineModuleFab(output, registries, "mum/eca", ECA_CAPACITY_UPGRADE, 4, 5_000_000, builder -> builder
                .input(LIGHTNING_ROD, 4)
                .input(NIOBIUM_INGOT, 12)
                .input(T3_CIRCUIT, 2));

        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 1, 500_000, builder -> builder
                .input(LTX_LIME_PIGMENT, 4)
                .input(DIAMOND, 2)
                .input(TITANIUM_INGOT, 8)
                .input(T2_CIRCUIT, 2)
                .input(REDSTONE, 8));
        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 2, 1_000_000, builder -> builder
                .input(LTX_LIME_PIGMENT, 4)
                .input(DIAMOND, 4)
                .input(TITANIUM_INGOT, 8)
                .input(T3_CIRCUIT, 2)
                .input(REDSTONE, 8));
        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 3, 5_000_000, builder -> builder
                .input(LTX_LIME_PIGMENT, 8)
                .input(AMETHYST_SHARD, 4)
                .input(TITANIUM_INGOT, 16)
                .input(T4_CIRCUIT)
                .input(REDSTONE, 32));
        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 4, 10_000_000, builder -> builder
                .input(LTX_LIME_PIGMENT, 8)
                .input(AMETHYST_SHARD, 8)
                .input(TITANIUM_INGOT, 16)
                .input(T4_CIRCUIT, 2)
                .input(REDSTONE, 32));
    }

    private void grindingRecipes(RecipeOutput output)
    {
        grinding().input(STONE).output(COBBLESTONE).save(output);
        grinding().input(COBBLESTONES_NORMAL).output(GRAVEL).output(FLINT, 1, 0.25f).save(output);
        grinding().input(Tags.Items.GRAVELS).output(SAND).save(output);
        grinding().input(CROPS_SUGAR_CANE).output(RESINOUS_BIOMASS).output(SUGAR, 2).save(output, "grind_sugar_cane");
        grinding().input(BAMBOO).output(RESINOUS_BIOMASS).save(output, "grind_bamboo");
        grinding().input(VITRIOL_BERRIES).output(ACIDIC_BIOMASS).save(output);
        grinding().input(CompoundIngredient.of(Ingredient.of(CHARCOAL), Ingredient.of(ItemTags.COALS))).output(CARBON_DUST).save(output);
        grinding().input(LTXITags.Items.DEEPSLATE_GRINDABLES).output(DEEPSLATE_DUST).save(output, "grind_deepslate");

        orePebbleGrinding(COAL_ORE_PEBBLES, Tags.Items.ORES_COAL, null, "coal", output);
        orePebbleGrinding(COPPER_ORE_PEBBLES, Tags.Items.ORES_COPPER, Tags.Items.RAW_MATERIALS_COPPER, "copper", output);
        orePebbleGrinding(IRON_ORE_PEBBLES, Tags.Items.ORES_IRON, Tags.Items.RAW_MATERIALS_IRON, "iron", output);
        orePebbleGrinding(LAPIS_ORE_PEBBLES, Tags.Items.ORES_LAPIS, null, "lapis", output);
        orePebbleGrinding(REDSTONE_ORE_PEBBLES, Tags.Items.ORES_REDSTONE, null, "redstone", output);
        orePebbleGrinding(GOLD_ORE_PEBBLES, Tags.Items.ORES_GOLD, Tags.Items.RAW_MATERIALS_GOLD, "gold", output);
        orePebbleGrinding(DIAMOND_ORE_PEBBLES, Tags.Items.ORES_DIAMOND, null, "diamond", output);
        orePebbleGrinding(EMERALD_ORE_PEBBLES, Tags.Items.ORES_EMERALD, null, "emerald", output);
        orePebbleGrinding(QUARTZ_ORE_PEBBLES, Tags.Items.ORES_QUARTZ, null, "quartz", output);
        grinding().input(ORES_NETHERITE_SCRAP).output(NETHERITE_ORE_PEBBLES, 2).save(output, "grind_debris");
        orePebbleGrinding(TITANIUM_ORE_PEBBLES, LTXITags.Items.TITANIUM_ORES, LTXITags.Items.RAW_TITANIUM_MATERIALS, "titanium", output);
        orePebbleGrinding(NIOBIUM_ORE_PEBBLES, LTXITags.Items.NIOBIUM_ORES, LTXITags.Items.RAW_NIOBIUM_MATERIALS, "niobium", output);
        grinding().input(RAW_TITANIUM_CLUSTER).output(RAW_TITANIUM, 5).save(output, "grind_titanium_clusters");
        grinding().input(RAW_NIOBIUM_CLUSTER).output(RAW_NIOBIUM, 5).save(output, "grind_niobium_clusters");
    }

    private void mfcRecipes(RecipeOutput output)
    {
        fusing().input(NETHERITE_ORE_PEBBLES, 2).input(GOLD_INGOT).output(NETHERITE_INGOT).save(output, "pebble_netherite");
        fusing().input(NETHERITE_SCRAP, 4).input(GOLD_INGOT, 1).output(NETHERITE_INGOT).save(output, "scrap_netherite");
        NEON_LIGHTS.forEach((color, holder) -> fusing().input(NEON_LIGHT_MATERIALS, 2).input(neonLightDye(color)).time(80).output(holder, 8).save(output));
        fusing().input(IRON_INGOT).input(CARBON_DUST).input(DEEPSLATE_DUST, 4).fluidInput(LTXIFluids.OXYGEN, 250).time(400).output(SLATESTEEL_INGOT).save(output);
        fusing().input(TITANIUM_INGOT).input(GEMS_QUARTZ, 3).output(TITANIUM_GLASS, 2).save(output);
    }

    private void electroCentrifugingRecipes(RecipeOutput output)
    {
        electroCentrifuging().input(DYES_LIME).output(LTX_LIME_PIGMENT).save(output);
        electroCentrifuging().input(ACIDIC_BIOMASS).output(VIRIDIC_GREEN_PIGMENT, 4).time(120).save(output);

        electroCentrifuging()
                .input(MUD)
                .output(DIRT)
                .output(CLAY_BALL)
                .output(MANGROVE_PROPAGULE, 1, 0.05f)
                .fluidOutput(Fluids.WATER, 1000)
                .time(120)
                .save(output, "split_mud");

        electroCentrifuging()
                .fluidInput(Fluids.WATER, 1000)
                .fluidOutput(LTXIFluids.HYDROGEN, 1000)
                .fluidOutput(LTXIFluids.OXYGEN, 500)
                .time(300)
                .save(output, "water_electrolyzing");
    }

    private void mixingRecipes(RecipeOutput output)
    {
        mixing().input(DIRT).fluidInput(Fluids.WATER, 1000).output(MUD).time(120).save(output);
        mixing().input(ACIDIC_BIOMASS, 4).fluidInput(Fluids.WATER, 1000).fluidOutput(LTXIFluids.VIRIDIC_ACID, 1000).save(output);
        mixing().input(RESINOUS_BIOMASS, 2).fluidInput(LTXIFluids.VIRIDIC_ACID, 250).output(MONOMER_CHEMICAL).save(output);
    }


    private void energizingRecipes(RecipeOutput output)
    {
        energizing().input(LTX_LIME_PIGMENT).output(ELECTRIC_CHARTREUSE_PIGMENT).time(120).save(output);
        energizing().input(DYES_LIGHT_BLUE).output(ENERGY_BLUE_PIGMENT).time(120).save(output);
        energizing().input(DYES_BLUE).output(NEURO_BLUE_PIGMENT).time(120).save(output);
    }


    private void chemLabRecipes(RecipeOutput output)
    {
        chemLab().input(MONOMER_CHEMICAL).fluidInput(LTXIFluids.OXYGEN, 100).output(POLYMER_INGOT).save(output);
        chemLab().input(POLYMER_INGOT).input(COPPER_INGOT, 2).fluidInput(LTXIFluids.VIRIDIC_ACID, 125).output(CIRCUIT_BOARD).save(output);
    }

    // Helpers
    private void orePebblesCooking(ItemLike orePebble, ItemLike resultItem, int resultCount, RecipeOutput output)
    {
        String name = getItemName(orePebble);
        smelting(stackOf(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "smelt_" + name);
        blasting(stackOf(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "blast_" + name);
    }

    private LTXIBuilder<GrindingRecipe> grinding()
    {
        return new LTXIBuilder<>(modResources, GrindingRecipe::new);
    }

    private void orePebbleGrinding(ItemLike orePebble, TagKey<Item> oreTag, @Nullable TagKey<Item> rawOreTag, String name, RecipeOutput output)
    {
        grinding().input(oreTag).output(orePebble, 3).save(output, "grind_" + name + "_ores");
        if (rawOreTag != null) grinding().input(rawOreTag).output(orePebble, 2).save(output, "grind_raw_" + name + "_materials");
    }

    private LTXIBuilder<MaterialFusingRecipe> fusing()
    {
        return new LTXIBuilder<>(modResources, MaterialFusingRecipe::new);
    }

    private LTXIBuilder<ElectroCentrifugingRecipe> electroCentrifuging()
    {
        return new LTXIBuilder<>(modResources, ElectroCentrifugingRecipe::new);
    }

    private LTXIBuilder<MixingRecipe> mixing()
    {
        return new LTXIBuilder<>(modResources, MixingRecipe::new);
    }

    private LTXIBuilder<EnergizingRecipe> energizing()
    {
        return new LTXIBuilder<>(modResources, EnergizingRecipe::new);
    }

    private LTXIBuilder<ChemicalReactingRecipe> chemLab()
    {
        return new LTXIBuilder<>(modResources, ChemicalReactingRecipe::new);
    }

    private FabricatingBuilder fabricating(int energyRequired)
    {
        return new FabricatingBuilder(modResources, energyRequired);
    }

    private FabricatingBuilder upgradeableItemFabricating(Supplier<? extends UpgradableEquipmentItem> itemSupplier, HolderLookup.Provider registries, int energyRequired)
    {
        return fabricating(energyRequired).output(defaultUpgradableItem(itemSupplier, registries));
    }

    private <U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> Ingredient moduleIngredient(HolderLookup.Provider registries, ResourceKey<U> upgradeKey, int upgradeRank, ItemLike moduleItem, DataComponentType<UE> componentType, ObjectIntFunction<Holder<U>, UE> entryFactory)
    {
        Holder<U> upgradeHolder = registries.holderOrThrow(upgradeKey);
        return DataComponentIngredient.of(true, componentType, entryFactory.applyWithInt(upgradeHolder, upgradeRank), moduleItem);
    }

    private Ingredient eumIngredient(HolderLookup.Provider registries, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleIngredient(registries, upgradeKey, upgradeRank, EQUIPMENT_UPGRADE_MODULE, LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY.get(), EquipmentUpgradeEntry::new);
    }

    private Ingredient mumIngredient(HolderLookup.Provider registries, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleIngredient(registries, upgradeKey, upgradeRank, MACHINE_UPGRADE_MODULE, LTXIDataComponents.MACHINE_UPGRADE_ENTRY.get(), MachineUpgradeEntry::new);
    }

    private <U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> ItemStack moduleStack(HolderLookup.Provider registries, ResourceKey<U> upgradeKey, int upgradeRank, ItemLike moduleItem, DataComponentType<UE> componentType, ObjectIntFunction<Holder<U>, UE> entryFactory)
    {
        Holder<U> upgrade = registries.holderOrThrow(upgradeKey);
        ItemStack stack = new ItemStack(moduleItem);
        stack.set(componentType, entryFactory.applyWithInt(upgrade, upgradeRank));
        return stack;
    }

    private ItemStack eumStack(HolderLookup.Provider registries, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleStack(registries, upgradeKey, upgradeRank, EQUIPMENT_UPGRADE_MODULE, LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY.get(), EquipmentUpgradeEntry::new);
    }

    private ItemStack mumStack(HolderLookup.Provider registries, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleStack(registries, upgradeKey, upgradeRank, MACHINE_UPGRADE_MODULE, LTXIDataComponents.MACHINE_UPGRADE_ENTRY.get(), MachineUpgradeEntry::new);
    }

    private <U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> void upgradeFabricating(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<U> upgradeKey, int upgradeRank, int energyRequired, DataComponentType<UE> componentType, ObjectIntFunction<Holder<U>, UE> entryFactory, ItemLike moduleItem, boolean addBaseModuleInput, @Nullable String suffix, UnaryOperator<FabricatingBuilder> op)
    {
        FabricatingBuilder builder = fabricating(energyRequired).group(group).output(moduleStack(registries, upgradeKey, upgradeRank, moduleItem, componentType, entryFactory));

        if (addBaseModuleInput)
        {
            Ingredient moduleIngredient = upgradeRank == 1 ? Ingredient.of(EMPTY_UPGRADE_MODULE) : moduleIngredient(registries, upgradeKey, upgradeRank - 1, moduleItem, componentType, entryFactory);
            builder.input(moduleIngredient);
        }

        String name = upgradeKey.registry().getPath() + "s/" + upgradeKey.location().getPath();
        if (upgradeRank > 1) name = name + "_" + upgradeRank;
        if (suffix != null) name += suffix;
        op.apply(builder).save(output, name);
    }

    private void equipmentModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank, int energyRequired, boolean addBaseModuleInput, @Nullable String suffix, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY.get(), EquipmentUpgradeEntry::new, EQUIPMENT_UPGRADE_MODULE, addBaseModuleInput, suffix, op);
    }

    private void equipmentModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        equipmentModuleFab(output, registries, group, upgradeKey, upgradeRank, energyRequired, true, null, op);
    }

    private void machineModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank, int energyRequired, boolean addBaseModuleInput, @Nullable String suffix, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, LTXIDataComponents.MACHINE_UPGRADE_ENTRY.get(), MachineUpgradeEntry::new, MACHINE_UPGRADE_MODULE, addBaseModuleInput, suffix, op);
    }

    private void machineModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        machineModuleFab(output, registries, group, upgradeKey, upgradeRank, energyRequired, true, null, op);
    }

    private ItemStack defaultUpgradableItem(Supplier<? extends UpgradableEquipmentItem> itemSupplier, HolderLookup.Provider registries)
    {
        return itemSupplier.get().createStackWithDefaultUpgrades(registries);
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
        return either.map(Ingredient::of, Ingredient::of);
    }

    // Builder classes
    private static class FabricatingBuilder extends LimaCustomRecipeBuilder<FabricatingRecipe, FabricatingBuilder>
    {
        private final int energyRequired;
        private boolean advancementLocked = false;

        FabricatingBuilder(ModResources resources, int energyRequired)
        {
            super(resources);
            this.energyRequired = energyRequired;
        }

        public FabricatingBuilder requiresAdvancement()
        {
            this.advancementLocked = true;
            return this;
        }

        @Override
        protected FabricatingRecipe buildRecipe()
        {
            Preconditions.checkState(itemResults.size() == 1, "Fabricating recipe must have only 1 output");
            ItemResult result = itemResults.getFirst();

            return new FabricatingRecipe(itemIngredients, result, energyRequired, advancementLocked, getGroupOrBlank());
        }
    }

    private static class LTXIBuilder<R extends LTXIRecipe> extends LimaCustomRecipeBuilder<R, LTXIBuilder<R>>
    {
        private final LTXIRecipe.LTXIRecipeFactory<R> factory;
        private int craftTime = LTXIRecipe.DEFAULT_CRAFTING_TIME;

        LTXIBuilder(ModResources modResources, LTXIRecipe.LTXIRecipeFactory<R> factory)
        {
            super(modResources);
            this.factory = factory;
        }

        LTXIBuilder<R> time(int craftTime)
        {
            this.craftTime = craftTime;
            return this;
        }

        @Override
        protected R buildRecipe()
        {
            return factory.apply(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime);
        }
    }
}