package liedge.ltxindustries.client.gui;

import liedge.ltxindustries.menu.tooltip.ItemGridTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public final class ClientItemGridTooltip extends ClientGridTooltip<ItemStack>
{
    private final boolean renderDecorations;

    public ClientItemGridTooltip(ItemGridTooltip tooltip)
    {
        super(tooltip);
        this.renderDecorations = tooltip.renderDecorations();
    }

    @Override
    protected int elementSize()
    {
        return 18;
    }

    @Override
    protected void renderGridElement(ItemStack element, Font font, int rx, int ry, GuiGraphicsExtractor graphics)
    {
        int x = rx + 1;
        int y = ry + 1;

        graphics.item(element, x, y);
        if (renderDecorations) graphics.itemDecorations(font, element, x, y);
    }
}