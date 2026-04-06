package liedge.ltxindustries.registry.game;

import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.bootstrap.LTXIEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXICreativeTabs
{
    private LTXICreativeTabs() {}

    private static final DeferredRegister<CreativeModeTab> TABS = LTXIndustries.RESOURCES.deferredRegister(Registries.CREATIVE_MODE_TAB);

    public static void register(IEventBus bus)
    {
        TABS.register(bus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(() -> LTXIItems.HANABI.get().createDecorativeStack(GrenadeType.ELECTRIC))
            .displayItems((parameters, output) -> buildMainTab(id, parameters, output))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UPGRADE_MODULES_TAB = TABS.register("upgrade_modules", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LTXIItems.UPGRADE_MODULE::toStack)
            .displayItems((parameters, output) -> LimaCreativeTabFillerItem.addToTab(id, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LTXIItems.UPGRADE_MODULE))
            .build());

    private static void buildMainTab(Identifier tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output)
    {
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LTXIItems.getRegisteredItems());

        // Add GuideME tablet (if installed)
        // TODO Return integration
        //ItemStack guideTablet = GuideMEIntegration.createGuideTabletItem();
        //if (!guideTablet.isEmpty()) output.accept(guideTablet, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

        HolderLookup<Enchantment> enchantments = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        addEnchantedBooks(output, enchantments, LTXIEnchantments.AMMO_SCAVENGER);
        addEnchantedBooks(output, enchantments, LTXIEnchantments.RAZOR);
    }

    private static void addEnchantedBooks(CreativeModeTab.Output output, HolderLookup<Enchantment> registries, ResourceKey<Enchantment> enchantment)
    {
        Holder<Enchantment> holder = registries.getOrThrow(enchantment);
        final int max = holder.value().getMaxLevel();
        for (int i = 1; i <= max; i++)
        {
            CreativeModeTab.TabVisibility visibility = i == max ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
            ItemStack book = EnchantmentHelper.createBook(new EnchantmentInstance(holder, i));
            output.accept(book, visibility);
        }
    }
}