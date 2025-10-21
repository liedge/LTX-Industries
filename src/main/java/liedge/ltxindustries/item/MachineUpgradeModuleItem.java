package liedge.ltxindustries.item;

import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.MeshBlockEntity;
import liedge.ltxindustries.blockentity.base.UpgradesHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXICreativeTabs;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

import static liedge.ltxindustries.registry.game.LTXIDataComponents.MACHINE_UPGRADE_ENTRY;

public class MachineUpgradeModuleItem extends UpgradeModuleItem<MachineUpgrade, MachineUpgradeEntry>
{
    private static final Style STYLE = Style.EMPTY.withColor(0x4cc9bf);

    public static ItemStack createStack(Holder<MachineUpgrade> upgradeHolder, int upgradeRank)
    {
        ItemStack stack = new ItemStack(LTXIItems.MACHINE_UPGRADE_MODULE.get());
        stack.set(MACHINE_UPGRADE_ENTRY, new MachineUpgradeEntry(upgradeHolder, upgradeRank));
        return stack;
    }

    public MachineUpgradeModuleItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();

        UpgradesHolderBlockEntity blockEntity = null;
        BlockEntity toCheck = level.getBlockEntity(blockPos);
        if (toCheck instanceof UpgradesHolderBlockEntity)
        {
            blockEntity = (UpgradesHolderBlockEntity) toCheck;
        }
        else if (toCheck instanceof MeshBlockEntity meshBlockEntity)
        {
            BlockPos primaryPos = meshBlockEntity.getPrimaryPos(blockPos, level.getBlockState(blockPos));
            if (primaryPos != null) blockEntity = LimaBlockUtil.getSafeBlockEntity(level, primaryPos, UpgradesHolderBlockEntity.class);
        }

        MachineUpgradeEntry entry = stack.get(entryComponentType());
        if (entry != null && blockEntity != null && player != null)
        {
            if (!level.isClientSide())
            {
                if (blockEntity.getUpgrades().canInstallUpgrade(blockEntity, entry))
                {
                    int previousRank = blockEntity.getUpgrades().getUpgradeRank(entry.upgrade());

                    MachineUpgrades newUpgrades = blockEntity.getUpgrades().toMutableContainer().set(entry).toImmutable();
                    blockEntity.setUpgrades(newUpgrades);

                    if (!player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                        if (previousRank > 0) ItemHandlerHelper.giveItemToPlayer(player, createStack(entry.upgrade(), previousRank));
                    }

                    player.displayClientMessage(LTXILangKeys.UPGRADE_INSTALL_SUCCESS.translate().withStyle(moduleTypeStyle()), true);
                    level.playSound(null, blockPos, LTXISounds.UPGRADE_INSTALL.get(), SoundSource.PLAYERS, 1f, 1f);
                }
                else
                {
                    player.displayClientMessage(LTXILangKeys.UPGRADE_INSTALL_FAIL.translate().withStyle(LTXIConstants.HOSTILE_ORANGE.chatStyle()), true);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public DataComponentType<MachineUpgradeEntry> entryComponentType()
    {
        return MACHINE_UPGRADE_ENTRY.get();
    }

    @Override
    protected ResourceKey<Registry<MachineUpgrade>> upgradeRegistryKey()
    {
        return LTXIRegistries.Keys.MACHINE_UPGRADES;
    }

    @Override
    protected ResourceLocation creativeTabId()
    {
        return LTXICreativeTabs.MACHINE_MODULES_TAB.getId();
    }

    @Override
    protected MachineUpgradeEntry createUpgradeEntry(Holder<MachineUpgrade> upgradeHolder, int upgradeRank)
    {
        return new MachineUpgradeEntry(upgradeHolder, upgradeRank);
    }

    @Override
    protected Translatable moduleTypeTooltip()
    {
        return LTXILangKeys.MACHINE_UPGRADE_MODULE_TOOLTIP;
    }

    @Override
    protected Style moduleTypeStyle()
    {
        return STYLE;
    }

    @Override
    protected List<ItemStack> getAllCompatibleItems(MachineUpgrade upgrade)
    {
        return upgrade.supportedSet().stream().flatMap(type -> type.value().getValidBlocks().stream()).map(block -> block.asItem().getDefaultInstance()).toList();
    }
}