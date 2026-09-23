package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.util.TriState;

import java.util.*;

public final class IORules
{
    public static final Codec<IORules> CODEC = RecordCodecBuilder.<IORules>create(i -> i.group(
            LimaCoreCodecs.enumSetCodec(RelativeHorizontalSide.CODEC).fieldOf("sides").forGetter(o -> o.sides),
            LimaCoreCodecs.enumSetCodec(IOAccess.CODEC).fieldOf("accesses").forGetter(o -> o.accesses),
            IOAccess.CODEC.fieldOf("default_access").forGetter(o -> o.defaultAccess),
            TriState.CODEC.optionalFieldOf("auto_input", TriState.FALSE).forGetter(o -> o.autoInput),
            TriState.CODEC.optionalFieldOf("auto_output", TriState.FALSE).forGetter(o -> o.autoOutput))
            .apply(i, IORules::new))
            .validate(IORules::validate);

    private static DataResult<IORules> validate(IORules value)
    {
        if (value.sides.isEmpty())
            return DataResult.error(() -> "IO rules has no sides.");

        if (value.accesses.isEmpty())
            return DataResult.error(() -> "IO rules has no accesses.");

        if (!value.accesses.contains(value.defaultAccess))
            return DataResult.error(() -> "Default IO access not contained within valid access set.");

        if (!value.autoInput.isFalse() && value.accesses.stream().noneMatch(IOAccess::allowsInput))
            return DataResult.error(() -> "Auto input enabled but no side permits input.");

        if (!value.autoOutput.isFalse() && value.accesses.stream().noneMatch(IOAccess::allowsOutput))
            return DataResult.error(() -> "Auto output enabled but no side permits output.");

        return DataResult.success(value);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    private final Set<RelativeHorizontalSide> sides;
    private final Set<IOAccess> accesses;
    private final IOAccess defaultAccess;
    private final TriState autoInput;
    private final TriState autoOutput;

    private IORules(Set<RelativeHorizontalSide> sides, Set<IOAccess> accesses, IOAccess defaultAccess, TriState autoInput, TriState autoOutput)
    {
        this.sides = sides;
        this.accesses = accesses;
        this.defaultAccess = defaultAccess;
        this.autoInput = autoInput;
        this.autoOutput = autoOutput;
    }

    public Set<RelativeHorizontalSide> getSides()
    {
        return Collections.unmodifiableSet(sides);
    }

    public Set<IOAccess> getAccesses()
    {
        return Collections.unmodifiableSet(accesses);
    }

    public IOAccess getDefaultAccess()
    {
        return defaultAccess;
    }

    public boolean allowsAutoInput()
    {
        return !autoInput.isFalse();
    }

    public boolean allowsAutoOutput()
    {
        return !autoOutput.isFalse();
    }

    public boolean defaultAutoInput()
    {
        return autoInput.isTrue();
    }

    public boolean defaultAutoOutput()
    {
        return autoOutput.isTrue();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        else if(!(obj instanceof IORules other))
            return false;
        else
        {
            return this.sides.equals(other.sides) &&
                    this.accesses.equals(other.accesses) &&
                    this.defaultAccess.equals(other.defaultAccess) &&
                    this.autoInput.equals(other.autoInput) &&
                    this.autoOutput.equals(other.autoOutput);
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(sides, accesses, defaultAccess, autoInput, autoOutput);
    }

    public static final class Builder
    {
        private Set<RelativeHorizontalSide> sides = EnumSet.allOf(RelativeHorizontalSide.class);
        private Set<IOAccess> accesses = EnumSet.of(IOAccess.DISABLED);
        private IOAccess defaultAccess = IOAccess.DISABLED;
        private TriState autoInput = TriState.FALSE;
        private TriState autoOutput = TriState.FALSE;

        private Builder() { }

        public Builder forSides(Collection<RelativeHorizontalSide> sides)
        {
            this.sides = EnumSet.copyOf(sides);
            return this;
        }

        public Builder permits(Collection<IOAccess> accesses)
        {
            this.accesses = EnumSet.copyOf(accesses);
            return this;
        }

        public Builder withDefaultIOAccess(IOAccess defaultIOAccess)
        {
            this.defaultAccess = defaultIOAccess;
            return this;
        }

        public Builder allowsAutoInput()
        {
            this.autoInput = TriState.DEFAULT;
            return this;
        }

        public Builder allowsAutoOutput()
        {
            this.autoOutput = TriState.DEFAULT;
            return this;
        }

        public Builder autoInputByDefault()
        {
            this.autoInput = TriState.TRUE;
            return allowsAutoInput();
        }

        public Builder autoOutputByDefault()
        {
            this.autoOutput = TriState.TRUE;
            return allowsAutoOutput();
        }

        public IORules build()
        {
            IORules rules = new IORules(sides, accesses, defaultAccess, autoInput, autoOutput);
            return validate(rules).getOrThrow();
        }
    }
}