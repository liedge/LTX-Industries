package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum BlockEntityInputType implements StringRepresentable, Translatable
{
    ITEMS("items"),
    ENERGY("energy"),
    FLUIDS("fluids");

    public static final Codec<BlockEntityInputType> CODEC = LimaEnumCodec.create(BlockEntityInputType.class);
    public static final StreamCodec<FriendlyByteBuf, BlockEntityInputType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(BlockEntityInputType.class);
    public static final Translatable SIDEBAR_TOOLTIP = LTXILangKeys.tooltip("configure_io");

    private final String name;
    private final String descriptionId;

    BlockEntityInputType(String name)
    {
        this.name = name;
        this.descriptionId = LTXIndustries.RESOURCES.translationKey("input_type.{}", name);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }
}