package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.ltxindustries.menu.SpecialInfiniteTankMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

public final class SpecialInfiniteTankScreen extends MachineBaseScreen<SpecialInfiniteTankMenu>
{
    public SpecialInfiniteTankScreen(SpecialInfiniteTankMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitSlotSprite(graphics, LayoutSlot.FLUID_SLOT_SPRITE, 79, 35);
    }

    @Override
    protected void extractFluidSlot(GuiGraphicsExtractor graphics, LimaFluidSlot slot, int mouseX, int mouseY)
    {
        int x = slot.getX();
        int y = slot.getY();

        FluidStack stack = slot.getFluid();

        LimaGuiUtil.fluidSprite(graphics, stack, x, y);
        String text = LimaCommonConstants.INFINITY_SYMBOL;
        graphics.text(font, text, x + 16 - font.width(text), y + 9, -1, true);
    }
}