package liedge.ltxindustries.registry.game;

import liedge.limacore.recipe.LimaRecipeSerializer;
import liedge.limacore.registry.LimaDeferredRecipeSerializers;
import liedge.ltxindustries.LTXICommonIds;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
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

    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<GrindingRecipe>> GRINDING = register(LTXICommonIds.ID_GRINDING_RECIPE, GrindingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(3));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<MaterialFusingRecipe>> MATERIAL_FUSING = register(LTXICommonIds.ID_MATERIAL_FUSING_RECIPE, MaterialFusingRecipe::new, builder ->
            builder.withItemIngredients(3).withOptionalFluidIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING = register(LTXICommonIds.ID_ELECTRO_CENTRIFUGING_RECIPE, ElectroCentrifugingRecipe::new, builder ->
            builder.withOptionalItemIngredients(1).withOptionalFluidIngredients(1).withOptionalItemResults(4).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<MixingRecipe>> MIXING = register(LTXICommonIds.ID_MIXING_RECIPE, MixingRecipe::new, builder ->
            builder.withOptionalItemIngredients(4).withOptionalFluidIngredients(2).withOptionalItemResults(1).withOptionalFluidResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<EnergizingRecipe>> ENERGIZING = register(LTXICommonIds.ID_ENERGIZING_RECIPE, EnergizingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<ChemicalReactingRecipe>> CHEMICAL_REACTING = register(LTXICommonIds.ID_CHEMICAL_REACTING, ChemicalReactingRecipe::new, builder ->
            builder.withOptionalItemIngredients(3).withOptionalFluidIngredients(3).withOptionalItemResults(2).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<FabricatingRecipe>> FABRICATING = SERIALIZERS.register(LTXICommonIds.ID_FABRICATING_RECIPE, id -> LimaRecipeSerializer.of(id, FabricatingRecipe.CODEC, FabricatingRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<GardenSimulatingRecipe>> GARDEN_SIMULATING = register(LTXICommonIds.ID_GARDEN_SIMULATING_RECIPE, GardenSimulatingRecipe::new, builder ->
            builder.defaultTime(600).withItemIngredients(1).withOptionalFluidIngredients(1).withItemResults(4));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<DefaultUpgradeModuleRecipe>> DEFAULT_UPGRADE_MODULE = SERIALIZERS.register("default_upgrade_module", () -> new SimpleCraftingRecipeSerializer<>(DefaultUpgradeModuleRecipe::new));

    private static <R extends LTXIRecipe> DeferredHolder<RecipeSerializer<?>, LTXIRecipeSerializer<R>> register(String name, LTXIRecipeSupplier<R> factory, UnaryOperator<LTXIRecipeSerializer.Builder<R>> op)
    {
        return SERIALIZERS.register(name, id -> op.apply(LTXIRecipeSerializer.builder(factory)).build(id));
    }
}