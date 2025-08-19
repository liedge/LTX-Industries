package liedge.ltxindustries.registry.game;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.LTXICommonIds;
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

    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GrindingRecipe>> GRINDING = registerType(LTXICommonIds.ID_GRINDING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MaterialFusingRecipe>> MATERIAL_FUSING = registerType(LTXICommonIds.ID_MATERIAL_FUSING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING = registerType(LTXICommonIds.ID_ELECTRO_CENTRIFUGING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MixingRecipe>> MIXING = registerType(LTXICommonIds.ID_MIXING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<EnergizingRecipe>> ENERGIZING = registerType(LTXICommonIds.ID_ENERGIZING_RECIPE);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<ChemicalReactingRecipe>> CHEMICAL_REACTING = registerType(LTXICommonIds.ID_CHEMICAL_REACTING);
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FabricatingRecipe>> FABRICATING = registerType(LTXICommonIds.ID_FABRICATING_RECIPE);

    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, LimaRecipeType<R>> registerType(String name)
    {
        return TYPES.register(name, LimaRecipeType::create);
    }
}