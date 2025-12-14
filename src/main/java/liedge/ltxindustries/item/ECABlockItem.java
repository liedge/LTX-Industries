package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class ECABlockItem extends BlockItem implements EnergyHolderItem, TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    public ECABlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIMachinesConfig.ECA_BASE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return LTXIMachinesConfig.ECA_BASE_TRANSFER_RATE.getAsInt();
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return LTXIConstants.REM_BLUE.argb32();
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        return Math.round(13f * getChargePercentage(stack));
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        appendStorageEnergyTooltip(consumer, stack);
    }
}