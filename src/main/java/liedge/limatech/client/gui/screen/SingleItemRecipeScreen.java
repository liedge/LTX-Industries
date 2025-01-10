package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.SimpleRecipeMachineBlockEntity;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.MachineProgressWidget;
import liedge.limatech.client.gui.widget.MachineUpgradesButton;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import liedge.limatech.menu.SidedUpgradableMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static liedge.limatech.menu.SidedUpgradableMachineMenu.IO_CONTROLS_BUTTON_ID;

public class SingleItemRecipeScreen<CTX extends SimpleRecipeMachineBlockEntity<?, ?>, M extends SidedUpgradableMachineMenu<CTX>> extends SidedUpgradableMachineScreen<M>
{
    public static final ResourceLocation SCREEN_TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "single_input_machine");

    public SingleItemRecipeScreen(M menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
    }

    @Override
    protected void addWidgets()
    {
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 75, topPos + 39));

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