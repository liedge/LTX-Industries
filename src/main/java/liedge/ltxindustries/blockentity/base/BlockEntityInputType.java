package liedge.ltxindustries.blockentity.base;

import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum BlockEntityInputType
{
    ITEMS("items"),
    ENERGY("energy"),
    FLUIDS("fluids");

    public static final StreamCodec<FriendlyByteBuf, BlockEntityInputType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(BlockEntityInputType.class);

    private final Translatable menuTitle;
    private final Translatable sidebarTooltip;

    BlockEntityInputType(String name)
    {
        this.menuTitle = LTXIndustries.RESOURCES.translationHolder("input_type.{}", name, "title");
        this.sidebarTooltip = LTXIndustries.RESOURCES.translationHolder("input_type.{}", name, "tooltip");
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