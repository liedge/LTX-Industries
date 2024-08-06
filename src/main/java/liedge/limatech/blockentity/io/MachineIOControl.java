package liedge.limatech.blockentity.io;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class MachineIOControl implements INBTSerializable<CompoundTag>
{
    private final SidedMachineIOHolder holder;
    private final MachineInputType inputType;
    private final Set<IOAccess> validAccessValues;
    private final IOAccess defaultAccess;
    private final Map<Direction, IOAccess> map;

    private Direction facing;
    private boolean autoInput;
    private boolean autoOutput;

    private @Nullable LimaDataWatcher<CompoundTag> dataWatcher;

    public MachineIOControl(SidedMachineIOHolder holder, MachineInputType inputType, Set<IOAccess> validAccessValues, IOAccess defaultAccess, Direction facing, boolean autoInput, boolean autoOutput)
    {
        this.holder = holder;
        this.inputType = inputType;
        this.validAccessValues = validAccessValues;
        validateIO(defaultAccess);
        this.defaultAccess = defaultAccess;
        this.map = LimaCollectionsUtil.fillAndCreateEnumMap(Direction.class, $ -> defaultAccess);
        this.facing = facing;
        this.autoInput = holder.allowsAutoInput(inputType) && autoInput;
        this.autoOutput = holder.allowsAutoOutput(inputType) && autoOutput;
    }

    public MachineIOControl(SidedMachineIOHolder holder, MachineInputType inputType, Set<IOAccess> validAccessValues, IOAccess defaultAccess, Direction facing)
    {
        this(holder, inputType, validAccessValues, defaultAccess, facing, false, false);
    }

    public MachineInputType getInputType()
    {
        return inputType;
    }

    public IOAccess getSideIO(Direction side)
    {
        return map.getOrDefault(side, defaultAccess);
    }

    public void setSideIO(Direction side, IOAccess access)
    {
        Preconditions.checkArgument(validAccessValues.contains(access), "IO access not contained in valid values.");
        IOAccess oldAccess = map.put(side, access);
        if (oldAccess != access) onChanged();
    }

    public void cycleSideIO(Direction side, boolean forward)
    {
        IOAccess current = getSideIO(side);
        IOAccess next = forward ? OrderedEnum.nextAvailable(validAccessValues, current) : OrderedEnum.previousAvailable(validAccessValues, current);
        setSideIO(side, next);
    }

    public Direction getFacing()
    {
        return facing;
    }

    public void setFacing(Direction newFacing)
    {
        final Direction oldFacing = this.facing;

        if (oldFacing != newFacing)
        {
            IOAccess front = map.get(oldFacing);
            IOAccess back = map.get(oldFacing.getOpposite());
            IOAccess left = map.get(oldFacing.getClockWise());
            IOAccess right = map.get(oldFacing.getCounterClockWise());

            this.facing = newFacing;

            map.put(newFacing, front);
            map.put(newFacing.getOpposite(), back);
            map.put(newFacing.getClockWise(), left);
            map.put(newFacing.getCounterClockWise(), right);

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
        return holder.allowsAutoInput(inputType);
    }

    public boolean allowsAutoOutput()
    {
        return holder.allowsAutoOutput(inputType);
    }

    public void toggleAutoInput()
    {
        if (!autoInput && holder.allowsAutoInput(inputType))
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
        if (!autoOutput && holder.allowsAutoOutput(inputType))
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

    public LimaDataWatcher<CompoundTag> getOrCreateDataWatcher(Supplier<RegistryAccess> registriesAccessor)
    {
        if (dataWatcher == null) dataWatcher = ManualDataWatcher.manuallyTrack(LimaCoreNetworkSerializers.COMPOUND_TAG, () -> serializeNBT(registriesAccessor.get()), tag -> deserializeNBT(registriesAccessor.get(), tag));
        return dataWatcher;
    }

    private void onChanged()
    {
        holder.onIOControlsChanged(inputType);
        if (dataWatcher != null) dataWatcher.setChanged(true);
    }

    private void validateIO(IOAccess access)
    {
        Preconditions.checkArgument(validAccessValues.contains(access), "Specified IO access not contained in valid IO access values.");
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();

        for (Direction side : Direction.values())
        {
            tag.putString(side.getSerializedName(), getSideIO(side).getSerializedName());
        }

        tag.putString("facing", facing.getSerializedName());
        tag.putBoolean("auto_input", autoInput);
        tag.putBoolean("auto_output", autoOutput);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
    {
        for (Direction side : Direction.values())
        {
            map.put(side, IOAccess.CODEC.byName(nbt.getString(side.getSerializedName()), defaultAccess));
        }

        this.facing = LimaCoreCodecs.STRICT_DIRECTION.byName(nbt.getString("facing"));
        this.autoInput = nbt.getBoolean("auto_input");
        this.autoOutput = nbt.getBoolean("auto_output");
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof MachineIOControl other)
        {
            return this.map.equals(other.map);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }
}