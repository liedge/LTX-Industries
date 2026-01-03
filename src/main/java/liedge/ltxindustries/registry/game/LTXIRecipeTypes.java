package liedge.ltxindustries.registry.game;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIRecipeTypes
{
    private LTXIRecipeTypes() {}

    private static final DeferredRegister<RecipeType<?>> TYPES = LTXIndustries.RESOURCES.deferredRegister(Registries.RECIPE_TYPE);

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GrindingRecipe>> GRINDING = registerType(LTXIIdentifiers.ID_GRINDING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MaterialFusingRecipe>> MATERIAL_FUSING = registerType(LTXIIdentifiers.ID_MATERIAL_FUSING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING = registerType(LTXIIdentifiers.ID_ELECTRO_CENTRIFUGING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MixingRecipe>> MIXING = registerType(LTXIIdentifiers.ID_MIXING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<EnergizingRecipe>> ENERGIZING = registerType(LTXIIdentifiers.ID_ENERGIZING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<ChemicalReactingRecipe>> CHEMICAL_REACTING = registerType(LTXIIdentifiers.ID_CHEMICAL_REACTING);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<AssemblingRecipe>> ASSEMBLING = registerType(LTXIIdentifiers.ID_ASSEMBLING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FabricatingRecipe>> FABRICATING = registerType(LTXIIdentifiers.ID_FABRICATING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GeoSynthesisRecipe>> GEO_SYNTHESIS = registerType(LTXIIdentifiers.ID_GEO_SYNTHESIS_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GardenSimulatingRecipe>> GARDEN_SIMULATING = registerType(LTXIIdentifiers.ID_GARDEN_SIMULATING_RECIPE);

    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, LimaRecipeType<R>> registerType(String name)
    {
        return TYPES.register(name, LimaRecipeType::create);
    }
}