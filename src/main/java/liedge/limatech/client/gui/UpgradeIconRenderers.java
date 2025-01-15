package liedge.limatech.client.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limatech.lib.upgrades.UpgradeIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class UpgradeIconRenderers
{
    private static final UpgradeIconRenderers INSTANCE = new UpgradeIconRenderers();

    public static void renderIcon(GuiGraphics graphics, UpgradeIcon icon, int x, int y)
    {
        INSTANCE.renderInternal(graphics, x, y, icon);
    }

    private final Map<UpgradeIcon.Type, IconRenderer<?>> renderers = new Object2ObjectOpenHashMap<>();

    private UpgradeIconRenderers()
    {
        this.<UpgradeIcon.SpriteSheetIcon>registerRenderer(UpgradeIcon.Type.UPGRADE_SPRITE, (graphics, x, y, icon) -> renderSpriteIcon(graphics, icon.location(), x, y, 0, 16, 16));
        this.<UpgradeIcon.ItemStackIcon>registerRenderer(UpgradeIcon.Type.ITEM_STACK, (graphics, x, y, icon) -> renderFakeItemOrDefault(graphics, icon.stack(), x, y));
        this.<UpgradeIcon.ItemStackWithSpriteIcon>registerRenderer(UpgradeIcon.Type.ITEM_WITH_SPRITE_OVERLAY, (graphics, x, y, icon) -> {
            renderFakeItemOrDefault(graphics, icon.stack(), x, y);
            renderSpriteIcon(graphics, icon.spriteLocation(), x + icon.xOffset(), y + icon.yOffset(), 200, icon.width(), icon.height());
        });
    }

    private <T extends UpgradeIcon> void registerRenderer(UpgradeIcon.Type type, IconRenderer<T> iconRenderer)
    {
        renderers.put(type, iconRenderer);
    }

    @SuppressWarnings("unchecked")
    private <T extends UpgradeIcon> void renderInternal(GuiGraphics graphics, int x, int y, UpgradeIcon uncheckedIcon)
    {
        IconRenderer<T> renderer = (IconRenderer<T>) renderers.get(uncheckedIcon.getType());
        renderer.render(graphics, x, y, (T) uncheckedIcon);
    }

    private static void renderSpriteIcon(GuiGraphics graphics, ResourceLocation location, int x, int y, int blitOffset, int width, int height)
    {
        TextureAtlasSprite sprite = UpgradeIconTextures.getUpgradeSprites().getSprite(location);
        graphics.blit(x, y, blitOffset, width, height, sprite);
    }

    private static void renderFakeItemOrDefault(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (!(stack.getItem() instanceof ItemGuiRenderOverride))
        {
            graphics.renderFakeItem(stack, x, y);
        }
        else
        {
            renderSpriteIcon(graphics, UpgradeIcon.DEFAULT_ICON_LOCATION, x, y, 0, 16, 16);
        }
    }

    @FunctionalInterface
    private interface IconRenderer<T extends UpgradeIcon>
    {
        void render(GuiGraphics graphics, int x, int y, T icon);
    }
}