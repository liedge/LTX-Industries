package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.TimedProcessMachineBlockEntity;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class MachineProgressWidget extends FillBarWidget.HorizontalBar
{
    private static final ResourceLocation BG_SPRITE = LTXIndustries.RESOURCES.location("crafting_progress_bg");
    private static final ResourceLocation FG_SPRITE = LTXIndustries.RESOURCES.location("crafting_progress");

    private final TimedProcessMachineBlockEntity machine;
    private final TextureAtlasSprite foregroundSprite;

    public MachineProgressWidget(TimedProcessMachineBlockEntity machine, int x, int y)
    {
        super(x, y, 24, 6, 22, 4, LTXIWidgetSprites.sprite(BG_SPRITE));
        this.machine = machine;
        this.foregroundSprite = LTXIWidgetSprites.sprite(FG_SPRITE);
    }

    @Override
    protected float getFillPercentage()
    {
        return machine.getProcessTimePercent();
    }

    @Override
    protected TextureAtlasSprite getForegroundSprite(float fillPercentage)
    {
        return foregroundSprite;
    }
}