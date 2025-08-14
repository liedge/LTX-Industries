package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.menu.LimaMenu;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.RecipeLayoutMenu;
import liedge.ltxindustries.menu.layout.RecipeMenuLayout;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public final class RecipeLayoutScreen extends LTXIMachineScreen<RecipeLayoutMenu<?>>
{
    private final RecipeMenuLayout layout;

    public RecipeLayoutScreen(RecipeLayoutMenu<?> menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.layout = menu.getLayout();
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + layout.progressBarX(), topPos + layout.progressBarY()));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, LimaMenu.DEFAULT_INV_X - 1, LimaMenu.DEFAULT_INV_Y - 1);
        blitPowerInSlot(guiGraphics, 7, 52);

        renderLayout(guiGraphics, leftPos, topPos, layout);
    }

    public static void renderLayout(GuiGraphics graphics, int screenX, int screenY, RecipeMenuLayout layout)
    {
        renderLayoutSlots(graphics, screenX, screenY, layout.itemInputSlots());
        renderLayoutSlots(graphics, screenX, screenY, layout.itemOutputSlots());
        renderLayoutSlots(graphics, screenX, screenY, layout.fluidInputSlots());
        renderLayoutSlots(graphics, screenX, screenY, layout.fluidOutputSlots());
    }

    private static void renderLayoutSlots(GuiGraphics graphics, int screenX, int screenY, List<LayoutSlot> slots)
    {
        for (LayoutSlot slot : slots)
        {
            graphics.blitSprite(slot.sprite(), screenX + slot.x() - 1, screenY + slot.y() - 1, 18, 18);
        }
    }
}