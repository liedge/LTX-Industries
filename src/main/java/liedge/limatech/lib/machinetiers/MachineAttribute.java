package liedge.limatech.lib.machinetiers;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum MachineAttribute implements StringRepresentable
{
    ENERGY_CAPACITY("energy_capacity"),
    ENERGY_TRANSFER_RATE("energy_transfer_rate"),
    PROCESS_ENERGY_CONSUMPTION("energy_consumption");

    public static final LimaEnumCodec<MachineAttribute> CODEC = LimaEnumCodec.createStrict(MachineAttribute.class);

    private final String name;

    MachineAttribute(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}