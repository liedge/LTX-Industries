package liedge.ltxindustries.registry.game;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import liedge.ltxindustries.recipe.RecomposingRecipe;
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

    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GrindingRecipe>> GRINDING = registerType("grinding");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<RecomposingRecipe>> RECOMPOSING = registerType("recomposing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MaterialFusingRecipe>> MATERIAL_FUSING = registerType("material_fusing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FabricatingRecipe>> FABRICATING = registerType("fabricating");

    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, LimaRecipeType<R>> registerType(String name)
    {
        return TYPES.register(name, LimaRecipeType::create);
    }
}