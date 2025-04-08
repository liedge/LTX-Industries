package liedge.limatech.client.gui;

import liedge.limatech.menu.tooltip.ItemGridTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
    protected void renderGridElement(ItemStack element, Font font, int rx, int ry, GuiGraphics graphics)
    {
        int x = rx + 1;
        int y = ry + 1;

        graphics.renderItem(element, x, y);
        if (renderDecorations) graphics.renderItemDecorations(font, element, x, y);
    }
}