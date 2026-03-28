package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.menu.UpgradeStationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class UpgradeStationScreen extends UpgradesConfigScreen<EquipmentUpgrade, UpgradeStationMenu>
{
    private static final Identifier SLOT_SPRITE = LTXIndustries.RESOURCES.id("slot/equipment_module");

    public UpgradeStationScreen(UpgradeStationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 0);
    }

    @Override
    protected void blitSlotSprites(GuiGraphicsExtractor graphics)
    {
        blitSlotSprite(graphics, SLOT_SPRITE, 23, 86);
        blitOutputSlot(graphics, 21, 62);
    }

    @Override
    protected Identifier fallbackModuleSprite()
    {
        return UpgradesConfigScreen.EQUIPMENT_MODULE_SPRITE;
    }
}