package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.BaseLimaRenderable;
import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public abstract class BaseGridRenderable<T> extends BaseLimaRenderable
{
    final List<T> elements;
    final int elementWidth;
    final int elementHeight;
    final int gridWidth;
    final int gridHeight;
    final int gridSize;

    protected BaseGridRenderable(List<T> elements, int x, int y, int elementWidth, int elementHeight, int gridWidth, int gridHeight)
    {
        super(x, y, elementWidth * gridWidth, elementHeight * gridHeight);
        this.elements = elements;
        this.elementWidth = elementWidth;
        this.elementHeight = elementHeight;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridSize = gridWidth * gridHeight;
    }

    protected int elementsStart()
    {
        return 0;
    }

    protected int elementsEnd()
    {
        return Math.min(elementsStart() + gridSize, elements.size());
    }

    public boolean isValidGridIndex(int gridIndex)
    {
        return gridIndex >= 0 && gridIndex < gridSize;
    }

    public boolean isValidElementIndex(int elementIndex)
    {
        return elementIndex >= elementsStart() && elementIndex < elementsEnd();
    }

    public int getGridIndexAt(double mouseX, double mouseY)
    {
        int x = (int) LimaCoreMath.divideDouble(mouseX - getX(), elementWidth);
        int y = (int) LimaCoreMath.divideDouble(mouseY - getY(), elementHeight);

        return y * gridWidth + x;
    }

    protected abstract void renderElement(GuiGraphics graphics, T element, int posX, int posY, int gridIndex, int mouseX, int mouseY, float partialTick);

    protected abstract void renderElementTooltip(GuiGraphics graphics, T element, int mouseX, int mouseY, int gridIndex, int elementIndex);

    protected abstract void onElementClicked(T element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex);

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int start = elementsStart();
        int end = elementsEnd();

        for (int i = start; i < end; i++)
        {
            int gridIndex = i - start;
            int posX = getX() + (gridIndex % gridWidth) * elementWidth;
            int posY = getY() + (gridIndex / gridWidth) * elementHeight;
            T element = elements.get(i);

            renderElement(graphics, element, posX, posY, gridIndex, mouseX, mouseY, partialTick);
        }
    }

    public void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (!isMouseOver(mouseX, mouseY)) return;

        int gridIndex = getGridIndexAt(mouseX, mouseY);
        int elementIndex = elementsStart() + gridIndex;

        if (isValidGridIndex(gridIndex) && isValidElementIndex(elementIndex))
        {
            T element = elements.get(elementIndex);
            renderElementTooltip(graphics, element, mouseX, mouseY, gridIndex, elementIndex);
        }
    }

    public boolean clickGrid(double mouseX, double mouseY, int button)
    {
        if (!isMouseOver(mouseX, mouseY)) return false;

        int gridIndex = getGridIndexAt(mouseX, mouseY);
        int elementIndex = elementsStart() + gridIndex;

        if (isValidGridIndex(gridIndex) && isValidElementIndex(elementIndex))
        {
            T element = elements.get(elementIndex);
            onElementClicked(element, mouseX, mouseY, button, gridIndex, elementIndex);
        }

        return true;
    }
}