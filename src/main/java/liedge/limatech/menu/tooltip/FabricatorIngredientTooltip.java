package liedge.limatech.menu.tooltip;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public final class FabricatorIngredientTooltip implements GridTooltip<Ingredient>
{
    private final List<Ingredient> ingredients;

    public FabricatorIngredientTooltip(List<Ingredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    @Override
    public List<Ingredient> gridElements()
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