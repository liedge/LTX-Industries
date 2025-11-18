package liedge.ltxindustries.menu.tooltip;

import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;

import java.util.List;

public final class FabricatorIngredientTooltip implements GridTooltip<LimaSizedItemIngredient>
{
    private final List<LimaSizedItemIngredient> ingredients;

    public FabricatorIngredientTooltip(List<LimaSizedItemIngredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    @Override
    public List<LimaSizedItemIngredient> gridElements()
    {
        return ingredients;
    }

    @Override
    public int maxColumns()
    {
        return 4;
    }

    @Override
    public int maxRows()
    {
        return 4;
    }
}