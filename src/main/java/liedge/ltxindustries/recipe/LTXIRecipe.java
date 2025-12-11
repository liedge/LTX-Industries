package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Objects;

public abstract class LTXIRecipe extends LimaCustomRecipe<LTXIRecipeInput>
{
    public static final int DEFAULT_CRAFTING_TIME = 200;

    private final int craftTime;
    @Nullable
    private final Holder<RecipeMode> mode;

    protected LTXIRecipe(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, @UnknownNullability List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
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

            if (inputMode == null || !Objects.equals(inputMode, mode) || !inputMode.value().recipeTypes().contains(typeHolder()))
            {
                return false;
            }
        }

        return super.matches(input, level);
    }

    private Holder<RecipeType<?>> typeHolder()
    {
        return BuiltInRegistries.RECIPE_TYPE.wrapAsHolder(getType());
    }
}