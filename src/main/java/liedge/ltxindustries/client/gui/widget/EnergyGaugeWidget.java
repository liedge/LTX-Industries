package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.energy.VariableEnergyHandler;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.resources.Identifier;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;

public class EnergyGaugeWidget extends FillBarWidget.VerticalBar
{
    private static final Identifier BG_SPRITE = LTXIndustries.RESOURCES.id("widget/energy_bar_background");
    private static final Identifier FG_SPRITE_NORMAL = LTXIndustries.RESOURCES.id("widget/energy_bar_fill");
    private static final Identifier FG_SPRITE_OVERCHARGE = LTXIndustries.RESOURCES.id("widget/energy_bar_overcharge_fill");

    private final VariableEnergyHandler energy;

    public EnergyGaugeWidget(VariableEnergyHandler energy, int x, int y)
    {
        super(x, y, 12, 40, 10, 38);
        this.energy = energy;
    }

    public EnergyGaugeWidget(EnergyHolderBlockEntity blockEntity, int x, int y)
    {
        this(blockEntity.getEnergy(), x, y);
    }

    @Override
    protected float getFillPercentage()
    {
        return LimaEnergyUtil.getFillPercentage(energy);
    }

    @Override
    protected Identifier getBackgroundSprite()
    {
        return BG_SPRITE;
    }

    @Override
    protected Identifier getForegroundSprite(float fillPercentage)
    {
        return fillPercentage > 1f ? FG_SPRITE_OVERCHARGE : FG_SPRITE_NORMAL;
    }

    @Override
    public boolean hasTooltip()
    {
        return true;
    }

    @Override
    public void createWidgetTooltip(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendStorageEnergyTooltip(consumer, energy.getAmountAsInt(), energy.getCapacityAsInt(), energy.getTransferRate());
        if (getFillPercentage() > 1) consumer.accept(LTXILangKeys.ENERGY_OVERCHARGE_TOOLTIP.translate().withStyle(HOSTILE_ORANGE.chatStyle()));
    }
}