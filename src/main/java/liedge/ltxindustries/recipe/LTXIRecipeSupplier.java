package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function6;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface LTXIRecipeSupplier<R extends LTXIRecipe> extends Function6<List<LimaSizedItemIngredient>, List<LimaSizedFluidIngredient>, List<ItemResult>, List<FluidResult>, Integer, Optional<Holder<RecipeMode>>, R>
{
    R create(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode);

    @Override
    default R apply(List<LimaSizedItemIngredient> ingredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidResult> fluidresults, Integer craftTime, Optional<Holder<RecipeMode>> mode)
    {
        return create(ingredients, fluidIngredients, itemResults, fluidresults, craftTime, mode.orElse(null));
    }
}