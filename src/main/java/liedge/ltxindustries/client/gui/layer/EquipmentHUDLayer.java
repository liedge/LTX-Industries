package liedge.ltxindustries.client.gui.layer;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class EquipmentHUDLayer extends LimaGuiLayer
{
    public static final EquipmentHUDLayer INSTANCE = new EquipmentHUDLayer();

    private EquipmentHUDLayer()
    {
        super(LTXIndustries.RESOURCES.id("equipment_hud"));
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks)
    {
        ItemStack heldItem = player.getMainHandItem();
        if (IClientItemExtensions.of(heldItem) instanceof Renderer renderer)
        {
            HorizontalAlignment xAlign = LTXIClientConfig.getEquipmentHUDXAlign();
            VerticalAlignment yAlign = LTXIClientConfig.getEquipmentHUDYAlign();
            int xOffset = LTXIClientConfig.EQUIPMENT_HUD_X_OFFSET.getAsInt();
            int yOffset = LTXIClientConfig.EQUIPMENT_HUD_Y_OFFSET.getAsInt();

            renderer.renderHUDLayer(graphics, player, heldItem, xAlign, yAlign, xOffset, yOffset, partialTicks);
        }
    }

    public interface Renderer extends IClientItemExtensions
    {
        void renderHUDLayer(GuiGraphics graphics, LocalPlayer player, ItemStack heldItem, HorizontalAlignment xAlign, VerticalAlignment yAlign, int xOffset, int yOffset, float partialTick);
    }
}