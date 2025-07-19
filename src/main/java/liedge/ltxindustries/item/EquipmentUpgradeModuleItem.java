package liedge.ltxindustries.item;

import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXICreativeTabs;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static liedge.ltxindustries.LTXIConstants.LIME_GREEN;
import static liedge.ltxindustries.registry.game.LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY;

public class EquipmentUpgradeModuleItem extends UpgradeModuleItem<EquipmentUpgrade, EquipmentUpgradeEntry>
{
    public static ItemStack createStack(Holder<EquipmentUpgrade> upgradeHolder, int upgradeRank)
    {
        ItemStack stack = new ItemStack(LTXIItems.EQUIPMENT_UPGRADE_MODULE.get());
        stack.set(EQUIPMENT_UPGRADE_ENTRY, new EquipmentUpgradeEntry(upgradeHolder, upgradeRank));
        return stack;
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
        return LTXIRegistries.Keys.EQUIPMENT_UPGRADES;
    }

    @Override
    protected ResourceLocation creativeTabId()
    {
        return LTXICreativeTabs.EQUIPMENT_MODULES_TAB.getId();
    }

    @Override
    protected EquipmentUpgradeEntry createUpgradeEntry(Holder<EquipmentUpgrade> upgradeHolder, int upgradeRank)
    {
        return new EquipmentUpgradeEntry(upgradeHolder, upgradeRank);
    }

    @Override
    protected Translatable moduleTypeTooltip()
    {
        return LTXILangKeys.EQUIPMENT_UPGRADE_MODULE_TOOLTIP;
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