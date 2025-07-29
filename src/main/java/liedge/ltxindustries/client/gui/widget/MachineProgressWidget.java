package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.TimedProcessMachineBlockEntity;
import net.minecraft.resources.ResourceLocation;

public class MachineProgressWidget extends FillBarWidget.HorizontalBar
{
    public static final ResourceLocation BACKGROUND_SPRITE = LTXIndustries.RESOURCES.location("widget/machine_progress_background");
    public static final ResourceLocation FILL_SPRITE = LTXIndustries.RESOURCES.location("widget/machine_progress_fill");
    public static final int BACKGROUND_WIDTH = 24;
    public static final int BACKGROUND_HEIGHT = 6;
    public static final int FILL_WIDTH = 22;
    public static final int FILL_HEIGHT = 4;

    protected final TimedProcessMachineBlockEntity blockEntity;

    public MachineProgressWidget(TimedProcessMachineBlockEntity blockEntity, int x, int y)
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
    protected ResourceLocation getBackgroundSprite()
    {
        return BACKGROUND_SPRITE;
    }

    @Override
    protected ResourceLocation getForegroundSprite(float fillPercentage)
    {
        return FILL_SPRITE;
    }
}