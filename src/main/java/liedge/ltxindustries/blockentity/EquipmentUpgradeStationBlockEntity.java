package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.itemhandler.BlockInventoryType;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.ltxindustries.item.EquipmentUpgradeModuleItem;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;

public class EquipmentUpgradeStationBlockEntity extends LimaBlockEntity implements ItemHolderBlockEntity
{
    public static final int EQUIPMENT_ITEM_SLOT = 0;
    public static final int UPGRADE_MODULE_SLOT = 1;

    private final LimaBlockEntityItemHandler inventory;

    private ItemStack previewItem = ItemStack.EMPTY;

    public EquipmentUpgradeStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.EQUIPMENT_UPGRADE_STATION.get(), pos, state);
        this.inventory = new LimaBlockEntityItemHandler(this, 2, BlockInventoryType.GENERAL);
    }

    public ItemStack getPreviewItem()
    {
        return previewItem;
    }

    public LimaBlockEntityItemHandler getStationInventory()
    {
        return inventory;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepItemSynced(() -> inventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy(), stack -> this.previewItem = stack));
    }

    @Override
    public @Nullable LimaBlockEntityItemHandler getItemHandler(BlockInventoryType inventoryType)
    {
        return null;
    }

    @Override
    public boolean isItemValid(BlockInventoryType inventoryType, int slot, ItemStack stack)
    {
        if (inventoryType == BlockInventoryType.GENERAL)
        {
            if (slot == EQUIPMENT_ITEM_SLOT) return stack.getItem() instanceof UpgradableEquipmentItem; // All upgradeable equipment
            else return stack.getItem() instanceof EquipmentUpgradeModuleItem;
        }

        return true;
    }

    @Override
    public IOAccess getSideIOForItems(@Nullable Direction side)
    {
        return IOAccess.DISABLED;
    }

    @Override
    public @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side)
    {
        return null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put(KEY_ITEM_CONTAINER, inventory.serializeNBT(registries));
    }
}