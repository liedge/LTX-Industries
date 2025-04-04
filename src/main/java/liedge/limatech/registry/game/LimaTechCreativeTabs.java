package liedge.limatech.registry.game;

import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.bootstrap.LimaTechEnchantments;
import liedge.limatech.util.config.LimaTechServerConfig;
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

import java.util.stream.IntStream;

public final class LimaTechCreativeTabs
{
    private LimaTechCreativeTabs() {}

    private static final DeferredRegister<CreativeModeTab> TABS = LimaTech.RESOURCES.deferredRegister(Registries.CREATIVE_MODE_TAB);

    public static void register(IEventBus bus)
    {
        TABS.register(bus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(() -> LimaTechItems.GRENADE_LAUNCHER.get().createDefaultStack(null, true, GrenadeType.ELECTRIC))
            .displayItems((parameters, output) -> buildMainTab(id, parameters, output))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EQUIPMENT_MODULES_TAB = TABS.register("equipment_modules", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LimaTechItems.EQUIPMENT_UPGRADE_MODULE::toStack)
            .displayItems(((parameters, output) -> LimaCreativeTabFillerItem.addToTab(id, parameters, output, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY, LimaTechItems.EQUIPMENT_UPGRADE_MODULE)))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MACHINE_MODULES_TAB = TABS.register("machine_modules", id -> LimaItemUtil.tabBuilderWithTitle(id)
            .icon(LimaTechItems.MACHINE_UPGRADE_MODULE::toStack)
            .displayItems((parameters, output) -> LimaCreativeTabFillerItem.addToTab(id, parameters, output, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY, LimaTechItems.MACHINE_UPGRADE_MODULE))
            .build());

    private static void buildMainTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output)
    {
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechBlocks.getRegisteredBlocks());
        LimaCreativeTabFillerItem.addHoldersToTab(tabId, parameters, output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, LimaTechItems.getRegisteredItems());

        HolderLookup<Enchantment> enchantments = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        boolean enchantedBookConfig = LimaTechServerConfig.GENERATE_ALL_ENCHANTED_BOOK_LEVELS.getAsBoolean();
        addEnchantedBooks(output, enchantments, LimaTechEnchantments.AMMO_SCAVENGER, enchantedBookConfig);
        addEnchantedBooks(output, enchantments, LimaTechEnchantments.RAZOR, enchantedBookConfig);
    }

    private static void addEnchantedBooks(CreativeModeTab.Output output, HolderLookup<Enchantment> registries, ResourceKey<Enchantment> enchantment, boolean allLevels)
    {
        Holder<Enchantment> holder = registries.getOrThrow(enchantment);
        int max = holder.value().getMaxLevel();
        int min = allLevels ? holder.value().getMinLevel() : max;
        IntStream.rangeClosed(min, max).mapToObj(lvl -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, lvl))).forEach(stack -> output.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
    }
}