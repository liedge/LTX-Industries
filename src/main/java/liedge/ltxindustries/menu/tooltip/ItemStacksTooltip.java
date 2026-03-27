package liedge.ltxindustries.menu.tooltip;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemStacksTooltip(List<ItemStack> stacks, int columnsPerRow, int maxRows, boolean decorate) implements GridTooltip
{
    public static ItemStacksTooltip createSingle(ItemStack stack, boolean renderDecorations)
    {
        return new ItemStacksTooltip(List.of(stack), 1, 1, renderDecorations);
    }
}