package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.Translatable;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.client.gui.widget.LimaRenderableButton;
import liedge.ltxindustries.client.gui.widget.LimaSidebarButton;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.menu.BlockIOConfigurationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.function.BooleanSupplier;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.client.LTXILangKeys.*;

public class BlockIOConfigurationScreen extends LTXIScreen<BlockIOConfigurationMenu>
{
    private static final ResourceLocation BUTTON_GRID_TEXTURE = RESOURCES.textureLocation("gui", "io_button_grid");
    private static final ResourceLocation INPUT_SPRITE = RESOURCES.location("widget/io_selector_input");
    private static final ResourceLocation OUTPUT_SPRITE = RESOURCES.location("widget/io_selector_output");
    private static final ResourceLocation BOTH_SPRITE = RESOURCES.location("widget/io_selector_both");
    private static final ResourceLocation DISABLED_SPRITE = RESOURCES.location("widget/io_selector_disabled");
    private static final ResourceLocation AUTO_OUT_DISABLED_SPRITE = RESOURCES.location("widget/auto_output_disabled");
    private static final ResourceLocation AUTO_OUT_ENABLED_SPRITE = RESOURCES.location("widget/auto_output_enabled");
    private static final ResourceLocation AUTO_IN_DISABLED_SPRITE = RESOURCES.location("widget/auto_input_disabled");
    private static final ResourceLocation AUTO_IN_ENABLED_SPRITE = RESOURCES.location("widget/auto_input_enabled");

    public BlockIOConfigurationScreen(BlockIOConfigurationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.inventoryLabelY = 73;
        this.leftPadding = 18;
        this.rightPadding = 18;
    }

    private BlockIOConfiguration getIOConfiguration()
    {
        return menu.getIOConfiguration();
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, BlockIOConfigurationMenu.BACK_BUTTON_ID));

        for (RelativeHorizontalSide side : menu.getIOConfigRules().validSides())
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

        int rightSidebarY = topPos + 3;
        if (menu.getIOConfigRules().allowsAutoInput())
        {
            addRenderableWidget(new AutoIOButton(rightPos, rightSidebarY, BlockIOConfigurationMenu.TOGGLE_AUTO_INPUT_BUTTON_ID, AUTO_INPUT_OFF_TOOLTIP, AUTO_INPUT_ON_TOOLTIP, AUTO_IN_DISABLED_SPRITE, AUTO_IN_ENABLED_SPRITE, () -> getIOConfiguration().autoInput()));
            rightSidebarY += LimaSidebarButton.SIDEBAR_BUTTON_HEIGHT;
        }
        if (menu.getIOConfigRules().allowsAutoOutput())
        {
            addRenderableWidget(new AutoIOButton(rightPos, rightSidebarY, BlockIOConfigurationMenu.TOGGLE_AUTO_OUTPUT_BUTTON_ID, AUTO_OUTPUT_OFF_TOOLTIP, AUTO_OUTPUT_ON_TOOLTIP, AUTO_OUT_DISABLED_SPRITE, AUTO_OUT_ENABLED_SPRITE, () -> getIOConfiguration().autoOutput()));
        }
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
                sendCustomButtonData(BlockIOConfigurationMenu.CYCLE_FORWARD_BUTTON_ID, side, LimaCoreNetworkSerializers.RELATIVE_SIDE);
            }
            else
            {
                sendCustomButtonData(BlockIOConfigurationMenu.CYCLE_BACKWARD_BUTTON_ID, side, LimaCoreNetworkSerializers.RELATIVE_SIDE);
            }
        }

        @Override
        protected ResourceLocation unfocusedSprite()
        {
            return switch (getIOConfiguration().getIOAccess(side))
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
                    .append(ComponentUtils.wrapInSquareBrackets(LimaComponentUtil.localizeDirection(side.resolveAbsoluteSide(menu.menuContext().blockEntity().getFacing())).translate()));

            consumer.accept(sideTooltip);
            consumer.accept(getIOConfiguration().getIOAccess(side).translate());
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

        @Override
        protected void renderContents(GuiGraphics graphics, int guiX, int guiY)
        {
            ResourceLocation sprite = stateGetter.getAsBoolean() ? onSprite : offSprite;
            renderSprite(graphics, sprite, guiX, guiY);
        }
    }
}