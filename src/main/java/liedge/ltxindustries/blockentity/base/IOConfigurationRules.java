package liedge.ltxindustries.blockentity.base;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;

import java.util.*;

public record IOConfigurationRules(Set<RelativeHorizontalSide> validSides, Set<IOAccess> validIOAccesses, IOAccess defaultIOAccess,
                    boolean allowsAutoInput, boolean allowsAutoOutput, boolean defaultAutoInput, boolean defaultAutoOutput)
{
    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private static final Set<RelativeHorizontalSide> ALL_SIDES = ImmutableSet.copyOf(EnumSet.allOf(RelativeHorizontalSide.class));

        private Set<RelativeHorizontalSide> validSides;
        private Set<IOAccess> validIOAccesses;
        private IOAccess defaultIOAccess = IOAccess.DISABLED;
        private boolean allowAutoInput;
        private boolean allowAutoOutput;
        private boolean defaultAutoInput;
        private boolean defaultAutoOutput;

        private Builder() {}

        public Builder forSides(Collection<RelativeHorizontalSide> sides)
        {
            Preconditions.checkArgument(!sides.isEmpty(), "Valid sides cannot be empty.");
            this.validSides = cloneIfNecessary(sides);
            return this;
        }

        public Builder forSides(RelativeHorizontalSide... sides)
        {
            return forSides(List.of(sides));
        }

        public Builder forAllSides()
        {
            return forSides(ALL_SIDES);
        }

        public Builder permits(Collection<IOAccess> accesses)
        {
            Preconditions.checkArgument(!accesses.isEmpty(), "Valid IO access levels cannot be empty.");
            this.validIOAccesses = cloneIfNecessary(accesses);
            return this;
        }

        public Builder permits(IOAccess... accesses)
        {
            return permits(List.of(accesses));
        }

        public Builder withDefaultIOAccess(IOAccess defaultIOAccess)
        {
            this.defaultIOAccess = defaultIOAccess;
            return this;
        }

        public Builder allowsAutoInput()
        {
            this.allowAutoInput = true;
            return this;
        }

        public Builder allowsAutoOutput()
        {
            this.allowAutoOutput = true;
            return this;
        }

        public Builder autoInputByDefault()
        {
            this.defaultAutoInput = true;
            return allowsAutoInput();
        }

        public Builder autoOutputByDefault()
        {
            this.defaultAutoOutput = true;
            return allowsAutoOutput();
        }

        public IOConfigurationRules build()
        {
            Objects.requireNonNull(validSides, "Valid sides not defined.");
            Objects.requireNonNull(validIOAccesses, "Valid IO accesses not defined.");
            Preconditions.checkState(validIOAccesses.contains(defaultIOAccess), "Default IO access not contained within valid accesses.");
            Preconditions.checkState(!allowAutoInput || validIOAccesses.stream().anyMatch(IOAccess::allowsInput), "Auto input is enabled but no valid IO access permits input.");
            Preconditions.checkState(!allowAutoOutput || validIOAccesses.stream().anyMatch(IOAccess::allowsOutput), "Auto output is enabled but no valid access permits output.");

            return new IOConfigurationRules(validSides, validIOAccesses, defaultIOAccess, allowAutoInput, allowAutoOutput, defaultAutoInput, defaultAutoOutput);
        }

        private <E extends Enum<E>> Set<E> cloneIfNecessary(Collection<E> collection)
        {
            if (collection instanceof ImmutableSet<E> || collection instanceof ObjectSets.UnmodifiableSet<E>) return (Set<E>) collection;
            else return ImmutableSet.copyOf(EnumSet.copyOf(collection));
        }
    }
}