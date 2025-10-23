package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class LTXIRecipe extends LimaCustomRecipe<LTXIRecipeInput>
{
    public static final int DEFAULT_CRAFTING_TIME = 200;

    private final int craftTime;
    @Nullable
    private final Holder<RecipeMode> mode;

    protected LTXIRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults);
        this.craftTime = craftTime;
        this.mode = mode;
    }

    public int getCraftTime()
    {
        return craftTime;
    }

    public @Nullable Holder<RecipeMode> getMode()
    {
        return mode;
    }

    @Override
    public boolean matches(LTXIRecipeInput input, Level level)
    {
        if (mode != null)
        {
            Holder<RecipeMode> inputMode = input.mode();

            if (inputMode == null || !Objects.equals(inputMode, mode) || !inputMode.value().recipeType().equals(this.getType()))
                return false;
        }

        return super.matches(input, level);
    }
}