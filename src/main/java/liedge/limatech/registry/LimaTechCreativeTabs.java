package liedge.limatech.registry;

import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.LimaTech;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechCreativeTabs
{
    private LimaTechCreativeTabs() {}

    private static final DeferredRegister<CreativeModeTab> TABS = LimaTech.RESOURCES.deferredRegister(Registries.CREATIVE_MODE_TAB);

    public static void initRegister(IEventBus bus)
    {
        TABS.register(bus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LimaTechItems.MACHINE_WRENCH::toStack)
            .displayItems((parameters, output) -> buildMainTab(id, parameters, output))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEAPON_MODS_TAB = TABS.register("weapon_mods", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LimaTechItems.EQUIPMENT_UPGRADE_ITEM::toStack)
            .displayItems(((parameters, output) -> LimaCreativeTabFillerItem.addToTab(id, parameters, output, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY, LimaTechItems.EQUIPMENT_UPGRADE_ITEM)))
            .build());

    private static void buildMainTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output)
    {
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechBlocks.getRegisteredBlocks());
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechItems.getRegisteredItems());

        HolderLookup<Enchantment> enchantments = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        output.accept(createMaxEnchantBook(enchantments, LimaTechEnchantments.AMMO_SCAVENGER), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        output.accept(createMaxEnchantBook(enchantments, LimaTechEnchantments.RAZOR), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }

    private static ItemStack createMaxEnchantBook(HolderLookup<Enchantment> registries, ResourceKey<Enchantment> enchantmentKey)
    {
        Holder<Enchantment> holder = registries.getOrThrow(enchantmentKey);
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, holder.value().getMaxLevel()));
    }
}