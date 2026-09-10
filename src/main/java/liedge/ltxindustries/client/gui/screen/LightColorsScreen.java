package liedge.ltxindustries.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.BaseGridRenderable;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.data.LightChannels;
import liedge.ltxindustries.data.LightColors;
import liedge.ltxindustries.menu.LightColorsMenu;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jspecify.annotations.Nullable;

import java.util.HexFormat;
import java.util.List;

public class LightColorsScreen extends LTXIScreen<LightColorsMenu>
{
    private final List<LightColors.Channel> channels = new ObjectArrayList<>();
    private LightColors.@Nullable Channel selectedChannel;
    private LightColors clientColors = LightColors.EMPTY;
    private boolean updatingWidgets;

    // Widgets
    private @Nullable SelectorGrid selectorGrid;
    private @Nullable ColorSlider redSlider;
    private @Nullable ColorSlider greenSlider;
    private @Nullable ColorSlider blueSlider;
    private @Nullable EditBox rgbEditBox;

    private @Nullable ChannelEnableButton enableChannelButton;
    private @Nullable ExtendedButton resetButton;
    private @Nullable ExtendedButton applyButton;

    public LightColorsScreen(LightColorsMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 170, 18, 0, 0);
    }

    @Override
    protected void containerTick()
    {
        if (menu.shouldUpdateScreen())
        {
            LightChannels serverChannels = menu.getLightChannels();

            channels.clear();
            serverChannels.forEach(channels::add);
            clientColors = menu.getLightColors();

            if (selectedChannel != null && !serverChannels.contains(selectedChannel))
            {
                selectedChannel = null;
            }

            writeWidgets();
        }

        updateResetApplyButtons();
    }

    private void writeWidgets()
    {
        if (redSlider == null || greenSlider == null || blueSlider == null || rgbEditBox == null || enableChannelButton == null) return;

        updatingWidgets = true;

        boolean enableEdit = selectedChannel != null;

        redSlider.active =  enableEdit;
        greenSlider.active = enableEdit;
        blueSlider.active = enableEdit;
        rgbEditBox.active = enableEdit;
        enableChannelButton.active = enableEdit;

        if (enableEdit)
        {
            boolean enableChannel = clientColors.containsKey(selectedChannel);
            enableChannelButton.setEnableChannel(enableChannel);

            if (enableChannel)
            {
                int color = clientColors.getOrDefault(selectedChannel, -1);
                rgbEditBox.setValue(String.format("%06x", ARGB.transparent(color)));
                setSlidersColor(color);
            }
            else
            {
                redSlider.active = false;
                greenSlider.active = false;
                blueSlider.active = false;
                rgbEditBox.active = false;

                rgbEditBox.setValue("");
                setSlidersColor(-1);
            }
        }
        else
        {
            rgbEditBox.setValue("");
            setSlidersColor(-1);
            enableChannelButton.setEnableChannel(false);
        }

        updatingWidgets = false;
    }

    private void updateResetApplyButtons()
    {
        if (resetButton == null || applyButton == null) return;

        boolean enableButtons = selectedChannel != null && !clientColors.equals(menu.getLightColors());

        resetButton.active = enableButtons;
        applyButton.active = enableButtons;
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this));

        this.selectorGrid = addRenderableOnly(new SelectorGrid(leftPos + 10, topPos + 12, this));

        this.redSlider = addRenderableWidget(new ColorSlider(leftPos + 35, topPos + 7, "R: ", this));
        this.greenSlider = addRenderableWidget(new ColorSlider(leftPos + 35, topPos + 20, "G: ", this));
        this.blueSlider = addRenderableWidget(new ColorSlider(leftPos + 35, topPos + 33, "B: ", this));
        this.rgbEditBox = addRenderableWidget(createRGBEditBox(leftPos + 47, topPos + 47));

        this.enableChannelButton = addRenderableWidget(new ChannelEnableButton(leftPos + 113, topPos + 47, this));
        this.resetButton = addRenderableWidget(new ExtendedButton(leftPos + 35, topPos + 61, 50, 14, LTXILangKeys.GUI_RESET.translate(), _ -> {
            this.clientColors = menu.getLightColors();
            writeWidgets();
        }));
        this.applyButton = addRenderableWidget(new ExtendedButton(leftPos + 87, topPos + 61, 50, 14, LTXILangKeys.GUI_APPLY.translate(), _ ->
                sendCustomButtonData(0, clientColors, LTXINetworkSerializers.LIGHT_COLORS)));

        writeWidgets();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int csX = leftPos + 35;
        int csY = topPos + 47;
        int csColor = selectedChannel != null ? clientColors.getOrDefault(selectedChannel, 0xff000000) : 0xff000000;

        graphics.fill(csX, csY, csX + 12, csY + 12, 0xffa0a0a0);
        graphics.fill(csX + 1, csY + 1, csX + 11, csY + 11, csColor);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT && selectorGrid != null)
        {
            if (selectorGrid.onGridClicked(event.x(), event.y(), 0)) return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (rgbEditBox != null)
        {
            if (rgbEditBox.keyPressed(event))
            {
                return true;
            }
            else if (rgbEditBox.canConsumeInput() && !event.isEscape())
            {
                return true;
            }
        }

        return super.keyPressed(event);
    }

    private int getSlidersColor()
    {
        if (redSlider == null || greenSlider == null || blueSlider == null) return -1;

        int red = redSlider.getValueInt();
        int green = greenSlider.getValueInt();
        int blue = blueSlider.getValueInt();

        return ARGB.color(0, red, green, blue);
    }

    private void setSlidersColor(int color)
    {
        if (redSlider == null || greenSlider == null || blueSlider == null) return;

        redSlider.setValue(ARGB.red(color));
        greenSlider.setValue(ARGB.green(color));
        blueSlider.setValue(ARGB.blue(color));
    }

    // Widget classes

    private EditBox createRGBEditBox(int x, int y)
    {
        EditBox box = new EditBox(font, x, y, 54, 12, Component.empty());
        box.setMaxLength(6);
        box.setFilter(s -> s.matches("[a-fA-F0-9]{0,6}"));
        box.setResponder(value ->
        {
            if (!updatingWidgets && selectedChannel != null && value.matches("[a-fA-F0-9]{6}"))
            {
                int color = HexFormat.fromHexDigits(value);
                clientColors = clientColors.setColor(selectedChannel, color);
                writeWidgets();
            }
        });

        return box;
    }

    private static class ColorSlider extends ExtendedSlider
    {
        private final LightColorsScreen parent;

        ColorSlider(int x, int y, String prefix, LightColorsScreen parent)
        {
            super(x, y, 128, 12, Component.literal(prefix), Component.empty(), 0, 255, 0, 1, 0, true);
            this.parent = parent;
        }

        @Override
        protected void applyValue()
        {
            if (parent.updatingWidgets) return;

            if (parent.selectedChannel != null)
            {
                int color = parent.getSlidersColor();
                parent.clientColors = parent.clientColors.setColor(parent.selectedChannel, color);
                parent.writeWidgets();
            }
        }
    }

    private static class ChannelEnableButton extends ExtendedButton
    {
        private final LightColorsScreen parent;
        private boolean enableChannel;

        ChannelEnableButton(int x, int y, LightColorsScreen parent)
        {
            super(x, y, 50, 12, Component.empty(), _ -> {});
            this.parent = parent;
        }

        @Override
        public void onPress(InputWithModifiers input)
        {
            setEnableChannel(!enableChannel);

            if (!parent.updatingWidgets && parent.selectedChannel != null)
            {
                if (enableChannel)
                {
                    parent.clientColors = parent.clientColors.setColor(parent.selectedChannel, -1);
                }
                else
                {
                    parent.clientColors = parent.clientColors.clearColor(parent.selectedChannel);
                }

                parent.writeWidgets();
            }
        }

        private void setEnableChannel(boolean enableChannel)
        {
            this.enableChannel = enableChannel;

            Component message = enableChannel ? LTXILangKeys.GUI_ENABLED.translate() : LTXILangKeys.GUI_DISABLED.translate();
            setMessage(message);
        }
    }

    private static class SelectorGrid extends BaseGridRenderable<LightColors.Channel>
    {
        private final LightColorsScreen parent;

        SelectorGrid(int x, int y, LightColorsScreen parent)
        {
            super(x, y, 18, 18, 1, 3);
            this.parent = parent;
        }

        @Override
        public List<LightColors.Channel> getElements()
        {
            return parent.channels;
        }

        @Override
        public void renderElement(GuiGraphicsExtractor graphics, LightColors.Channel element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY)
        {
            Identifier sprite;
            if (parent.selectedChannel == element)
            {
                sprite = GRID_UNIT_SELECTED;
            }
            else if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, posX, posY, 18, 18))
            {
                sprite = GRID_UNIT_FOCUSED;
            }
            else
            {
                sprite = GRID_UNIT;
            }

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, posY, 18, 18);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UpgradeStationScreen.COLOR_WHEEL_SPRITE, posX + 1, posY + 1, 16, 16);

            Font font = Minecraft.getInstance().font;
            String overlay = switch (element)
            {
                case PRIMARY -> "P";
                case SECONDARY -> "S";
                case ENERGY -> "E";
            };
            graphics.text(font, overlay, posX + 17 - font.width(overlay), posY + 9, -1, true);
        }

        @Override
        public void extractElementTooltip(TooltipLineConsumer consumer, LightColors.Channel element, int mouseX, int mouseY, int gridIndex, int elementIndex)
        {
            consumer.accept(element.translate());
        }

        @Override
        public void onElementClicked(LightColors.Channel element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex)
        {
            if (parent.selectedChannel != element)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));

                parent.selectedChannel = element;
                parent.writeWidgets();
            }
        }
    }
}