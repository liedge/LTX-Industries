package liedge.ltxindustries.client.gui;

import liedge.ltxindustries.menu.tooltip.GridTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public abstract class SquareElementGridTooltip<E, GT extends GridTooltip> implements ClientTooltipComponent
{
    private final List<E> elements;
    private final int elementSize;
    private final int columnsPerRow;
    private final int maxIndex;

    private final int width;
    private final int height;

    protected SquareElementGridTooltip(GT data, List<E> elements, int elementSize)
    {
        this.elements = elements;
        this.elementSize = elementSize;

        this.columnsPerRow = data.columnsPerRow();
        this.maxIndex = Math.min(elements.size(), columnsPerRow * data.maxRows());

        int rows = (int) Math.ceil(elements.size() / (double) columnsPerRow);
        this.width = Math.min(columnsPerRow, maxIndex) * elementSize;
        this.height = Math.min(rows, data.maxRows()) * elementSize;
    }

    protected abstract void extractElementImage(GuiGraphicsExtractor graphics, Font font, E element, int index, int x, int y);

    @Override
    public int getWidth(Font font)
    {
        return width;
    }

    @Override
    public int getHeight(Font font)
    {
        return height;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics)
    {
        for (int index = 0; index < maxIndex; index++)
        {
            int elementX = x + (index % columnsPerRow) * elementSize;
            int elementY = y + (index / columnsPerRow) * elementSize;

            extractElementImage(graphics, font, elements.get(index), index, elementX, elementY);
        }
    }
}