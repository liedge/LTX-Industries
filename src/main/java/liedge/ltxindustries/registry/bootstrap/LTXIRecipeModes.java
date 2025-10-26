package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;

import static liedge.ltxindustries.recipe.RecipeMode.builder;
import static liedge.ltxindustries.registry.game.LTXIRecipeTypes.ELECTRO_CENTRIFUGING;
import static liedge.ltxindustries.registry.game.LTXIRecipeTypes.GARDEN_SIMULATING;

public final class LTXIRecipeModes
{
    private LTXIRecipeModes() {}

    public static final ResourceKey<RecipeMode> ECF_ELECTROLYZE = key("electrolyze");
    public static final ResourceKey<RecipeMode> GS_WOODS = key("lumber");
    public static final ResourceKey<RecipeMode> GS_ORCHARD = key("orchard");
    public static final ResourceKey<RecipeMode> GS_FOLIAGE = key("foliage");

    private static ResourceKey<RecipeMode> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(LTXIRegistries.Keys.RECIPE_MODES, name);
    }

    public static void bootstrap(BootstrapContext<RecipeMode> context)
    {
        builder(ECF_ELECTROLYZE).forType(ELECTRO_CENTRIFUGING).icon(LTXIItems.ELECTRIC_CHEMICAL).styledName(LTXIConstants.ELECTRIC_GREEN).register(context);
        builder(GS_WOODS).forType(GARDEN_SIMULATING).icon(Items.OAK_LOG).register(context);
        builder(GS_ORCHARD).forType(GARDEN_SIMULATING).icon(Items.APPLE).register(context);
        builder(GS_FOLIAGE).forType(GARDEN_SIMULATING).icon(Items.OAK_LEAVES).register(context);
    }
}