package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.MachineProgressWidget;
import liedge.limatech.client.gui.widget.MachineUpgradesButton;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import liedge.limatech.menu.MaterialFusingChamberMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static liedge.limatech.menu.SidedUpgradableMachineMenu.IO_CONTROLS_BUTTON_ID;

public class MaterialFusingChamberScreen extends SidedUpgradableMachineScreen<MaterialFusingChamberMenu>
{
    public static final ResourceLocation SCREEN_TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "material_fusing_chamber");

    public MaterialFusingChamberScreen(MaterialFusingChamberMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
    }

    @Override
    protected void addWidgets()
    {
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 81, topPos + 41));
        addRenderableWidget(new MachineUpgradesButton(rightPos, topPos + 3, this));
        addRenderableWidget(new OpenIOControlButton(rightPos, topPos + 23, this, IO_CONTROLS_BUTTON_ID, MachineInputType.ITEMS));
        addRenderableWidget(new OpenIOControlButton(rightPos, topPos + 43, this, IO_CONTROLS_BUTTON_ID, MachineInputType.ENERGY));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return SCREEN_TEXTURE;
    }
}