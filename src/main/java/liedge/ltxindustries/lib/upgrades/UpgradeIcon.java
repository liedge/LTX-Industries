package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public interface UpgradeIcon
{
    Codec<UpgradeIcon> CODEC = Type.CODEC.dispatchWithInline(SpriteSheetIcon.class, SpriteSheetIcon.INLINE_CODEC, UpgradeIcon::getType, Type::getCodec);

    static NoRenderIcon noRenderIcon()
    {
        return NoRenderIcon.INSTANCE;
    }

    static SpriteSheetIcon sprite(ResourceLocation location)
    {
        return new SpriteSheetIcon(location);
    }

    static SpriteSheetIcon sprite(String path)
    {
        return sprite(RESOURCES.location(path));
    }

    static ItemStackIcon itemIcon(ItemStack stack)
    {
        return new ItemStackIcon(stack);
    }

    static ItemStackIcon itemIcon(ItemLike itemLike)
    {
        return itemIcon(new ItemStack(itemLike));
    }

    static SpriteOverlayIcon overlayIcon(UpgradeIcon background, ResourceLocation overlay, int width, int height, int xOffset, int yOffset)
    {
        return new SpriteOverlayIcon(background, overlay, width, height, xOffset, yOffset);
    }

    Type getType();

    final class NoRenderIcon implements UpgradeIcon
    {
        private static final NoRenderIcon INSTANCE = new NoRenderIcon();
        private static final MapCodec<NoRenderIcon> MAP_CODEC = MapCodec.unit(INSTANCE);

        private NoRenderIcon() {}

        @Override
        public Type getType()
        {
            return Type.NO_RENDER;
        }
    }

    record SpriteSheetIcon(ResourceLocation location) implements UpgradeIcon
    {
        private static final Codec<SpriteSheetIcon> INLINE_CODEC = ResourceLocation.CODEC.xmap(SpriteSheetIcon::new, SpriteSheetIcon::location);
        private static final MapCodec<SpriteSheetIcon> MAP_CODEC = INLINE_CODEC.fieldOf("sprite");

        @Override
        public Type getType()
        {
            return Type.UPGRADE_SPRITE;
        }
    }

    record ItemStackIcon(ItemStack stack) implements UpgradeIcon
    {
        private static final Codec<ItemStackIcon> CODEC = ItemStack.SINGLE_ITEM_CODEC.xmap(ItemStackIcon::new, ItemStackIcon::stack);
        private static final MapCodec<ItemStackIcon> MAP_CODEC = CODEC.fieldOf("item");

        @Override
        public Type getType()
        {
            return Type.ITEM_STACK;
        }
    }

    record SpriteOverlayIcon(UpgradeIcon background, ResourceLocation overlay, int width, int height, int xOffset, int yOffset) implements UpgradeIcon
    {
        private static final Codec<UpgradeIcon> BACKGROUND_CODEC = LimaCoreCodecs.xorSubclassCodec(SpriteSheetIcon.INLINE_CODEC, ItemStackIcon.CODEC, SpriteSheetIcon.class, ItemStackIcon.class);
        private static final Codec<Integer> DIMS_CODEC = Codec.intRange(1, 16);

        private static DataResult<SpriteOverlayIcon> validate(SpriteOverlayIcon value)
        {
            int maxXO = 16 - value.width;
            int maxYO = 16 - value.height;

            if (value.xOffset < 0 || value.xOffset > maxXO) return DataResult.error(() -> "X offset out of valid range [0," + maxXO + ")");
            if (value.yOffset < 0 || value.yOffset > maxYO) return DataResult.error(() -> "Y offset out of valid range [0," + maxYO + ")");
            else return DataResult.success(value);
        }

        private static final MapCodec<SpriteOverlayIcon> CODEC = RecordCodecBuilder.<SpriteOverlayIcon>mapCodec(instance -> instance.group(
                BACKGROUND_CODEC.fieldOf("background").forGetter(SpriteOverlayIcon::background),
                ResourceLocation.CODEC.fieldOf("overlay").forGetter(SpriteOverlayIcon::overlay),
                DIMS_CODEC.fieldOf("width").forGetter(SpriteOverlayIcon::width),
                DIMS_CODEC.fieldOf("height").forGetter(SpriteOverlayIcon::height),
                DIMS_CODEC.fieldOf("x_offset").forGetter(SpriteOverlayIcon::xOffset),
                DIMS_CODEC.fieldOf("y_offset").forGetter(SpriteOverlayIcon::yOffset))
                .apply(instance, SpriteOverlayIcon::new))
                .validate(SpriteOverlayIcon::validate);

        @Override
        public Type getType()
        {
            return Type.SPRITE_OVERLAY;
        }
    }

    enum Type implements StringRepresentable
    {
        NO_RENDER("no_render", NoRenderIcon.MAP_CODEC),
        UPGRADE_SPRITE("upgrade_sprite", SpriteSheetIcon.MAP_CODEC),
        ITEM_STACK("item_stack", ItemStackIcon.MAP_CODEC),
        SPRITE_OVERLAY("overlay", SpriteOverlayIcon.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends UpgradeIcon> codec;

        Type(String name, MapCodec<? extends UpgradeIcon> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends UpgradeIcon> getCodec()
        {
            return codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}