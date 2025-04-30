package liedge.limatech.registry.game;

import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.bootstrap.LimaTechEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechCreativeTabs
{
    private LimaTechCreativeTabs() {}

    private static final DeferredRegister<CreativeModeTab> TABS = LimaTech.RESOURCES.deferredRegister(Registries.CREATIVE_MODE_TAB);

    public static void register(IEventBus bus)
    {
        TABS.register(bus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(() -> LimaTechItems.GRENADE_LAUNCHER.get().createDecorativeStack(GrenadeType.ELECTRIC))
            .displayItems((parameters, output) -> buildMainTab(id, parameters, output))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EQUIPMENT_MODULES_TAB = TABS.register("equipment_modules", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LimaTechItems.EQUIPMENT_UPGRADE_MODULE::toStack)
            .displayItems(((parameters, output) -> LimaCreativeTabFillerItem.addToTab(id, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechItems.EQUIPMENT_UPGRADE_MODULE)))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MACHINE_MODULES_TAB = TABS.register("machine_modules", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LimaTechItems.MACHINE_UPGRADE_MODULE::toStack)
            .displayItems((parameters, output) -> LimaCreativeTabFillerItem.addToTab(id, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechItems.MACHINE_UPGRADE_MODULE))
            .build());

    private static void buildMainTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output)
    {
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechBlocks.getRegisteredBlocks());
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechItems.getRegisteredItems());

        HolderLookup<Enchantment> enchantments = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        addEnchantedBooks(output, enchantments, LimaTechEnchantments.AMMO_SCAVENGER);
        addEnchantedBooks(output, enchantments, LimaTechEnchantments.RAZOR);
    }

    private static void addEnchantedBooks(CreativeModeTab.Output output, HolderLookup<Enchantment> registries, ResourceKey<Enchantment> enchantment)
    {
        Holder<Enchantment> holder = registries.getOrThrow(enchantment);
        final int max = holder.value().getMaxLevel();
        for (int i = 1; i <= max; i++)
        {
            CreativeModeTab.TabVisibility visibility = i == max ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
            output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, i)), visibility);
        }
    }
}