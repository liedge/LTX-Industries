package liedge.limatech.blockentity.base;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;

import java.util.EnumSet;
import java.util.Set;

public record SidedAccessRules(Set<RelativeHorizontalSide> validSides, Set<IOAccess> validIOStates, IOAccess defaultAccess,
                               boolean allowAutoInput, boolean allowAutoOutput)
{
    public SidedAccessRules
    {
        checkSet(validSides, "valid sides");
        checkSet(validIOStates, "valid IO states");
        Preconditions.checkArgument(validIOStates.contains(defaultAccess), "Default access is not contained within valid IO states");
    }

    // Static helpers
    public static final Set<RelativeHorizontalSide> ALL_SIDES = ImmutableSet.copyOf(EnumSet.allOf(RelativeHorizontalSide.class));

    public static SidedAccessRules allSides(Set<IOAccess> validIOStates, IOAccess defaultAccess, boolean allowAutoInput, boolean allowAutoOutput)
    {
        return new SidedAccessRules(ALL_SIDES, validIOStates, defaultAccess, allowAutoInput, allowAutoOutput);
    }

    private static void checkSet(Set<?> set, String setName) throws IllegalArgumentException
    {
        if (set.isEmpty())
        {
            throw new IllegalArgumentException("Sided access rules " + setName + " set must contain at least 1 value.");
        }

        if (!(set instanceof ImmutableSet<?>) && !(set instanceof ObjectSets.UnmodifiableSet<?>))
        {
            throw new IllegalArgumentException("Sided access rules " + setName + " set must be of type ImmutableSet or UnmodifiableSet");
        }
    }
}