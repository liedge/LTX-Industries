package liedge.ltxindustries.lib.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CompositeIcon(ItemLikeIcon background, ItemLikeIcon foreground) implements ItemLikeIcon
{
    private static final Codec<ItemLikeIcon> LAYER_CODEC = ItemLikeIcon.CODEC.validate(icon ->
    {
        if (icon.getType().isValidCompositeLayer())
            return DataResult.success(icon);
        else
            return DataResult.error(() -> String.format("Icon of type '%s' cannot be used as a composite layer.", icon.getType().getSerializedName()));
    });

    public static final MapCodec<CompositeIcon> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            LAYER_CODEC.fieldOf("background").forGetter(CompositeIcon::background),
            LAYER_CODEC.fieldOf("foreground").forGetter(CompositeIcon::foreground))
            .apply(i, CompositeIcon::new));

    public static CompositeIcon of(ItemLikeIcon background, ItemLikeIcon foreground)
    {
        return new CompositeIcon(background, foreground);
    }

    @Override
    public ItemLikeIconType getType()
    {
        return ItemLikeIconType.COMPOSITE;
    }
}