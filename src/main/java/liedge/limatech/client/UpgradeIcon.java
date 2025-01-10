package liedge.limatech.client;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

import java.util.function.Function;

public interface UpgradeIcon
{
    Codec<UpgradeIcon> DISPATCH_CODEC = Type.CODEC.dispatch(UpgradeIcon::getType, Type::getCodec);
    Codec<UpgradeIcon> CODEC = Codec.either(UpgradeSprite.FLAT_CODEC, DISPATCH_CODEC).xmap(
            either -> either.map(Function.identity(), Function.identity()),
            icon -> icon instanceof UpgradeSprite sprite ? Either.left(sprite) : Either.right(icon));

    UpgradeIcon DEFAULT_ICON = new UpgradeSprite(LimaTech.RESOURCES.location("generic"));

    void render(GuiGraphics graphics, int x, int y);

    Type getType();

    default boolean shouldRenderRank(UpgradeBaseEntry<?> entry)
    {
        return entry.upgrade().value().maxRank() > 1;
    }

    record UpgradeSprite(ResourceLocation iconPath) implements UpgradeIcon
    {
        private static final Codec<UpgradeSprite> FLAT_CODEC = ResourceLocation.CODEC.xmap(UpgradeSprite::new, UpgradeSprite::iconPath);
        private static final MapCodec<UpgradeSprite> CODEC = FLAT_CODEC.fieldOf("path");

        @Override
        public void render(GuiGraphics graphics, int x, int y)
        {
            TextureAtlasSprite sprite = UpgradeIconTextures.getUpgradeSprites().getSprite(iconPath);
            graphics.blit(x, y, 0, 16, 16, sprite);
        }

        @Override
        public Type getType()
        {
            return Type.UPGRADE_SPRITE;
        }
    }

    record ItemStackRender(ItemStack stack) implements UpgradeIcon
    {
        private static final MapCodec<ItemStackRender> CODEC = ItemStack.CODEC.fieldOf("item").xmap(ItemStackRender::new, ItemStackRender::stack);

        @Override
        public void render(GuiGraphics graphics, int x, int y)
        {
            if (!(stack.getItem() instanceof ItemGuiRenderOverride)) // Avoid deadlock with recursive rendering
            {
                graphics.renderFakeItem(stack, x, y);
            }
            else
            {
                DEFAULT_ICON.render(graphics, x, y);
            }
        }

        @Override
        public Type getType()
        {
            return Type.ITEM_STACK;
        }

        @Override
        public boolean shouldRenderRank(UpgradeBaseEntry<?> entry)
        {
            return false;
        }
    }

    enum Type implements StringRepresentable
    {
        UPGRADE_SPRITE("upgrade_sprite", UpgradeSprite.CODEC),
        ITEM_STACK("item_stack", ItemStackRender.CODEC);

        public static final Codec<Type> CODEC = LimaEnumCodec.createStrict(Type.class);

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