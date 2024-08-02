package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;

public class MachineProgressWidget extends VariableBarWidget.HorizontalBar
{
    public MachineProgressWidget(int x, int y)
    {
        super(x, y);
    }

    @Override
    protected UnmanagedSprite backgroundSprite()
    {
        return ScreenWidgetSprites.MACHINE_PROGRESS_BACKGROUND;
    }

    @Override
    protected UnmanagedSprite foregroundSprite()
    {
        return ScreenWidgetSprites.MACHINE_PROGRESS_FOREGROUND;
    }

    @Override
    protected float fillPercent()
    {
        return 0;
    }
}