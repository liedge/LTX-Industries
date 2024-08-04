package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;
import liedge.limatech.blockentity.ProgressMachine;
import liedge.limatech.client.LimaTechLangKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MachineProgressWidget extends VariableBarWidget.HorizontalBar
{
    private final ProgressMachine machine;

    public MachineProgressWidget(ProgressMachine machine, int x, int y)
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
        return machine.getProgressAsPercent();
    }

    @Override
    public List<Component> getTooltipLines()
    {
        float fill = fillPercent();
        if (fill > 0)
        {
            int fillTooltip = (int) (fill * 100f);
            return List.of(LimaTechLangKeys.CRAFTING_PROGRESS_TOOLTIP.translateArgs(fillTooltip).withStyle(ChatFormatting.GRAY));
        }
        else
        {
            return List.of();
        }
    }
}