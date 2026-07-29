package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.data.generation.LTXIBootstrapUtil;
import liedge.ltxindustries.lib.icon.ItemIcon;
import liedge.ltxindustries.lib.icon.SpriteIcon;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.ChatFormatting;
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
    public static final ResourceKey<RecipeMode> ORE_PROCESSING = key("ore_processing");

    // Atmospheric Scrubber modes
    public static final ResourceKey<RecipeMode> AMBIENT_FLUIDS = key("ambient_fluids");
    public static final ResourceKey<RecipeMode> AMBIENT_GASES = key("ambient_gases");
    public static final ResourceKey<RecipeMode> LOCALIZED_FLUIDS = key("localized_fluids");
    public static final ResourceKey<RecipeMode> LOCALIZED_GASES = key("localized_gases");

    // Bio/ARU Garden modes
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
        builder(ORE_PROCESSING).icon(ItemIcon.of(LTXIItems.RAW_SILVER)).register(context);

        builder(AMBIENT_FLUIDS).icon(LTXIBootstrapUtil.blueRingOverlay(ItemIcon.of(Items.WATER_BUCKET))).register(context);
        builder(AMBIENT_GASES).icon(LTXIBootstrapUtil.blueRingOverlay(ItemIcon.of(LTXIItems.ARGON_BUCKET))).register(context);
        builder(LOCALIZED_FLUIDS).icon(LTXIBootstrapUtil.greenRingOverlay(ItemIcon.of(Items.WATER_BUCKET))).styledName(ChatFormatting.YELLOW).register(context);
        builder(LOCALIZED_GASES).icon(LTXIBootstrapUtil.greenRingOverlay(ItemIcon.of(LTXIItems.ARGON_BUCKET))).styledName(ChatFormatting.YELLOW).register(context);

        builder(GS_FARMING).icon(ItemIcon.of(Items.WHEAT)).register(context);
        builder(GS_WOODS).icon(ItemIcon.of(Items.OAK_LOG)).register(context);
        builder(GS_ORCHARD).icon(ItemIcon.of(Items.APPLE)).register(context);
        builder(GS_FOLIAGE).icon(ItemIcon.of(Items.OAK_LEAVES)).register(context);
    }
}