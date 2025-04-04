package liedge.limatech.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradeBaseEntry;
import liedge.limatech.menu.tooltip.ItemGridTooltip;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.util.config.LimaTechServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

import static liedge.limatech.LimaTechConstants.HOSTILE_ORANGE;

public abstract class UpgradeModuleItem<U extends UpgradeBase<?, U>, UE extends UpgradeBaseEntry<U>> extends Item implements LimaCreativeTabFillerItem, TooltipShiftHintItem
{
    protected UpgradeModuleItem(Properties properties)
    {
        super(properties);
    }

    public abstract DataComponentType<UE> entryComponentType();

    protected abstract ResourceKey<Registry<U>> upgradeRegistryKey();

    protected abstract ResourceLocation creativeTabId();

    protected abstract UE createUpgradeEntry(Holder<U> upgradeHolder, int upgradeRank);

    protected abstract Translatable moduleTypeTooltip();

    protected abstract Style moduleTypeStyle();

    protected abstract List<ItemStack> getAllCompatibleItems(U upgrade);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isCrouching() && stack.get(entryComponentType()) == null)
        {
            return InteractionResultHolder.sidedSuccess(new ItemStack(LimaTechItems.EMPTY_UPGRADE_MODULE.get()), level.isClientSide());
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        UE entry = stack.get(entryComponentType());
        if (entry != null)
        {
            return entry.upgrade().value().title();
        }
        else
        {
            return moduleTypeTooltip().translate().withStyle(HOSTILE_ORANGE.chatStyle());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        UE entry = stack.get(entryComponentType());
        if (entry != null)
        {
            tooltipComponents.add(moduleTypeTooltip().translate().withStyle(moduleTypeStyle()));
            U upgrade = entry.upgrade().value();
            if (upgrade.maxRank() > 1) tooltipComponents.add(LimaTechLang.UPGRADE_RANK_TOOLTIP.translateArgs(entry.upgradeRank(), upgrade.maxRank()).withStyle(LimaTechConstants.UPGRADE_RANK_MAGENTA.chatStyle()));
            tooltipComponents.add(upgrade.description());
        }
        else
        {
            tooltipComponents.add(LimaTechLang.INVALID_UPGRADE_HINT.translate().withStyle(HOSTILE_ORANGE.chatStyle()));
        }
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        UE entry = stack.get(entryComponentType());
        if (entry != null)
        {
            U upgrade = entry.upgrade().value();

            consumer.accept(upgrade.getEffectsTooltip(entry.upgradeRank()));
            consumer.accept(LimaTechLang.UPGRADE_COMPATIBILITY_TOOLTIP.translate().withStyle(moduleTypeStyle()));

            List<ItemStack> compatibleItems = getAllCompatibleItems(upgrade);
            if (compatibleItems.size() > 12 && upgrade.supportedSet() instanceof HolderSet.Named<?> namedSet)
            {
                consumer.accept(Component.translatable(Tags.getTagTranslationKey(namedSet.key())).withStyle(moduleTypeStyle()));
            }
            else
            {
                consumer.accept(new ItemGridTooltip(compatibleItems, 6, 2));
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
        if (tabId.equals(creativeTabId()))
        {
            HolderLookup.RegistryLookup<U> registry = parameters.holders().lookupOrThrow(upgradeRegistryKey());
            boolean generateAll = LimaTechServerConfig.GENERATE_ALL_UPGRADE_RANKS.getAsBoolean();

            registry.listElements()
                    .flatMap(holder -> {
                        int max = holder.value().maxRank();
                        int min = generateAll ? 1 : max;
                        return IntStream.rangeClosed(min, max).mapToObj(rank -> createUpgradeEntry(holder, rank));
                    })
                    .forEach(entry -> {
                        ItemStack stack = new ItemStack(this);
                        stack.set(entryComponentType(), entry);
                        output.accept(stack, tabVisibility);
                    });
        }
    }
}