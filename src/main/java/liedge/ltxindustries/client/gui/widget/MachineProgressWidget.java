package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.VariableTimedProcessBlockEntity;
import net.minecraft.resources.Identifier;

public class MachineProgressWidget extends FillBarWidget.HorizontalBar
{
    public static final Identifier BACKGROUND_SPRITE = LTXIndustries.RESOURCES.id("widget/machine_progress_background");
    public static final Identifier FILL_SPRITE = LTXIndustries.RESOURCES.id("widget/machine_progress_fill");
    public static final int BACKGROUND_WIDTH = 24;
    public static final int BACKGROUND_HEIGHT = 6;
    public static final int FILL_WIDTH = 22;
    public static final int FILL_HEIGHT = 4;

    protected final VariableTimedProcessBlockEntity blockEntity;

    public MachineProgressWidget(VariableTimedProcessBlockEntity blockEntity, int x, int y)
    {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, FILL_WIDTH, FILL_HEIGHT);
        this.blockEntity = blockEntity;
    }

    @Override
    protected float getFillPercentage()
    {
        return blockEntity.getProcessTimePercent();
    }

    @Override
    protected Identifier getBackgroundSprite()
    {
        return BACKGROUND_SPRITE;
    }

    @Override
    protected Identifier getForegroundSprite(float fillPercentage)
    {
        return FILL_SPRITE;
    }
}