package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class LTXIRecipe extends LimaCustomRecipe<LTXIRecipeInput>
{
    public static final int DEFAULT_CRAFTING_TIME = 200;

    private final int craftTime;
    @Nullable
    private final Holder<RecipeMode> mode;

    protected LTXIRecipe(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemInputs, fluidInputs, itemResults, fluidResults);
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
        if (this.mode != null)
        {
            Holder<RecipeMode> inputMode = input.mode();
            if (!Objects.equals(this.mode, inputMode)) return false;
        }

        return super.matches(input, level);
    }
}