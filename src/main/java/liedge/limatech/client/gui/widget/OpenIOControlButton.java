package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limatech.blockentity.base.BlockEntityInputType;
import liedge.limatech.registry.game.LimaTechNetworkSerializers;
import net.minecraft.client.gui.components.Tooltip;

import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.*;

public class OpenIOControlButton extends LimaSidebarButton
{
    private final LimaMenuScreen<?> parent;
    private final int buttonId;
    private final BlockEntityInputType inputType;

    public OpenIOControlButton(int x, int y, LimaMenuScreen<?> parent, int buttonId, BlockEntityInputType inputType)
    {
        super(x, y, inputType.translate());
        this.parent = parent;
        this.buttonId = buttonId;
        this.inputType = inputType;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendCustomButtonData(buttonId, inputType, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE);
    }

    @Override
    protected UnmanagedSprite iconSprite()
    {
        return switch (inputType)
        {
            case ITEMS -> SIDEBAR_ICON_ITEM_IO;
            case ENERGY -> SIDEBAR_ICON_ENERGY_IO;
            case FLUIDS -> SIDEBAR_ICON_FLUIDS_IO;
        };
    }
}