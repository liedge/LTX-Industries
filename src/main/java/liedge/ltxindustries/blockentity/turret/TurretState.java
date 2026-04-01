package liedge.ltxindustries.blockentity.turret;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum TurretState implements StringRepresentable
{
    INACTIVE("inactive", false),
    SEARCHING("searching", false),
    CHARGING("charging", true),
    FIRING("firing", true),
    COOLDOWN("cooldown", false);

    public static final LimaEnumCodec<TurretState> CODEC = LimaEnumCodec.create(TurretState.class);

    private final String name;
    private final boolean extendedRender;

    TurretState(String name, boolean extendedRender)
    {
        this.name = name;
        this.extendedRender = extendedRender;
    }

    public boolean isExtendedRender()
    {
        return extendedRender;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}