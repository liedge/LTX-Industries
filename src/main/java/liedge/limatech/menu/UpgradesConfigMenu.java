package liedge.limatech.menu;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.inventory.menu.LimaItemHandlerMenuSlot;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class UpgradesConfigMenu<CTX, U extends UpgradeBase<?, U>, UCT extends UpgradesContainerBase<?, U>> extends LimaMenu<CTX>
{
    private final List<Object2IntMap.Entry<Holder<U>>> remoteUpgrades = new ObjectArrayList<>();
    private boolean screenUpdate = true;

    protected UpgradesConfigMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(getUpgradesSerializer(), this::getUpgradesFromContext, eid -> {
            this.remoteUpgrades.clear();
            this.remoteUpgrades.addAll(eid.toEntrySet());
            this.screenUpdate = true;
        }));
    }

    @Override
    protected abstract void defineButtonEventHandlers(EventHandlerBuilder builder);

    protected abstract NetworkSerializer<UCT> getUpgradesSerializer();

    protected abstract UCT getUpgradesFromContext();

    protected abstract boolean canInstallUpgrade(ItemStack upgradeModuleItem);

    protected abstract void tryInstallUpgrade(ItemStack upgradeModuleItem, ServerLevel level);

    protected abstract void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId);

    protected void addUpgradeInsertionSlot(int slotIndex)
    {
        addSlot(new InsertSlot(slotIndex, 24, 87));
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

    private class InsertSlot extends LimaItemHandlerMenuSlot
    {
        public InsertSlot(int slotIndex, int xPos, int yPos)
        {
            super(menuContainer(), slotIndex, xPos, yPos);
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