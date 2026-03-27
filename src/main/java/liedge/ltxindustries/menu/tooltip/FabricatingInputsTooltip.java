package liedge.ltxindustries.menu.tooltip;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public record FabricatingInputsTooltip(ResourceKey<Recipe<?>> key) implements GridTooltip
{
    @Override
    public int columnsPerRow()
    {
        return 4;
    }

    @Override
    public int maxRows()
    {
        return 4;
    }
}