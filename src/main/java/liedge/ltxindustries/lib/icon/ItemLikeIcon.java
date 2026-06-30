package liedge.ltxindustries.lib.icon;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

public interface ItemLikeIcon
{
    Codec<ItemLikeIcon> CODEC = Codec.lazyInitialized(() ->
    {
        Codec<ItemLikeIcon> dispatchCodec = ItemLikeIconType.CODEC.dispatch(ItemLikeIcon::getType, ItemLikeIconType::getCodec);
        return Codec.either(SpriteIcon.INLINE_CODEC, dispatchCodec).xmap(Either::unwrap, SpriteIcon::encodeAutoInline);
    });

    ItemLikeIconType getType();

    default ItemLikeIcon add(ItemLikeIcon foreground)
    {
        if (getType().isValidCompositeLayer() && foreground.getType().isValidCompositeLayer())
        {
            return new CompositeIcon(this, foreground);
        }
        else
        {
            throw new UnsupportedOperationException("Background or foreground icon cannot be used as a composite layer type.");
        }
    }
}