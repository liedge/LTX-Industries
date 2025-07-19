package liedge.ltxindustries.menu;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.inventory.menu.LimaItemHandlerMenuSlot;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.List;

public abstract class UpgradesConfigMenu<CTX extends ItemHolderBlockEntity, U extends UpgradeBase<?, U>, UC extends UpgradesContainerBase<?, U>> extends LimaMenu<CTX>
{
    private int quickTransferSlot = -1;

    private final List<Object2IntMap.Entry<Holder<U>>> remoteUpgrades = new ObjectArrayList<>();
    private final int handlerIndex;
    private boolean screenUpdate = true;

    protected UpgradesConfigMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, int handlerIndex)
    {
        super(type, containerId, inventory, menuContext);
        this.handlerIndex = handlerIndex;
    }

    @Override
    public final ItemStack quickMoveStack(Player player, int index)
    {
        // Capture the quick transfer slot if applicable
        int size = menuContainer().getSlots();
        if (index >= size)
        {
            int shiftedIndex = index - size;
            this.quickTransferSlot = shiftedIndex < 27 ? shiftedIndex + 9 : shiftedIndex - 27;
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
    protected final IItemHandlerModifiable menuContainer()
    {
        return menuContext.getItemHandler(handlerIndex);
    }

    @Override
    protected abstract void defineButtonEventHandlers(EventHandlerBuilder builder);

    protected abstract NetworkSerializer<UC> getUpgradesSerializer();

    protected abstract UC getUpgrades();

    protected abstract boolean canInstallUpgrade(ItemStack upgradeModuleItem);

    protected abstract void tryInstallUpgrade(ItemStack upgradeModuleItem, ServerLevel level);

    protected abstract void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId);

    protected void addUpgradeInsertionSlot(int slotIndex)
    {
        addSlot(new InsertSlot(slotIndex, 24, 87));
    }

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

    protected void sendSoundToPlayer(ServerPlayer player, Holder<SoundEvent> sound)
    {
        BlockPos pos = menuContext.getAsLimaBlockEntity().getBlockPos();
        ClientboundSoundPacket packet = new ClientboundSoundPacket(sound, SoundSource.PLAYERS, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1f, 1f, player.getRandom().nextLong());
        player.connection.send(packet);
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