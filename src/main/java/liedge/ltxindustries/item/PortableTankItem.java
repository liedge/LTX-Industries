package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.FluidHolderItem;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public final class PortableTankItem extends BlockItem implements FluidHolderItem, TooltipShiftHintItem
{
    public PortableTankItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public int getFluidCapacity(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.FLUID_CAPACITY, LTXIMachinesConfig.getPortableTankCapacity());
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        SimpleFluidContent content = stack.getOrDefault(LimaCoreDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY);

        if (!content.isEmpty())
        {
            FluidStack fluidStack = content.copy();
            consumer.accept(fluidStack.getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }
        else
        {
            consumer.accept(LTXILangKeys.NO_FLUID_STORED.translate().withStyle(ChatFormatting.DARK_GRAY));
        }

        String formattedAmount = LimaTransferUtil.formatStoredFluidMillibucket(content.getAmount(), getFluidCapacity(stack));
        consumer.accept(Component.literal(formattedAmount).withStyle(ChatFormatting.GRAY));
    }
}