package liedge.limatech.client.gui.screen;

import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.lib.Translatable;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.base.IOController;
import liedge.limatech.client.gui.widget.LimaBackButton;
import liedge.limatech.client.gui.widget.LimaRenderableButton;
import liedge.limatech.menu.IOControllerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.BooleanSupplier;

import static liedge.limatech.client.LimaTechLang.*;

public class IOControllerScreen extends LimaMenuScreen<IOControllerMenu>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "io_controller");
    private static final UnmanagedSprite IO_BUTTON_DISABLED = new UnmanagedSprite(TEXTURE, 176, 0, 16, 16);
    private static final UnmanagedSprite IO_BUTTON_INPUT = new UnmanagedSprite(TEXTURE, 192, 0, 16, 16);
    private static final UnmanagedSprite IO_BUTTON_OUTPUT = new UnmanagedSprite(TEXTURE, 208, 0, 16, 16);
    private static final UnmanagedSprite IO_BUTTON_INPUT_AND_OUTPUT = new UnmanagedSprite(TEXTURE, 224, 0, 16, 16);
    private static final UnmanagedSprite AUTO_OUTPUT_DISABLED = new UnmanagedSprite(TEXTURE, 176, 16, 14, 14);
    private static final UnmanagedSprite AUTO_INPUT_DISABLED = new UnmanagedSprite(TEXTURE, 190, 16, 14, 14);
    private static final UnmanagedSprite AUTO_OUTPUT_ENABLED = new UnmanagedSprite(TEXTURE, 204, 16, 14, 14);
    private static final UnmanagedSprite AUTO_INPUT_ENABLED = new UnmanagedSprite(TEXTURE, 218, 16, 14, 14);

    private final IOController ioController;

    public IOControllerScreen(IOControllerMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, LimaTechConstants.LIME_GREEN.packedRGB());
        this.ioController = menu.getIOControl();
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new LimaBackButton(leftPos + 6, topPos + 6, this, 0));

        for (RelativeHorizontalSide side : ioController.getAccessRules().validSides())
        {
            IOButton button = switch (side)
            {
                case TOP -> new IOButton(leftPos + 76, topPos + 21, side);
                case BOTTOM -> new IOButton(leftPos + 76, topPos + 53, side);
                case FRONT -> new IOButton(leftPos + 76, topPos + 37, side);
                case REAR -> new IOButton(leftPos + 92, topPos + 53, side);
                case LEFT -> new IOButton(leftPos + 60, topPos + 37, side);
                case RIGHT -> new IOButton(leftPos + 92, topPos + 37, side);
            };

            addRenderableWidget(button);
        }

        if (ioController.allowsAutoInput()) addRenderableWidget(new AutoIOButton(leftPos + 43, topPos + 38, 3, AUTO_INPUT_OFF_TOOLTIP, AUTO_INPUT_ON_TOOLTIP, AUTO_INPUT_DISABLED, AUTO_INPUT_ENABLED, ioController::isAutoInput));
        if (ioController.allowsAutoOutput()) addRenderableWidget(new AutoIOButton(leftPos + 111, topPos + 38, 4, AUTO_OUTPUT_OFF_TOOLTIP, AUTO_OUTPUT_ON_TOOLTIP, AUTO_OUTPUT_DISABLED, AUTO_OUTPUT_ENABLED, ioController::isAutoOutput));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }

    private class IOButton extends LimaRenderableButton
    {
        private final RelativeHorizontalSide side;

        public IOButton(int x, int y, RelativeHorizontalSide side)
        {
            super(x, y, 16, 16);
            this.side = side;
        }

        @Override
        public void onPress(int button)
        {
            if (button == 0)
            {
                sendCustomButtonData(1, side, LimaCoreNetworkSerializers.RELATIVE_SIDE);
            }
            else
            {
                sendCustomButtonData(2, side, LimaCoreNetworkSerializers.RELATIVE_SIDE);
            }
        }

        @Override
        protected UnmanagedSprite unfocusedSprite()
        {
            return switch (ioController.getSideIOState(side))
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
            Component sideTooltip = side.translate().withStyle(LimaTechConstants.LIME_GREEN.chatStyle())
                    .append(CommonComponents.SPACE)
                    .append(ComponentUtils.wrapInSquareBrackets(LimaComponentUtil.localizeDirection(side.resolveAbsoluteSide(menu.menuContext().blockEntity().getFacing())).translate().withStyle(ChatFormatting.GRAY)));

            consumer.accept(sideTooltip);
            consumer.accept(ioController.getSideIOState(side).translate());
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
}