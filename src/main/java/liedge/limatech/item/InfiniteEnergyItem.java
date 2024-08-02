package liedge.limatech.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InfiniteEnergyItem extends Item implements TooltipShiftHintItem.TextOnly
{
    public InfiniteEnergyItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return true;
    }

    @Override
    public void appendTooltipHintLines(@Nullable Level level, ItemStack stack, Consumer<FormattedText> consumer)
    {
        consumer.accept(Component.literal("Supplies an extremely high (~2.14 billion) amount of energy units.").withStyle(ChatFormatting.GRAY));
    }
}