package liedge.ltxindustries.client.gui;

import liedge.ltxindustries.menu.tooltip.GridTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public abstract class ClientGridTooltip<T> implements ClientTooltipComponent
{
    private final List<T> elements;
    private final int maxColumns;
    private final int maxElements;
    private final int width;
    private final int height;

    protected ClientGridTooltip(GridTooltip<T> tooltip)
    {
        this.elements = tooltip.gridElements();
        this.maxColumns = tooltip.maxColumns();
        this.maxElements = Math.min(elements.size(), maxColumns * tooltip.maxRows());
        int rows = (int) Math.ceil(elements.size() / (double) maxColumns);
        this.width = Math.min(maxColumns, elements.size()) * elementSize();
        this.height = Math.min(rows, tooltip.maxRows()) * elementSize();
    }

    protected abstract int elementSize();

    protected abstract void renderGridElement(T element, Font font, int rx, int ry, GuiGraphics graphics);

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getWidth(Font font)
    {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics)
    {
        for (int i = 0; i < maxElements; i++)
        {
            int rx = x + (i % maxColumns) * elementSize();
            int ry = y + (i / maxColumns) * elementSize();
            renderGridElement(elements.get(i), font, rx, ry, graphics);
        }
    }
}