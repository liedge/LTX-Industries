package liedge.ltxindustries.client.gui.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.util.LimaMathUtil;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.WeaponAmmoSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.LTXIConstants.*;

public final class WeaponHUDInfoLayer extends LimaGuiLayer
{
    private static final ResourceLocation AMMO_DISPLAY_SPRITE = RESOURCES.location("ammo_display");
    private static final ResourceLocation ENERGY_AMMO_DISPLAY_SPRITE = RESOURCES.location("energy_ammo_display");
    private static final ResourceLocation INFINITE_AMMO_DISPLAY_SPRITE = RESOURCES.location("infinite_ammo_display");

    public static final WeaponHUDInfoLayer WEAPON_HUD_INFO_LAYER = new WeaponHUDInfoLayer();

    private WeaponHUDInfoLayer()
    {
        super(RESOURCES.location("weapon_hud"));
    }

    @Override
    protected boolean isVisible(LocalPlayer player)
    {
        return super.isVisible(player) && player.getMainHandItem().getItem() instanceof WeaponItem;
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks)
    {
        ItemStack heldItem = player.getMainHandItem();
        WeaponItem weaponItem = (WeaponItem) heldItem.getItem();
        WeaponAmmoSource ammoSource = WeaponItem.getAmmoSourceFromItem(heldItem);

        int ammo = weaponItem.getAmmoLoaded(heldItem);
        int ammoTextColor = (ammo > 0 || ammoSource == WeaponAmmoSource.INFINITE) ? LIME_GREEN.argb32() : 16733525;
        int x = 10;

        PoseStack poseStack = graphics.pose();

        if (ammoSource == WeaponAmmoSource.NORMAL)
        {
            int y = (graphics.guiHeight() - 13) / 2;
            graphics.blitSprite(AMMO_DISPLAY_SPRITE, x, y, 49, 13);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor);

            poseStack.pushPose();

            poseStack.translate(x + 31, y + 4.25f, 0);
            poseStack.scale(0.8f, 0.8f, 1f);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(weaponItem.getAmmoCapacity(heldItem)), 0, 0, 0x9a9a9a, false);

            poseStack.popPose();
        }
        else if (ammoSource == WeaponAmmoSource.COMMON_ENERGY_UNIT)
        {
            int y = (graphics.guiHeight() - 23) / 2;
            int energy = (int) (LimaMathUtil.divideFloat(weaponItem.getEnergyStored(heldItem), weaponItem.getEnergyCapacity(heldItem)) * 100f);
            String energyText = energy + "%";

            graphics.blitSprite(ENERGY_AMMO_DISPLAY_SPRITE, x, y, 49, 23);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor);

            poseStack.pushPose();

            poseStack.translate(x + 31, y + 4.25f, 0);
            poseStack.scale(0.8f, 0.8f, 1f);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(weaponItem.getAmmoCapacity(heldItem)), 0, 0, 0x9a9a9a, false);

            int width = Minecraft.getInstance().font.width(energyText);
            graphics.drawString(Minecraft.getInstance().font, energyText, -4 + (24 - width) / 2, 12, REM_BLUE.argb32(), false);

            poseStack.popPose();
        }
        else
        {
            int y = (graphics.guiHeight() - 13) / 2;
            graphics.blitSprite(INFINITE_AMMO_DISPLAY_SPRITE, x, y, 47, 13);
            graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor);

            poseStack.pushPose();

            poseStack.translate(x + 32, y + 1, 0);
            poseStack.scale(1.5f, 1.5f, 1f);
            graphics.drawString(Minecraft.getInstance().font, LimaComponentUtil.INFINITY_SYMBOL, 0, 0, CREATIVE_PINK.argb32(), false);

            poseStack.popPose();
        }
    }
}