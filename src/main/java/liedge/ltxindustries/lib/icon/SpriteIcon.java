package liedge.ltxindustries.lib.icon;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.resources.Identifier;

public record SpriteIcon(Identifier id, int width, int height, int xPos, int yPos) implements ItemLikeIcon
{
    private static final Codec<Integer> DIMS_CODEC = Codec.intRange(1, 16);
    static final Codec<SpriteIcon> INLINE_CODEC = Identifier.CODEC.xmap(SpriteIcon::create, SpriteIcon::id);
    public static final MapCodec<SpriteIcon> CODEC = RecordCodecBuilder.<SpriteIcon>mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(SpriteIcon::id),
            DIMS_CODEC.optionalFieldOf("width", 16).forGetter(SpriteIcon::width),
            DIMS_CODEC.optionalFieldOf("height", 16).forGetter(SpriteIcon::height),
            DIMS_CODEC.optionalFieldOf("x", 0).forGetter(SpriteIcon::xPos),
            DIMS_CODEC.optionalFieldOf("y", 0).forGetter(SpriteIcon::yPos))
            .apply(i, SpriteIcon::new))
            .validate(SpriteIcon::validate);

    private static DataResult<SpriteIcon> validate(SpriteIcon icon)
    {
        int maxX = 16 - icon.width;
        int maxY = 16 - icon.height;

        if (icon.xPos < 0 || icon.xPos > maxX) return DataResult.error(() -> "Icon x position out of valid range [0," + maxX + ")");
        if (icon.yPos < 0 || icon.yPos > maxY) return DataResult.error(() -> "Icon y position out of valid range [0," + maxY + ")");

        return DataResult.success(icon);
    }

    public static SpriteIcon create(Identifier id, int width, int height, int xPos, int yPos)
    {
        return new SpriteIcon(id, width, height, xPos, yPos);
    }

    public static SpriteIcon create(String path, int width, int height, int xPos, int yPos)
    {
        return create(LTXIndustries.RESOURCES.id(path), width, height, xPos, yPos);
    }

    public static SpriteIcon create(Identifier id)
    {
        return create(id, 16, 16, 0, 0);
    }

    public static SpriteIcon create(String path)
    {
        return create(path, 16, 16, 0, 0);
    }

    static Either<SpriteIcon, ItemLikeIcon> encodeAutoInline(ItemLikeIcon icon)
    {
        if (icon instanceof SpriteIcon spriteIcon && spriteIcon.isFullSize())
        {
            return Either.left(spriteIcon);
        }
        else
        {
            return Either.right(icon);
        }
    }

    @Override
    public ItemLikeIconType getType()
    {
        return ItemLikeIconType.SPRITE;
    }

    private boolean isFullSize()
    {
        return width == 16 && height == 16 && xPos == 0 && yPos == 0;
    }
}