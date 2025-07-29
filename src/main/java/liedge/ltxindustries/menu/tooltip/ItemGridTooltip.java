package liedge.ltxindustries.menu.tooltip;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemGridTooltip(List<ItemStack> gridElements, int maxColumns, int maxRows, boolean renderDecorations) implements GridTooltip<ItemStack>
{
    public static ItemGridTooltip createSingle(ItemStack stack, boolean renderDecorations)
    {
        return new ItemGridTooltip(List.of(stack), 1, 1, renderDecorations);
    }
}