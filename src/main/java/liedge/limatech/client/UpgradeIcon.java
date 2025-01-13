package liedge.limatech.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.UpgradeIconTextures;
import liedge.limatech.lib.upgradesystem.UpgradeBaseEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public interface UpgradeIcon
{
    Codec<UpgradeIcon> CODEC = Type.CODEC.flatDispatch(SpriteSheetIcon.class, SpriteSheetIcon.FLAT_CODEC, UpgradeIcon::getType, Type::getCodec);
    ResourceLocation DEFAULT_ICON_LOCATION = LimaTech.RESOURCES.location("generic");
    UpgradeIcon DEFAULT_ICON = new SpriteSheetIcon(UpgradeIcon.DEFAULT_ICON_LOCATION);

    void render(GuiGraphics graphics, int x, int y);

    Type getType();

    default boolean shouldRenderRank(UpgradeBaseEntry<?> entry)
    {
        return entry.upgrade().value().maxRank() > 1;
    }

    default void renderFakeItemOrDefault(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (!(stack.getItem() instanceof ItemGuiRenderOverride))
        {
            graphics.renderFakeItem(stack, x, y);
        }
        else
        {
            DEFAULT_ICON.render(graphics, x, y);
        }
    }

    record SpriteSheetIcon(ResourceLocation location) implements UpgradeIcon
    {
        private static final Codec<SpriteSheetIcon> FLAT_CODEC = ResourceLocation.CODEC.xmap(SpriteSheetIcon::new, SpriteSheetIcon::location);
        private static final MapCodec<SpriteSheetIcon> CODEC = FLAT_CODEC.fieldOf("sprite");

        @Override
        public void render(GuiGraphics graphics, int x, int y)
        {
            TextureAtlasSprite sprite = UpgradeIconTextures.getUpgradeSprites().getSprite(location);
            graphics.blit(x, y, 0, 16, 16, sprite);
        }

        @Override
        public Type getType()
        {
            return Type.UPGRADE_SPRITE;
        }
    }

    record ItemStackIcon(ItemStack stack) implements UpgradeIcon
    {
        private static final MapCodec<ItemStackIcon> CODEC = ItemStack.CODEC.fieldOf("item").xmap(ItemStackIcon::new, ItemStackIcon::stack);

        @Override
        public void render(GuiGraphics graphics, int x, int y)
        {
            renderFakeItemOrDefault(graphics, stack, x, y);
        }

        @Override
        public Type getType()
        {
            return Type.ITEM_STACK;
        }
    }

    record ItemStackWithSpriteIcon(ItemStack stack, ResourceLocation spriteLocation, int width, int height, int xOffset, int yOffset) implements UpgradeIcon
    {
        private static DataResult<ItemStackWithSpriteIcon> validateOffsets(ItemStackWithSpriteIcon icon)
        {
            int maxOffsetX = 16 - icon.width;
            int maxOffsetY = 16 - icon.height;

            if (icon.xOffset < 0 || icon.xOffset > maxOffsetX) return DataResult.error(() -> "X offset out of valid range [0," + maxOffsetX + ")");
            if (icon.yOffset < 0 || icon.yOffset > maxOffsetY) return DataResult.error(() -> "Y offset out of valid range [0," + maxOffsetY + ")");

            return DataResult.success(icon);
        }

        private static final MapCodec<ItemStackWithSpriteIcon> CODEC = RecordCodecBuilder.<ItemStackWithSpriteIcon>mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("item").forGetter(ItemStackWithSpriteIcon::stack),
                ResourceLocation.CODEC.fieldOf("sprite").forGetter(ItemStackWithSpriteIcon::spriteLocation),
                Codec.intRange(4, 16).fieldOf("width").forGetter(ItemStackWithSpriteIcon::width),
                Codec.intRange(4, 16).fieldOf("height").forGetter(ItemStackWithSpriteIcon::height),
                Codec.INT.fieldOf("x_offset").forGetter(ItemStackWithSpriteIcon::xOffset),
                Codec.INT.fieldOf("y_offset").forGetter(ItemStackWithSpriteIcon::yOffset))
                .apply(instance, ItemStackWithSpriteIcon::new)).validate(ItemStackWithSpriteIcon::validateOffsets);

        @Override
        public void render(GuiGraphics graphics, int x, int y)
        {
            renderFakeItemOrDefault(graphics, stack, x, y);

            TextureAtlasSprite sprite = UpgradeIconTextures.getUpgradeSprites().getSprite(spriteLocation);
            graphics.blit(x + xOffset, y + yOffset, 200, width, height, sprite);
        }

        @Override
        public Type getType()
        {
            return Type.ITEM_WITH_SPRITE_OVERLAY;
        }
    }

    enum Type implements StringRepresentable
    {
        UPGRADE_SPRITE("upgrade_sprite", SpriteSheetIcon.CODEC),
        ITEM_STACK("item_stack", ItemStackIcon.CODEC),
        ITEM_WITH_SPRITE_OVERLAY("item_sprite_overlay", ItemStackWithSpriteIcon.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.createStrict(Type.class);

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