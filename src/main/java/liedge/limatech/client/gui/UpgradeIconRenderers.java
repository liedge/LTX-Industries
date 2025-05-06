package liedge.limatech.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
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

    /**
     * Renders an upgrade icon based on its type. Returns {@code true} if rendering succeeded/should proceed, or {@code false} if rendering failed/should be cancelled.
     * @param graphics GuiGraphics, used in most GUI rendering contexts
     * @param icon The upgrade icon
     * @param x GUI X position
     * @param y GUI y position
     * @return The rendering result
     */
    public static boolean renderIcon(GuiGraphics graphics, UpgradeIcon icon, int x, int y)
    {
        return INSTANCE.renderInternal(graphics, x, y, icon);
    }

    /**
     * Calls {@link UpgradeIconRenderers#renderIcon(GuiGraphics, UpgradeIcon, int, int)} and renders the default
     * upgrade sprite icon if icon rendering failed.
     * @param graphics GuiGraphics, used in most GUI rendering contexts
     * @param icon The upgrade icon
     * @param x GUI X position
     * @param y GUI Y position
     */
    public static void renderWithSpriteFallback(GuiGraphics graphics, UpgradeIcon icon, int x, int y)
    {
        if (!renderIcon(graphics, icon, x, y))
        {
            renderSpriteIcon(graphics, UpgradeIcon.DEFAULT_ICON_LOCATION, x, y, 0, 16);
        }
    }

    private final Map<UpgradeIcon.Type, IconRenderer<?>> renderers = new Object2ObjectOpenHashMap<>();

    private UpgradeIconRenderers()
    {
        this.<UpgradeIcon.NoRenderIcon>registerRenderer(UpgradeIcon.Type.NO_RENDER, (graphics, x, y, icon) -> false);
        this.<UpgradeIcon.SpriteSheetIcon>registerRenderer(UpgradeIcon.Type.UPGRADE_SPRITE, (graphics, x, y, icon) -> renderSpriteIcon(graphics, icon.location(), x, y, 150, 16));
        this.<UpgradeIcon.ItemStackIcon>registerRenderer(UpgradeIcon.Type.ITEM_STACK, (graphics, x, y, icon) -> renderItemStackIcon(graphics, icon.stack(), x, y));
        this.registerRenderer(UpgradeIcon.Type.COMPOSITE, UpgradeIconRenderers::renderCompositeIcon);
    }

    private <T extends UpgradeIcon> void registerRenderer(UpgradeIcon.Type type, IconRenderer<T> iconRenderer)
    {
        renderers.put(type, iconRenderer);
    }

    @SuppressWarnings("unchecked")
    private <T extends UpgradeIcon> boolean renderInternal(GuiGraphics graphics, int x, int y, UpgradeIcon uncheckedIcon)
    {
        IconRenderer<T> renderer = (IconRenderer<T>) renderers.get(uncheckedIcon.getType());
        return renderer.render(graphics, x, y, (T) uncheckedIcon);
    }

    private static boolean renderSpriteIcon(GuiGraphics graphics, ResourceLocation location, int x, int y, int blitOffset, int size)
    {
        TextureAtlasSprite sprite = UpgradeIconSprites.getInstance().getSprite(location);
        graphics.blit(x, y, blitOffset, size, size, sprite);
        return true;
    }

    private static boolean renderItemStackIcon(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof ItemGuiRenderOverride) return false;

        graphics.renderFakeItem(stack, x, y);
        return true;
    }

    private static boolean renderCompositeIcon(GuiGraphics graphics, int x, int y, UpgradeIcon.CompositeIcon icon)
    {
        if (!renderIcon(graphics, icon.background(), x, y)) return false;

        int nx = x + icon.xOffset();
        int ny = y + icon.yOffset();

        return switch (icon.overlay())
        {
            case UpgradeIcon.SpriteSheetIcon sprite -> renderSpriteIcon(graphics, sprite.location(), nx, ny, 200, icon.overlaySize());
            case UpgradeIcon.ItemStackIcon item -> renderSizedItemStack(graphics, item.stack(), nx, ny, icon.overlaySize());
            default -> false;
        };
    }

    private static boolean renderSizedItemStack(GuiGraphics graphics, ItemStack stack, int x, int y, int size)
    {
        if (stack.getItem() instanceof ItemGuiRenderOverride) return false;

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();

        poseStack.translate(x, y, 50); // Position at GUI X and Y + offsets and blitOffset
        float f = size / 16f;
        poseStack.scale(f, f, 1f);

        graphics.renderFakeItem(stack, 0, 0); // 0, 0 as item is already positioned by poseStack

        poseStack.popPose();

        return true;
    }

    @FunctionalInterface
    private interface IconRenderer<T extends UpgradeIcon>
    {
        boolean render(GuiGraphics graphics, int x, int y, T icon);
    }
}