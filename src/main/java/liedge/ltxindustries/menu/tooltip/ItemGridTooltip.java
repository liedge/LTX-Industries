package liedge.ltxindustries.menu.tooltip;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemGridTooltip(List<ItemStack> gridElements, int maxColumns, int maxRows, boolean renderDecorations) implements GridTooltip<ItemStack> { }