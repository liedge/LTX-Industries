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

public final class LTXIRecipeSerializers
{
    private LTXIRecipeSerializers() {}

    private static final LimaDeferredRecipeSerializers SERIALIZERS = LTXIndustries.RESOURCES.deferredRecipeSerializers();

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<GrindingRecipe>> GRINDING = SERIALIZERS.registerSerializer(LTXICommonIds.ID_GRINDING_RECIPE, GrindingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(3));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<MaterialFusingRecipe>> MATERIAL_FUSING = SERIALIZERS.registerSerializer(LTXICommonIds.ID_MATERIAL_FUSING_RECIPE, MaterialFusingRecipe::new, builder ->
            builder.withItemIngredients(3).withOptionalFluidIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING = SERIALIZERS.registerSerializer(LTXICommonIds.ID_ELECTRO_CENTRIFUGING_RECIPE, ElectroCentrifugingRecipe::new, builder ->
            builder.withOptionalItemIngredients(1).withOptionalFluidIngredients(1).withOptionalItemResults(4).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<MixingRecipe>> MIXING = SERIALIZERS.registerSerializer(LTXICommonIds.ID_MIXING_RECIPE, MixingRecipe::new, builder ->
            builder.withOptionalItemIngredients(4).withOptionalFluidIngredients(2).withOptionalItemResults(1).withOptionalFluidResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<EnergizingRecipe>> ENERGIZING = SERIALIZERS.registerSerializer(LTXICommonIds.ID_ENERGIZING_RECIPE, EnergizingRecipe::new, builder ->
            builder.withItemIngredients(1).withItemResults(1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<ChemicalReactingRecipe>> CHEMICAL_REACTING = SERIALIZERS.registerSerializer(LTXICommonIds.ID_CHEMICAL_REACTING, ChemicalReactingRecipe::new, builder ->
            builder.withOptionalItemIngredients(3).withOptionalFluidIngredients(3).withOptionalItemResults(2).withOptionalFluidResults(2));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<FabricatingRecipe>> FABRICATING = SERIALIZERS.register(LTXICommonIds.ID_FABRICATING_RECIPE, id -> LimaRecipeSerializer.of(id, FabricatingRecipe.CODEC, FabricatingRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<DefaultUpgradeModuleRecipe>> DEFAULT_UPGRADE_MODULE = SERIALIZERS.register("default_upgrade_module", () -> new SimpleCraftingRecipeSerializer<>(DefaultUpgradeModuleRecipe::new));
}