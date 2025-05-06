package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.BaseFabricatorBlockEntity;
import liedge.limatech.client.LimaTechLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class FabricatorProgressWidget extends FillBarWidget.VerticalBar
{
    private static final ResourceLocation BG_SPRITE = LimaTech.RESOURCES.location("fabricator_progress_bg");
    private static final ResourceLocation FG_SPRITE = LimaTech.RESOURCES.location("fabricator_progress");

    private final BaseFabricatorBlockEntity blockEntity;
    private final TextureAtlasSprite foregroundSprite;

    public FabricatorProgressWidget(int x, int y, BaseFabricatorBlockEntity blockEntity)
    {
        super(x, y, 5, 22, 3, 20, LimaWidgetSprites.sprite(BG_SPRITE));
        this.blockEntity = blockEntity;
        this.foregroundSprite = LimaWidgetSprites.sprite(FG_SPRITE);
    }

    @Override
    protected float getFillPercentage()
    {
        int recipeEnergy = blockEntity.getRecipeCheck().getLastUsedRecipe(Minecraft.getInstance().level).map(holder -> holder.value().getEnergyRequired()).orElse(0);
        return LimaMathUtil.divideFloat(blockEntity.getEnergyCraftProgress(), recipeEnergy);
    }

    @Override
    protected TextureAtlasSprite getForegroundSprite(float fillPercentage)
    {
        return foregroundSprite;
    }

    @Override
    public boolean hasTooltip()
    {
        return blockEntity.isCrafting();
    }

    @Override
    public void createWidgetTooltip(TooltipLineConsumer consumer)
    {
        int fill = (int) (getFillPercentage() * 100f);
        consumer.accept(LimaTechLang.CRAFTING_PROGRESS_TOOLTIP.translateArgs(fill).withStyle(ChatFormatting.GRAY));
    }
}