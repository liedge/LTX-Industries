package liedge.limatech.client.gui.screen;

import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.lib.Translatable;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.client.gui.widget.LimaRenderableButton;
import liedge.limatech.client.gui.widget.ScreenWidgetSprites;
import liedge.limatech.menu.MachineIOControlMenu;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.BooleanSupplier;

import static liedge.limatech.client.LimaTechLang.*;

public class MachineIOControlScreen extends LimaMenuScreen<MachineIOControlMenu>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "machine_io_control");
    private static final UnmanagedSprite IO_BUTTON_DISABLED = new UnmanagedSprite(TEXTURE, 176, 0, 16, 16);
    private static final UnmanagedSprite IO_BUTTON_INPUT = new UnmanagedSprite(TEXTURE, 192, 0, 16, 16);
    private static final UnmanagedSprite IO_BUTTON_OUTPUT = new UnmanagedSprite(TEXTURE, 208, 0, 16, 16);
    private static final UnmanagedSprite IO_BUTTON_INPUT_AND_OUTPUT = new UnmanagedSprite(TEXTURE, 224, 0, 16, 16);
    private static final UnmanagedSprite AUTO_OUTPUT_DISABLED = new UnmanagedSprite(TEXTURE, 176, 16, 14, 14);
    private static final UnmanagedSprite AUTO_INPUT_DISABLED = new UnmanagedSprite(TEXTURE, 190, 16, 14, 14);
    private static final UnmanagedSprite AUTO_OUTPUT_ENABLED = new UnmanagedSprite(TEXTURE, 204, 16, 14, 14);
    private static final UnmanagedSprite AUTO_INPUT_ENABLED = new UnmanagedSprite(TEXTURE, 218, 16, 14, 14);

    private final MachineIOControl ioControl;

    public MachineIOControlScreen(MachineIOControlMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, LimaTechConstants.LIME_GREEN.packedRGB());
        this.ioControl = menu.getIOControl();
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new BackButton(leftPos + 6, topPos + 6));

        Direction front = ioControl.getFacing();
        addRenderableWidget(new IOButton(leftPos + 76, topPos + 21, Direction.UP, TOP_IO_LABEL));
        addRenderableWidget(new IOButton(leftPos + 76, topPos + 53, Direction.DOWN, BOTTOM_IO_LABEL));
        addRenderableWidget(new IOButton(leftPos + 60, topPos + 37, front.getClockWise(), LEFT_IO_LABEL));
        addRenderableWidget(new IOButton(leftPos +  76, topPos + 37, front, FRONT_IO_LABEL));
        addRenderableWidget(new IOButton(leftPos + 92, topPos + 37, front.getCounterClockWise(), RIGHT_IO_LABEL));
        addRenderableWidget(new IOButton(leftPos + 92, topPos + 53, front.getOpposite(), REAR_IO_LABEL));

        if (ioControl.allowsAutoInput()) addRenderableWidget(new AutoIOButton(leftPos + 43, topPos + 38, 3, AUTO_INPUT_OFF_TOOLTIP, AUTO_INPUT_ON_TOOLTIP, AUTO_INPUT_DISABLED, AUTO_INPUT_ENABLED, ioControl::isAutoInput));
        if (ioControl.allowsAutoOutput()) addRenderableWidget(new AutoIOButton(leftPos + 111, topPos + 38, 4, AUTO_OUTPUT_OFF_TOOLTIP, AUTO_OUTPUT_ON_TOOLTIP, AUTO_OUTPUT_DISABLED, AUTO_OUTPUT_ENABLED, ioControl::isAutoOutput));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }

    private class IOButton extends LimaRenderableButton
    {
        private final Direction side;
        private final Component ioLabel;

        public IOButton(int x, int y, Direction side, Translatable ioTranslatable)
        {
            super(x, y, 16, 16);
            this.side = side;
            this.ioLabel = ioTranslatable.translateArgs(LimaComponentUtil.localizeDirection(side).translate());
        }

        @Override
        public void onPress(int button)
        {
            if (button == 0)
            {
                sendCustomButtonData(1, side, LimaCoreNetworkSerializers.DIRECTION);
            }
            else
            {
                sendCustomButtonData(2, side, LimaCoreNetworkSerializers.DIRECTION);
            }
        }

        @Override
        protected UnmanagedSprite unfocusedSprite()
        {
            return switch (ioControl.getSideIO(side))
            {
                case DISABLED -> IO_BUTTON_DISABLED;
                case INPUT_AND_OUTPUT -> IO_BUTTON_INPUT_AND_OUTPUT;
                case INPUT_ONLY -> IO_BUTTON_INPUT;
                case OUTPUT_ONLY -> IO_BUTTON_OUTPUT;
            };
        }

        @Override
        protected UnmanagedSprite focusedSprite()
        {
            return unfocusedSprite();
        }

        @Override
        public boolean hasTooltip()
        {
            return true;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            consumer.accept(ioLabel);
            consumer.accept(ioControl.getSideIO(side).translate());
        }

        @Override
        protected boolean isValidClickButton(int button)
        {
            return button == 0 || button == 1;
        }

        @Override
        protected boolean acceptsKeyboardInput()
        {
            return false;
        }
    }

    private class AutoIOButton extends LimaRenderableButton
    {
        private final int buttonId;
        private final Component offLabel;
        private final Component onLabel;
        private final UnmanagedSprite offSprite;
        private final UnmanagedSprite onSprite;
        private final BooleanSupplier stateGetter;

        protected AutoIOButton(int x, int y, int buttonId, Translatable offLabel, Translatable onLabel, UnmanagedSprite offSprite, UnmanagedSprite onSprite, BooleanSupplier stateGetter)
        {
            super(x, y, 14, 14);

            this.buttonId = buttonId;
            this.offLabel = offLabel.translate();
            this.onLabel = onLabel.translate();
            this.offSprite = offSprite;
            this.onSprite = onSprite;
            this.stateGetter = stateGetter;
        }

        @Override
        public void onPress(int button)
        {
            sendUnitButtonData(buttonId);
        }

        @Override
        protected UnmanagedSprite unfocusedSprite()
        {
            return stateGetter.getAsBoolean() ? onSprite : offSprite;
        }

        @Override
        protected UnmanagedSprite focusedSprite()
        {
            return unfocusedSprite();
        }

        @Override
        public boolean hasTooltip()
        {
            return true;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            Component label = stateGetter.getAsBoolean() ? onLabel : offLabel;
            consumer.accept(label);
        }
    }

    private class BackButton extends LimaRenderableButton
    {
        public BackButton(int x, int y)
        {
            super(x, y, 12, 12, BACK_BUTTON_LABEL.translate());
            setTooltip(Tooltip.create(BACK_BUTTON_LABEL.translate()));
        }

        @Override
        public void onPress(int button)
        {
            sendUnitButtonData(0);
        }

        @Override
        protected UnmanagedSprite unfocusedSprite()
        {
            return ScreenWidgetSprites.BACK_BUTTON_NOT_FOCUSED;
        }

        @Override
        protected UnmanagedSprite focusedSprite()
        {
            return ScreenWidgetSprites.BACK_BUTTON_FOCUSED;
        }
    }
}