package liedge.ltxindustries.lib.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public record TextIcon(String text, Style style, boolean dropShadow) implements ItemLikeIcon
{
    public static final MapCodec<TextIcon> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.string(1, 2).fieldOf("text").forGetter(TextIcon::text),
            Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(TextIcon::style),
            Codec.BOOL.optionalFieldOf("drop_shadow", true).forGetter(TextIcon::dropShadow))
            .apply(i, TextIcon::new));

    public static TextIcon create(String text, Style style, boolean dropShadow)
    {
        return new TextIcon(text, style, dropShadow);
    }

    public static TextIcon create(String text, ChatFormatting formatting, boolean dropShadow)
    {
        return create(text, Style.EMPTY.applyFormat(formatting), dropShadow);
    }

    public static TextIcon create(String text, Style style)
    {
        return create(text, style, true);
    }

    public static TextIcon create(String text, ChatFormatting formatting)
    {
        return create(text, formatting, true);
    }

    public static TextIcon create(String text)
    {
        return create(text, Style.EMPTY);
    }

    @Override
    public ItemLikeIconType getType()
    {
        return ItemLikeIconType.TEXT;
    }
}