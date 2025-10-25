package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.menu.LimaMenu;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.RecipeLayoutMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public final class RecipeLayoutScreen extends LTXIMachineScreen<RecipeLayoutMenu<?>>
{
    private final RecipeLayout layout;

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

    public static void renderLayout(GuiGraphics graphics, int screenX, int screenY, RecipeLayout layout)
    {
        for (LayoutSlot.Type slotType : LayoutSlot.Type.values())
        {
            if (slotType.getContentsType() == null) continue;
            List<LayoutSlot> layoutSlots = layout.getSlotsForType(slotType);

            for (LayoutSlot slot : layoutSlots)
            {
                int sx = screenX + slot.x() - 1;
                int sy = screenY + slot.y() - 1;
                graphics.blitSprite(slot.type().getSprite(), sx, sy, 18, 18);
            }
        }
    }
}