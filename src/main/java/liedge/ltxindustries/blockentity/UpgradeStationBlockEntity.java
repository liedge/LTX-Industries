package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class UpgradeStationBlockEntity extends LimaBlockEntity implements ItemHolderBlockEntity
{
    public static final int EQUIPMENT_ITEM_SLOT = 0;
    public static final int UPGRADE_MODULE_SLOT = 1;

    private final LimaBlockEntityItems inventory;

    private ItemStack previewItem = ItemStack.EMPTY;

    public UpgradeStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.UPGRADE_STATION.get(), pos, state);
        this.inventory = new LimaBlockEntityItems(this, BlockContentsType.GENERAL, 2);
    }

    public ItemStack getPreviewItem()
    {
        return previewItem;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.ITEM_RESOURCE, () -> inventory.getResource(EQUIPMENT_ITEM_SLOT), resource -> this.previewItem = resource.toStack()));
    }

    @Override
    public @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType)
    {
        return contentsType == BlockContentsType.GENERAL ? inventory : null;
    }

    @Override
    public IOAccess getTopLevelItemIO(@Nullable Direction side)
    {
        return IOAccess.DISABLED;
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        if (contentsType == BlockContentsType.GENERAL)
        {
            if (index == EQUIPMENT_ITEM_SLOT) return resource.getItem() instanceof UpgradableEquipmentItem;
            return resource.is(LTXIItems.UPGRADE_MODULE.asItem()) && resource.has(LTXIDataComponents.UPGRADE_ENTRY);
        }

        return true;
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        inventory.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        inventory.serialize(output);
    }
}