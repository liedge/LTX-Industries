package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;
import liedge.limatech.blockentity.TimedProcessMachineBlockEntity;

public class MachineProgressWidget extends VariableBarWidget.HorizontalBar
{
    private final TimedProcessMachineBlockEntity machine;

    public MachineProgressWidget(TimedProcessMachineBlockEntity machine, int x, int y)
    {
        super(x, y);
        this.machine = machine;
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
        return machine.getProcessTimePercent();
    }
}