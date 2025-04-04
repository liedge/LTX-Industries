package liedge.limatech.registry.game;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.LimaTech;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.recipe.MaterialFusingRecipe;
import liedge.limatech.recipe.RecomposingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechRecipeTypes
{
    private LimaTechRecipeTypes() {}

    private static final DeferredRegister<RecipeType<?>> TYPES = LimaTech.RESOURCES.deferredRegister(Registries.RECIPE_TYPE);

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GrindingRecipe>> GRINDING = registerType("grinding");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<RecomposingRecipe>> RECOMPOSING = registerType("recomposing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MaterialFusingRecipe>> MATERIAL_FUSING = registerType("material_fusing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FabricatingRecipe>> FABRICATING = registerType("fabricating");

    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, LimaRecipeType<R>> registerType(String name)
    {
        return TYPES.register(name, LimaRecipeType::create);
    }
}