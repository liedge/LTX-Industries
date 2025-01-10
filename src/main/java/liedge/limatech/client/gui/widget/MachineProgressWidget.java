package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limatech.blockentity.TimedProcessMachineBlockEntity;

import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.MACHINE_PROGRESS_BACKGROUND;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.MACHINE_PROGRESS_FOREGROUND;

public class MachineProgressWidget extends FillBarWidget.HorizontalBar
{
    private final TimedProcessMachineBlockEntity machine;

    public MachineProgressWidget(TimedProcessMachineBlockEntity machine, int x, int y)
    {
        super(x, y, MACHINE_PROGRESS_BACKGROUND);
        this.machine = machine;
    }

    @Override
    protected float getFillPercentage()
    {
        return machine.getProcessTimePercent();
    }

    @Override
    protected UnmanagedSprite getForegroundSprite(float fillPercentage)
    {
        return MACHINE_PROGRESS_FOREGROUND;
    }
}