package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;

public class EnergyGaugeWidget extends FillBarWidget.VerticalBar
{
    private static final ResourceLocation BG_SPRITE = LTXIndustries.RESOURCES.location("energy_gauge_bg");
    private static final ResourceLocation FG_SPRITE_NORMAL = LTXIndustries.RESOURCES.location("energy_gauge_normal");
    private static final ResourceLocation FG_SPRITE_OVERCHARGE = LTXIndustries.RESOURCES.location("energy_gauge_overcharge");

    private final LimaEnergyStorage energyStorage;
    private final TextureAtlasSprite foregroundSprite;
    private final TextureAtlasSprite overchargedForegroundSprite;

    public EnergyGaugeWidget(LimaEnergyStorage energyStorage, int x, int y)
    {
        super(x, y, 10, 48, 8, 46, LTXIWidgetSprites.sprite(BG_SPRITE));
        this.energyStorage = energyStorage;
        this.foregroundSprite = LTXIWidgetSprites.sprite(FG_SPRITE_NORMAL);
        this.overchargedForegroundSprite = LTXIWidgetSprites.sprite(FG_SPRITE_OVERCHARGE);
    }

    @Override
    protected float getFillPercentage()
    {
        return LimaEnergyUtil.getFillPercentage(energyStorage);
    }

    @Override
    protected TextureAtlasSprite getForegroundSprite(float fillPercentage)
    {
        return fillPercentage > 1f ? overchargedForegroundSprite : foregroundSprite;
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