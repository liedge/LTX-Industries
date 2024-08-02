package liedge.limatech.menu;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemGridTooltip(List<ItemStack> itemStacks, int maxColumns) implements TooltipComponent { }