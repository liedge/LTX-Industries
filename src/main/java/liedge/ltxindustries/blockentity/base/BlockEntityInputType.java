package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum BlockEntityInputType implements StringRepresentable
{
    ITEMS("items"),
    ENERGY("energy"),
    FLUIDS("fluids");

    public static final Codec<BlockEntityInputType> CODEC = LimaEnumCodec.create(BlockEntityInputType.class);
    public static final StreamCodec<FriendlyByteBuf, BlockEntityInputType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(BlockEntityInputType.class);

    private final String name;
    private final Translatable menuTitle;
    private final Translatable sidebarTooltip;

    BlockEntityInputType(String name)
    {
        this.name = name;
        this.menuTitle = LTXIndustries.RESOURCES.translationHolder("input_type.{}", name, "title");
        this.sidebarTooltip = LTXIndustries.RESOURCES.translationHolder("input_type.{}", name, "tooltip");
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public Translatable getMenuTitle()
    {
        return menuTitle;
    }

    public Translatable getSidebarTooltip()
    {
        return sidebarTooltip;
    }
}