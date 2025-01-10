package liedge.limatech.registry;

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

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GrindingRecipe>> GRINDING = register("grinding");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<RecomposingRecipe>> RECOMPOSING = register("recomposing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<MaterialFusingRecipe>> MATERIAL_FUSING = register("material_fusing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FabricatingRecipe>> FABRICATING = register("fabricating");

    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, LimaRecipeType<R>> register(String name)
    {
        return TYPES.register(name, LimaRecipeType::create);
    }
}