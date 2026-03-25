package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public final class LTXIRecipeSerializers
{
    private LTXIRecipeSerializers() {}

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = LTXIndustries.RESOURCES.deferredRegister(Registries.RECIPE_SERIALIZER);

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindingRecipe>> GRINDING = register(LTXIIdentifiers.ID_GRINDING_RECIPE, GrindingRecipe::new, builder ->
            builder.withItemIngredients(1).withOptionalItemResults(3).withOptionalFluidResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaterialFusingRecipe>> MATERIAL_FUSING = register(LTXIIdentifiers.ID_MATERIAL_FUSING_RECIPE, MaterialFusingRecipe::new, builder ->
            builder.withItemIngredients(3).withOptionalFluidIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING = register(LTXIIdentifiers.ID_ELECTRO_CENTRIFUGING_RECIPE, ElectroCentrifugingRecipe::new, builder ->
            builder.withOptionalItemIngredients(1).withOptionalFluidIngredients(1).withOptionalItemResults(4).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MixingRecipe>> MIXING = register(LTXIIdentifiers.ID_MIXING_RECIPE, MixingRecipe::new, builder ->
            builder.withOptionalItemIngredients(4).withOptionalFluidIngredients(2).withOptionalItemResults(1).withOptionalFluidResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EnergizingRecipe>> ENERGIZING = register(LTXIIdentifiers.ID_ENERGIZING_RECIPE, EnergizingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChemicalReactingRecipe>> CHEMICAL_REACTING = register(LTXIIdentifiers.ID_CHEMICAL_REACTING, ChemicalReactingRecipe::new, builder ->
            builder.withOptionalItemIngredients(3).withOptionalFluidIngredients(3).withOptionalItemResults(2).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AssemblingRecipe>> ASSEMBLING = register(LTXIIdentifiers.ID_ASSEMBLING_RECIPE, AssemblingRecipe::new, builder ->
            builder.defaultTime(400).withItemIngredients(6).withOptionalFluidIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FabricatingRecipe>> FABRICATING = SERIALIZERS.register(LTXIIdentifiers.ID_FABRICATING_RECIPE, () -> new RecipeSerializer<>(FabricatingRecipe.CODEC, FabricatingRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GeoSynthesisRecipe>> GEO_SYNTHESIS = register(LTXIIdentifiers.ID_GEO_SYNTHESIS_RECIPE, GeoSynthesisRecipe::new, builder ->
            builder.defaultTime(60).withItemIngredients(1).withFluidIngredients(2, 2).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GardenSimulatingRecipe>> GARDEN_SIMULATING = register(LTXIIdentifiers.ID_GARDEN_SIMULATING_RECIPE, GardenSimulatingRecipe::new, builder ->
            builder.defaultTime(600).withItemIngredients(1).withOptionalFluidIngredients(1).withItemResults(4));

    private static <R extends LTXIRecipe> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> register(String name, LTXIRecipeSupplier<R> factory, UnaryOperator<LTXIRecipeCodecBuilder<R>> op)
    {
        return SERIALIZERS.register(name, () -> op.apply(new LTXIRecipeCodecBuilder<>(factory)).build());
    }
}