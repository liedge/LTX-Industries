package liedge.ltxindustries.blockentity.base;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
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
    private final Map<RelativeHorizontalSide, IOAccess> controlMap;

    private boolean autoInput;
    private boolean autoOutput;

    private @Nullable LimaDataWatcher<CompoundTag> dataWatcher;

    public IOController(SidedAccessBlockEntity blockEntity, BlockEntityInputType inputType)
    {
        this.blockEntity = blockEntity;
        this.accessRules = blockEntity.getType().getSideAccessRules(inputType);
        this.inputType = inputType;
        this.controlMap = accessRules.validSides().stream().collect(LimaStreamsUtil.toEnumMap(RelativeHorizontalSide.class, $ -> accessRules.defaultIOState()));
        this.autoInput = accessRules.defaultAutoInput();
        this.autoOutput = accessRules.defaultAutoOutput();
    }

    public SidedAccessRules getAccessRules()
    {
        return accessRules;
    }

    public IOAccess getSideIOState(RelativeHorizontalSide side)
    {
        return Objects.requireNonNullElse(controlMap.get(side), IOAccess.DISABLED);
    }

    public IOAccess getSideIOState(Direction absoluteSide)
    {
        return getSideIOState(RelativeHorizontalSide.of(blockEntity.getFacing(), absoluteSide));
    }

    public void setSideIOState(RelativeHorizontalSide side, IOAccess access)
    {
        // Merged with first call site
        Preconditions.checkArgument(accessRules.validSides().contains(side), "Side %s is not valid for input type %s.", side, inputType);
        Preconditions.checkArgument(accessRules.validIOStates().contains(access), "IO access %s not valid for input type %s.", access, inputType);
        IOAccess oldAccess = controlMap.put(side, access);

        if (oldAccess != access) onChanged();
    }

    public void setSideIOState(Direction absoluteSide, IOAccess access)
    {
        setSideIOState(RelativeHorizontalSide.of(blockEntity.getFacing(), absoluteSide), access);
    }

    public void cycleSideIOState(RelativeHorizontalSide side, boolean forward)
    {
        IOAccess current = getSideIOState(side);
        IOAccess next = forward ? OrderedEnum.nextAvailable(accessRules.validIOStates(), current) : OrderedEnum.previousAvailable(accessRules.validIOStates(), current);
        setSideIOState(side, next);
    }

    public void cycleSideIOState(Direction absoluteSide, boolean forward)
    {
        cycleSideIOState(RelativeHorizontalSide.of(blockEntity.getFacing(), absoluteSide), forward);
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
        if (allowsAutoInput())
        {
            boolean previous = autoInput;
            this.autoInput = !previous;
            onChanged();
        }
    }

    public void toggleAutoOutput()
    {
        if (allowsAutoOutput())
        {
            boolean previous = autoOutput;
            this.autoOutput = !previous;
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

    // Remove supplier dependency for registry access. Not needed for serializing this as NBT
    private CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        for (RelativeHorizontalSide side : accessRules.validSides())
        {
            tag.putString(side.getSerializedName(), getSideIOState(side).getSerializedName());
        }

        tag.putBoolean("auto_input", autoInput);
        tag.putBoolean("auto_output", autoOutput);

        return tag;
    }

    private void deserializeNBT(CompoundTag tag)
    {
        for (RelativeHorizontalSide side : accessRules.validSides())
        {
            IOAccess access = IOAccess.CODEC.byNameOrThrow(tag.getString(side.getSerializedName()));
            if (!accessRules.validSides().contains(side) || !accessRules.validIOStates().contains(access)) access = accessRules.defaultIOState();
            controlMap.put(side, access); // Second call site
        }

        this.autoInput = allowsAutoInput() && tag.getBoolean("auto_input");
        this.autoOutput = allowsAutoOutput() && tag.getBoolean("auto_output");
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