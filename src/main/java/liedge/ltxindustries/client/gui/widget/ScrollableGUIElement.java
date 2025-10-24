package liedge.ltxindustries.client.gui.widget;

public interface ScrollableGUIElement
{
    int getScrollDelta();

    boolean canScroll();

    void scrollUpdated(int scrollPosition);
}