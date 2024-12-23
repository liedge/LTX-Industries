package liedge.limatech.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.machinetiers.MachineTier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import static liedge.limatech.LimaTechConstants.LIME_GREEN;
import static liedge.limatech.registry.LimaTechDataComponents.MACHINE_TIER;

public class TieredMachineBlockItem extends ContentsTooltipBlockItem
{
    public TieredMachineBlockItem(Block block, Properties properties)
    {
        super(block, properties, true, true);
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        consumer.accept(LimaTechLang.MACHINE_TIER_TOOLTIP.translateArgs(stack.getOrDefault(MACHINE_TIER, MachineTier.TIER_1).getTierLevel()).withStyle(LIME_GREEN.chatStyle()));
        super.appendTooltipHintComponents(level, stack, consumer);
    }
}