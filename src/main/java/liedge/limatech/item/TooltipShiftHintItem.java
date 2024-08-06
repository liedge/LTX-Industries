package liedge.limatech.item;

import com.mojang.datafixers.util.Either;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface TooltipShiftHintItem
{
    Translatable HINT_HOVER_TOOLTIP = LimaTech.RESOURCES.translationHolder("tooltip.{}.shift_hint_tooltip");

    void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipCollector collector);

    @FunctionalInterface
    interface TooltipCollector extends Consumer<Either<FormattedText, TooltipComponent>>
    {
        default void with(FormattedText text)
        {
            accept(Either.left(text));
        }

        default void with(TooltipComponent component)
        {
            accept(Either.right(component));
        }
    }
}