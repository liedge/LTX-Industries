package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.Translatable;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.client.gui.widget.LimaRenderableButton;
import liedge.ltxindustries.client.gui.widget.LimaSidebarButton;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.menu.IOControllerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.function.BooleanSupplier;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.client.LTXILangKeys.AUTO_OUTPUT_OFF_TOOLTIP;
import static liedge.ltxindustries.client.LTXILangKeys.AUTO_OUTPUT_ON_TOOLTIP;

public class IOControllerScreen extends LTXIScreen<IOControllerMenu>
{
    private static final ResourceLocation BUTTON_GRID_TEXTURE = RESOURCES.textureLocation("gui", "io_button_grid");
    private static final ResourceLocation INPUT_SPRITE = RESOURCES.location("widget/io_selector_input");
    private static final ResourceLocation OUTPUT_SPRITE = RESOURCES.location("widget/io_selector_output");
    private static final ResourceLocation BOTH_SPRITE = RESOURCES.location("widget/io_selector_both");
    private static final ResourceLocation DISABLED_SPRITE = RESOURCES.location("widget/io_selector_disabled");
    private static final ResourceLocation AUTO_OUT_DISABLED_SPRITE = RESOURCES.location("widget/auto_output_disabled");
    private static final ResourceLocation AUTO_OUT_ENABLED_SPRITE = RESOURCES.location("widget/auto_output_enabled");
    private static final ResourceLocation AUTO_IN_OFF_SPRITE = RESOURCES.location("widget/auto_input_off");
    private static final ResourceLocation AUTO_IN_ON_SPRITE = RESOURCES.location("widget/auto_input_on");

    private final IOController ioController;

    public IOControllerScreen(IOControllerMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.ioController = menu.getIOControl();
        this.inventoryLabelY = 73;
        this.leftPadding = 18;
        this.rightPadding = 18;
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, IOControllerMenu.BACK_BUTTON_ID));

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

        //if (ioController.allowsAutoInput()) addRenderableWidget(new AutoIOButton(leftPos + 43, topPos + 38, 3, AUTO_INPUT_OFF_TOOLTIP, AUTO_INPUT_ON_TOOLTIP, AUTO_IN_OFF_SPRITE, AUTO_IN_ON_SPRITE, ioController::isAutoInput));
        if (ioController.allowsAutoOutput()) addRenderableWidget(new AutoIOButton(rightPos, topPos + 3, IOControllerMenu.TOGGLE_AUTO_OUTPUT_BUTTON_ID, AUTO_OUTPUT_OFF_TOOLTIP, AUTO_OUTPUT_ON_TOOLTIP, AUTO_OUT_DISABLED_SPRITE, AUTO_OUT_ENABLED_SPRITE, ioController::isAutoOutput));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 83);
        guiGraphics.blit(BUTTON_GRID_TEXTURE, leftPos + 60, topPos + 21, 0f, 0f, 48, 48, 48, 48);
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
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                sendCustomButtonData(IOControllerMenu.CYCLE_FORWARD_BUTTON_ID, side, LimaCoreNetworkSerializers.RELATIVE_SIDE);
            }
            else
            {
                sendCustomButtonData(IOControllerMenu.CYCLE_BACKWARD_BUTTON_ID, side, LimaCoreNetworkSerializers.RELATIVE_SIDE);
            }
        }

        @Override
        protected ResourceLocation unfocusedSprite()
        {
            return switch (ioController.getSideIOState(side))
            {
                case DISABLED -> DISABLED_SPRITE;
                case INPUT_ONLY -> INPUT_SPRITE;
                case OUTPUT_ONLY -> OUTPUT_SPRITE;
                case INPUT_AND_OUTPUT -> BOTH_SPRITE;
            };
        }

        @Override
        public boolean hasTooltip()
        {
            return true;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            Component sideTooltip = side.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle())
                    .append(CommonComponents.SPACE)
                    .append(ComponentUtils.wrapInSquareBrackets(LimaComponentUtil.localizeDirection(side.resolveAbsoluteSide(menu.menuContext().blockEntity().getFacing())).translate().withStyle(ChatFormatting.GRAY)));

            consumer.accept(sideTooltip);
            consumer.accept(ioController.getSideIOState(side).translate());
        }

        @Override
        protected boolean isValidClickButton(int button)
        {
            return button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        }

        @Override
        protected boolean acceptsKeyboardInput()
        {
            return false;
        }
    }

    private class AutoIOButton extends LimaSidebarButton.RightSided
    {
        private final int buttonId;
        private final Component offLabel;
        private final Component onLabel;
        private final ResourceLocation offSprite;
        private final ResourceLocation onSprite;
        private final BooleanSupplier stateGetter;

        protected AutoIOButton(int x, int y, int buttonId, Translatable offLabel, Translatable onLabel, ResourceLocation offSprite, ResourceLocation onSprite, BooleanSupplier stateGetter)
        {
            super(x, y, Component.empty());

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
        protected ResourceLocation iconSprite()
        {
            return stateGetter.getAsBoolean() ? onSprite : offSprite;
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