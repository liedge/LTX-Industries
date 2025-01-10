package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import net.minecraft.client.gui.components.Tooltip;

import static liedge.limatech.client.LimaTechLang.BACK_BUTTON_LABEL;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.BACK_BUTTON_FOCUSED;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.BACK_BUTTON_NOT_FOCUSED;

public class LimaBackButton extends LimaRenderableButton
{
    private final LimaMenuScreen<?> parent;
    private final int buttonId;

    public LimaBackButton(int x, int y, LimaMenuScreen<?> parent, int buttonId)
    {
        super(x, y, 12, 12, BACK_BUTTON_LABEL.translate());
        this.parent = parent;
        this.buttonId = buttonId;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(buttonId);
    }

    @Override
    protected UnmanagedSprite unfocusedSprite()
    {
        return BACK_BUTTON_NOT_FOCUSED;
    }

    @Override
    protected UnmanagedSprite focusedSprite()
    {
        return BACK_BUTTON_FOCUSED;
    }
}