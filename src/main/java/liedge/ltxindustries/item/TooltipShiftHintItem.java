package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface TooltipShiftHintItem
{
    void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer);
}