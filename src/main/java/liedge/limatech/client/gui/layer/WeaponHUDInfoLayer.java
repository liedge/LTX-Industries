package liedge.limatech.client.gui.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import static liedge.limatech.LimaTechConstants.*;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.*;

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
        WeaponAmmoSource ammoSource = WeaponItem.getAmmoSourceFromItem(heldItem);

        int ammo = weaponItem.getAmmoLoaded(heldItem);
        int ammoTextColor = (ammo > 0 || ammoSource == WeaponAmmoSource.INFINITE) ? LIME_GREEN.packedRGB() : 16733525;
        int x = 10;

        PoseStack poseStack = graphics.pose();

        if (ammoSource == WeaponAmmoSource.NORMAL)
        {
            int y = (graphics.guiHeight() - NORMAL_AMMO_INDICATOR.height()) / 2;
            NORMAL_AMMO_INDICATOR.singleBlit(graphics, x, y);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor);

            poseStack.pushPose();

            poseStack.translate(x + 31, y + 4.25f, 0);
            poseStack.scale(0.8f, 0.8f, 1f);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(weaponItem.getAmmoCapacity(heldItem)), 0, 0, 0x9a9a9a, false);

            poseStack.popPose();
        }
        else if (ammoSource == WeaponAmmoSource.COMMON_ENERGY_UNIT)
        {
            int y = (graphics.guiHeight() - ENERGY_AMMO_INDICATOR.height()) / 2;
            int energy = (int) (LimaMathUtil.divideFloat(weaponItem.getEnergyStored(heldItem), weaponItem.getEnergyCapacity(heldItem)) * 100f);
            String energyText = energy + "%";

            ENERGY_AMMO_INDICATOR.singleBlit(graphics, x, y);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor);

            poseStack.pushPose();

            poseStack.translate(x + 31, y + 4.25f, 0);
            poseStack.scale(0.8f, 0.8f, 1f);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(weaponItem.getAmmoCapacity(heldItem)), 0, 0, 0x9a9a9a, false);

            int width = Minecraft.getInstance().font.width(energyText);
            graphics.drawString(Minecraft.getInstance().font, energyText, -4 + (24 - width) / 2, 12, REM_BLUE.packedRGB(), false);

            poseStack.popPose();
        }
        else
        {
            int y = (graphics.guiHeight() - INFINITE_AMMO_INDICATOR.height()) / 2;
            INFINITE_AMMO_INDICATOR.singleBlit(graphics, x, y);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor);

            poseStack.pushPose();

            poseStack.translate(x + 32, y + 1, 0);
            poseStack.scale(1.5f, 1.5f, 1f);
            graphics.drawString(Minecraft.getInstance().font, LimaComponentUtil.INFINITY_SYMBOL, 0, 0, CREATIVE_PINK.packedRGB(), false);

            poseStack.popPose();
        }
    }
}