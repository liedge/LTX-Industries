package liedge.ltxindustries.menu.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface GridTooltip extends TooltipComponent
{
    int columnsPerRow();

    int maxRows();
}