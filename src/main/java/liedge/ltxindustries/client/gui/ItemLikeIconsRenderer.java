package liedge.ltxindustries.client.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.ltxindustries.client.LTXIAtlasIds;
import liedge.ltxindustries.lib.icon.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class ItemLikeIconsRenderer
{
    public static int render(GuiGraphicsExtractor graphics, ItemLikeIcon icon, int x, int y)
    {
        return INSTANCE.renderInternal(graphics, icon, x, y);
    }

    private static final ItemLikeIconsRenderer INSTANCE = new ItemLikeIconsRenderer();

    private final Map<ItemLikeIconType, Renderer<?>> renderers = new  Object2ObjectOpenHashMap<>();

    private ItemLikeIconsRenderer()
    {
        this.<EmptyIcon>register(ItemLikeIconType.NONE, (_, _, _, _) -> 0);
        this.register(ItemLikeIconType.SPRITE, ItemLikeIconsRenderer::renderSprite);
        this.register(ItemLikeIconType.ITEM, ItemLikeIconsRenderer::renderItem);
        this.register(ItemLikeIconType.TEXT, ItemLikeIconsRenderer::renderText);
        this.register(ItemLikeIconType.COMPOSITE, ItemLikeIconsRenderer::renderComposite);
    }

    private <T extends ItemLikeIcon> void register(ItemLikeIconType type, Renderer<T> renderer)
    {
        renderers.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    private <T extends ItemLikeIcon> int renderInternal(GuiGraphicsExtractor graphics, ItemLikeIcon icon, int x, int y)
    {
        Renderer<T> renderer = (Renderer<T>) renderers.get(icon.getType());
        return renderer.render(graphics, x, y, (T) icon);
    }

    private static int renderSprite(GuiGraphicsExtractor graphics, int x, int y, SpriteIcon icon)
    {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(LTXIAtlasIds.MODULAR_ICONS_ID);
        TextureAtlasSprite sprite = atlas.getSprite(icon.id());

        if (sprite.equals(atlas.missingSprite())) return 0;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + icon.xPos(), y + icon.yPos(), icon.width(), icon.height(), -1);
        return 1;
    }

    private static int renderItem(GuiGraphicsExtractor graphics, int x, int y, ItemIcon icon)
    {
        ItemStack renderStack = icon.getRenderStack();
        if (renderStack.getItem() instanceof ItemGuiRenderOverride) return 0;

        graphics.fakeItem(renderStack, x, y);
        return 1;
    }

    private static int renderText(GuiGraphicsExtractor graphics, int x, int y, TextIcon icon)
    {
        Font font = Minecraft.getInstance().font;
        Component text = Component.literal(icon.text()).withStyle(icon.style());

        graphics.text(font, text, x + 17 - font.width(text), y + 9, -1, icon.dropShadow());
        return 1;
    }

    private static int renderComposite(GuiGraphicsExtractor graphics, int x, int y, CompositeIcon icon)
    {
        return render(graphics, icon.background(), x, y) + render(graphics, icon.foreground(), x, y);
    }

    @FunctionalInterface
    private interface Renderer<T extends ItemLikeIcon>
    {
        int render(GuiGraphicsExtractor graphics, int x, int y, T icon);
    }
}