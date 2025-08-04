package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.FabricatorProgressWidget;
import liedge.ltxindustries.menu.AutoFabricatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutoFabricatorScreen extends LTXIMachineScreen<AutoFabricatorMenu>
{
    private static final ResourceLocation BLUEPRINT_SLOT_SPRITE = LTXIndustries.RESOURCES.location("slot/encoded_blueprint");

    public AutoFabricatorScreen(AutoFabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 180);

        this.inventoryLabelX = 14;
        this.inventoryLabelY = 87;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new FabricatorProgressWidget(leftPos + 171, topPos + 70, menu.menuContext()));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 14, 97);
        blitSlotGrid(guiGraphics, 32, 29, 8, 2);
        blitPowerInSlot(guiGraphics, 7, 52);
        blitSlotSprite(guiGraphics, BLUEPRINT_SLOT_SPRITE, 119, 72);
        blitOutputSlot(guiGraphics, 149, 70);
    }
}