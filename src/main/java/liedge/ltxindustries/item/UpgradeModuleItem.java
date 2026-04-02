package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.MeshBlockEntity;
import liedge.ltxindustries.blockentity.base.UpgradesHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.menu.tooltip.ItemStacksTooltip;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXICreativeTabs;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;
import static liedge.ltxindustries.LTXIConstants.LIME_GREEN;

public final class UpgradeModuleItem extends Item implements LimaCreativeTabFillerItem, TooltipShiftHintItem
{
    public static ItemStack get(Holder<Upgrade> upgrade, int upgradeRank)
    {
        ItemStack stack = LTXIItems.UPGRADE_MODULE.toStack();
        stack.set(LTXIDataComponents.UPGRADE_ENTRY, new UpgradeEntry(upgrade, upgradeRank));
        return stack;
    }

    public UpgradeModuleItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isCrouching() && stack.get(LTXIDataComponents.UPGRADE_ENTRY) == null)
        {
            return InteractionResult.SUCCESS_SERVER.heldItemTransformedTo(LTXIItems.EMPTY_UPGRADE_MODULE.toStack());
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack usedItem = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        UpgradesHolderBlockEntity blockEntity = null;
        BlockEntity toCheck = level.getBlockEntity(pos);
        if (toCheck instanceof UpgradesHolderBlockEntity)
        {
            blockEntity = (UpgradesHolderBlockEntity) toCheck;
        }
        else if (toCheck instanceof MeshBlockEntity meshBE)
        {
            BlockPos primaryPos = meshBE.getPrimaryPos(pos, level.getBlockState(pos));
            if (primaryPos != null) blockEntity = LimaBlockUtil.getSafeBlockEntity(level, primaryPos, UpgradesHolderBlockEntity.class);
        }

        UpgradeEntry entry = usedItem.get(LTXIDataComponents.UPGRADE_ENTRY);
        if (blockEntity != null && entry != null && player != null && !level.isClientSide())
        {
            Upgrades previousUpgrades = blockEntity.getUpgrades();

            if (previousUpgrades.canInstallUpgrade(blockEntity.getAsLimaBlockEntity(), entry))
            {
                int previousRank = previousUpgrades.getUpgradeRank(entry.upgrade());
                Upgrades newUpgrades = previousUpgrades.mutable().set(entry).build();
                blockEntity.setUpgrades(newUpgrades);

                player.sendOverlayMessage(LTXILangKeys.UPGRADE_INSTALL_SUCCESS.translate().withStyle(LIME_GREEN.chatStyle()));
                level.playSound(null, pos, LTXISounds.UPGRADE_INSTALL.get(), SoundSource.PLAYERS, 1f, 1f);

                usedItem.consume(1, player);
                if (usedItem.isEmpty() && previousRank > 0)
                {
                    player.getInventory().placeItemBackInInventory(get(entry.upgrade(), previousRank));
                }

                return InteractionResult.SUCCESS_SERVER;
            }
            else
            {
                player.sendOverlayMessage(LTXILangKeys.UPGRADE_INSTALL_FAIL.translate().withStyle(HOSTILE_ORANGE.chatStyle()));
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public Component getName(ItemStack stack)
    {
        UpgradeEntry entry = stack.get(LTXIDataComponents.UPGRADE_ENTRY);
        if (entry != null)
        {
            return entry.upgrade().value().display().title();
        }
        else
        {
            return super.getName(stack).copy().withStyle(HOSTILE_ORANGE.chatStyle());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag)
    {
        UpgradeEntry entry = stack.get(LTXIDataComponents.UPGRADE_ENTRY);
        if (entry != null)
        {
            Upgrade upgrade = entry.upgrade().value();
            if (upgrade.maxRank() > 1) tooltipAdder.accept(LTXILangKeys.UPGRADE_RANK_TOOLTIP.translateArgs(entry.rank(), upgrade.maxRank()).withStyle(LTXIConstants.UPGRADE_RANK_MAGENTA.chatStyle()));
            tooltipAdder.accept(upgrade.display().description());
        }
        else
        {
            tooltipAdder.accept(LTXILangKeys.INVALID_UPGRADE_HINT.translate().withStyle(HOSTILE_ORANGE.chatStyle()));
        }
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        UpgradeEntry entry = stack.get(LTXIDataComponents.UPGRADE_ENTRY);
        if (entry == null) return;

        Upgrade upgrade = entry.upgrade().value();
        upgrade.appendEffectTooltips(entry.rank(), consumer::accept);

        List<ItemStack> itemUsers = upgrade.users().items().stream().map(item -> item.value().getDefaultInstance()).limit(24).toList();
        if (!itemUsers.isEmpty())
        {
            consumer.accept(LTXILangKeys.EQUIPMENT_COMPATIBILITY_TOOLTIP.translate().withStyle(LIME_GREEN.chatStyle()));
            consumer.accept(new ItemStacksTooltip(itemUsers, 8, 3, false));
        }

        List<ItemStack> machineUsers = upgrade.users().blockEntities().stream().flatMap(be -> be.value().getValidBlocks().stream())
                .map(block -> block.asItem().getDefaultInstance()).toList();
        if (!machineUsers.isEmpty())
        {
            consumer.accept(LTXILangKeys.MACHINE_COMPATIBILITY_TOOLTIP.translate().withStyle(LIME_GREEN.chatStyle()));
            consumer.accept(new ItemStacksTooltip(machineUsers, 8, 3, false));
        }
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(Identifier tabId)
    {
        return false;
    }

    @Override
    public void addAdditionalToCreativeTab(Identifier tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        if (tabId.equals(LTXICreativeTabs.UPGRADE_MODULES_TAB.getId()))
        {
            HolderLookup.RegistryLookup<Upgrade> registry = parameters.holders().lookupOrThrow(LTXIRegistries.Keys.UPGRADES);
            List<Holder.Reference<Upgrade>> holders = registry.listElements().sorted(Upgrade.BY_CATEGORY_THEN_ID).toList();

            for (Holder<Upgrade> holder : holders)
            {
                int maxRank = holder.value().maxRank();

                for (int i = 1; i <= maxRank; i++)
                {
                    UpgradeEntry entry = new UpgradeEntry(holder, i);
                    ItemStack stack = getDefaultInstance();
                    stack.set(LTXIDataComponents.UPGRADE_ENTRY, entry);
                    CreativeModeTab.TabVisibility visibility = maxRank == i ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;

                    output.accept(stack, visibility);
                }
            }
        }
    }
}