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
    Codec<UpgradeIcon> CODEC = Type.CODEC.flatDispatch(SpriteSheetIcon.class, SpriteSheetIcon.CODEC, UpgradeIcon::getType, Type::getCodec);
    ResourceLocation DEFAULT_ICON_LOCATION = RESOURCES.location("default");

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

    static CompositeIcon compositeIcon(UpgradeIcon background, UpgradeIcon overlay, int overlaySize, int xOffset, int yOffset)
    {
        return new CompositeIcon(background, overlay, overlaySize, xOffset, yOffset);
    }

    static CompositeIcon bottomRightComposite(UpgradeIcon background, UpgradeIcon overlay, int overlaySize, int padding)
    {
        int offset = 16 - overlaySize - padding;
        return UpgradeIcon.compositeIcon(background, overlay, overlaySize, offset, offset);
    }

    static CompositeIcon bottomRightComposite(UpgradeIcon background, UpgradeIcon overlay, int overlaySize)
    {
        return bottomRightComposite(background, overlay, overlaySize, 1);
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
        private static final Codec<SpriteSheetIcon> CODEC = ResourceLocation.CODEC.xmap(SpriteSheetIcon::new, SpriteSheetIcon::location);
        private static final MapCodec<SpriteSheetIcon> MAP_CODEC = CODEC.fieldOf("sprite");

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

    record CompositeIcon(UpgradeIcon background, UpgradeIcon overlay, int overlaySize, int xOffset, int yOffset) implements UpgradeIcon
    {
        private static final Codec<UpgradeIcon> SUB_CODEC = LimaCoreCodecs.xorSubclassCodec(SpriteSheetIcon.CODEC, ItemStackIcon.CODEC, SpriteSheetIcon.class, ItemStackIcon.class);
        private static final MapCodec<CompositeIcon> MAP_CODEC = RecordCodecBuilder.<CompositeIcon>mapCodec(instance -> instance.group(
                SUB_CODEC.fieldOf("background").forGetter(CompositeIcon::background),
                SUB_CODEC.fieldOf("overlay").forGetter(CompositeIcon::overlay),
                Codec.intRange(4, 16).fieldOf("overlay_size").forGetter(CompositeIcon::overlaySize),
                Codec.INT.fieldOf("x_offset").forGetter(CompositeIcon::xOffset),
                Codec.INT.fieldOf("y_offset").forGetter(CompositeIcon::yOffset))
                .apply(instance, CompositeIcon::new)).validate(CompositeIcon::validate);

        private static DataResult<CompositeIcon> validate(CompositeIcon icon)
        {
            int maxOffset = 16 - icon.overlaySize;
            if (icon.xOffset < 0 || icon.xOffset > maxOffset) return DataResult.error(() -> "X offset out of valid range [0," + maxOffset + ")");
            if (icon.yOffset < 0 || icon.yOffset > maxOffset) return DataResult.error(() -> "Y offset out of valid range [0," + maxOffset + ")");
            return DataResult.success(icon);
        }

        @Override
        public Type getType()
        {
            return Type.COMPOSITE;
        }
    }

    enum Type implements StringRepresentable
    {
        NO_RENDER("no_render", NoRenderIcon.MAP_CODEC),
        UPGRADE_SPRITE("upgrade_sprite", SpriteSheetIcon.MAP_CODEC),
        ITEM_STACK("item_stack", ItemStackIcon.MAP_CODEC),
        COMPOSITE("composite", CompositeIcon.MAP_CODEC);

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