package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.LimaRenderable;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.gui.screen.LTXIScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.List;

public interface GridGUIElement<T> extends LimaRenderable
{
    List<T> getElements();

    int elementWidth();

    int elementHeight();

    default int elementStart()
    {
        return 0;
    }

    default int elementEnd()
    {
        return Math.min(elementStart() + gridSize(), getElements().size());
    }

    int gridWidth();

    int gridHeight();

    default int gridSize()
    {
        return gridWidth() * gridHeight();
    }

    default boolean isValidGridIndex(int gridIndex)
    {
        return gridIndex >= 0 && gridIndex < gridSize();
    }

    default boolean isValidElementIndex(int elementIndex)
    {
        return elementIndex >= elementStart() && elementIndex < elementEnd();
    }

    default int getGridIndexAt(double mouseX, double mouseY)
    {
        int x = (int) LimaCoreMath.divideDouble(mouseX - getX(), elementWidth());
        int y = (int) LimaCoreMath.divideDouble(mouseY - getY(), elementHeight());

        return y * gridWidth() + x;
    }

    default boolean isMouseOverElement(double mouseX, double mouseY, int elementX0, int elementY0)
    {
        return LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, elementX0, elementY0, elementWidth(), elementHeight());
    }

    void renderElement(GuiGraphicsExtractor graphics, T element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY);

    void extractElementTooltip(TooltipLineConsumer consumer, T element, int mouseX, int mouseY, int gridIndex, int elementIndex);

    void onElementClicked(T element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex);

    @Override
    default void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        int x = getX();
        int y = getY();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LTXIScreen.DARK_PANEL, x - 1, y - 1, getWidth() + 2, getHeight() + 2);

        if (getElements().isEmpty()) return;

        int start = elementStart();
        for (int i = start; i < elementEnd(); i++)
        {
            int gridIndex = i - start;
            int posX = x + (gridIndex % gridWidth()) * elementWidth();
            int posY = y + (gridIndex / gridWidth()) * elementHeight();
            T element = getElements().get(i);

            renderElement(graphics, element, posX, posY, gridIndex, i, mouseX, mouseY);
        }
    }

    @Override
    default void extractTooltip(TooltipLineConsumer consumer, int mouseX, int mouseY)
    {
        List<T> elements = getElements();
        if (elements.isEmpty()) return;

        int gridIndex = getGridIndexAt(mouseX, mouseY);
        int elementIndex = elementStart() + gridIndex;

        if (isValidGridIndex(gridIndex) && isValidElementIndex(elementIndex))
        {
            T element = elements.get(elementIndex);
            extractElementTooltip(consumer, element, mouseX, mouseY, gridIndex, elementIndex);
        }
    }

    default boolean onGridClicked(double mouseX, double mouseY, int button)
    {
        if (!isMouseOver(mouseX, mouseY)) return false;
        else if (getElements().isEmpty()) return true;

        int gridIndex = getGridIndexAt(mouseX, mouseY);
        int elementIndex = elementStart() + gridIndex;

        if (isValidGridIndex(gridIndex) && isValidElementIndex(elementIndex))
        {
            T element = getElements().get(elementIndex);
            onElementClicked(element, mouseX, mouseY, button, gridIndex, elementIndex);
        }

        return true;
    }
}