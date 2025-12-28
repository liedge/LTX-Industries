package liedge.ltxindustries.menu.tooltip;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;

import java.util.List;

public record RecipeIngredientsTooltip(List<LimaSizedItemIngredient> gridElements, int maxColumns, int maxRows) implements GridTooltip<LimaSizedItemIngredient>
{
    public static RecipeIngredientsTooltip create(LimaCustomRecipe<?> recipe, int maxColumns, int maxRows)
    {
        return new RecipeIngredientsTooltip(recipe.getItemIngredients(), maxColumns, maxRows);
    }
}