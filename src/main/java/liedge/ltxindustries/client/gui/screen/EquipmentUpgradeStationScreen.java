package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.menu.EquipmentUpgradeStationMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EquipmentUpgradeStationScreen extends UpgradesConfigScreen<EquipmentUpgrade, EquipmentUpgradeStationMenu>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("gui", "equipment_upgrades");

    public EquipmentUpgradeStationScreen(EquipmentUpgradeStationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    @Override
    protected int upgradeRemovalButtonId()
    {
        return 0;
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }
}