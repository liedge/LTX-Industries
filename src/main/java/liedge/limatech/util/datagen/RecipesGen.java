package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaRecipeProvider;
import liedge.limacore.data.generation.recipe.LimaSizedIngredientListRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechTags;
import liedge.limatech.item.weapon.WeaponItem;
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
import liedge.limatech.registry.LimaTechDataComponents;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static liedge.limatech.registry.LimaTechBlocks.*;
import static liedge.limatech.registry.LimaTechEquipmentUpgrades.*;
import static liedge.limatech.registry.LimaTechItems.*;
import static liedge.limatech.registry.LimaTechMachineUpgrades.ESA_CAPACITY_UPGRADE;
import static liedge.limatech.registry.LimaTechMachineUpgrades.FABRICATOR_UPGRADE;
import static net.minecraft.world.item.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.DYES_LIME;
import static net.neoforged.neoforge.common.Tags.Items.GLASS_BLOCKS;

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

        titaniumTool(output, TITANIUM_SWORD, "t", "t", "s");
        titaniumTool(output, TITANIUM_SHOVEL, "t", "s", "s");
        titaniumTool(output, TITANIUM_PICKAXE, "ttt", " s ", " s ");
        titaniumTool(output, TITANIUM_AXE, "tt", "ts", " s");
        titaniumTool(output, TITANIUM_HOE, "tt", " s", " s");
        shaped(TITANIUM_SHEARS).input('t', TITANIUM_INGOT).patterns(" t", "t ").save(output);

        shaped(COPPER_CIRCUIT).input('r', REDSTONE).input('m', COPPER_INGOT).input('t', TITANIUM_NUGGET).patterns("rrr", "mmm", "ttt").save(output);
        shaped(GOLD_CIRCUIT).input('r', REDSTONE).input('m', GOLD_INGOT).input('t', TITANIUM_NUGGET).patterns("rrr", "mmm", "ttt").save(output);
        shaped(NIOBIUM_CIRCUIT).input('r', REDSTONE).input('m', NIOBIUM_INGOT).input('t', TITANIUM_NUGGET).patterns("rrr", "mmm", "ttt").save(output);

        shaped(EMPTY_UPGRADE_MODULE).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).patterns("ttt", "tct", "lcl").save(output);

        shaped(MACHINE_WRENCH).input('t', TITANIUM_INGOT).input('l', DYES_LIME).patterns("t t", " l ", " t ").save(output);

        shaped(ENERGY_STORAGE_ARRAY).input('t', TITANIUM_INGOT).input('c', GOLD_CIRCUIT).input('l', DYES_LIME).input('b', COPPER_BLOCK).patterns("tlt", "cbc", "tlt").save(output);
        shaped(DIGITAL_FURNACE).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', FURNACE).patterns("tlt", "cac", "ttt").save(output);
        shaped(GRINDER).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', GRINDSTONE).patterns("tlt", "cac", "ttt").save(output);
        shaped(RECOMPOSER).input('t', TITANIUM_INGOT).input('c', GOLD_CIRCUIT).input('r', LIGHTNING_ROD).input('g', GLASS).patterns("ttt", "rgr", "tct").save(output);
        shaped(MATERIAL_FUSING_CHAMBER).input('t', TITANIUM_INGOT).input('c', COPPER_CIRCUIT).input('l', DYES_LIME).input('a', BLAST_FURNACE).patterns("tlt", "cac", "ttt").save(output);
        shaped(FABRICATOR).input('t', TITANIUM_INGOT).input('c', GOLD_CIRCUIT).input('l', DYES_LIME).input('a', CRAFTER).patterns("tlt", "cac", "ttt").save(output);
        shaped(EQUIPMENT_UPGRADE_STATION).input('t', TITANIUM_INGOT).input('a', ANVIL).input('l', DYES_LIME).patterns("ttt",  "lal", "ttt").save(output);

        GLOW_BLOCKS.forEach((color, deferredBlock) -> {
            String path = deferredBlock.getId().getPath();
            shaped(deferredBlock, 4).input('d', color.getTag()).input('g', GLOWSTONE).patterns("dg", "gd").save(output, path + "_a");
            shaped(deferredBlock, 8).input('d', color.getTag()).input('g', GLOW_INK_SAC).patterns("dg", "gd").save(output, path + "_b");
        });
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

        // Material fusing recipes
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

        // Fabricating recipes
        fabricating(ROCKET_TURRET, 1_000_000)
                .input(TARGETING_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_CIRCUIT, 4)
                .input(GOLD_CIRCUIT, 8)
                .group("turrets").save(output);
        fabricating(AUTO_AMMO_CANISTER, 100_000)
                .input(TITANIUM_INGOT, 3)
                .input(GUNPOWDER, 4)
                .input(Ingredient.of(GLOWSTONE_DUST, REDSTONE), 4)
                .input(DYES_LIME)
                .input(GLASS_BLOCKS, 2)
                .group("weapon_ammo").save(output);
        weaponFabricating(SUBMACHINE_GUN, registries, 400_000)
                .input(TITANIUM_INGOT, 16)
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 4)
                .group("weapons").save(output);
        weaponFabricating(SHOTGUN, registries, 1_000_000)
                .input(TITANIUM_INGOT, 32)
                .input(COPPER_CIRCUIT, 16)
                .input(GOLD_CIRCUIT, 8)
                .group("weapons").save(output);
        weaponFabricating(GRENADE_LAUNCHER, registries, 20_000_000)
                .input(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 16)
                .group("weapons").save(output);
        weaponFabricating(ROCKET_LAUNCHER, registries, 30_000_000)
                .input(EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .input(TITANIUM_INGOT, 48)
                .input(NIOBIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 16)
                .group("weapons").save(output);
        weaponFabricating(MAGNUM, registries, 50_000_000)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_INGOT, 24)
                .input(SLATE_ALLOY_INGOT, 4)
                .input(GOLD_CIRCUIT, 16)
                .input(NIOBIUM_CIRCUIT, 8)
                .group("weapons").save(output);

        equipmentModuleFab(output, registries, "stealth_upgrades", UNIVERSAL_ANTI_VIBRATION, 1, 500_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 16)
                .input(ECHO_SHARD, 1)
                .input(ItemTags.WOOL, 16));
        equipmentModuleFab(output, registries, "stealth_upgrades", UNIVERSAL_STEALTH_DAMAGE, 1, 750_000, builder -> builder
                .input(GOLD_CIRCUIT, 6)
                .input(TITANIUM_INGOT, 16)
                .input(PHANTOM_MEMBRANE, 8)
                .input(ENDER_PEARL, 4)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY)).build(), POTION)));

        equipmentModuleFab(output, registries, "looting_upgrades", LOOTING_ENCHANTMENT, 1, 125_000, builder -> builder
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(RABBIT_FOOT, 1)
                .input(LAPIS_LAZULI, 8));
        equipmentModuleFab(output, registries, "looting_upgrades", LOOTING_ENCHANTMENT, 2, 250_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(EMERALD, 4)
                .input(DIAMOND, 4)
                .input(LAPIS_LAZULI, 32));
        equipmentModuleFab(output, registries, "looting_upgrades", LOOTING_ENCHANTMENT, 3, 500_000, builder -> builder
                .input(ZOMBIE_HEAD)
                .input(CREEPER_HEAD)
                .input(SKELETON_SKULL)
                .input(WITHER_SKELETON_SKULL)
                .input(LAPIS_LAZULI, 48));
        equipmentModuleFab(output, registries, "looting_upgrades", LOOTING_ENCHANTMENT, 4, 1_000_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 4)
                .input(EMERALD_BLOCK, 4)
                .input(NETHER_STAR, 1)
                .input(LAPIS_BLOCK, 16));
        equipmentModuleFab(output, registries, "looting_upgrades", LOOTING_ENCHANTMENT, 5, 10_000_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 8)
                .input(SLATE_ALLOY_INGOT, 16)
                .input(ECHO_SHARD, 2)
                .input(DRAGON_HEAD, 1)
                .input(NETHER_STAR, 4)
                .input(LAPIS_BLOCK, 64));

        equipmentModuleFab(output, registries, "razor_upgrades", RAZOR_ENCHANTMENT, 1, 250_000, builder -> builder
                .input(COPPER_CIRCUIT, 4)
                .input(TITANIUM_SWORD)
                .input(TITANIUM_SHEARS));
        equipmentModuleFab(output, registries,"razor_upgrades", RAZOR_ENCHANTMENT, 2, 500_000, builder -> builder
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 2)
                .input(TITANIUM_SWORD, 2)
                .input(ZOMBIE_HEAD));
        equipmentModuleFab(output, registries, "razor_upgrades", RAZOR_ENCHANTMENT, 3, 1_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(ZOMBIE_HEAD, 2)
                .input(SKELETON_SKULL, 2)
                .input(CREEPER_HEAD, 2));
        equipmentModuleFab(output, registries, "razor_upgrades", RAZOR_ENCHANTMENT, 4, 2_00_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(WITHER_SKELETON_SKULL, 4)
                .input(PIGLIN_HEAD, 4));
        equipmentModuleFab(output, registries, "razor_upgrades", RAZOR_ENCHANTMENT, 5, 4_000_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 4)
                .input(ZOMBIE_HEAD, 16)
                .input(CREEPER_HEAD, 16)
                .input(SKELETON_SKULL, 16)
                .input(WITHER_SKELETON_SKULL, 8)
                .input(PIGLIN_HEAD, 8)
                .input(DRAGON_HEAD, 1));

        equipmentModuleFab(output, registries, "ammo_scavenger_upgrades", AMMO_SCAVENGER_ENCHANTMENT, 1, 300_000, builder -> builder
                .input(GOLD_CIRCUIT, 2)
                .input(TITANIUM_INGOT, 8)
                .input(GUNPOWDER, 8)
                .input(AUTO_AMMO_CANISTER, 2));
        equipmentModuleFab(output, registries, "ammo_scavenger_upgrades", AMMO_SCAVENGER_ENCHANTMENT, 2, 600_000, builder -> builder
                .input(GOLD_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 12)
                .input(LAPIS_LAZULI, 16)
                .input(AUTO_AMMO_CANISTER, 4));
        equipmentModuleFab(output, registries, "ammo_scavenger_upgrades", AMMO_SCAVENGER_ENCHANTMENT, 3, 900_000, builder -> builder
                .input(GOLD_CIRCUIT, 6)
                .input(TITANIUM_INGOT, 16)
                .input(AUTO_AMMO_CANISTER, 8)
                .input(SPECIALIST_AMMO_CANISTER, 2));
        equipmentModuleFab(output, registries, "ammo_scavenger_upgrades", AMMO_SCAVENGER_ENCHANTMENT, 4, 1_200_000, builder -> builder
                .input(GOLD_CIRCUIT, 8)
                .input(NIOBIUM_CIRCUIT, 1)
                .input(SPECIALIST_AMMO_CANISTER, 4)
                .input(EXPLOSIVES_AMMO_CANISTER, 2));
        equipmentModuleFab(output, registries, "ammo_scavenger_upgrades", AMMO_SCAVENGER_ENCHANTMENT, 5, 1_500_000, builder -> builder
                .input(NIOBIUM_CIRCUIT, 2)
                .input(SLATE_ALLOY_INGOT, 2)
                .input(AUTO_AMMO_CANISTER, 16)
                .input(SPECIALIST_AMMO_CANISTER, 8)
                .input(EXPLOSIVES_AMMO_CANISTER, 4)
                .input(MAGNUM_AMMO_CANISTER, 2));

        equipmentModuleFab(output, registries, "grenade_cores", FLAME_GRENADE_CORE, 1, 250_000, builder -> builder
                .input(COPPER_CIRCUIT, 16)
                .input(TITANIUM_INGOT, 8)
                .input(FIRE_CHARGE, 21));
        equipmentModuleFab(output, registries, "grenade_cores", FREEZE_GRENADE_CORE, 1, 250_000, builder -> builder
                .input(COPPER_CIRCUIT, 16)
                .input(TITANIUM_INGOT, 8)
                .input(SNOW_BLOCK, 16)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_SLOWNESS)).build(), POTION)));
        equipmentModuleFab(output, registries, "grenade_cores", ELECTRIC_GRENADE_CORE, 1, 500_000, builder -> builder
                .input(COPPER_CIRCUIT, 8)
                .input(GOLD_CIRCUIT, 8)
                .input(TITANIUM_INGOT, 8)
                .input(LIGHTNING_ROD, 4)
                .input(BREEZE_ROD, 1));
        equipmentModuleFab(output, registries, "grenade_cores", ACID_GRENADE_CORE, 1, 1_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 16)
                .input(NIOBIUM_CIRCUIT, 4)
                .input(TITANIUM_INGOT, 32)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_POISON)).build(), POTION)));
        equipmentModuleFab(output, registries, "grenade_cores", NEURO_GRENADE_CORE, 1, 5_000_000, builder -> builder
                .input(GOLD_CIRCUIT, 16)
                .input(NIOBIUM_CIRCUIT, 16)
                .input(TITANIUM_INGOT, 64)
                .input(NETHER_STAR)
                .input(DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.LONG_WEAKNESS)).build(), POTION)));

        machineModuleFab(output, registries, "esa_upgrades", ESA_CAPACITY_UPGRADE, 1, 250_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(COPPER_INGOT, 6));
        machineModuleFab(output, registries, "esa_upgrades", ESA_CAPACITY_UPGRADE, 2, 500_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(COPPER_INGOT, 12)
                .input(GOLD_CIRCUIT, 2));
        machineModuleFab(output, registries, "esa_upgrades", ESA_CAPACITY_UPGRADE, 3, 1_000_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(GOLD_INGOT, 12)
                .input(NIOBIUM_CIRCUIT, 2));
        machineModuleFab(output, registries, "esa_upgrades", ESA_CAPACITY_UPGRADE, 4, 5_000_000, builder -> builder
                .input(LIGHTNING_ROD, 2)
                .input(NIOBIUM_INGOT, 12)
                .input(NIOBIUM_CIRCUIT, 2));

        machineModuleFab(output, registries, "fab_upgrades", FABRICATOR_UPGRADE, 1, 500_000, builder -> builder
                .input(DYES_LIME, 4)
                .input(DIAMOND)
                .input(TITANIUM_INGOT, 4)
                .input(COPPER_CIRCUIT, 2)
                .input(REDSTONE, 8));
        machineModuleFab(output, registries, "fab_upgrades", FABRICATOR_UPGRADE, 2, 1_000_000, builder -> builder
                .input(DYES_LIME, 4)
                .input(DIAMOND, 4)
                .input(TITANIUM_INGOT, 8)
                .input(COPPER_CIRCUIT, 4)
                .input(GOLD_CIRCUIT, 2)
                .input(REDSTONE, 8));
        machineModuleFab(output, registries, "fab_upgrades", FABRICATOR_UPGRADE, 3, 5_000_000, builder -> builder
                .input(DYES_LIME, 8)
                .input(DIAMOND_BLOCK)
                .input(TITANIUM_INGOT, 16)
                .input(GOLD_CIRCUIT, 4)
                .input(NIOBIUM_CIRCUIT, 2)
                .input(REDSTONE, 16));
        machineModuleFab(output, registries, "fab_upgrades", FABRICATOR_UPGRADE, 4, 10_000_000, builder -> builder
                .input(DYES_LIME, 8)
                .input(DIAMOND_BLOCK)
                .input(TITANIUM_INGOT, 32)
                .input(NIOBIUM_CIRCUIT, 6)
                .input(REDSTONE, 32));
    }

    private void orePebblesCooking(ItemLike orePebble, ItemLike resultItem, int resultCount, RecipeOutput output)
    {
        String name = getItemName(orePebble);
        smelting(stackOf(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "smelt_" + name);
        blasting(stackOf(resultItem, resultCount)).input(orePebble).xp(0.5f).save(output, "blast_" + name);
    }

    private LimaSizedIngredientListRecipeBuilder.SimpleBuilder<GrindingRecipe, ?> grinding(ItemStack result)
    {
        return LimaSizedIngredientListRecipeBuilder.simpleBuilder(modResources, result, GrindingRecipe::new);
    }

    private void orePebbleGrinding(ItemLike orePebble, TagKey<Item> oreTag, @Nullable TagKey<Item> rawOreTag, String name, RecipeOutput output)
    {
        grinding(stackOf(orePebble, 3)).input(oreTag).save(output, "grind_" + name + "_ores");
        if (rawOreTag != null) grinding(stackOf(orePebble, 2)).input(rawOreTag).save(output, "grind_raw_" + name + "_materials");
    }

    private LimaSizedIngredientListRecipeBuilder.SimpleBuilder<RecomposingRecipe, ?> recomposing(ItemStack result)
    {
        return LimaSizedIngredientListRecipeBuilder.simpleBuilder(modResources, result, RecomposingRecipe::new);
    }

    private LimaSizedIngredientListRecipeBuilder.SimpleBuilder<MaterialFusingRecipe, ?> fusing(ItemStack result)
    {
        return LimaSizedIngredientListRecipeBuilder.simpleBuilder(modResources, result, MaterialFusingRecipe::new);
    }

    private FabricatingBuilder fabricating(ItemLike result, int energyRequired)
    {
        return fabricating(stackOf(result), energyRequired);
    }

    private FabricatingBuilder fabricating(ItemStack result, int energyRequired)
    {
        return new FabricatingBuilder(modResources, result, energyRequired);
    }

    private FabricatingBuilder weaponFabricating(Supplier<? extends WeaponItem> itemSupplier, HolderLookup.Provider registries, int energyRequired)
    {
        ItemStack result = itemSupplier.get().getDefaultInstance(registries);
        return new FabricatingBuilder(modResources, result, energyRequired);
    }

    private <U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> void upgradeFabricating(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<U> upgradeKey, int upgradeRank, int energyRequired, Supplier<? extends DataComponentType<UE>> entryDataComponent, BiFunction<Holder<U>, Integer, UE> entryFactory, ItemLike moduleItem, UnaryOperator<FabricatingBuilder> op)
    {
        Holder<U> upgradeHolder = registries.holderOrThrow(upgradeKey);
        ItemStack result = new ItemStack(moduleItem);
        result.set(entryDataComponent, entryFactory.apply(upgradeHolder, upgradeRank));
        FabricatingBuilder builder = fabricating(result, energyRequired).group(group);

        if (upgradeRank == 1)
        {
            builder.input(EMPTY_UPGRADE_MODULE);
        }
        else
        {
            Ingredient previousRankUpgrade = DataComponentIngredient.of(true, entryDataComponent, entryFactory.apply(upgradeHolder, upgradeRank - 1), moduleItem);
            builder.input(previousRankUpgrade);
        }

        String name = upgradeKey.registry().getPath() + "s/" + upgradeKey.location().getPath();
        if (upgradeRank > 1) name = name + "_" + upgradeRank;
        op.apply(builder).save(output, name);
    }

    private void equipmentModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY, EquipmentUpgradeEntry::new, EQUIPMENT_UPGRADE_MODULE, op);
    }

    private void machineModuleFab(RecipeOutput output, HolderLookup.Provider registries, String group, ResourceKey<MachineUpgrade> upgradeKey, int upgradeRank, int energyRequired, UnaryOperator<FabricatingBuilder> op)
    {
        upgradeFabricating(output, registries, group, upgradeKey, upgradeRank, energyRequired, LimaTechDataComponents.MACHINE_UPGRADE_ENTRY, MachineUpgradeEntry::new, MACHINE_UPGRADE_MODULE, op);
    }

    private void titaniumTool(RecipeOutput output, ItemLike tool, String p1, String p2, String p3)
    {
        shaped(stackOf(tool)).input('t', TITANIUM_INGOT).input('s', Tags.Items.RODS_WOODEN).patterns(p1, p2, p3).save(output);
    }

    private static class FabricatingBuilder extends LimaSizedIngredientListRecipeBuilder.SimpleBuilder<FabricatingRecipe, FabricatingBuilder>
    {
        private final int energyRequired;

        FabricatingBuilder(ModResources resources, ItemStack result, int energyRequired)
        {
            super(resources, result);
            this.energyRequired = energyRequired;
        }

        @Override
        protected FabricatingRecipe buildRecipe()
        {
            return new FabricatingRecipe(ingredients, resultItem, energyRequired, getGroupOrBlank());
        }
    }
}