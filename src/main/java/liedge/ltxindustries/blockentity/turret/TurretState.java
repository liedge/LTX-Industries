package liedge.ltxindustries.blockentity.turret;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum TurretState implements StringRepresentable
{
    INACTIVE("inactive"),
    SEARCHING("searching"),
    CHARGING("charging"),
    FIRING("firing"),
    COOLDOWN("cooldown");

    public static final LimaEnumCodec<TurretState> CODEC = LimaEnumCodec.create(TurretState.class);

    private final String name;

    TurretState(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}