package liedge.limatech.client.gui.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limatech.LimaTech;
import liedge.limatech.client.renderer.item.WeaponRenderProperties;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.util.config.LimaTechClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.RELOADING_INDICATOR;
import static liedge.limatech.client.gui.layer.HUDOverlaySprites.RELOADING_INDICATOR_INNER;

public final class WeaponCrosshairLayer extends LimaGuiLayer
{
    public static final WeaponCrosshairLayer CROSSHAIR_LAYER = new WeaponCrosshairLayer();

    private WeaponCrosshairLayer()
    {
        super(LimaTech.RESOURCES.location("crosshair"));
    }

    @Override
    protected boolean isVisible(LocalPlayer player)
    {
        return super.isVisible(player) && Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getMainHandItem().getItem() instanceof WeaponItem;
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks)
    {
        final int screenWidth = graphics.guiWidth();
        final int screenHeight = graphics.guiHeight();

        ItemStack heldItem = player.getMainHandItem();
        WeaponItem weaponItem = (WeaponItem) heldItem.getItem();
        ClientWeaponControls controls = ClientWeaponControls.of(player);

        if (controls.getReloadTimer().isRunningClient())
        {
            final int centerX = (screenWidth - RELOADING_INDICATOR.width()) / 2;
            final int centerY = (screenHeight - RELOADING_INDICATOR.height()) / 2;

            RELOADING_INDICATOR.singleBlit(graphics, centerX, centerY);
            RELOADING_INDICATOR_INNER.partialHorizontalBlit(graphics, centerX + 17, centerY + 6, controls.getReloadTimer().lerpProgressNotPaused(partialTicks));
        }
        else
        {
            if (!LimaTechClientConfig.isSolidCrosshairColor())
            {
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO);
            }

            WeaponRenderProperties.fromItem(weaponItem).renderCrosshair(player, weaponItem, controls, graphics, partialTicks, screenWidth, screenHeight, LimaTechClientConfig.getCrosshairColor());

            RenderSystem.defaultBlendFunc();
        }
    }
}