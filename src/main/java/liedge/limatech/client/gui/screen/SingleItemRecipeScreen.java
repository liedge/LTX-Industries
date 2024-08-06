package liedge.limatech.client.gui.screen;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.SimpleRecipeMachineBlockEntity;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.MachineProgressWidget;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SingleItemRecipeScreen<CTX extends SimpleRecipeMachineBlockEntity<?, ?>, M extends LimaItemHandlerMenu<CTX>> extends LimaTechSidebarScreen<M>
{
    public static final ResourceLocation SCREEN_TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "single_input_machine");

    public SingleItemRecipeScreen(M menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, 15);
    }

    @Override
    protected void positionLabels()
    {
        super.positionLabels();
        this.inventoryLabelX = bgWidth - 6 - font.width(playerInventoryTitle);
    }

    @Override
    protected void addWidgets()
    {
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 75, topPos + 39));
        addRenderableWidget(new OpenIOControlButton(leftPos + bgWidth, topPos + 3, this, 0, MachineInputType.ITEMS));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return SCREEN_TEXTURE;
    }
}