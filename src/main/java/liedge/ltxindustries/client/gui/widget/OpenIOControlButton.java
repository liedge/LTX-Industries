package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class OpenIOControlButton extends LimaSidebarButton.RightSided
{
    private static final ResourceLocation ITEMS_SPRITE = LTXIndustries.RESOURCES.location("widget/item_io");
    private static final ResourceLocation ENERGY_SPRITE = LTXIndustries.RESOURCES.location("widget/power_io");
    private static final ResourceLocation FLUIDS_SPRITE = LTXIndustries.RESOURCES.location("widget/fluid_io");

    private final LimaMenuScreen<?> parent;
    private final int buttonId;
    private final BlockEntityInputType inputType;
    private final ResourceLocation icon;

    public OpenIOControlButton(int x, int y, LimaMenuScreen<?> parent, int buttonId, BlockEntityInputType inputType)
    {
        super(x, y, inputType.translate());
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
    public void onPress(int button)
    {
        parent.sendCustomButtonData(buttonId, inputType, LTXINetworkSerializers.MACHINE_INPUT_TYPE);
    }

    @Override
    protected ResourceLocation iconSprite()
    {
        return icon;
    }
}