package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.menu.EquipmentUpgradeStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EquipmentUpgradeStationScreen extends UpgradesConfigScreen<EquipmentUpgrade, EquipmentUpgradeStationMenu>
{
    private static final ResourceLocation SLOT_SPRITE = LTXIndustries.RESOURCES.location("slot/equipment_module");

    public EquipmentUpgradeStationScreen(EquipmentUpgradeStationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    @Override
    protected void blitSlotSprites(GuiGraphics graphics)
    {
        blitSlotSprite(graphics, SLOT_SPRITE, 23, 86);
        blitOutputSlot(graphics, 21, 62);
    }
}