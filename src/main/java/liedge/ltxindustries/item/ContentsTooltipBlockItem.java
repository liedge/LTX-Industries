package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public final class ContentsTooltipBlockItem extends BlockItem implements TooltipShiftHintItem
{
    public static BlockItem energyTooltipItem(Block block, Properties properties)
    {
        return new ContentsTooltipBlockItem(block, properties, true, false);
    }

    public static BlockItem energyOwnerTooltipItem(Block block, Properties properties)
    {
        return new ContentsTooltipBlockItem(block, properties, true, true);
    }

    private final boolean showEnergy;
    private final boolean showOwner;

    private ContentsTooltipBlockItem(Block block, Properties properties, boolean showEnergy, boolean showOwner)
    {
        super(block, properties);
        this.showEnergy = showEnergy;
        this.showOwner = showOwner;
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        if (showEnergy) LTXITooltipUtil.appendEnergyOnlyTooltip(consumer, stack.getOrDefault(LimaCoreDataComponents.ENERGY, 0));

        if (showOwner) consumer.accept(LTXITooltipUtil.makeOwnerComponent(stack.get(LimaCoreDataComponents.OWNER)).withStyle(ChatFormatting.GRAY));
    }
}