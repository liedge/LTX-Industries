package liedge.limatech.lib;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.OrderedEnum;
import net.minecraft.util.StringRepresentable;

public enum MachineOverclock implements OrderedEnum<MachineOverclock>, StringRepresentable
{
    NO_OVERCLOCK(0),
    OVERCLOCK_1(1),
    OVERCLOCK_2(2),
    OVERCLOCK_3(3),
    OVERCLOCK_4(4),
    OVERCLOCK_5(5),
    OVERCLOCK_6(6),
    OVERCLOCK_7(7),
    OVERCLOCK_8(8),
    OVERCLOCK_9(9),
    OVERCLOCK_10(10),
    OVERCLOCK_11(11),
    OVERCLOCK_12(12);

    public static final LimaEnumCodec<MachineOverclock> CODEC = LimaEnumCodec.createLenient(MachineOverclock.class, NO_OVERCLOCK);

    private final String name;
    private final int overclockTier;
    private final int globalModifier;

    MachineOverclock(int overclockTier)
    {
        this.name = "oc_" + overclockTier;
        this.overclockTier = overclockTier;
        this.globalModifier = 1 << overclockTier;
    }

    public int getOverclockTier()
    {
        return overclockTier;
    }

    public int getGlobalModifier()
    {
        return globalModifier;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}