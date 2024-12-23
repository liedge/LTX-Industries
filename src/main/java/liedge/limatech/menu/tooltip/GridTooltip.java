package liedge.limatech.menu.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

public interface GridTooltip<T> extends TooltipComponent
{
    List<T> gridElements();

    int maxColumns();

    int maxRows();
}