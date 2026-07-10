package liedge.ltxindustries.data.generation;

import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIDataMaps;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.RecipeModes.*;

class DataMapsGen extends DataMapProvider
{
    DataMapsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries)
    {
        // Vibration frequencies
        builder(NeoForgeDataMaps.VIBRATION_FREQUENCIES)
                .add(LTXIGameEvents.WEAPON_FIRED, new VibrationFrequency(3), false)
                .add(LTXIGameEvents.PROJECTILE_IMPACT, new VibrationFrequency(2), false);

        defaultRecipeModes(registries, LTXIRecipeTypes.GRINDING, DEFAULT_GRINDING);
        defaultRecipeModes(registries, LTXIRecipeTypes.ELECTRO_CENTRIFUGING, DEFAULT_ELECTRO_CENTRIFUGING);
        defaultRecipeModes(registries, LTXIRecipeTypes.MIXING, DEFAULT_MIXING);
        defaultRecipeModes(registries, LTXIRecipeTypes.ENERGIZING, DEFAULT_ENERGIZING);
        defaultRecipeModes(registries, LTXIRecipeTypes.CHEMICAL_REACTING, DEFAULT_CHEMICAL_REACTING);
        defaultRecipeModes(registries, LTXIRecipeTypes.GARDEN_SIMULATING, DEFAULT_GARDEN_SIMULATING);
    }

    private void defaultRecipeModes(HolderLookup.Provider registries, Holder<RecipeType<?>> recipeType, TagKey<RecipeMode> tag)
    {
        builder(LTXIDataMaps.DEFAULT_RECIPE_MODES).add(recipeType, registries.getOrThrow(tag), false);
    }
}