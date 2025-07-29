package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.resources.ResourceLocation;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;

public class EnergyGaugeWidget extends FillBarWidget.VerticalBar
{
    private static final ResourceLocation BG_SPRITE = LTXIndustries.RESOURCES.location("widget/energy_bar_background");
    private static final ResourceLocation FG_SPRITE_NORMAL = LTXIndustries.RESOURCES.location("widget/energy_bar_fill");
    private static final ResourceLocation FG_SPRITE_OVERCHARGE = LTXIndustries.RESOURCES.location("widget/energy_bar_overcharge_fill");

    private final LimaEnergyStorage energyStorage;

    public EnergyGaugeWidget(LimaEnergyStorage energyStorage, int x, int y)
    {
        super(x, y, 12, 40, 10, 38);
        this.energyStorage = energyStorage;
    }

    public EnergyGaugeWidget(EnergyHolderBlockEntity blockEntity, int x, int y)
    {
        this(blockEntity.getEnergyStorage(), x, y);
    }

    @Override
    protected float getFillPercentage()
    {
        return LimaEnergyUtil.getFillPercentage(energyStorage);
    }

    @Override
    protected ResourceLocation getBackgroundSprite()
    {
        return BG_SPRITE;
    }

    @Override
    protected ResourceLocation getForegroundSprite(float fillPercentage)
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
        LTXITooltipUtil.appendStorageEnergyTooltip(consumer, energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored(), energyStorage.getTransferRate());
        if (getFillPercentage() > 1) consumer.accept(LTXILangKeys.ENERGY_OVERCHARGE_TOOLTIP.translate().withStyle(HOSTILE_ORANGE.chatStyle()));
    }
}