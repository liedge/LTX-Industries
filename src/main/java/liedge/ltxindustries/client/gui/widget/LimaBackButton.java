package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import static liedge.ltxindustries.client.LTXILangKeys.BACK_BUTTON_LABEL;

public class LimaBackButton extends LimaRenderableButton
{
    private static final ResourceLocation SPRITE = LTXIndustries.RESOURCES.location("back_button");
    private static final ResourceLocation SPRITE_FOCUS = LTXIndustries.RESOURCES.location("back_button_focus");


    private final LimaMenuScreen<?> parent;
    private final int buttonId;
    private final TextureAtlasSprite sprite;
    private final TextureAtlasSprite spriteFocus;

    public LimaBackButton(int x, int y, LimaMenuScreen<?> parent, int buttonId)
    {
        super(x, y, 12, 12, BACK_BUTTON_LABEL.translate());
        this.parent = parent;
        this.buttonId = buttonId;
        this.sprite = LTXIWidgetSprites.sprite(SPRITE);
        this.spriteFocus = LTXIWidgetSprites.sprite(SPRITE_FOCUS);
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(buttonId);
    }

    @Override
    protected TextureAtlasSprite unfocusedSprite()
    {
        return sprite;
    }

    @Override
    protected TextureAtlasSprite focusedSprite()
    {
        return spriteFocus;
    }
}