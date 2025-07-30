package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.blockentity.StateBlockRecipeMachineBlockEntity;
import liedge.ltxindustries.client.gui.widget.EnergyGaugeWidget;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.SidedUpgradableMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

public class SingleItemRecipeScreen<CTX extends StateBlockRecipeMachineBlockEntity<?, ?>, M extends SidedUpgradableMachineMenu<CTX>> extends SidedUpgradableMachineScreen<M>
{
    @SuppressWarnings("RedundantTypeArguments")
    public static <CTX extends StateBlockRecipeMachineBlockEntity<?, ?>, M extends SidedUpgradableMachineMenu<CTX>> void registerScreen(RegisterMenuScreensEvent event, Supplier<? extends MenuType<M>> menuTypeSupplier, RecipeScreenType recipeScreenType)
    {
        event.<M, SingleItemRecipeScreen<CTX, M>>register(menuTypeSupplier.get(), (menu, inventory, title) -> new SingleItemRecipeScreen<>(menu, inventory, title, recipeScreenType));
    }

    private final RecipeScreenType recipeScreenType;

    private SingleItemRecipeScreen(M menu, Inventory inventory, Component title, RecipeScreenType recipeScreenType)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.recipeScreenType = recipeScreenType;
    }

    public RecipeScreenType getRecipeScreenType()
    {
        return recipeScreenType;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext(), leftPos + 10, topPos + 9));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 75, topPos + 39));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 83);
        blitPowerInSlot(guiGraphics, 7, 52);
        blitEmptySlot(guiGraphics, 53, 33);
        blitOutputSlot(guiGraphics, 103, 31);
    }
}