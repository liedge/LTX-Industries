package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.CookingMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BaseCookingMenuScreen extends LTXIMachineScreen<CookingMachineMenu<?>>
{
    public static MenuScreens.ScreenConstructor<CookingMachineMenu<?>, BaseCookingMenuScreen> provider(CookingType cookingType)
    {
        return (menu, inventory, title) -> new BaseCookingMenuScreen(menu, inventory, title, cookingType);
    }

    private final CookingType cookingType;

    private BaseCookingMenuScreen(CookingMachineMenu<?> menu, Inventory inventory, Component title, CookingType cookingType)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.cookingType = cookingType;
    }

    public CookingType getCookingType()
    {
        return cookingType;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
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

    public enum CookingType
    {
        SMELTING,
        SMOKING,
        BLASTING
    }
}