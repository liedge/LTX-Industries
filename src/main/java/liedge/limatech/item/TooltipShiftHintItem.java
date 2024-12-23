package liedge.limatech.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface TooltipShiftHintItem
{
    Translatable HINT_HOVER_TOOLTIP = LimaTech.RESOURCES.translationHolder("tooltip.{}.shift_hint_tooltip");

    void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer);
}