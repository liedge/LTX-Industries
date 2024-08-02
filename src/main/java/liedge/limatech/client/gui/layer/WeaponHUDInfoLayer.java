package liedge.limatech.client.gui.layer;

import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limatech.LimaTech;
import liedge.limatech.client.renderer.item.WeaponRenderProperties;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class WeaponHUDInfoLayer extends LimaGuiLayer
{
    public static final WeaponHUDInfoLayer WEAPON_HUD_INFO_LAYER = new WeaponHUDInfoLayer();

    private WeaponHUDInfoLayer()
    {
        super(LimaTech.RESOURCES.location("weapon_hud"));
    }

    @Override
    protected boolean shouldRender(LocalPlayer player)
    {
        return Minecraft.getInstance().options.getCameraType().isFirstPerson() && !player.isSpectator() && player.getMainHandItem().getItem() instanceof WeaponItem;
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks)
    {
        ItemStack heldItem = player.getMainHandItem();
        WeaponItem weaponItem = (WeaponItem) heldItem.getItem();

        WeaponRenderProperties.fromItem(weaponItem).renderHUDInfo(heldItem, weaponItem, graphics);
    }
}