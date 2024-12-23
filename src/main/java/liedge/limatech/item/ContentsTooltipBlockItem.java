package liedge.limatech.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class ContentsTooltipBlockItem extends BlockItem implements TooltipShiftHintItem
{
    private final boolean showEnergy;
    private final boolean showItems;

    public ContentsTooltipBlockItem(Block block, Properties properties, boolean showEnergy, boolean showItems)
    {
        super(block, properties);
        this.showEnergy = showEnergy;
        this.showItems = showItems;
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        if (showEnergy) LimaTechTooltipUtil.appendSimpleEnergyItemTooltip(consumer, stack);

        if (showItems) LimaTechTooltipUtil.appendInventoryPreviewTooltip(consumer, stack);
    }
}