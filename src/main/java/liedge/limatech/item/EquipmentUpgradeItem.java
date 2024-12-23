package liedge.limatech.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limatech.LimaTechTags;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.menu.tooltip.ItemGridTooltip;
import liedge.limatech.registry.LimaTechCreativeTabs;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import liedge.limatech.upgradesystem.EquipmentUpgradeEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

import static liedge.limatech.LimaTechConstants.HOSTILE_ORANGE;
import static liedge.limatech.LimaTechConstants.LIME_GREEN;

public class EquipmentUpgradeItem extends Item implements LimaCreativeTabFillerItem, TooltipShiftHintItem
{
    public static ItemStack createStack(HolderLookup.Provider registries, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank)
    {
        ItemStack stack = new ItemStack(LimaTechItems.EQUIPMENT_UPGRADE_ITEM.asItem());
        Holder<EquipmentUpgrade> holder = registries.holderOrThrow(upgradeKey);
        stack.set(LimaTechDataComponents.UPGRADE_ITEM_DATA, new EquipmentUpgradeEntry(holder, upgradeRank));
        return stack;
    }

    public static ItemStack createStack(HolderLookup.Provider registries, ResourceKey<EquipmentUpgrade> upgradeKey)
    {
        return createStack(registries, upgradeKey, 1);
    }

    public EquipmentUpgradeItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(stack);
        if (data != null)
        {
            return LimaTechLang.EQUIPMENT_UPGRADE_ITEM_CUSTOM_NAME.translateArgs(data.upgrade().value().title()).withStyle(LIME_GREEN.chatStyle());
        }
        else
        {
            return LimaTechLang.INVALID_EQUIPMENT_UPGRADE_ITEM.translate().withStyle(HOSTILE_ORANGE.chatStyle());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(stack);
        if (data != null)
        {
            EquipmentUpgrade upgrade = data.upgrade().value();
            if (upgrade.maxRank() > 1) tooltipComponents.add(LimaTechLang.EQUIPMENT_UPGRADE_RANK.translateArgs(data.upgradeRank()));
            tooltipComponents.add(upgrade.description().copy().withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(stack);
        if (data != null)
        {
            EquipmentUpgrade upgrade = data.upgrade().value();

            HolderSet<Item> supportedItems = upgrade.supportedItems();

            if (supportedItems instanceof HolderSet.Named<Item> named && named.key() == LimaTechTags.Items.LTX_WEAPONS)
            {
                consumer.accept(LimaTechLang.UPGRADE_COMPATIBLE_ALL_WEAPONS.translate().withStyle(LIME_GREEN.chatStyle()));
            }
            else
            {
                consumer.accept(LimaTechLang.UPGRADE_COMPATIBLE_SPECIFIC_WEAPONS.translate().withStyle(LIME_GREEN.chatStyle()));
                List<ItemStack> stacks = upgrade.supportedItems().stream().map(holder -> holder.value().getDefaultInstance()).toList();
                consumer.accept(new ItemGridTooltip(stacks, 6));
            }
        }
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return false;
    }

    @Override
    public void addAdditionalToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        if (tabId.equals(LimaTechCreativeTabs.WEAPON_MODS_TAB.getId()))
        {
            HolderLookup.RegistryLookup<EquipmentUpgrade> registry = parameters.holders().lookupOrThrow(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY);
            registry.listElements()
                    .flatMap(holder -> IntStream.rangeClosed(1, holder.value().maxRank()).mapToObj(rank -> new EquipmentUpgradeEntry(holder, rank)))
                    .forEach(entry -> {
                        ItemStack stack = new ItemStack(this);
                        stack.set(LimaTechDataComponents.UPGRADE_ITEM_DATA, entry);
                        output.accept(stack, tabVisibility);
                    });
        }
    }
}