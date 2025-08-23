package liedge.ltxindustries.blockentity.template;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.fluid.FluidHolderBlockEntity;
import liedge.limacore.capability.fluid.LimaBlockEntityFluidHandler;
import liedge.limacore.capability.fluid.LimaFluidUtil;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public abstract class ProductionMachineBlockEntity extends EnergyMachineBlockEntity implements FluidHolderBlockEntity
{
    public static final int INPUT_TANK_CAPACITY = 32_000;
    public static final int OUTPUT_TANK_CAPACITY = 64_000;

    private final @Nullable LimaBlockEntityItemHandler inputInventory;
    private final @Nullable LimaBlockEntityItemHandler outputInventory;

    private final @Nullable LimaBlockEntityFluidHandler inputFluids;
    private final @Nullable LimaBlockEntityFluidHandler outputFluids;
    private final @Nullable IOController fluidController;
    private final Map<Direction, BlockCapabilityCache<IFluidHandler, Direction>> fluidConnections = new EnumMap<>(Direction.class);
    private int autoFluidOutputTimer = 0;

    protected ProductionMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, pos, state, null, auxInventorySize);

        this.inputInventory = inputSlots > 0 ? new LimaBlockEntityItemHandler(this, inputSlots, BlockContentsType.INPUT) : null;
        this.outputInventory = outputSlots > 0 ? new LimaBlockEntityItemHandler(this, outputSlots, BlockContentsType.OUTPUT) : null;
        this.inputFluids = inputTanks > 0 ? new LimaBlockEntityFluidHandler(this, inputTanks, BlockContentsType.INPUT) : null;
        this.outputFluids = outputTanks > 0 ? new LimaBlockEntityFluidHandler(this, outputTanks, BlockContentsType.OUTPUT) : null;
        this.fluidController = inputFluids != null || outputFluids != null ? new IOController(this, BlockEntityInputType.FLUIDS) : null;
    }

    protected ProductionMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, int inputSlots, int outputSlots)
    {
        this(type, pos, state, auxInventorySize, inputSlots, outputSlots, 0, 0);
    }

    // Item stuff
    public LimaBlockEntityItemHandler getInputInventory()
    {
        return getItemHandlerOrThrow(BlockContentsType.INPUT);
    }

    public LimaBlockEntityItemHandler getOutputInventory()
    {
        return getItemHandlerOrThrow(BlockContentsType.OUTPUT);
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

    // Fluid handling implementation
    @Override
    public @Nullable LimaBlockEntityFluidHandler getFluidHandler(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case INPUT -> inputFluids;
            case OUTPUT -> outputFluids;
            default -> null;
        };
    }

    @Override
    public int getBaseFluidCapacity(BlockContentsType contentsType, int tank)
    {
        return switch (contentsType)
        {
            case INPUT -> INPUT_TANK_CAPACITY;
            case OUTPUT -> OUTPUT_TANK_CAPACITY;
            default -> 0;
        };
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType, int tank)
    {
        return getBaseFluidCapacity(contentsType, tank) / 4;
    }

    @Override
    public boolean isValidFluid(BlockContentsType contentsType, int tank, FluidStack stack)
    {
        return true;
    }

    @Override
    public @Nullable IFluidHandler createFluidIOWrapper(@Nullable Direction side)
    {
        return wrapInputOutputTanks(side);
    }

    @Override
    protected IOController getFluidIOController()
    {
        return fluidController != null ? fluidController : super.getFluidIOController();
    }

    @Override
    public void onFluidsChanged(BlockContentsType contentsType, int tank)
    {
        setChanged();
    }

    @Override
    public @Nullable IFluidHandler getNeighborFluidHandler(Direction side)
    {
        return fluidConnections.get(side).getCapability();
    }

    protected void autoOutputFluids()
    {
        if (outputFluids != null && fluidController != null && fluidController.isAutoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (fluidController.getSideIOState(side).allowsOutput())
                {
                    IFluidHandler neighborFluids = getNeighborFluidHandler(side);
                    if (neighborFluids != null) LimaFluidUtil.transferFluidsFromLimaTank(outputFluids, neighborFluids, OUTPUT_TANK_CAPACITY, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    protected void autoOutputFluids(int frequency)
    {
        if (autoFluidOutputTimer >= frequency)
        {
            autoOutputFluids();
            autoFluidOutputTimer = 0;
        }
        else
        {
            autoFluidOutputTimer++;
        }
    }

    // Startup and Serialization
    @Override
    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        super.createConnectionCaches(level, side);

        // Only create the fluid cache if we have fluid capabilities
        if (inputFluids != null || outputFluids != null)
        {
            fluidConnections.put(side, createCapabilityCache(Capabilities.FluidHandler.BLOCK, level, side));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        if (tag.contains(LimaCommonConstants.KEY_FLUID_TANKS, Tag.TAG_COMPOUND))
        {
            CompoundTag tanksTag = tag.getCompound(LimaCommonConstants.KEY_FLUID_TANKS);
            for (BlockContentsType type : BlockContentsType.values())
            {
                LimaBlockEntityFluidHandler handler = getFluidHandler(type);
                if (handler != null && tanksTag.contains(type.getSerializedName())) handler.deserializeNBT(registries, tanksTag.getList(type.getSerializedName(), Tag.TAG_COMPOUND));
            }
        }

        if (fluidController != null) fluidController.deserializeNBT(registries, tag.getCompound(KEY_FLUID_IO));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        CompoundTag tanksTag = new CompoundTag();
        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityFluidHandler handler = getFluidHandler(type);
            if (handler != null) tanksTag.put(type.getSerializedName(), handler.serializeNBT(registries));
        }
        if (!tanksTag.isEmpty()) tag.put(LimaCommonConstants.KEY_FLUID_TANKS, tanksTag);

        if (fluidController != null) tag.put(KEY_FLUID_IO, fluidController.serializeNBT(registries));
    }
}