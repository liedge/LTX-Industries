package liedge.ltxindustries.lib.icon;

import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum ItemLikeIconType implements StringRepresentable
{
    NONE("none", MapCodec.unit(EmptyIcon.INSTANCE)),
    SPRITE("sprite", SpriteIcon.CODEC),
    ITEM("item", ItemIcon.CODEC),
    TEXT("text", TextIcon.CODEC),
    COMPOSITE("composite", CompositeIcon.CODEC);

    public static final LimaEnumCodec<ItemLikeIconType> CODEC = LimaEnumCodec.create(ItemLikeIconType.class);

    private final String name;
    private final MapCodec<? extends ItemLikeIcon> codec;

    ItemLikeIconType(String name, MapCodec<? extends ItemLikeIcon> codec)
    {
        this.name = name;
        this.codec = codec;
    }

    public boolean isValidCompositeLayer()
    {
        return this != NONE && this != COMPOSITE;
    }

    public MapCodec<? extends ItemLikeIcon> getCodec()
    {
        return codec;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}