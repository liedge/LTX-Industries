package liedge.limatech.blockentity;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limatech.item.EquipmentUpgradeModuleItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechBlockEntities;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;

public class EquipmentUpgradeStationBlockEntity extends LimaBlockEntity implements ItemHolderBlockEntity, LimaMenuProvider
{
    public static final int EQUIPMENT_ITEM_SLOT = 0;
    public static final int UPGRADE_INPUT_SLOT = 1;

    private final LimaBlockEntityItemHandler inventory = new LimaBlockEntityItemHandler(this, 2);

    private ItemStack previewItem = ItemStack.EMPTY;

    public EquipmentUpgradeStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.EQUIPMENT_UPGRADE_STATION.get(), pos, state);
    }

    public ItemStack getPreviewItem()
    {
        return previewItem;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepItemSynced(() -> getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT).copy(), stack -> this.previewItem = stack));
    }

    @Override
    public LimaItemHandlerBase getItemHandler(int handlerIndex) throws IndexOutOfBoundsException
    {
        Preconditions.checkElementIndex(handlerIndex, 1, "Item Handlers");
        return inventory;
    }

    @Override
    public boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        if (slot == EQUIPMENT_ITEM_SLOT)
        {
            return stack.getItem() instanceof WeaponItem;
        }
        else
        {
            return stack.getItem() instanceof EquipmentUpgradeModuleItem;
        }
    }

    @Override
    public IOAccess getItemHandlerSideIO(Direction side)
    {
        return IOAccess.DISABLED;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        inventory.copyFromComponent(componentInput.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(ITEM_CONTAINER, inventory.copyToComponent());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(KEY_ITEM_CONTAINER);
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

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.EQUIPMENT_UPGRADE_STATION.get();
    }
}