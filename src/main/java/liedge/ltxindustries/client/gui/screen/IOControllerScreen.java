package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.Translatable;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.client.gui.widget.LimaBackButton;
import liedge.ltxindustries.client.gui.widget.LimaRenderableButton;
import liedge.ltxindustries.client.gui.widget.LTXIWidgetSprites;
import liedge.ltxindustries.menu.IOControllerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.BooleanSupplier;

import static liedge.ltxindustries.client.LTXILangKeys.*;

public class IOControllerScreen extends LimaMenuScreen<IOControllerMenu>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("gui", "io_controller");

    private static final ResourceLocation INPUT_SPRITE = LTXIndustries.RESOURCES.location("io_selector_input");
    private static final ResourceLocation OUTPUT_SPRITE = LTXIndustries.RESOURCES.location("io_selector_output");
    private static final ResourceLocation BOTH_SPRITE = LTXIndustries.RESOURCES.location("io_selector_both");
    private static final ResourceLocation DISABLED_SPRITE = LTXIndustries.RESOURCES.location("io_selector_disabled");
    private static final ResourceLocation AUTO_OUT_OFF_SPRITE = LTXIndustries.RESOURCES.location("auto_output_off");
    private static final ResourceLocation AUTO_OUT_ON_SPRITE = LTXIndustries.RESOURCES.location("auto_output_on");
    private static final ResourceLocation AUTO_IN_OFF_SPRITE = LTXIndustries.RESOURCES.location("auto_input_off");
    private static final ResourceLocation AUTO_IN_ON_SPRITE = LTXIndustries.RESOURCES.location("auto_input_on");

    private final IOController ioController;
    private final TextureAtlasSprite inputSprite;
    private final TextureAtlasSprite outputSprite;
    private final TextureAtlasSprite bothSprite;
    private final TextureAtlasSprite disabledSprite;

    public IOControllerScreen(IOControllerMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, LTXIConstants.LIME_GREEN.argb32());
        this.ioController = menu.getIOControl();
        this.inputSprite = LTXIWidgetSprites.sprite(INPUT_SPRITE);
        this.outputSprite = LTXIWidgetSprites.sprite(OUTPUT_SPRITE);
        this.bothSprite = LTXIWidgetSprites.sprite(BOTH_SPRITE);
        this.disabledSprite = LTXIWidgetSprites.sprite(DISABLED_SPRITE);
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

        if (ioController.allowsAutoInput()) addRenderableWidget(new AutoIOButton(leftPos + 43, topPos + 38, 3, AUTO_INPUT_OFF_TOOLTIP, AUTO_INPUT_ON_TOOLTIP, AUTO_IN_OFF_SPRITE, AUTO_IN_ON_SPRITE, ioController::isAutoInput));
        if (ioController.allowsAutoOutput()) addRenderableWidget(new AutoIOButton(leftPos + 111, topPos + 38, 4, AUTO_OUTPUT_OFF_TOOLTIP, AUTO_OUTPUT_ON_TOOLTIP, AUTO_OUT_OFF_SPRITE, AUTO_OUT_ON_SPRITE, ioController::isAutoOutput));
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
        protected TextureAtlasSprite unfocusedSprite()
        {
            return switch (ioController.getSideIOState(side))
            {
                case DISABLED -> disabledSprite;
                case INPUT_ONLY -> inputSprite;
                case OUTPUT_ONLY -> outputSprite;
                case INPUT_AND_OUTPUT -> bothSprite;
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
        private final TextureAtlasSprite offSprite;
        private final TextureAtlasSprite onSprite;
        private final BooleanSupplier stateGetter;

        protected AutoIOButton(int x, int y, int buttonId, Translatable offLabel, Translatable onLabel, ResourceLocation offSpriteLoc, ResourceLocation onSpriteLoc, BooleanSupplier stateGetter)
        {
            super(x, y, 14, 14);

            this.buttonId = buttonId;
            this.offLabel = offLabel.translate();
            this.onLabel = onLabel.translate();
            this.offSprite = LTXIWidgetSprites.sprite(offSpriteLoc);
            this.onSprite = LTXIWidgetSprites.sprite(onSpriteLoc);
            this.stateGetter = stateGetter;
        }

        @Override
        public void onPress(int button)
        {
            sendUnitButtonData(buttonId);
        }

        @Override
        protected TextureAtlasSprite unfocusedSprite()
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