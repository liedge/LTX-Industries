package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.util.Mth;

public abstract class BaseScrollGridRenderable<T> extends BaseGridRenderable<T> implements ScrollableGUIElement
{
    private int scrollRows;
    private int scrollDelta = 1;
    private int currentScrollRow;

    protected BaseScrollGridRenderable(int x, int y, int elementWidth, int elementHeight, int gridWidth, int gridHeight)
    {
        super(x, y, elementWidth, elementHeight, gridWidth, gridHeight);
    }

    public void reset()
    {
        this.scrollRows = LimaCollectionsUtil.splitCollectionToSegments(getElements(), gridWidth());
        this.scrollDelta = canScroll() ? (getHeight() - ScrollbarWidget.SCROLLER_HEIGHT) / scrollRows : 1;
    }

    @Override
    public int elementStart()
    {
        return currentScrollRow * gridWidth();
    }

    @Override
    public int getScrollDelta()
    {
        return scrollDelta;
    }

    @Override
    public boolean canScroll()
    {
        return scrollRows > gridHeight();
    }

    @Override
    public void scrollUpdated(int scrollPosition)
    {
        int newScrollRow = Mth.clamp(scrollPosition / scrollDelta, 0, Math.max(0, scrollRows - 1));
        if (newScrollRow != currentScrollRow) onScrollRowChanged(newScrollRow);
    }

    protected void onScrollRowChanged(int newScrollRow)
    {
        this.currentScrollRow = newScrollRow;
    }
}