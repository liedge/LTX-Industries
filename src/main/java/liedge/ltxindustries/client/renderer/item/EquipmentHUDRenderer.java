package liedge.ltxindustries.client.renderer.item;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.VerticalAlignment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public interface EquipmentHUDRenderer
{
    void renderHUDLayer(GuiGraphics graphics,
                        LocalPlayer player,
                        ItemStack heldItem,
                        HorizontalAlignment xAlign,
                        VerticalAlignment yAlign,
                        int xOffset,
                        int yOffset,
                        float partialTick);
}