package liedge.limatech.blockentity.base;

import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum BlockEntityInputType implements Translatable
{
    ITEMS("items"),
    ENERGY("energy"),
    FLUIDS("fluids");

    public static final StreamCodec<FriendlyByteBuf, BlockEntityInputType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(BlockEntityInputType.class);

    private final String translationKey;

    BlockEntityInputType(String name)
    {
        this.translationKey = LimaTech.RESOURCES.translationKey("input_type", "{}", name);
    }

    @Override
    public String descriptionId()
    {
        return translationKey;
    }
}