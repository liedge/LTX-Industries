package liedge.ltxindustries.blockentity.base;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record SidedAccessRules(Set<RelativeHorizontalSide> validSides, Set<IOAccess> validIOStates, IOAccess defaultIOState,
                               boolean allowAutoInput, boolean allowAutoOutput, boolean defaultAutoInput, boolean defaultAutoOutput)
{
    public static final Set<RelativeHorizontalSide> ALL_SIDES = ImmutableSet.copyOf(EnumSet.allOf(RelativeHorizontalSide.class));

    public static Builder builder()
    {
        return new Builder();
    }

    public static SidedAccessRules allSides(Set<IOAccess> validIOStates, IOAccess defaultIOState, boolean allowAutoInput, boolean allowAutoOutput)
    {
        return SidedAccessRules.builder()
                .setValidIOStates(validIOStates)
                .setDefaultIOState(defaultIOState)
                .defineAutoInput(allowAutoInput)
                .defineAutoOutput(allowAutoOutput)
                .build();
    }

    public static final class Builder
    {
        private Set<RelativeHorizontalSide> validSides = ALL_SIDES;
        private Set<IOAccess> validIOStates;
        private IOAccess defaultIOState = IOAccess.DISABLED;
        private boolean allowAutoInput;
        private boolean allowAutoOutput;
        private boolean defaultAutoInput;
        private boolean defaultAutoOutput;

        private Builder() {}

        public Builder setValidSides(Set<RelativeHorizontalSide> validSides)
        {
            this.validSides = validSides;
            return this;
        }

        public Builder setValidSides(RelativeHorizontalSide... validSides)
        {
            return setValidSides(EnumSet.copyOf(Arrays.asList(validSides)));
        }

        public Builder setValidIOStates(Set<IOAccess> validIOStates)
        {
            this.validIOStates = validIOStates;
            return this;
        }

        public Builder setDefaultIOState(IOAccess defaultIOState)
        {
            this.defaultIOState = defaultIOState;
            return this;
        }

        public Builder defineAutoInput(boolean allowAutoInput, boolean defaultAutoInput)
        {
            this.allowAutoInput = allowAutoInput;
            this.defaultAutoInput = defaultAutoInput;
            return this;
        }

        public Builder defineAutoInput(boolean allowAutoInput)
        {
            return defineAutoInput(allowAutoInput, false);
        }

        public Builder defineAutoOutput(boolean allowAutoOutput, boolean defaultAutoOutput)
        {
            this.allowAutoOutput = allowAutoOutput;
            this.defaultAutoOutput = defaultAutoOutput;
            return this;
        }

        public Builder defineAutoOutput(boolean allowAutoOutput)
        {
            return defineAutoOutput(allowAutoOutput, false);
        }

        public SidedAccessRules build()
        {
            validSides = validateAndWrapSet(validSides, "valid sides");
            validIOStates = validateAndWrapSet(validIOStates, "valid IO states");
            Preconditions.checkState(validIOStates.contains(defaultIOState), "Default IO state is not contained in valid IO states.");
            Preconditions.checkState(!defaultAutoInput || allowAutoInput, "Default auto input cannot be enabled if auto input is not supported.");
            Preconditions.checkState(!defaultAutoOutput || allowAutoOutput, "Default auto output cannot be enabled if auto output is not supported.");

            return new SidedAccessRules(validSides, validIOStates, defaultIOState, allowAutoInput, allowAutoOutput, defaultAutoInput, defaultAutoOutput);
        }

        // Helpers
        private <T> Set<T> validateAndWrapSet(@Nullable Set<T> set, String setName)
        {
            if (set == null || set.isEmpty()) throw new IllegalStateException("Sided access rules " + setName + " set cannot be empty or null.");

            if (set instanceof ImmutableSet<?> || set instanceof ObjectSets.UnmodifiableSet<?>)
            {
                return set;
            }
            else if (set instanceof ObjectSet<T> objectSet)
            {
                return ObjectSets.unmodifiable(objectSet); // Use fast-util wrapper for their object sets
            }
            else
            {
                return Collections.unmodifiableSet(set); // Use default unmodifiable set
            }
        }
    }
}