package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaRecipeProvider;
import liedge.limacore.data.generation.recipe.LimaSizedIngredientsRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.function.ObjectIntFunction;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechTags;
import liedge.limatech.integration.guideme.GuideMEIntegration;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradeBaseEntry;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.lib.upgrades.machine.MachineUpgradeEntry;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.recipe.MaterialFusingRecipe;
import liedge.limatech.recipe.RecomposingRecipe;
import liedge.limatech.registry.game.LimaTechDataComponents;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static liedge.limatech.registry.bootstrap.LimaTechEquipmentUpgrades.*;
import static liedge.limatech.registry.bootstrap.LimaTechMachineUpgrades.*;
import static liedge.limatech.registry.game.LimaTechBlocks.*;
import static liedge.limatech.registry.game.LimaTechItems.*;
import static net.minecraft.world.item.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

class RecipesGen extends LimaRecipeProvider
{
    RecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries, LimaTech.RESOURCES);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries)
    {
        //#region Crafting table recipes
        nineStorageRecipes(output, RAW_TITANIUM, RAW_TITANIUM_BLOCK);
        nineStorageRecipes(output, RAW_NIOBIUM, RAW_NIOBIUM_BLOCK);

        nuggetIngotBlockRecipes(output, "titanium", TITANIUM_NUGGET, TITANIUM_INGOT, TITANIUM_BLOCK);
        nuggetIngotBlockRecipes(output, "niobium", NIOBIUM_NUGGET, NIOBIUM_INGOT, NIOBIUM_BLOCK);
        nuggetIngotBlockRecipes(output, "slate_alloy", SLATE_ALLOY_NUGGET, SLATE_ALLOY_INGOT, SLATE_ALLOY_BLOCK);

        shaped(COPPER_CIRCUIT).input('r', REDSTONE).input('m', COPPER_INGOT).input('t', TITANIUM_NUGGET).patterns("rrr", "mmm", "ttt").save(output);
        shaped(GOLD_CIRCUIT).input('r', REDSTONE).input('m', GOLD_INGOT).input('t', TITANIUM_NUGGET).patterns("rrr", "mmm", "ttt").save(output);
        shaped(NIOBIUM_CIRCUIT).input('r', REDSTONE).input('m', NIOBIUM_INGOT).input('t', TITANIUM_NUGGET).patterns("rrr", "mmm", "ttt").save(output);

        shaped(EMPTY_UPGRADE_MODULE).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('g', GLASS_PANES).input('l', DYES_LIME).patterns("ttt", "tgt", "lcl").save(output);
        shaped(FABRICATION_BLUEPRINT).input('l', DYES_LIME).input('p', PAPER).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).patterns("lll", "ppp", "tct").save(output);

        shaped(defaultUpgradableItem(LTX_WRENCH, registries)).input('t', TITANIUM_INGOT).input('l', DYES_LIME).patterns("t t", " l ", " t ").save(output);

        shapeless(GuideMEIntegration.createGuideTabletItem())
                .condition(new ModLoadedCondition("guideme"))
                .input(BOOK)
                .input(TITANIUM_INGOT)
                .input(DYES_LIME)
                .save(output, "guide_tablet");

        // Machine recipes
        shaped(ENERGY_STORAGE_ARRAY).input('t', TITANIUM_INGOT).input('c', GOLD_CIRCUIT).input('l', DYES_LIME).input('b', COPPER_BLOCK).patterns("tlt", "cbc", "tlt").save(output);
        shaped(DIGITAL_FURNACE).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', FURNACE).input('g', GLASS_BLOCKS).patterns("tgt", "cac", "tlt").save(output);
        shaped(DIGITAL_SMOKER).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', SMOKER).input('g', GLASS_BLOCKS).patterns("tgt", "cac", "tlt").save(output);
        shaped(DIGITAL_BLAST_FURNACE).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', BLAST_FURNACE).input('g', GLASS_BLOCKS).patterns("tgt", "cac", "tlt").save(output);
        shaped(GRINDER).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', GRINDSTONE).patterns("tlt", "cac", "ttt").save(output);
        shaped(RECOMPOSER).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('r', LIGHTNING_ROD).input('g', GLASS_BLOCKS).patterns("ttt", "rgr", "tct").save(output);
        shaped(MATERIAL_FUSING_CHAMBER).input('t', TITANIUM_INGOT).input('c', GOLD_CIRCUIT).input('l', DYES_LIME).input('a', BLAST_FURNACE).patterns("tlt", "cac", "ttt").save(output);
        shaped(FABRICATOR).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', CRAFTER).patterns("tlt", "cac", "ttt").save(output);
        shaped(EQUIPMENT_UPGRADE_STATION).input('t', TITANIUM_INGOT).input('a', ANVIL).input('l', DYES_LIME).patterns("ttt",  "lal", "ttt").save(output);

        // Standard machine systems
        final String smsRecipeName = STANDARD_MACHINE_SYSTEMS.location().getPath();
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 1)).input('m', EMPTY_UPGRADE_MODULE).input('r', REDSTONE).input('c', COPPER_CIRCUIT)
                .patterns(" r ", "cmc", " r ").save(output, smsRecipeName + "_1");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 2)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 1)).input('r', REDSTONE).input('c', COPPER_CIRCUIT)
                .patterns(" r ", "cmc", " r ").save(output, smsRecipeName + "_2");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 3)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 2)).input('r', REDSTONE).input('c', COPPER_CIRCUIT)
                .patterns(" r ", "cmc", " r ").save(output, smsRecipeName + "_3");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 4)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 3)).input('r', REDSTONE).input('c', GOLD_CIRCUIT)
                .patterns(" r ", "cmc", " r ").save(output, smsRecipeName + "_4");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 5)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 4)).input('r', REDSTONE).input('c', GOLD_CIRCUIT)
                .patterns(" r ", "cmc", " r ").save(output, smsRecipeName + "_5");
        shaped(mumStack(registries, STANDARD_MACHINE_SYSTEMS, 6)).input('m', mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 5)).input('r', REDSTONE).input('c', GOLD_CIRCUIT)
                .patterns(" r ", "cmc", " r ").save(output, smsRecipeName + "_6");

        GLOW_BLOCKS.forEach((color, deferredBlock) -> shaped(deferredBlock, 4).input('d', color.getTag()).input('g', GLOWSTONE).patterns("dg", "gd").save(output));
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

        // Grinding recipes
        grinding(stackOf(COBBLESTONE)).input(STONE).save(output);
        grinding(stackOf(GRAVEL)).input(Tags.Items.COBBLESTONES_NORMAL).save(output);
        grinding(stackOf(SAND)).input(Tags.Items.GRAVELS).save(output);
        grinding(stackOf(DEEPSLATE_POWDER))
                .input(LimaTechTags.Items.DEEPSLATE_GRINDABLES)
                .save(output, "grind_deepslate");

        orePebbleGrinding(COAL_ORE_PEBBLES, Tags.Items.ORES_COAL, null, "coal", output);
        orePebbleGrinding(COPPER_ORE_PEBBLES, Tags.Items.ORES_COPPER, Tags.Items.RAW_MATERIALS_COPPER, "copper", output);
        orePebbleGrinding(IRON_ORE_PEBBLES, Tags.Items.ORES_IRON, Tags.Items.RAW_MATERIALS_IRON, "iron", output);
        orePebbleGrinding(LAPIS_ORE_PEBBLES, Tags.Items.ORES_LAPIS, null, "lapis", output);
        orePebbleGrinding(REDSTONE_ORE_PEBBLES, Tags.Items.ORES_REDSTONE, null, "redstone", output);
        orePebbleGrinding(GOLD_ORE_PEBBLES, Tags.Items.ORES_GOLD, Tags.Items.RAW_MATERIALS_GOLD, "gold", output);
        orePebbleGrinding(DIAMOND_ORE_PEBBLES, Tags.Items.ORES_DIAMOND, null, "diamond", output);
        orePebbleGrinding(EMERALD_ORE_PEBBLES, Tags.Items.ORES_EMERALD, null, "emerald", output);
        orePebbleGrinding(QUARTZ_ORE_PEBBLES, Tags.Items.ORES_QUARTZ, null, "quartz", output);
        orePebbleGrinding(NETHERITE_ORE_PEBBLES, Tags.Items.ORES_NETHERITE_SCRAP, null, "netherite", output);
        orePebbleGrinding(TITANIUM_ORE_PEBBLES, LimaTechTags.Items.TITANIUM_ORES, LimaTechTags.Items.RAW_TITANIUM_MATERIALS, "titanium", output);
        orePebbleGrinding(NIOBIUM_ORE_PEBBLES, LimaTechTags.Items.NIOBIUM_ORES, LimaTechTags.Items.RAW_NIOBIUM_MATERIALS, "niobium", output);

        // Recomposing Recipes
        recomposing(stackOf(LIME_PIGMENT, 2))
                .input(ItemTags.LEAVES, 8)
                .save(output, "extract_dye_from_leaves");
        recomposing(stackOf(LIME_PIGMENT))
                .input(Tags.Items.SEEDS, 8)
                .save(output, "extract_dye_from_seeds");
        recomposing(stackOf(WHITE_PIGMENT, 12))
                .input(TITANIUM_INGOT)
                .save(output, "extract_titanium_white");
        recomposing(GuideMEIntegration.createGuideTabletItem())
                .condition(new ModLoadedCondition("guideme"))
                .input(BOOK)
                .save(output, "guide_tablet");

        // Material fusing recipes
        fusing(stackOf(COPPER_CIRCUIT)).input(COPPER_INGOT).input(REDSTONE, 2).input(TITANIUM_NUGGET).save(output);
        fusing(stackOf(GOLD_CIRCUIT)).input(GOLD_INGOT).input(REDSTONE, 2).input(TITANIUM_NUGGET).save(output);
        fusing(stackOf(NIOBIUM_CIRCUIT)).input(NIOBIUM_INGOT).input(REDSTONE, 2).input(TITANIUM_NUGGET).save(output);
        fusing(stackOf(SLATE_ALLOY_INGOT))
                .input(DEEPSLATE_POWDER, 4)
                .input(NETHERITE_INGOT)
                .save(output, "slate_alloy_from_netherite_ingot");
        fusing(stackOf(SLATE_ALLOY_INGOT))
                .input(DEEPSLATE_POWDER, 4)
                .input(NETHERITE_SCRAP, 2)
                .input(GOLD_INGOT)
                .save(output, "slate_alloy_from_netherite_alloying");
        fusing(stackOf(NETHERITE_INGOT))
                .input(NETHERITE_SCRAP, 4)
                .input(GOLD_INGOT)
                .save(output);
        GLOW_BLOCKS.forEach((color, block) -> fusing(stackOf(block, 8)).input(LimaTechTags.Items.GLOW_BLOCK_MATERIALS, 2).input(color.getTag()).save(output));
        fusing(stackOf(TITANIUM_GLASS)).input(TITANIUM_INGOT).input(GEMS_QUARTZ, 3).save(output);
        fusing(stackOf(SLATE_GLASS)).input(SLATE_ALLOY_INGOT).input(GEMS_QUARTZ, 3).save(output);

        // Fabricating recipes
        fabricating(MOLECULAR_RECONSTRUCTOR, 2_000_000)
                .input(ANVIL)
                .input(TITANIUM_INGOT, 24)
                .input(GOLD_CIRCUIT, 2)
                .input(NIOBIUM_CIRCUIT, 1)
                .group("machines")
                .save(output);

        fabricating(EXPLOSIVES_WEAPON_TECH_SALVAGE, 500_000)
                .input(GUNPOWDER, 8)
                .input(TITANIUM_INGOT, 4)
                .input(GOLD_CIRCUIT)
                .group("tech_parts")
                .requiresAdvancement()
                .unlockedBy("visited_fortress", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.holderOrThrow(BuiltinStructures.FORTRESS))))
                .save(output);
        fabricating(AUTO_AMMO_CANISTER, 50_000)
                .input(TITANIUM_INGOT, 1)
                .input(Ingredient.of(GLOWSTONE_DUST, REDSTONE))
                .input(DYES_LIME)
                .group("weapon_ammo").save(output);
        fabricating(ROCKET_TURRET, 2_500_000)
                .input(TARGETING_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 32)
                .input(GOLD_CIRCUIT, 4)
                .input(NIOBIUM_CIRCUIT, 2)
                .group("turrets").save(output);
        fabricating(RAILGUN_TURRET, 5_000_000)
                .input(TARGETING_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 64)
                .input(GOLD_CIRCUIT, 8)
                .input(NIOBIUM_CIRCUIT, 4)
                .group("turrets").save(output);

        // Tools fabricating
        upgradeableItemFabricating(LTX_DRILL, registries, 300_000)
                .input(IRON_PICKAXE)
                .input(IRON_SHOVEL)
                .input(TITANIUM_INGOT, 8)
                .input(COPPER_CIRCUIT, 4)
                .input(DYES_LIME, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_SWORD, registries, 500_000)
                .input(DIAMOND_SWORD)
                .input(TITANIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 2)
                .input(DYES_LIME, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_SHOVEL, registries, 500_000)
                .input(DIAMOND_SHOVEL)
                .input(TITANIUM_INGOT, 8)
                .input(GOLD_CIRCUIT, 2)
                .input(DYES_LIME, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_AXE, registries, 500_000)
                .input(DIAMOND_AXE)
                .input(TITANIUM_INGOT, 24)
                .input(GOLD_CIRCUIT, 2)
                .input(DYES_LIME, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_HOE, registries, 500_000)
                .input(DIAMOND_HOE)
                .input(TITANIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 2)
                .input(DYES_LIME, 4)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_SHEARS, registries, 300_000)
                .input(SHEARS)
                .input(TITANIUM_INGOT, 12)
                .input(COPPER_CIRCUIT, 2)
                .input(DYES_LIME, 2)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_BRUSH, registries, 300_000)
                .input(BRUSH)
                .input(TITANIUM_INGOT, 6)
                .input(COPPER_CIRCUIT, 2)
                .input(DYES_LIME, 2)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_FISHING_ROD, registries, 300_000)
                .input(FISHING_ROD)
                .input(TITANIUM_INGOT, 12)
                .input(COPPER_CIRCUIT, 2)
                .input(DYES_LIME, 2)
                .group("ltx/tool").save(output);
        upgradeableItemFabricating(LTX_LIGHTER, registries, 300_000)
                .input(FLINT_AND_STEEL)
                .input(TITANIUM_INGOT, 6)
                .input(COPPER_CIRCUIT, 2)
                .input(DYES_LIME, 2)
                .group("ltx/tool").save(output);

        // Weapons fabrication
        upgradeableItemFabricating(SUBMACHINE_GUN, registries, 400_000)
                .input(TITANIUM_INGOT, 16)
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 2)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(SHOTGUN, registries, 1_000_000)
                .input(TITANIUM_INGOT, 32)
                .input(COPPER_CIRCUIT, 16)
                .input(GOLD_CIRCUIT, 8)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(GRENADE_LAUNCHER, registries, 20_000_000)
                .input(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 16)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(ROCKET_LAUNCHER, registries, 30_000_000)
                .input(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 48)
                .input(NIOBIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 16)
                .group("ltx/weapon").save(output);
        upgradeableItemFabricating(MAGNUM, registries, 50_000_000)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_INGOT, 24)
                .input(SLATE_ALLOY_INGOT, 4)
                .input(GOLD_CIRCUIT, 16)
                .input(NIOBIUM_CIRCUIT, 8)
                .group("ltx/weapon").save(output);

        equipmentModuleFab(output, registries, "eum/tool", DRILL_DIAMOND_LEVEL, 1, 300_000, builder -> builder
                .input(DIAMOND_BLOCK)
                .input(TITANIUM_INGOT, 4)
                .input(GOLD_CIRCUIT, 2));
        equipmentModuleFab(output, registries, "eum/tool", DRILL_NETHERITE_LEVEL, 1, 500_000, builder -> builder
                .input(eumIngredient(registries, DRILL_DIAMOND_LEVEL, 1))
                .input(NETHERITE_INGOT, 3)
                .input(TITANIUM_INGOT, 8)
                .input(NIOBIUM_CIRCUIT));
        equipmentModuleFab(output, registries, "eum/tool", DRILL_OMNI_MINER, 1, 20_000_000, builder -> builder
                .input(DIAMOND_SHOVEL)
                .input(DIAMOND_PICKAXE)
                .input(DIAMOND_AXE)
                .input(DIAMOND_HOE)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(TITANIUM_BLOCK, 2));
        equipmentModuleFab(output, registries, "eum/tool", TOOL_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(GOLD_CIRCUIT, 1)
                .input(TITANIUM_INGOT, 4)
                .input(SCULK_SENSOR, 1)
                .input(ItemTags.WOOL, 8));

        UnaryOperator<FabricatingBuilder> directDrops = builder -> builder
                .input(NIOBIUM_CIRCUIT, 1)
                .input(NETHER_STAR)
                .input(ENDER_CHEST)
                .input(ENDER_PEARL, 8)
                .input(ECHO_SHARD);
        equipmentModuleFab(output, registries, "eum/tool", TOOL_DIRECT_DROPS, 1, 15_000_000, directDrops);
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_DIRECT_DROPS, 1, 15_000_000, directDrops);

        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_ARMOR_PIERCE, 1, 1_000_000, builder -> builder
                .input(COPPER_CIRCUIT, 8)
                .input(OBSIDIAN, 8)
                .input(TITANIUM_INGOT, 8));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_ARMOR_PIERCE, 2, 2_500_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(OBSIDIAN, 32)
                .input(TITANIUM_BLOCK, 2));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_ARMOR_PIERCE, 3, 5_000_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 2)
                .input(OBSIDIAN, 64)
                .input(SLATE_ALLOY_INGOT, 4));

        equipmentModuleFab(output, registries, "eum/weapon", HIGH_IMPACT_ROUNDS, 1, 1_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(TNT, 8)
                .input(PISTON, 2));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_VIBRATION_CANCEL, 1, 500_000, builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(NIOBIUM_CIRCUIT, 1)
                .input(TITANIUM_INGOT, 16)
                .input(ECHO_SHARD, 2)
                .input(ItemTags.WOOL, 16));
        equipmentModuleFab(output, registries, "eum/weapon", UNIVERSAL_STEALTH_DAMAGE, 1, 750_000, builder -> builder
                .input(GOLD_CIRCUIT, 6)
                .input(TITANIUM_INGOT, 16)
                .input(PHANTOM_MEMBRANE, 8)
                .input(ENDER_PEARL, 4)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY)).build(), POTION)));

        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_SHIELD_REGEN, 1, 1_500_000, builder -> builder
                .input(GOLD_CIRCUIT)
                .input(GOLDEN_APPLE, 1)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.FIRE_RESISTANCE)).build(), POTION))
                .input(SHIELD)
                .input(DIAMOND, 4));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_SHIELD_REGEN, 2, 3_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(GOLDEN_APPLE, 2)
                .input(DIAMOND, 8)
                .input(AMETHYST_SHARD, 2));
        equipmentModuleFab(output, registries, "eum/weapon", WEAPON_SHIELD_REGEN, 3, 5_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(AMETHYST_SHARD, 8)
                .input(ENCHANTED_GOLDEN_APPLE));

        equipmentModuleFab(output, registries, "eum/enchant", SILK_TOUCH_ENCHANT, 1, 500_000, builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(EMERALD, 1)
                .input(SLIME_BALL, 8)
                .input(GLASS_BLOCKS, 16));
        UnaryOperator<FabricatingBuilder> multi1 = builder -> builder
                .input(COPPER_CIRCUIT, 4)
                .input(IRON_INGOT, 4)
                .input(RABBIT_FOOT)
                .input(LAPIS_LAZULI, 4);
        UnaryOperator<FabricatingBuilder> multi2 = builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 6)
                .input(RABBIT_FOOT, 2)
                .input(LAPIS_LAZULI, 8);
        UnaryOperator<FabricatingBuilder> multi3 = builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(EMERALD, 2)
                .input(DIAMOND, 2)
                .input(LAPIS_BLOCK, 2);
        UnaryOperator<FabricatingBuilder> multi4 = builder -> builder
                .input(NIOBIUM_CIRCUIT, 3)
                .input(EMERALD, 6)
                .input(DIAMOND, 6)
                .input(AMETHYST_BLOCK, 3)
                .input(NETHER_STAR, 1)
                .input(LAPIS_BLOCK, 4);
        UnaryOperator<FabricatingBuilder> multi5 = builder -> builder
                .input(NIOBIUM_CIRCUIT, 6)
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
                .input(COPPER_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 8)
                .input(ZOMBIE_HEAD)
                .input(CREEPER_HEAD)
                .input(SKELETON_SKULL));
        equipmentModuleFab(output, registries,"eum/enchant", RAZOR_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 2)
                .input(DIAMOND, 2)
                .input(ZOMBIE_HEAD, 2)
                .input(CREEPER_HEAD, 2)
                .input(SKELETON_SKULL, 2));
        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 3, 1_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(ZOMBIE_HEAD, 4)
                .input(SKELETON_SKULL, 4)
                .input(CREEPER_HEAD, 4));
        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 4, 2_00_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(WITHER_SKELETON_SKULL, 6)
                .input(PIGLIN_HEAD, 6));
        equipmentModuleFab(output, registries, "eum/enchant", RAZOR_ENCHANTMENT, 5, 4_000_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 4)
                .input(ZOMBIE_HEAD, 8)
                .input(CREEPER_HEAD, 8)
                .input(SKELETON_SKULL, 8)
                .input(WITHER_SKELETON_SKULL, 8)
                .input(PIGLIN_HEAD, 8)
                .input(DRAGON_HEAD, 1));

        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 1, 300_000, builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(GUNPOWDER, 8)
                .input(AUTO_AMMO_CANISTER, 2));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 2, 600_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 12)
                .input(LAPIS_LAZULI, 16)
                .input(AUTO_AMMO_CANISTER, 4));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 3, 900_000, builder -> builder
                .input(GOLD_CIRCUIT, 6)
                .input(TITANIUM_INGOT, 16)
                .input(AUTO_AMMO_CANISTER, 8)
                .input(SPECIALIST_AMMO_CANISTER, 2));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 4, 1_200_000, builder -> builder
                .input(GOLD_CIRCUIT, 8)
                .input(NIOBIUM_CIRCUIT, 1)
                .input(SPECIALIST_AMMO_CANISTER, 4)
                .input(EXPLOSIVES_AMMO_CANISTER, 2));
        equipmentModuleFab(output, registries, "eum/enchant", AMMO_SCAVENGER_ENCHANTMENT, 5, 1_500_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 2)
                .input(SLATE_ALLOY_INGOT, 2)
                .input(AUTO_AMMO_CANISTER, 16)
                .input(SPECIALIST_AMMO_CANISTER, 8)
                .input(EXPLOSIVES_AMMO_CANISTER, 4)
                .input(HEAVY_AMMO_CANISTER, 2));

        equipmentModuleFab(output, registries, "eum/weapon/gl", FLAME_GRENADE_CORE, 1, 250_000, builder -> builder
                .input(COPPER_CIRCUIT, 16)
                .input(TITANIUM_INGOT, 8)
                .input(FIRE_CHARGE, 21));
        equipmentModuleFab(output, registries, "eum/weapon/gl", CRYO_GRENADE_CORE, 1, 250_000, builder -> builder
                .input(COPPER_CIRCUIT, 16)
                .input(TITANIUM_INGOT, 8)
                .input(SNOW_BLOCK, 16)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_SLOWNESS)).build(), POTION)));
        equipmentModuleFab(output, registries, "eum/weapon/gl", ELECTRIC_GRENADE_CORE, 1, 500_000, builder -> builder
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 8)
                .input(TITANIUM_INGOT, 8)
                .input(LIGHTNING_ROD, 4));
        equipmentModuleFab(output, registries, "eum/weapon/gl", ACID_GRENADE_CORE, 1, 1_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 3)
                .input(NIOBIUM_CIRCUIT, 1)
                .input(TITANIUM_INGOT, 16)
                .requiresAdvancement()
                .unlockedBy("kill_bogged", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.BOGGED))));
        equipmentModuleFab(output, registries, "eum/weapon/gl", NEURO_GRENADE_CORE, 1, 5_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 6)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 32)
                .input(NETHER_STAR)
                .requiresAdvancement()
                .unlockedBy("kill_warden", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.WARDEN))));
        equipmentModuleFab(output, registries, "eum/weapon", GRENADE_LAUNCHER_PROJECTILE_SPEED, 1, 750_000, builder -> builder
                .input(GOLD_CIRCUIT)
                .input(FIREWORK_ROCKET, 9)
                .input(PHANTOM_MEMBRANE, 2));
        equipmentModuleFab(output, registries, "eum/weapon", GRENADE_LAUNCHER_PROJECTILE_SPEED, 2, 2_000_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 2)
                .input(FIREWORK_ROCKET, 36)
                .input(PHANTOM_MEMBRANE, 8));

        machineModuleFab(output, registries, "mum/gpm", ULTIMATE_MACHINE_SYSTEMS, 1, 250_000_000, false, null, builder -> builder
                .input(mumIngredient(registries, STANDARD_MACHINE_SYSTEMS, 6))
                .input(NIOBIUM_CIRCUIT, 8)
                .input(SLATE_ALLOY_INGOT, 16)
                .input(NETHER_STAR, 2)
                .input(ECHO_SHARD, 4)
                .input(DYES_LIME, 32)
                .input(REDSTONE_BLOCK, 16)
                .input(AMETHYST_BLOCK, 4));

        machineModuleFab(output, registries, "mum/esa", ESA_CAPACITY_UPGRADE, 1, 250_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(COPPER_INGOT, 6));
        machineModuleFab(output, registries, "mum/esa", ESA_CAPACITY_UPGRADE, 2, 500_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(COPPER_INGOT, 12)
                .input(GOLD_CIRCUIT, 2));
        machineModuleFab(output, registries, "mum/esa", ESA_CAPACITY_UPGRADE, 3, 1_000_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(GOLD_INGOT, 12)
                .input(NIOBIUM_CIRCUIT, 2));
        machineModuleFab(output, registries, "mum/esa", ESA_CAPACITY_UPGRADE, 4, 5_000_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(NIOBIUM_INGOT, 12)
                .input(NIOBIUM_CIRCUIT, 2));

        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 1, 500_000, builder -> builder
                .input(DYES_LIME, 4)
                .input(DIAMOND)
                .input(TITANIUM_INGOT, 4)
                .input(COPPER_CIRCUIT, 2)
                .input(REDSTONE, 8));
        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 2, 1_000_000, builder -> builder
                .input(DYES_LIME, 4)
                .input(DIAMOND, 4)
                .input(TITANIUM_INGOT, 8)
                .input(COPPER_CIRCUIT, 4)
                .input(GOLD_CIRCUIT, 2)
                .input(REDSTONE, 8));
        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 3, 5_000_000, builder -> builder
                .input(DYES_LIME, 8)
                .input(DIAMOND_BLOCK)
                .input(TITANIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 4)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(REDSTONE, 16));
        machineModuleFab(output, registries, "mum/fabricator", FABRICATOR_UPGRADE, 4, 10_000_000, builder -> builder
                .input(DYES_LIME, 8)
                .input(DIAMOND_BLOCK)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_CIRCUIT, 6)
                .input(REDSTONE, 32));
    }

    // Helpers
    private void orePebblesCooking(ItemLike orePebble, ItemLike resultItem, int resultCount, RecipeOutput output)
    {
        String name = getItemName(orePebble);
        smelting(stackOf(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "smelt_" + name);
        blasting(stackOf(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "blast_" + name);
    }

    private LimaSizedIngredientsRecipeBuilder.SimpleBuilder<GrindingRecipe, ?> grinding(ItemStack result)
    {
        return LimaSizedIngredientsRecipeBuilder.simpleBuilder(modResources, result, GrindingRecipe::new);
    }

    private void orePebbleGrinding(ItemLike orePebble, TagKey<Item> oreTag, @Nullable TagKey<Item> rawOreTag, String name, RecipeOutput output)
    {
        grinding(stackOf(orePebble, 3)).input(oreTag).save(output, "grind_" + name + "_ores");
        if (rawOreTag != null) grinding(stackOf(orePebble, 2)).input(rawOreTag).save(output, "grind_raw_" + name + "_materials");
    }

    private LimaSizedIngredientsRecipeBuilder.SimpleBuilder<RecomposingRecipe, ?> recomposing(ItemStack result)
    {
        return LimaSizedIngredientsRecipeBuilder.simpleBuilder(modResources, result, RecomposingRecipe::new);
    }

    private LimaSizedIngredientsRecipeBuilder.SimpleBuilder<MaterialFusingRecipe, ?> fusing(ItemStack result)
    {
        return LimaSizedIngredientsRecipeBuilder.simpleBuilder(modResources, result, MaterialFusingRecipe::new);
    }

    private FabricatingBuilder fabricating(ItemLike result, int energyRequired)
    {
        return fabricating(stackOf(result), energyRequired);
    }

    private FabricatingBuilder fabricating(ItemStack result, int energyRequired)
    {
        return new FabricatingBuilder(modResources, result, energyRequired);
    }

    private FabricatingBuilder upgradeableItemFabricating(Supplier<? extends UpgradableEquipmentItem> itemSupplier, HolderLookup.Provider registries, int energyRequired)
    {
        return new FabricatingBuilder(modResources, defaultUpgradableItem(itemSupplier, registries), energyRequired);
    }

    private <U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> Ingredient moduleIngredient(HolderLookup.Provider registries, ResourceKey<U> upgradeKey, int upgradeRank, ItemLike moduleItem, DataComponentType<UE> componentType, ObjectIntFunction<Holder<U>, UE> entryFactory)
    {
        Holder<U> upgradeHolder = registries.holderOrThrow(upgradeKey);
        return DataComponentIngredient.of(true, componentType, entryFactory.applyWithInt(upgradeHolder, upgradeRank), moduleItem);
    }

    private Ingredient eumIngredient(HolderLookup.Provider registries, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleIngredient(registries, upgradeKey, upgradeRank, EQUIPMENT_UPGRADE_MODULE, LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY.get(), EquipmentUpgradeEntry::new);
    }

    private Ingredient mumIngredient(HolderLookup.Provider registries, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleIngredient(registries, upgradeKey, upgradeRank, MACHINE_UPGRADE_MODULE, LimaTechDataComponents.MACHINE_UPGRADE_ENTRY.get(), MachineUpgradeEntry::new);
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
        return moduleStack(registries, upgradeKey, upgradeRank, EQUIPMENT_UPGRADE_MODULE, LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY.get(), EquipmentUpgradeEntry::new);
    }

    private ItemStack mumStack(HolderLookup.Provider registries, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank)
    {
        return moduleStack(registries, upgradeKey, upgradeRank, MACHINE_UPGRADE_MODULE, LimaTechDataComponents.MACHINE_UPGRADE_ENTRY.get(), MachineUpgradeEntry::new);
    }

    private <U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> void upgradeFabricating(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<U> upgradeKey, int upgradeRank, int energyRequired, DataComponentType<UE> componentType, ObjectIntFunction<Holder<U>, UE> entryFactory, ItemLike moduleItem, boolean addBaseModuleInput, @Nullable String suffix, UnaryOperator<FabricatingBuilder> op)
    {
        FabricatingBuilder builder = fabricating(moduleStack(registries, upgradeKey, upgradeRank, moduleItem, componentType, entryFactory), energyRequired).group(group);

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
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY.get(), EquipmentUpgradeEntry::new, EQUIPMENT_UPGRADE_MODULE, addBaseModuleInput, suffix, op);
    }

    private void equipmentModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        equipmentModuleFab(output, registries, group, upgradeKey, upgradeRank, energyRequired, true, null, op);
    }

    private void machineModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank, int energyRequired, boolean addBaseModuleInput, @Nullable String suffix, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, LimaTechDataComponents.MACHINE_UPGRADE_ENTRY.get(), MachineUpgradeEntry::new, MACHINE_UPGRADE_MODULE, addBaseModuleInput, suffix, op);
    }

    private void machineModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        machineModuleFab(output, registries, group, upgradeKey, upgradeRank, energyRequired, true, null, op);
    }

    private ItemStack defaultUpgradableItem(Supplier<? extends UpgradableEquipmentItem> itemSupplier, HolderLookup.Provider registries)
    {
        return itemSupplier.get().createStackWithDefaultUpgrades(registries);
    }

    // Builder classes
    private static class FabricatingBuilder extends LimaSizedIngredientsRecipeBuilder.SimpleBuilder<FabricatingRecipe, FabricatingBuilder>
    {
        private final int energyRequired;
        private boolean advancementLocked = false;

        FabricatingBuilder(ModResources resources, ItemStack result, int energyRequired)
        {
            super(resources, result);
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
            return new FabricatingRecipe(ingredients, resultItem, energyRequired, advancementLocked, getGroupOrBlank());
        }
    }
}