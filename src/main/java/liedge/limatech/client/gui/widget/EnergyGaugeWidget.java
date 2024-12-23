package liedge.limatech.client.gui.widget;

import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;
import liedge.limatech.util.LimaTechTooltipUtil;

public class EnergyGaugeWidget extends VariableBarWidget.VerticalBar
{
    private final LimaEnergyStorage energyStorage;

    public EnergyGaugeWidget(LimaEnergyStorage energyStorage, int x, int y)
    {
        super(x, y);
        this.energyStorage = energyStorage;
    }

    @Override
    protected UnmanagedSprite backgroundSprite()
    {
        return ScreenWidgetSprites.ENERGY_GAUGE_BACKGROUND;
    }

    @Override
    protected UnmanagedSprite foregroundSprite()
    {
        return ScreenWidgetSprites.ENERGY_GAUGE_FOREGROUND;
    }

    @Override
    protected float fillPercent()
    {
        return LimaEnergyUtil.getFillPercentage(energyStorage);
    }

    @Override
    public boolean hasTooltip()
    {
        return true;
    }

    @Override
    public void createWidgetTooltip(TooltipLineConsumer consumer)
    {
        LimaTechTooltipUtil.appendExtendedEnergyTooltip(consumer, energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored(), energyStorage.getTransferRate());
    }
}