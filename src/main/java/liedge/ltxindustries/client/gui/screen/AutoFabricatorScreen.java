package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.FabricatorProgressWidget;
import liedge.ltxindustries.menu.AutoFabricatorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AutoFabricatorScreen extends LTXIMachineScreen<AutoFabricatorMenu>
{
    private static final Identifier BLUEPRINT_SLOT_SPRITE = LTXIndustries.RESOURCES.id("slot/encoded_blueprint");

    public AutoFabricatorScreen(AutoFabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 180);

        this.inventoryLabelX = 14;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new FabricatorProgressWidget(leftPos + 171, topPos + 70, menu.menuContext()));
    }

    @Override
    protected void extractMenuBackground(GuiGraphicsExtractor graphics)
    {
        super.extractMenuBackground(graphics);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitInventoryAndHotbar(graphics, 14, 97);
        blitSlotGrid(graphics, 32, 29, 8, 2);
        blitPowerInSlot(graphics, 7, 52);
        blitSlotSprite(graphics, BLUEPRINT_SLOT_SPRITE, 119, 72);
        blitOutputSlot(graphics, 149, 70);
    }
}