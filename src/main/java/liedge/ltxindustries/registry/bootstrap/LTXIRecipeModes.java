package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public final class LTXIRecipeModes
{
    private LTXIRecipeModes() {}

    private static ResourceKey<RecipeMode> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(LTXIRegistries.Keys.RECIPE_MODES, name);
    }

    public static void bootstrap(BootstrapContext<RecipeMode> context)
    {
    }
}