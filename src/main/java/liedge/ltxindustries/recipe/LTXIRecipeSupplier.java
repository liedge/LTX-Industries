package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function6;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface LTXIRecipeSupplier<R extends LTXIRecipe> extends Function6<List<LimaSizedItemIngredient>, List<LimaSizedFluidIngredient>, List<ItemResult>, List<FluidStack>, Integer, Optional<Holder<RecipeMode>>, R>
{
    R create(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode);

    @Override
    default R apply(List<LimaSizedItemIngredient> ingredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidStacks, Integer craftTime, Optional<Holder<RecipeMode>> mode)
    {
        return create(ingredients, fluidIngredients, itemResults, fluidStacks, craftTime, mode.orElse(null));
    }
}