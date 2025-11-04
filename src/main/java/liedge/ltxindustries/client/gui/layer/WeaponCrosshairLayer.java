package liedge.ltxindustries.client.gui.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.ltxindustries.client.renderer.item.WeaponRenderer;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class WeaponCrosshairLayer extends LimaGuiLayer
{
    private static final ResourceLocation RELOAD_SPRITE = RESOURCES.location("reload_display_outer");
    private static final ResourceLocation RELOAD_SPRITE_INNER = RESOURCES.location("reload_display_inner");

    public static final WeaponCrosshairLayer CROSSHAIR_LAYER = new WeaponCrosshairLayer();

    private WeaponCrosshairLayer()
    {
        super(RESOURCES.location("crosshair"));
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
            final int centerX = (screenWidth - 35) / 2;
            final int centerY = (screenHeight - 15) / 2;

            graphics.blitSprite(RELOAD_SPRITE, centerX, centerY, 35, 15);
            TextureAtlasSprite innerSprite = Minecraft.getInstance().getGuiSprites().getSprite(RELOAD_SPRITE_INNER);
            LimaGuiUtil.partialHorizontalBlit(graphics, centerX + 17, centerY + 6, 16, 3, controls.getReloadTimer().lerpProgressNotPaused(partialTicks), innerSprite);
        }
        else
        {
            if (!LTXIClientConfig.SOLID_COLOR_CROSSHAIR.getAsBoolean())
            {
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO);
            }

            WeaponRenderer.fromItem(weaponItem).renderCrosshair(player, weaponItem, controls, graphics, partialTicks, screenWidth, screenHeight, LTXIClientConfig.getCrosshairColor());

            RenderSystem.defaultBlendFunc();
        }
    }
}