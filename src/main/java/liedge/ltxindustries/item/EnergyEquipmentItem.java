package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.lib.upgrades.effect.ValueOperation;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class EnergyEquipmentItem extends Item implements UpgradableEquipmentItem, TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    protected EnergyEquipmentItem(Properties properties)
    {
        super(properties);
    }

    public boolean supportsEnergyStorage(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return getEnergyCapacity(stack) / 20;
    }

    @Override
    public @Nullable EnergyHandler getEnergy(ItemStack stack, ItemAccess access)
    {
        return supportsEnergyStorage(stack) ? UpgradableEquipmentItem.super.getEnergy(stack, access) : null;
    }

    @Override
    public @Nullable EnergyHandler getNoTransferLimitEnergy(ItemStack stack, ItemAccess access)
    {
        return supportsEnergyStorage(stack) ? UpgradableEquipmentItem.super.getNoTransferLimitEnergy(stack, access) : null;
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        appendEquipmentEnergyTooltip(consumer, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
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
    public boolean isFoil(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment)
    {
        return false;
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(Identifier tabId)
    {
        return false;
    }

    @Override
    public void addAdditionalToCreativeTab(Identifier tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        ItemStack stack = createStackWithDefaultUpgrades(parameters.holders());
        stack.set(LimaCoreDataComponents.ENERGY, getBaseEnergyCapacity(stack));
        output.accept(stack, tabVisibility);
    }

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, Upgrades upgrades)
    {
        UpgradableEquipmentItem.super.onUpgradeRefresh(context, stack, upgrades);

        if (supportsEnergyStorage(stack))
        {
            setUpgradableInt(stack, upgrades, context, getBaseEnergyCapacity(stack), LimaCoreDataComponents.ENERGY_CAPACITY, LTXIUpgradeEffectComponents.ENERGY_CAPACITY);
            setUpgradableInt(stack, upgrades, context, getBaseEnergyTransferRate(stack), LimaCoreDataComponents.ENERGY_TRANSFER_RATE, LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE);
            setUpgradableInt(stack, upgrades, context, getBaseEnergyUsage(stack), LimaCoreDataComponents.ENERGY_USAGE, LTXIUpgradeEffectComponents.ENERGY_USAGE);
        }
    }

    protected void setUpgradableDouble(ItemStack stack, Upgrades upgrades, LootContext context, double baseFallback, Supplier<? extends DataComponentType<Double>> itemComponent, Supplier<? extends DataComponentType<List<ValueOperation>>> upgradeComponent)
    {
        double base = components().getOrDefault(itemComponent.get(), baseFallback);
        double value = upgrades.runValueOps(upgradeComponent, context, base);
        stack.set(itemComponent, value);
    }

    protected void setUpgradableInt(ItemStack stack, Upgrades upgrades, LootContext context, int baseFallback, Supplier<? extends DataComponentType<Integer>> itemComponent, Supplier<? extends DataComponentType<List<ValueOperation>>> upgradeComponent)
    {
        int base = components().getOrDefault(itemComponent.get(), baseFallback);
        int value = LimaCoreMath.roundInt(upgrades.runValueOps(upgradeComponent, context, base));
        stack.set(itemComponent, value);
    }
}