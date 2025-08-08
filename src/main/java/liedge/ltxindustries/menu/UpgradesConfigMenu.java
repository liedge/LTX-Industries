package liedge.ltxindustries.menu;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.menu.LimaItemHandlerMenu;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.menu.slot.LimaHandlerSlot;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.List;

public abstract class UpgradesConfigMenu<CTX extends ItemHolderBlockEntity, U extends UpgradeBase<?, U>, UC extends UpgradesContainerBase<?, U>> extends LimaItemHandlerMenu<CTX>
{
    public static final int UPGRADE_REMOVAL_BUTTON_ID = 0;

    private int quickTransferSlot = -1;

    private final List<Object2IntMap.Entry<Holder<U>>> remoteUpgrades = new ObjectArrayList<>();
    protected final LimaBlockEntityItemHandler moduleSourceInventory;
    protected final int moduleSlot;
    private boolean screenUpdate = true;

    protected UpgradesConfigMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, LimaBlockEntityItemHandler moduleSourceInventory, int moduleSlot)
    {
        super(type, containerId, inventory, menuContext);

        this.moduleSourceInventory = moduleSourceInventory;
        this.moduleSlot = moduleSlot;
        addSlot(new InsertSlot(24, 87));
    }

    @Override
    public final ItemStack quickMoveStack(Player player, int index)
    {
        // Capture the quick transfer slot if applicable
        if (index >= inventoryStart)
        {
            this.quickTransferSlot = slots.get(index).getContainerSlot();
        }
        else
        {
            this.quickTransferSlot = -1;
        }

        // Perform the transfer as usual and reset
        ItemStack result = super.quickMoveStack(player, index);
        this.quickTransferSlot = -1;
        return result;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(getUpgradesSerializer(), this::getUpgrades, eid -> {
            this.remoteUpgrades.clear();
            this.remoteUpgrades.addAll(eid.toEntrySet());
            this.screenUpdate = true;
        }));
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(UPGRADE_REMOVAL_BUTTON_ID, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::tryRemoveUpgrade);
    }

    protected abstract NetworkSerializer<UC> getUpgradesSerializer();

    protected abstract UC getUpgrades();

    protected abstract boolean canInstallUpgrade(ItemStack upgradeModuleItem);

    protected abstract void tryInstallUpgrade(ItemStack upgradeModuleItem, ServerLevel level);

    protected abstract void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId);

    protected abstract ItemStack createModuleItem(Holder<U> upgrade, int upgradeRank);

    protected void ejectModuleItem(ServerPlayer player, Holder<U> upgrade, int upgradeRank)
    {
        ItemStack moduleItem = createModuleItem(upgrade, upgradeRank);
        PlayerMainInvWrapper inventory = new PlayerMainInvWrapper(playerInventory);

        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (i == quickTransferSlot) continue;

            moduleItem = inventory.insertItem(i, moduleItem, false);
            if (moduleItem.isEmpty()) break;
        }

        if (!moduleItem.isEmpty())
        {
            ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY() + 0.5d, player.getZ(), moduleItem);
            itemEntity.setPickUpDelay(10);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
            player.level().addFreshEntity(itemEntity);
        }
    }

    public List<Object2IntMap.Entry<Holder<U>>> getRemoteUpgrades()
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

    private class InsertSlot extends LimaHandlerSlot
    {
        public InsertSlot(int xPos, int yPos)
        {
            super(moduleSourceInventory, moduleSlot, xPos, yPos);
        }

        @Override
        public void setByPlayer(ItemStack stack)
        {
            super.setByPlayer(stack);

            if (!stack.isEmpty() && level() instanceof ServerLevel serverLevel)
            {
                tryInstallUpgrade(stack, serverLevel);
            }
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return super.mayPlace(stack) && canInstallUpgrade(stack);
        }
    }
}