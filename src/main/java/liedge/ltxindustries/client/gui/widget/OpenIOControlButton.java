package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.resources.Identifier;

public class OpenIOControlButton extends LimaSidebarButton.RightSided
{
    private static final Identifier ITEMS_SPRITE = LTXIndustries.RESOURCES.id("widget/item_io");
    private static final Identifier ENERGY_SPRITE = LTXIndustries.RESOURCES.id("widget/power_io");
    private static final Identifier FLUIDS_SPRITE = LTXIndustries.RESOURCES.id("widget/fluid_io");

    private final LimaMenuScreen<?> parent;
    private final int buttonId;
    private final BlockEntityInputType inputType;
    private final Identifier icon;

    public OpenIOControlButton(int x, int y, LimaMenuScreen<?> parent, int buttonId, BlockEntityInputType inputType)
    {
        super(x, y, BlockEntityInputType.SIDEBAR_TOOLTIP.translateArgs(inputType.translate()));
        this.parent = parent;
        this.buttonId = buttonId;
        this.inputType = inputType;
        this.icon = switch (inputType)
        {
            case ITEMS -> ITEMS_SPRITE;
            case ENERGY -> ENERGY_SPRITE;
            case FLUIDS -> FLUIDS_SPRITE;
        };
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    protected void renderInnerContents(GuiGraphicsExtractor graphics, int guiX, int guiY)
    {
        renderSprite(graphics, icon, guiX, guiY);
    }

    @Override
    public void onPress(InputWithModifiers input)
    {
        if (input.isLeft()) parent.sendCustomButtonData(buttonId, inputType, LTXINetworkSerializers.MACHINE_INPUT_TYPE);
    }
}