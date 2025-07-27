package liedge.ltxindustries.registry.game;

import liedge.limacore.recipe.LimaRecipeSerializer;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIRecipeSerializers
{
    private LTXIRecipeSerializers() {}

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = LTXIndustries.RESOURCES.deferredRegister(Registries.RECIPE_SERIALIZER);

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<GrindingRecipe>> GRINDING = SERIALIZERS.register("grinding", id -> LimaSimpleSizedIngredientRecipe.maxIngredientsSerializer(id, GrindingRecipe::new, 1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<MaterialFusingRecipe>> MATERIAL_FUSING = SERIALIZERS.register("material_fusing", id -> LimaSimpleSizedIngredientRecipe.maxIngredientsSerializer(id, MaterialFusingRecipe::new, 3));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<FabricatingRecipe>> FABRICATING = SERIALIZERS.register("fabricating", id -> new LimaRecipeSerializer<>(id, FabricatingRecipe.CODEC, FabricatingRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<DefaultUpgradeModuleRecipe>> DEFAULT_UPGRADE_MODULE = SERIALIZERS.register("default_upgrade_module", () -> new SimpleCraftingRecipeSerializer<>(DefaultUpgradeModuleRecipe::new));
}