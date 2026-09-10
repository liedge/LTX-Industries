package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.UpgradeStationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

public class UpgradeStationScreen extends LTXIScreen<UpgradeStationMenu>
{
    private static final Identifier SLOT_SPRITE = LTXIndustries.RESOURCES.id("slot/equipment");
    static final Identifier COLOR_WHEEL_SPRITE = LTXIndustries.RESOURCES.id("widget/color_wheel");

    private @Nullable SpriteIconButton upgradesButton;
    private @Nullable SpriteIconButton colorChangeButton;

    public UpgradeStationScreen(UpgradeStationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 148, 0, 0, 0);
    }

    @Override
    protected void addWidgets()
    {
        this.upgradesButton = addRenderableWidget(subButton(0, LTXILangKeys.MANAGE_UPGRADES.translate(), UpgradesConfigScreen.MODULE_SPRITE, 57, 21));
        this.colorChangeButton = addRenderableWidget(subButton(1, LTXILangKeys.MANAGE_LIGHT_COLORS.translate(), COLOR_WHEEL_SPRITE, 79, 21));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        blitSprite(graphics, SLOT_SPRITE, 11, 16, 28, 28);
    }

    @Override
    protected void containerTick()
    {
        if (upgradesButton != null && colorChangeButton != null)
        {
            boolean enable = menu.menuContext().hasValidItem();

            upgradesButton.active = enable;
            colorChangeButton.active = enable;
        }
    }

    private SpriteIconButton subButton(int buttonId, Component message, Identifier sprite, int x, int y)
    {
        SpriteIconButton button = SpriteIconButton.builder(message, _ -> sendUnitButtonData(buttonId), true).sprite(sprite, 16, 16).size(18, 18).withTootip().build();
        button.setPosition(leftPos + x, topPos + y);

        return button;
    }
}