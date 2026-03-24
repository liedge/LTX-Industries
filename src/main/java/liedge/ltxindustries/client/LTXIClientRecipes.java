package liedge.ltxindustries.client;

import liedge.limacore.util.LimaStreamsUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public final class LTXIClientRecipes
{
    private LTXIClientRecipes() {}

    private static RecipeMap recipeMap = RecipeMap.EMPTY;

    public static RecipeMap getRecipeMap()
    {
        return recipeMap;
    }

    public static void setRecipeMap(RecipeMap recipeMap)
    {
        LTXIClientRecipes.recipeMap = recipeMap;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> @Nullable RecipeHolder<T> byKey(RecipeType<T> type, @Nullable ResourceKey<Recipe<?>> key)
    {
        if (key == null) return null;

        RecipeHolder<?> holder = recipeMap.byKey(key);
        if (holder != null && holder.value().getType() == type)
        {
            return (RecipeHolder<T>) holder;
        }

        return null;
    }

    public static <T extends Recipe<?>> @Nullable RecipeHolder<T> byKey(Supplier<? extends RecipeType<T>> typeSupplier, @Nullable ResourceKey<Recipe<?>> key)
    {
        return byKey(typeSupplier.get(), key);
    }

    public static <I extends RecipeInput, R extends Recipe<I>> Collection<RecipeHolder<R>> byType(Supplier<? extends RecipeType<R>> typeSupplier)
    {
        return recipeMap.byType(typeSupplier.get());
    }

    public static List<RecipeHolder<FabricatingRecipe>> getSortedFabricatingRecipes()
    {
        return recipeMap.byType(LTXIRecipeTypes.FABRICATING.get())
                .stream()
                .sorted(Comparator.<RecipeHolder<FabricatingRecipe>, String>comparing(holder -> holder.value().group())
                        .thenComparing(RecipeHolder::id))
                .collect(LimaStreamsUtil.toObjectList());
    }
}