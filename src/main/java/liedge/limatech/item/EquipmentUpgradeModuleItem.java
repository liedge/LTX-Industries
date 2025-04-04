package liedge.limatech.item;

import liedge.limacore.lib.Translatable;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.limatech.registry.game.LimaTechCreativeTabs;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static liedge.limatech.LimaTechConstants.LIME_GREEN;
import static liedge.limatech.registry.game.LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY;

public class EquipmentUpgradeModuleItem extends UpgradeModuleItem<EquipmentUpgrade, EquipmentUpgradeEntry>
{
    public static ItemStack createStack(Holder<EquipmentUpgrade> upgradeHolder, int upgradeRank)
    {
        ItemStack stack = new ItemStack(LimaTechItems.EQUIPMENT_UPGRADE_MODULE.get());
        stack.set(EQUIPMENT_UPGRADE_ENTRY, new EquipmentUpgradeEntry(upgradeHolder, upgradeRank));
        return stack;
    }

    public static ItemStack createStack(HolderLookup.Provider registries, ResourceKey<EquipmentUpgrade> upgradeKey, int upgradeRank)
    {
        return createStack(registries.holderOrThrow(upgradeKey), upgradeRank);
    }

    public EquipmentUpgradeModuleItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public DataComponentType<EquipmentUpgradeEntry> entryComponentType()
    {
        return EQUIPMENT_UPGRADE_ENTRY.get();
    }

    @Override
    protected ResourceKey<Registry<EquipmentUpgrade>> upgradeRegistryKey()
    {
        return LimaTechRegistries.Keys.EQUIPMENT_UPGRADES;
    }

    @Override
    protected ResourceLocation creativeTabId()
    {
        return LimaTechCreativeTabs.EQUIPMENT_MODULES_TAB.getId();
    }

    @Override
    protected EquipmentUpgradeEntry createUpgradeEntry(Holder<EquipmentUpgrade> upgradeHolder, int upgradeRank)
    {
        return new EquipmentUpgradeEntry(upgradeHolder, upgradeRank);
    }

    @Override
    protected Translatable moduleTypeTooltip()
    {
        return LimaTechLang.EQUIPMENT_UPGRADE_MODULE_TOOLTIP;
    }

    @Override
    protected Style moduleTypeStyle()
    {
        return LIME_GREEN.chatStyle();
    }

    @Override
    protected List<ItemStack> getAllCompatibleItems(EquipmentUpgrade upgrade)
    {
        return upgrade.supportedSet().stream().map(holder -> holder.value().getDefaultInstance()).toList();
    }
}