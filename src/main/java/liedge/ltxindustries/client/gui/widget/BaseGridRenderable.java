package liedge.ltxindustries.client.gui.widget;

import com.google.common.base.Preconditions;
import liedge.limacore.client.gui.BaseLimaRenderable;

import java.util.List;

public abstract class BaseGridRenderable<T> extends BaseLimaRenderable implements GridGUIElement<T>
{
    private final int elementWidth;
    private final int elementHeight;
    private final int gridWidth;
    private final int gridHeight;
    private final int gridSize;

    protected BaseGridRenderable(int x, int y, int elementWidth, int elementHeight, int gridWidth, int gridHeight)
    {
        super(x, y, elementWidth * gridWidth, elementHeight * gridHeight);
        Preconditions.checkArgument(elementWidth > 0 && elementHeight > 0, "Cannot have elements width or height below 1.");
        Preconditions.checkArgument(gridWidth > 0 && gridHeight > 0, "Cannot have grid width or height below 1.");
        this.elementWidth = elementWidth;
        this.elementHeight = elementHeight;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridSize = gridWidth * gridHeight;
    }

    @Override
    public int elementWidth()
    {
        return elementWidth;
    }

    @Override
    public int elementHeight()
    {
        return elementHeight;
    }

    @Override
    public int gridWidth()
    {
        return gridWidth;
    }

    @Override
    public int gridHeight()
    {
        return gridHeight;
    }

    @Override
    public int gridSize()
    {
        return gridSize;
    }

    public static abstract class FixedElements<T> extends BaseGridRenderable<T>
    {
        private final List<T> elements;

        protected FixedElements(int x, int y, int elementWidth, int elementHeight, int gridWidth, int gridHeight, List<T> elements)
        {
            super(x, y, elementWidth, elementHeight, gridWidth, gridHeight);
            this.elements = elements;
        }

        @Override
        public List<T> getElements()
        {
            return elements;
        }
    }
}