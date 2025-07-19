package liedge.ltxindustries.client.gui.widget;

public interface ScrollableGUIElement
{
    boolean canScroll();

    void scrollUpdated(int scrollPosition);
}