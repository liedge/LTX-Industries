package liedge.ltxindustries.client.gui.layer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.ltxindustries.client.item.WeaponClientItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class WeaponCrosshairLayer extends LimaGuiLayer
{
    private static final Identifier RELOAD_SPRITE = RESOURCES.id("hud/reload");
    private static final Identifier RELOAD_SPRITE_FILL = RESOURCES.id("hud/reload_fill");

    public static final WeaponCrosshairLayer INSTANCE = new WeaponCrosshairLayer();

    private WeaponCrosshairLayer()
    {
        super(RESOURCES.id("crosshair"));
    }

    @Override
    protected boolean isVisible(LocalPlayer player)
    {
        return super.isVisible(player) && Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getMainHandItem().getItem() instanceof WeaponItem;
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphicsExtractor graphics, float partialTicks)
    {
        final int screenWidth = graphics.guiWidth();
        final int screenHeight = graphics.guiHeight();

        ItemStack heldItem = player.getMainHandItem();
        WeaponItem weaponItem = (WeaponItem) heldItem.getItem();
        ClientExtendedInput controls = ClientExtendedInput.of(player);

        if (controls.getReloadTimer().isRunningClient())
        {
            final int centerX = (screenWidth - 21) / 2;
            final int centerY = (screenHeight - 9) / 2;

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, RELOAD_SPRITE, centerX, centerY, 21, 9);
            TextureAtlasSprite fillSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(RELOAD_SPRITE_FILL);
            LimaGuiUtil.partialHorizontalBlit(graphics, RenderPipelines.GUI_TEXTURED, fillSprite, centerX + 5, centerY + 3, 14, 3, controls.getReloadTimer().lerpProgressNotPaused(partialTicks), -1);
        }
        else
        {
            RenderPipeline pipeline = LTXIClientConfig.SOLID_COLOR_CROSSHAIR.getAsBoolean() ? RenderPipelines.GUI_TEXTURED : RenderPipelines.CROSSHAIR;
            WeaponClientItem.of(weaponItem)
                    .getCrosshairRenderer()
                    .render(graphics, pipeline, player, weaponItem, controls, screenWidth, screenHeight, LTXIClientConfig.getCrosshairColor(), partialTicks);
        }
    }
}