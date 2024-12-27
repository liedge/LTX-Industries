package liedge.limatech.registry;

import liedge.limacore.recipe.LimaRecipeSerializer;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.limatech.LimaTech;
import liedge.limatech.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechRecipeSerializers
{
    private LimaTechRecipeSerializers() {}

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = LimaTech.RESOURCES.deferredRegister(Registries.RECIPE_SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<GrindingRecipe>> GRINDING = SERIALIZERS.register("grinding", id -> LimaSimpleSizedIngredientRecipe.maxIngredientsSerializer(id, GrindingRecipe::new, 1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<RecomposingRecipe>> RECOMPOSING = SERIALIZERS.register("recomposing", id -> LimaSimpleSizedIngredientRecipe.maxIngredientsSerializer(id, RecomposingRecipe::new, 1));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<MaterialFusingRecipe>> MATERIAL_FUSING = SERIALIZERS.register("material_fusing", id -> LimaSimpleSizedIngredientRecipe.maxIngredientsSerializer(id, MaterialFusingRecipe::new, 3));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<FabricatingRecipe>> FABRICATING = SERIALIZERS.register("fabricating", id -> new LimaRecipeSerializer<>(id, FabricatingRecipe.CODEC, FabricatingRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<WeaponFabricatingRecipe>> WEAPON_FABRICATING = SERIALIZERS.register("weapon_fabricating", id -> new LimaRecipeSerializer<>(id, WeaponFabricatingRecipe.CODEC, WeaponFabricatingRecipe.STREAM_CODEC));

    public static void initRegister(IEventBus bus)
    {
        SERIALIZERS.register(bus);
    }
}