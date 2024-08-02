package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;
import liedge.limacore.lib.energy.EnergyStorageHolder;
import liedge.limacore.lib.energy.LimaEnergyStorage;
import liedge.limacore.lib.energy.LimaEnergyUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLangKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EnergyGaugeWidget extends VariableBarWidget.VerticalBar
{
    private final LimaEnergyStorage energyStorage;

    public EnergyGaugeWidget(EnergyStorageHolder supplier, int x, int y)
    {
        super(x, y);
        this.energyStorage = supplier.getEnergyStorage();
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
    public List<Component> getTooltipLines()
    {
        return List.of(
                LimaTechLangKeys.ENERGY_TOOLTIP.translate().withStyle(LimaTechConstants.REM_BLUE::applyStyle),
                Component.literal(LimaEnergyUtil.formatStoredAndTotal(energyStorage)).withStyle(ChatFormatting.GRAY));
    }
}