package liedge.limatech.client.gui.widget;

public interface ScrollableGUIElement
{
    boolean canScroll();

    void scrollUpdated(int scrollPosition);
}