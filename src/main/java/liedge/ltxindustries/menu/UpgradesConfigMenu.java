package liedge.ltxindustries.menu;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.item.UpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public abstract class UpgradesConfigMenu<CTX extends ItemHolderBlockEntity> extends BlockEntityMenu<CTX>
{
    public static final int UPGRADE_REMOVAL_BUTTON_ID = 0;

    private final List<Object2IntMap.Entry<Holder<Upgrade>>> remoteUpgrades = new ObjectArrayList<>();
    protected final LimaBlockEntityItems moduleSourceInventory;
    protected final int moduleSlot;
    private boolean screenUpdate = true;

    protected UpgradesConfigMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, LimaBlockEntityItems moduleSourceInventory, int moduleSlot)
    {
        super(type, containerId, inventory, menuContext);

        this.moduleSourceInventory = moduleSourceInventory;
        this.moduleSlot = moduleSlot;
        addSlot(new InsertSlot(24, 87));
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LTXINetworkSerializers.UPGRADES, this::getUpgrades, upgrades -> {
            this.remoteUpgrades.clear();
            this.remoteUpgrades.addAll(upgrades.toEntrySet());
            this.screenUpdate = true;
        }));
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(UPGRADE_REMOVAL_BUTTON_ID, LimaCoreNetworkSerializers.IDENTIFIER, this::tryRemoveUpgrade);
    }

    protected abstract Upgrades getUpgrades();

    protected abstract boolean canInstallUpgrade(ItemStack upgradeModuleItem);

    protected abstract boolean installUpgrade(ServerLevel level, ItemStack moduleItem);

    protected abstract void tryRemoveUpgrade(ServerPlayer sender, Identifier upgradeId);

    protected void ejectModuleItem(ServerPlayer player, Holder<Upgrade> upgrade, int upgradeRank, boolean insertIntoInventory)
    {
        ItemStack moduleStack = UpgradeModuleItem.get(upgrade, upgradeRank);

        int inserted = 0;

        if (insertIntoInventory)
        {
            PlayerInventoryWrapper inventory = PlayerInventoryWrapper.of(player);
            ItemResource resource = ItemResource.of(moduleStack);

            try (Transaction tx = Transaction.openRoot())
            {
                inserted = inventory.insert(resource, 1, tx);
                if (inserted > 0) tx.commit();
            }
        }

        if (inserted < 1)
        {
            ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getEyeY(), player.getZ(), moduleStack);
            itemEntity.setPickUpDelay(10);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
            player.level().addFreshEntity(itemEntity);
        }
    }

    public List<Object2IntMap.Entry<Holder<Upgrade>>> getRemoteUpgrades()
    {
        return remoteUpgrades;
    }

    public boolean shouldUpdateScreen()
    {
        if (screenUpdate)
        {
            screenUpdate = false;
            return true;
        }

        return false;
    }

    private class InsertSlot extends LimaItemSlot
    {
        public InsertSlot(int xPos, int yPos)
        {
            super(moduleSourceInventory, moduleSlot, xPos, yPos);
        }

        @Override
        public void setByPlayer(ItemStack stack)
        {
            if (!stack.isEmpty() && level() instanceof ServerLevel level)
            {
                if (installUpgrade(level, stack))
                {
                    super.setByPlayer(ItemStack.EMPTY);
                    return;
                }
            }

            super.setByPlayer(stack);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return super.mayPlace(stack) && canInstallUpgrade(stack);
        }
    }
}