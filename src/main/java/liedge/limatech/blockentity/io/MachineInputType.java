package liedge.limatech.blockentity.io;

import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum MachineInputType implements Translatable
{
    ITEMS("items"),
    ENERGY("energy"),
    FLUIDS("fluids");

    public static final StreamCodec<FriendlyByteBuf, MachineInputType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(MachineInputType.class);

    private final String _descriptionId;

    MachineInputType(String name)
    {
        this._descriptionId = LimaTech.RESOURCES.translationKey("input_type", "{}", name);
    }

    @Override
    public String descriptionId()
    {
        return _descriptionId;
    }
}