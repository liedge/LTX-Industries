package liedge.limatech.item;

import liedge.limacore.capability.energy.LimaComponentEnergyStorage;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.machinetiers.MachineTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.capability.energy.InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;
import static liedge.limatech.LimaTechConstants.LIME_GREEN;
import static liedge.limatech.registry.LimaTechDataComponents.MACHINE_TIER;
import static liedge.limatech.util.config.LimaTechMachinesConfig.*;

public class EnergyStorageArrayItem extends BlockItem implements EnergyHolderItem, TooltipShiftHintItem
{
    private final boolean infinite;

    public EnergyStorageArrayItem(Block block, Properties properties, boolean infinite)
    {
        super(block, properties);
        this.infinite = infinite;
    }

    @Override
    public int getEnergyStored(ItemStack stack)
    {
        return infinite ? INFINITE_ENERGY_STORAGE.getEnergyStored() : EnergyHolderItem.super.getEnergyStored(stack);
    }

    @Override
    public int getEnergyCapacity(ItemStack stack)
    {
        if (infinite)
        {
            return INFINITE_ENERGY_STORAGE.getMaxEnergyStored();
        }
        else
        {
            MachineTier tier = stack.getOrDefault(MACHINE_TIER, MachineTier.TIER_1);
            return tier.calculateInt(ESA_BASE_ENERGY_CAPACITY.getAsInt(), ESA_PER_TIER_CAPACITY_MULTIPLIER.getAsInt());
        }
    }

    @Override
    public int getEnergyTransferRate(ItemStack stack)
    {
        if (infinite)
        {
            return INFINITE_ENERGY_STORAGE.getTransferRate();
        }
        else
        {
            MachineTier tier = stack.getOrDefault(MACHINE_TIER, MachineTier.TIER_1);
            return tier.calculateInt(ESA_BASE_TRANSFER_RATE.getAsInt(), ESA_PER_TIER_TRANSFER_MULTIPLIER.getAsInt());
        }
    }

    @Override
    public IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        if (infinite)
        {
            return INFINITE_ENERGY_STORAGE;
        }
        else
        {
            return new LimaComponentEnergyStorage(stack, getEnergyCapacity(stack), getEnergyTransferRate(stack));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return !infinite;
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return LimaTechConstants.REM_BLUE.packedRGB();
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        return Math.round(13f * LimaMathUtil.divideFloat(getEnergyStored(stack), getEnergyCapacity(stack)));
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        if (!infinite) consumer.accept(LimaTechLang.MACHINE_TIER_TOOLTIP.translateArgs(stack.getOrDefault(MACHINE_TIER, MachineTier.TIER_1).getTierLevel()).withStyle(LIME_GREEN.chatStyle()));
        appendExtendedEnergyTooltip(consumer, stack);
    }
}