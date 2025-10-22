package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function5;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public abstract class LTXIRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public static final int DEFAULT_CRAFTING_TIME = 200;

    private final int craftTime;

    protected LTXIRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults);
        this.craftTime = craftTime;
    }

    public int getCraftTime()
    {
        return craftTime;
    }

    @FunctionalInterface
    public interface LTXIRecipeFactory<R extends LTXIRecipe> extends Function5<List<SizedIngredient>, List<SizedFluidIngredient>, List<ItemResult>, List<FluidStack>, Integer, R> {}
}