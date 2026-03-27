package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function6;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface LTXIRecipeSupplier<R extends LTXIRecipe> extends Function6<List<RecipeItemInput>, List<RecipeFluidInput>, List<ItemResult>, List<FluidResult>, Integer, Optional<Holder<RecipeMode>>, R>
{
    R create(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode);

    @Override
    default R apply(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, Integer craftTime, Optional<Holder<RecipeMode>> mode)
    {
        return create(itemInputs, fluidInputs, itemResults, fluidResults, craftTime, mode.orElse(null));
    }
}