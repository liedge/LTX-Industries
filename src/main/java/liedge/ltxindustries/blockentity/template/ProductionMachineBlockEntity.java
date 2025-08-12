package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class ProductionMachineBlockEntity extends EnergyMachineBlockEntity
{
    private final @Nullable LimaBlockEntityItemHandler inputInventory;
    private final LimaBlockEntityItemHandler outputInventory;

    protected ProductionMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, int inputSlots, int outputSlots)
    {
        super(type, pos, state, null, auxInventorySize);

        this.inputInventory = inputSlots > 0 ? new LimaBlockEntityItemHandler(this, inputSlots, BlockContentsType.INPUT) : null;
        this.outputInventory = new LimaBlockEntityItemHandler(this, outputSlots, BlockContentsType.OUTPUT);
    }

    public LimaBlockEntityItemHandler getInputInventory()
    {
        return getItemHandlerOrThrow(BlockContentsType.INPUT);
    }

    public LimaBlockEntityItemHandler getOutputInventory()
    {
        return outputInventory;
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int slot, ItemStack stack)
    {
        if (contentsType == BlockContentsType.INPUT)
        {
            return isItemValidForInputInventory(slot, stack);
        }
        else
        {
            return super.isItemValid(contentsType, slot, stack);
        }
    }

    protected boolean isItemValidForInputInventory(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public @Nullable LimaBlockEntityItemHandler getItemHandler(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case GENERAL -> null;
            case AUXILIARY -> getAuxInventory();
            case INPUT -> inputInventory;
            case OUTPUT -> outputInventory;
        };
    }

    @Override
    public @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side)
    {
        return wrapInputOutputInventories(side);
    }
}