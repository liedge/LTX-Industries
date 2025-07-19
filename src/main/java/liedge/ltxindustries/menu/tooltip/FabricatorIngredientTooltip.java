package liedge.ltxindustries.menu.tooltip;

import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public final class FabricatorIngredientTooltip implements GridTooltip<SizedIngredient>
{
    private final List<SizedIngredient> ingredients;

    public FabricatorIngredientTooltip(List<SizedIngredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    @Override
    public List<SizedIngredient> gridElements()
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