package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.Translatable;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SimpleHintItem extends Item implements TooltipShiftHintItem
{
    private Translatable shiftHint;

    public SimpleHintItem(Properties properties)
    {
        super(properties);
    }

    public Translatable getShiftHint()
    {
        if (shiftHint == null) shiftHint = Translatable.standalone(getDescriptionId() + ".hint");
        return shiftHint;
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        consumer.accept(getShiftHint().translate().withStyle(ChatFormatting.GRAY));
    }
}