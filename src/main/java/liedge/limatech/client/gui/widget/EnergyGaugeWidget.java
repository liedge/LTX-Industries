package liedge.limatech.client.gui.widget;

import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.util.LimaTechTooltipUtil;

import static liedge.limatech.LimaTechConstants.HOSTILE_ORANGE;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.*;

public class EnergyGaugeWidget extends FillBarWidget.VerticalBar
{
    private final LimaEnergyStorage energyStorage;

    public EnergyGaugeWidget(LimaEnergyStorage energyStorage, int x, int y)
    {
        super(x, y, ENERGY_GAUGE_BACKGROUND);
        this.energyStorage = energyStorage;
    }

    @Override
    protected float getFillPercentage()
    {
        return LimaEnergyUtil.getFillPercentage(energyStorage);
    }

    @Override
    protected UnmanagedSprite getForegroundSprite(float fillPercentage)
    {
        return fillPercentage > 1f ? ENERGY_GAUGE_OVERCHARGED_FOREGROUND : ENERGY_GAUGE_FOREGROUND;
    }

    @Override
    public boolean hasTooltip()
    {
        return true;
    }

    @Override
    public void createWidgetTooltip(TooltipLineConsumer consumer)
    {
        LimaTechTooltipUtil.appendStorageEnergyTooltip(consumer, energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored(), energyStorage.getTransferRate());
        if (getFillPercentage() > 1) consumer.accept(LimaTechLang.ENERGY_OVERCHARGE_TOOLTIP.translate().withStyle(HOSTILE_ORANGE.chatStyle()));
    }
}