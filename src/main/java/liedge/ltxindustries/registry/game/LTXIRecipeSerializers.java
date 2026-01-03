package liedge.ltxindustries.registry.game;

import liedge.limacore.recipe.LimaRecipeSerializer;
import liedge.limacore.registry.LimaDeferredRecipeSerializers;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.UnaryOperator;

public final class LTXIRecipeSerializers
{
    private LTXIRecipeSerializers() {}

    private static final LimaDeferredRecipeSerializers SERIALIZERS = LTXIndustries.RESOURCES.deferredRecipeSerializers();

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<GrindingRecipe>> GRINDING = register(LTXIIdentifiers.ID_GRINDING_RECIPE, GrindingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(3));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<MaterialFusingRecipe>> MATERIAL_FUSING = register(LTXIIdentifiers.ID_MATERIAL_FUSING_RECIPE, MaterialFusingRecipe::new, builder ->
            builder.withItemIngredients(3).withOptionalFluidIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING = register(LTXIIdentifiers.ID_ELECTRO_CENTRIFUGING_RECIPE, ElectroCentrifugingRecipe::new, builder ->
            builder.withOptionalItemIngredients(1).withOptionalFluidIngredients(1).withOptionalItemResults(4).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<MixingRecipe>> MIXING = register(LTXIIdentifiers.ID_MIXING_RECIPE, MixingRecipe::new, builder ->
            builder.withOptionalItemIngredients(4).withOptionalFluidIngredients(2).withOptionalItemResults(1).withOptionalFluidResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<EnergizingRecipe>> ENERGIZING = register(LTXIIdentifiers.ID_ENERGIZING_RECIPE, EnergizingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<ChemicalReactingRecipe>> CHEMICAL_REACTING = register(LTXIIdentifiers.ID_CHEMICAL_REACTING, ChemicalReactingRecipe::new, builder ->
            builder.withOptionalItemIngredients(3).withOptionalFluidIngredients(3).withOptionalItemResults(2).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<AssemblingRecipe>> ASSEMBLING = register(LTXIIdentifiers.ID_ASSEMBLING_RECIPE, AssemblingRecipe::new, builder ->
            builder.withItemIngredients(6).withOptionalFluidIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<FabricatingRecipe>> FABRICATING = SERIALIZERS.register(LTXIIdentifiers.ID_FABRICATING_RECIPE, id -> LimaRecipeSerializer.of(id, FabricatingRecipe.CODEC, FabricatingRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<GeoSynthesisRecipe>> GEO_SYNTHESIS = register(LTXIIdentifiers.ID_GEO_SYNTHESIS_RECIPE, GeoSynthesisRecipe::new, builder ->
            builder.defaultTime(60).withItemIngredients(1).withFluidIngredients(2, 2).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<GardenSimulatingRecipe>> GARDEN_SIMULATING = register(LTXIIdentifiers.ID_GARDEN_SIMULATING_RECIPE, GardenSimulatingRecipe::new, builder ->
            builder.defaultTime(600).withItemIngredients(1).withOptionalFluidIngredients(1).withItemResults(4));

    private static <R extends LTXIRecipe> DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<R>> register(String name, LTXIRecipeSupplier<R> factory, UnaryOperator<LTXIRecipeSerializer.Builder<R>> op)
    {
        return SERIALIZERS.register(name, id -> op.apply(LTXIRecipeSerializer.builder(factory)).build(id));
    }
}