package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.RecipeModes.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIRecipeModes.*;

class RecipeModeTagsGen extends LimaTagsProvider<RecipeMode>
{
    RecipeModeTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, LTXIRegistries.Keys.RECIPE_MODES, LTXIndustries.MODID, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        buildTag(DEFAULT_GRINDING).add(DYE_EXTRACTION);
        buildTag(DEFAULT_ELECTRO_CENTRIFUGING).add(DYE_EXTRACTION, CHEM_DISSOLUTION, ECF_ELECTROLYZE);
        buildTag(DEFAULT_MIXING).add(DYE_EXTRACTION, CHEM_DISSOLUTION);
        buildTag(DEFAULT_ENERGIZING).add(DYE_EXTRACTION);
        buildTag(DEFAULT_CHEMICAL_REACTING).add(CHEM_DISSOLUTION);
        buildTag(DEFAULT_GARDEN_SIMULATING).add(GS_FARMING, GS_WOODS, GS_ORCHARD, GS_FOLIAGE);
    }
}