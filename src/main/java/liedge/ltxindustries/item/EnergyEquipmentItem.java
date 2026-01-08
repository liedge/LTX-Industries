package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.lib.upgrades.effect.ValueOperation;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.function.Supplier;

public abstract class EnergyEquipmentItem extends Item implements UpgradableEquipmentItem, TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    protected EnergyEquipmentItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return getEnergyCapacity(stack) / 20;
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
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return false;
    }

    @Override
    public void addAdditionalToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        ItemStack stack = createStackWithDefaultUpgrades(parameters.holders());
        stack.set(LimaCoreDataComponents.ENERGY, getBaseEnergyCapacity(stack));
        output.accept(stack, tabVisibility);
    }

    protected void setUpgradableDouble(ItemStack stack, EquipmentUpgrades upgrades, LootContext context, double baseFallback, Supplier<? extends DataComponentType<Double>> itemComponent, Supplier<? extends DataComponentType<List<ValueOperation>>> upgradeComponent)
    {
        double base = components().getOrDefault(itemComponent.get(), baseFallback);
        double value = upgrades.runValueOps(upgradeComponent, context, base);
        stack.set(itemComponent, value);
    }

    protected void setUpgradableInt(ItemStack stack, EquipmentUpgrades upgrades, LootContext context, int baseFallback, Supplier<? extends DataComponentType<Integer>> itemComponent, Supplier<? extends DataComponentType<List<ValueOperation>>> upgradeComponent)
    {
        int base = components().getOrDefault(itemComponent.get(), baseFallback);
        int value = LimaCoreMath.round(upgrades.runValueOps(upgradeComponent, context, base));
        stack.set(itemComponent, value);
    }
}