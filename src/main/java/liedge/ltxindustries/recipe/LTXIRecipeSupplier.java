package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function6;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface LTXIRecipeSupplier<R extends LTXIRecipe> extends Function6<List<SizedIngredient>, List<SizedFluidIngredient>, List<ItemResult>, List<FluidStack>, Integer, Optional<Holder<RecipeMode>>, R>
{
    R create(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode);

    @Override
    default R apply(List<SizedIngredient> ingredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidStacks, Integer craftTime, Optional<Holder<RecipeMode>> mode)
    {
        return create(ingredients, fluidIngredients, itemResults, fluidStacks, craftTime, mode.orElse(null));
    }
}