package liedge.limatech.blockentity.base;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class IOController implements INBTSerializable<CompoundTag>
{
    private final SidedAccessBlockEntity blockEntity;
    private final SidedAccessRules accessRules;
    private final BlockEntityInputType inputType;
    private final Map<Direction, IOAccess> controlMap;

    private Direction facing;
    private boolean autoInput;
    private boolean autoOutput;

    private @Nullable LimaDataWatcher<CompoundTag> dataWatcher;

    public IOController(SidedAccessBlockEntity blockEntity, BlockEntityInputType inputType, Direction facing, boolean autoInput, boolean autoOutput)
    {
        this.blockEntity = blockEntity;
        this.accessRules = blockEntity.getType().getSideAccessRules(inputType);
        validateFacing(facing);
        this.inputType = inputType;
        this.controlMap = accessRules.validSides().stream().collect(LimaStreamsUtil.toEnumMap(Direction.class, $ -> accessRules.defaultAccess()));
        this.facing = facing;
        this.autoInput = accessRules.allowAutoInput() && autoInput;
        this.autoOutput = accessRules.allowAutoOutput() && autoOutput;
    }

    public IOController(SidedAccessBlockEntity blockEntity, BlockEntityInputType inputType, Direction facing)
    {
        this(blockEntity, inputType, facing, false, false);
    }

    public IOAccess getSideIO(Direction side)
    {
        return Objects.requireNonNull(controlMap.get(side), "Side not valid for IO controls.");
    }

    private @Nullable IOAccess setSideIOInternal(Direction side, IOAccess access)
    {
        Preconditions.checkArgument(accessRules.validSides().contains(side), "Side %s is not valid for input type %s.", side, inputType);
        Preconditions.checkArgument(accessRules.validIOStates().contains(access), "IO access %s not valid for input type %s.", access, inputType);
        return controlMap.put(side, access);
    }

    public void setSideIO(Direction side, IOAccess access)
    {
        IOAccess oldAccess = setSideIOInternal(side, access);
        if (oldAccess != access) onChanged();
    }

    public void cycleSideIO(Direction side, boolean forward)
    {
        IOAccess current = getSideIO(side);
        IOAccess next = forward ? OrderedEnum.nextAvailable(accessRules.validIOStates(), current) : OrderedEnum.previousAvailable(accessRules.validIOStates(), current);
        setSideIO(side, next);
    }

    public Direction getFacing()
    {
        return facing;
    }

    public void setFacing(Direction newFacing)
    {
        validateFacing(newFacing);
        final Direction oldFacing = this.facing;

        if (oldFacing != newFacing)
        {
            IOAccess front = getSideIO(oldFacing);
            IOAccess back = getSideIO(oldFacing.getOpposite());
            IOAccess left = getSideIO(oldFacing.getClockWise());
            IOAccess right = getSideIO(oldFacing.getCounterClockWise());

            this.facing = newFacing;

            setSideIOInternal(newFacing, front);
            setSideIOInternal(newFacing.getOpposite(), back);
            setSideIOInternal(newFacing.getClockWise(), left);
            setSideIOInternal(newFacing.getCounterClockWise(), right);

            onChanged();
        }
    }

    public boolean isAutoInput()
    {
        return autoInput;
    }

    public boolean isAutoOutput()
    {
        return autoOutput;
    }

    public boolean allowsAutoInput()
    {
        return accessRules.allowAutoInput();
    }

    public boolean allowsAutoOutput()
    {
        return accessRules.allowAutoOutput();
    }

    public void toggleAutoInput()
    {
        if (!autoInput && allowsAutoInput())
        {
            this.autoInput = true;
            onChanged();
        }
        else
        {
            this.autoInput = false;
            onChanged();
        }
    }

    public void toggleAutoOutput()
    {
        if (!autoOutput && allowsAutoOutput())
        {
            this.autoOutput = true;
            onChanged();
        }
        else
        {
            this.autoOutput = false;
            onChanged();
        }
    }

    public LimaDataWatcher<CompoundTag> getOrCreateDataWatcher()
    {
        if (dataWatcher == null) dataWatcher = ManualDataWatcher.manuallyTrack(LimaCoreNetworkSerializers.COMPOUND_TAG, this::serializeNBT, this::deserializeNBT);
        return dataWatcher;
    }

    private void onChanged()
    {
        blockEntity.onIOControlsChanged(inputType);
        getOrCreateDataWatcher().setChanged(true);
    }

    private void validateFacing(Direction facing)
    {
        Preconditions.checkArgument(facing.getAxis().isHorizontal(), "Front facing direction must be horizontal (N/S/E/W)");
    }

    // Remove supplier dependency for registry access. Not needed for serializing this as NBT
    private CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        for (Direction side : accessRules.validSides())
        {
            tag.putString(side.getSerializedName(), getSideIO(side).getSerializedName());
        }

        tag.putString("facing", facing.getSerializedName());
        tag.putBoolean("auto_input", autoInput);
        tag.putBoolean("auto_output", autoOutput);

        return tag;
    }

    private void deserializeNBT(CompoundTag tag)
    {
        for (Direction side : accessRules.validSides())
        {
            setSideIOInternal(side, IOAccess.CODEC.byNameOrThrow(tag.getString(side.getSerializedName())));
        }

        this.facing = LimaCoreCodecs.STRICT_DIRECTION.byNameOrThrow(tag.getString("facing"));
        this.autoInput = tag.getBoolean("auto_input");
        this.autoOutput = tag.getBoolean("auto_output");
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        return serializeNBT();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
    {
        deserializeNBT(nbt);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof IOController other)
        {
            return this.controlMap.equals(other.controlMap);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return controlMap.hashCode();
    }
}