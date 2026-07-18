package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.icon.ItemIcon;
import liedge.ltxindustries.lib.icon.SpriteIcon;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;

import static liedge.ltxindustries.recipe.RecipeMode.builder;

public final class LTXIRecipeModes
{
    private LTXIRecipeModes() {}

    public static final ResourceKey<RecipeMode> ELEMENT_EXTRACTION = key("element_extraction");

    public static final ResourceKey<RecipeMode> DYE_EXTRACTION = key("dye_extraction");
    public static final ResourceKey<RecipeMode> CHEM_DISSOLUTION = key("chemical_dissolve");

    public static final ResourceKey<RecipeMode> GS_FARMING = key("farming");
    public static final ResourceKey<RecipeMode> GS_WOODS = key("lumber");
    public static final ResourceKey<RecipeMode> GS_ORCHARD = key("orchard");
    public static final ResourceKey<RecipeMode> GS_FOLIAGE = key("foliage");

    private static ResourceKey<RecipeMode> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(LTXIRegistries.Keys.RECIPE_MODES, name);
    }

    public static void bootstrap(BootstrapContext<RecipeMode> context)
    {
        builder(ELEMENT_EXTRACTION).icon(SpriteIcon.create("sodium_ion")).register(context);
        builder(DYE_EXTRACTION).icon(ItemIcon.of(Items.LIME_DYE)).register(context);
        builder(CHEM_DISSOLUTION).icon(ItemIcon.of(LTXIItems.VIRIDIC_ACID_BUCKET)).register(context);

        builder(GS_FARMING).icon(ItemIcon.of(Items.WHEAT)).register(context);
        builder(GS_WOODS).icon(ItemIcon.of(Items.OAK_LOG)).register(context);
        builder(GS_ORCHARD).icon(ItemIcon.of(Items.APPLE)).register(context);
        builder(GS_FOLIAGE).icon(ItemIcon.of(Items.OAK_LEAVES)).register(context);
    }
}