package liedge.limatech.util;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limatech.menu.tooltip.ItemGridTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

import static liedge.limacore.capability.energy.LimaEnergyUtil.formatEnergyWithSuffix;
import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static liedge.limacore.util.LimaMathUtil.*;
import static liedge.limatech.LimaTechConstants.REM_BLUE;
import static liedge.limatech.client.LimaTechLang.*;

public final class LimaTechTooltipUtil
{
    private LimaTechTooltipUtil() {}

    public static void appendSimpleEnergyTooltip(TooltipLineConsumer consumer, int energy)
    {
        consumer.accept(INLINE_ENERGY_STORED.translateArgs(formatEnergyWithSuffix(energy)).withStyle(REM_BLUE.chatStyle()));
    }

    public static void appendExtendedEnergyTooltip(TooltipLineConsumer consumer, int energy, int capacity, int transferRate)
    {
        appendSimpleEnergyTooltip(consumer, energy);
        consumer.accept(INLINE_ENERGY_CAPACITY.translateArgs(formatEnergyWithSuffix(capacity)).withStyle(REM_BLUE.chatStyle()));
        consumer.accept(INLINE_ENERGY_TRANSFER_RATE.translateArgs(formatEnergyWithSuffix(transferRate)).withStyle(REM_BLUE.chatStyle()));
    }

    public static void appendSimpleEnergyItemTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        appendSimpleEnergyTooltip(consumer, stack.getOrDefault(ENERGY, 0));
    }

    public static void appendInventoryPreviewTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        List<ItemStack> inventory = stack.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().toList();
        if (!inventory.isEmpty())
        {
            consumer.accept(ITEM_INVENTORY_TOOLTIP.translate().withStyle(ChatFormatting.GRAY));
            consumer.accept(new ItemGridTooltip(inventory, 6));
        }
        else
        {
            consumer.accept(EMPTY_ITEM_INVENTORY_TOOLTIP.translate().withStyle(ChatFormatting.GRAY));
        }
    }

    public static ChatFormatting numSignColor(double zero, double value, boolean invertColors)
    {
        if (invertColors)
        {
            return value < zero ? ChatFormatting.GREEN : ChatFormatting.RED;
        }
        else
        {
            return value < zero ? ChatFormatting.RED : ChatFormatting.GREEN;
        }
    }

    public static ChatFormatting numSignColor(double value, boolean invertColors)
    {
        return numSignColor(0, value, invertColors);
    }

    public static String formatFlatNumber(double value)
    {
        return value < 1000 ? FORMAT_2_ROUND_FLOOR.format(value) : FORMAT_COMMA_INT.format(value);
    }

    public static MutableComponent flatNumberWithSign(double value)
    {
        String formattedValue = formatFlatNumber(value);
        if (value >= 0) formattedValue = "+" + formattedValue;
        return Component.literal(formattedValue);
    }

    public static MutableComponent flatNumberWithoutSign(double value)
    {
        return Component.literal(formatFlatNumber(value));
    }

    public static MutableComponent percentageWithSign(double value, boolean invertColors)
    {
        String formattedValue = FORMAT_PERCENTAGE.format(value);
        if (value >= 0) formattedValue = "+" + formattedValue;
        return Component.literal(formattedValue).withStyle(numSignColor(value, invertColors));
    }

    public static MutableComponent percentageWithoutSign(double value)
    {
        return Component.literal(FORMAT_PERCENTAGE.format(value));
    }
}