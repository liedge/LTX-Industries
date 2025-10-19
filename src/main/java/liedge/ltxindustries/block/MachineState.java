package liedge.ltxindustries.block;

import net.minecraft.util.StringRepresentable;

public enum MachineState implements StringRepresentable
{
    IDLE("idle"),
    STALLED("stalled"),
    ACTIVE("active");

    private final String name;

    public static MachineState of(boolean active)
    {
        return active ? ACTIVE : IDLE;
    }

    MachineState(String name)
    {
        this.name = name;
    }

    public boolean isActive()
    {
        return this == ACTIVE;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}