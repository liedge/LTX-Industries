package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import net.minecraft.client.gui.components.Tooltip;

public class OpenIOControlButton extends LimaRenderableButton
{
    private final LimaMenuScreen<?> parent;
    private final int buttonId;
    private final MachineInputType inputType;

    public OpenIOControlButton(int x, int y, LimaMenuScreen<?> parent, int buttonId, MachineInputType inputType)
    {
        super(x, y, 16, 16, inputType.translate());
        this.parent = parent;
        this.buttonId = buttonId;
        this.inputType = inputType;
        setTooltip(Tooltip.create(inputType.translate()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendCustomButtonData(buttonId, inputType, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE);
    }

    @Override
    protected UnmanagedSprite unfocusedSprite()
    {
        return switch (inputType)
        {
            case ITEMS -> ScreenWidgetSprites.ITEM_IO_BUTTON;
            case ENERGY -> ScreenWidgetSprites.ENERGY_IO_BUTTON;
            case FLUIDS -> ScreenWidgetSprites.FLUID_IO_BUTTON;
        };
    }
}