package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.limatech.LimaTech;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public abstract class ShortVerticalProgressWidget extends FillBarWidget.VerticalBar
{
    private static final ResourceLocation BG_SPRITE = LimaTech.RESOURCES.location("short_vertical_progress_bg");
    private static final ResourceLocation FG_SPRITE = LimaTech.RESOURCES.location("short_vertical_progress");

    private final TextureAtlasSprite foregroundSprite;

    protected ShortVerticalProgressWidget(int x, int y)
    {
        super(x, y, 5, 18, 3, 16, LimaWidgetSprites.sprite(BG_SPRITE));
        this.foregroundSprite = LimaWidgetSprites.sprite(FG_SPRITE);
    }

    @Override
    protected TextureAtlasSprite getForegroundSprite(float fillPercentage)
    {
        return foregroundSprite;
    }
}