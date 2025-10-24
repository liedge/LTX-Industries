package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class ScrollGridRenderable<T> extends BaseGridRenderable<T> implements ScrollableGUIElement
{
    private final int scrollRows;
    private final int scrollDelta;
    private int currentScrollRow;

    protected ScrollGridRenderable(List<T> elements, int x, int y, int elementWidth, int elementHeight, int gridWidth, int gridHeight, int scrollRange)
    {
        super(elements, x, y, elementWidth, elementHeight, gridWidth, gridHeight);
        this.scrollRows = LimaCollectionsUtil.splitCollectionToSegments(elements, gridWidth);
        this.scrollDelta = canScroll() ? (scrollRange - ScrollbarWidget.SCROLLER_HEIGHT) / scrollRows : 1;
    }

    protected void scrollRowChanged(int newScrollRow)
    {
        currentScrollRow = newScrollRow;
    }

    @Override
    protected int elementsStart()
    {
        return currentScrollRow * gridWidth;
    }

    @Override
    public int getScrollDelta()
    {
        return scrollDelta;
    }

    @Override
    public boolean canScroll()
    {
        return scrollRows > gridHeight;
    }

    @Override
    public void scrollUpdated(int scrollPosition)
    {
        int newScrollRow = Mth.clamp(scrollPosition / scrollDelta, 0, scrollRows - 1);
        if (newScrollRow != currentScrollRow) scrollRowChanged(newScrollRow);
    }
}