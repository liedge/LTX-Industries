package liedge.limatech.menu.tooltip;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemGridTooltip(List<ItemStack> gridElements, int maxColumns, int maxRows) implements GridTooltip<ItemStack>
{
    public ItemGridTooltip(List<ItemStack> gridElements, int maxColumns)
    {
        this(gridElements, maxColumns, 1);
    }
}