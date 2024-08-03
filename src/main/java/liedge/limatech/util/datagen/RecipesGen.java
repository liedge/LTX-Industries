package liedge.limatech.util.datagen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.generation.LimaRecipeProvider;
import liedge.limacore.data.generation.recipe.SingleResultRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTech;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechCrafting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static liedge.limatech.registry.LimaTechBlocks.*;
import static liedge.limatech.registry.LimaTechItems.*;
import static net.minecraft.world.item.Items.*;

class RecipesGen extends LimaRecipeProvider
{
    RecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries, LimaTech.RESOURCES);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
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
        shaped(out(TITANIUM_SHEARS)).input('t', in(TITANIUM_INGOT)).patterns(" t", "t ").save(output);

        shaped(out(COPPER_CIRCUIT)).input('r', in(REDSTONE)).input('m', in(COPPER_INGOT)).input('t', in(TITANIUM_NUGGET)).patterns("rrr", "mmm", "ttt").save(output);
        shaped(out(GOLD_CIRCUIT)).input('r', in(REDSTONE)).input('m', in(GOLD_INGOT)).input('t', in(TITANIUM_NUGGET)).patterns("rrr", "mmm", "ttt").save(output);
        shaped(out(NIOBIUM_CIRCUIT)).input('r', in(REDSTONE)).input('m', in(NIOBIUM_INGOT)).input('t', in(TITANIUM_NUGGET)).patterns("rrr", "mmm", "ttt").save(output);

        shaped(out(FABRICATOR)).input('t', in(TITANIUM_INGOT)).input('c', in(GOLD_CIRCUIT)).input('l', in(LIME_DYE)).input('a', in(CRAFTER)).patterns("tlt", "cac", "ttt").save(output);

        GLOW_BLOCKS.forEach((color, deferredBlock) -> {
            DyeItem dye = DyeItem.byColor(color);
            String path = deferredBlock.getId().getPath();
            shaped(out(deferredBlock, 4)).input('d', in(dye)).input('g', in(GLOWSTONE)).patterns("dg", "gd").save(output, path + "_a");
            shaped(out(deferredBlock, 8)).input('d', in(dye)).input('g', in(GLOW_INK_SAC)).patterns("dg", "gd").save(output, path + "_b");
        });
        //#endregion

        // Smelting/cooking recipes
        oreSmeltBlast(output, "smelt_raw_titanium", in(RAW_TITANIUM), out(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_stone_titanium", in(TITANIUM_ORE), out(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_deepslate_titanium", in(DEEPSLATE_TITANIUM_ORE), out(TITANIUM_INGOT));
        oreSmeltBlast(output, "smelt_raw_niobium", in(RAW_NIOBIUM), out(NIOBIUM_INGOT));
        oreSmeltBlast(output, "smelt_niobium_ore", in(NIOBIUM_ORE), out(NIOBIUM_INGOT));

        // Fabricating recipes
        fabricating(ROCKET_TURRET, 1_000_000)
                .input(in(TARGETING_TECH_SALVAGE))
                .input(in(TITANIUM_INGOT, 32))
                .input(in(NIOBIUM_CIRCUIT, 4))
                .input(in(GOLD_CIRCUIT, 8)).group("turrets").save(output);
        fabricating(SUBMACHINE_GUN, 325_000)
                .input(in(TITANIUM_INGOT, 16))
                .input(in(COPPER_CIRCUIT, 8))
                .input(in(GOLD_CIRCUIT, 4))
                .group("weapons").save(output);
        fabricating(SHOTGUN, 750_000)
                .input(in(TITANIUM_INGOT, 32))
                .input(in(COPPER_CIRCUIT, 16))
                .input(in(GOLD_CIRCUIT, 8))
                .group("weapons").save(output);
        fabricating(GRENADE_LAUNCHER, 5_000_000)
                .input(in(EXPLOSIVES_WEAPON_TECH_SALVAGE))
                .input(in(TITANIUM_INGOT, 32))
                .input(in(NIOBIUM_INGOT, 16))
                .input(in(GOLD_CIRCUIT, 16))
                .group("weapons").save(output);
        fabricating(ROCKET_LAUNCHER, 10_000_000)
                .input(in(EXPLOSIVES_WEAPON_TECH_SALVAGE))
                .input(in(TITANIUM_INGOT, 48))
                .input(in(NIOBIUM_INGOT, 16))
                .input(in(GOLD_CIRCUIT, 16))
                .group("weapons").save(output);
        fabricating(MAGNUM, 50_000_000)
                .input(in(TITANIUM_INGOT, 32))
                .input(in(NIOBIUM_INGOT, 24))
                .input(in(GOLD_CIRCUIT, 16))
                .input(in(NIOBIUM_CIRCUIT, 8))
                .group("weapons").save(output);
    }

    private FabricatingBuilder fabricating(ItemLike result, int energyRequired)
    {
        return fabricating(out(result), energyRequired);
    }

    private FabricatingBuilder fabricating(ItemStack result, int energyRequired)
    {
        return new FabricatingBuilder(modResources, result, energyRequired);
    }

    private void titaniumTool(RecipeOutput output, ItemLike tool, String p1, String p2, String p3)
    {
        shaped(out(tool)).input('t', in(TITANIUM_INGOT)).input('s', in(Tags.Items.RODS_WOODEN)).patterns(p1, p2, p3).save(output);
    }

    private static class FabricatingBuilder extends SingleResultRecipeBuilder<FabricatingRecipe, FabricatingBuilder>
    {
        private final List<Ingredient> ingredients = new ObjectArrayList<>();
        private final int energyRequired;

        FabricatingBuilder(ModResources resources, ItemStack result, int energyRequired)
        {
            super(LimaTechCrafting.FABRICATING_SERIALIZER.get(), resources, result);
            this.energyRequired = energyRequired;
        }

        private FabricatingBuilder input(Ingredient ingredient)
        {
            ingredients.add(ingredient);
            return this;
        }

        @Override
        protected void validate(ResourceLocation id) { }

        @Override
        protected FabricatingRecipe buildRecipe()
        {
            return new FabricatingRecipe(NonNullList.copyOf(ingredients), getGroupOrBlank(), result, energyRequired);
        }
    }
}