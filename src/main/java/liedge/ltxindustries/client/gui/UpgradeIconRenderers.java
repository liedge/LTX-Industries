package liedge.ltxindustries.client.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.ltxindustries.client.LTXIAtlasIds;
import liedge.ltxindustries.lib.upgrades.UpgradeIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean renderIcon(GuiGraphicsExtractor graphics, UpgradeIcon icon, int x, int y)
    {
        return INSTANCE.renderInternal(graphics, x, y, icon);
    }

    private final Map<UpgradeIcon.Type, IconRenderer<?>> renderers = new Object2ObjectOpenHashMap<>();

    private UpgradeIconRenderers()
    {
        this.<UpgradeIcon.NoRenderIcon>registerRenderer(UpgradeIcon.Type.NO_RENDER, (_, _, _, _) -> false);
        this.<UpgradeIcon.SpriteSheetIcon>registerRenderer(UpgradeIcon.Type.UPGRADE_SPRITE, (graphics, x, y, icon) -> renderSpriteIcon(graphics, icon.location(), x, y, 16, 16));
        this.registerRenderer(UpgradeIcon.Type.ITEM_STACK, UpgradeIconRenderers::renderItemStackIcon);
        this.registerRenderer(UpgradeIcon.Type.SPRITE_OVERLAY, UpgradeIconRenderers::renderOverlayIcon);
    }

    private <T extends UpgradeIcon> void registerRenderer(UpgradeIcon.Type type, IconRenderer<T> iconRenderer)
    {
        renderers.put(type, iconRenderer);
    }

    @SuppressWarnings("unchecked")
    private <T extends UpgradeIcon> boolean renderInternal(GuiGraphicsExtractor graphics, int x, int y, UpgradeIcon uncheckedIcon)
    {
        IconRenderer<T> renderer = (IconRenderer<T>) renderers.get(uncheckedIcon.getType());
        return renderer.render(graphics, x, y, (T) uncheckedIcon);
    }

    private static boolean renderSpriteIcon(GuiGraphicsExtractor graphics, Identifier location, int x, int y, int width, int height)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(LTXIAtlasIds.UPGRADE_ICONS_ID).getSprite(location);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, -1);
        return true;
    }

    private static boolean renderItemStackIcon(GuiGraphicsExtractor graphics, int x, int y, UpgradeIcon.ItemStackIcon icon)
    {
        if (icon.template().item().value() instanceof ItemGuiRenderOverride) return false;

        graphics.fakeItem(icon.template().create(), x, y);
        return true;
    }

    private static boolean renderOverlayIcon(GuiGraphicsExtractor graphics, int x, int y, UpgradeIcon.SpriteOverlayIcon icon)
    {
        if (!renderIcon(graphics, icon.background(), x, y)) return false;

        int ix = x + icon.xOffset();
        int iy = y + icon.yOffset();

        return renderSpriteIcon(graphics, icon.overlay(), ix, iy, icon.width(), icon.height());
    }

    @FunctionalInterface
    private interface IconRenderer<T extends UpgradeIcon>
    {
        boolean render(GuiGraphicsExtractor graphics, int x, int y, T icon);
    }
}