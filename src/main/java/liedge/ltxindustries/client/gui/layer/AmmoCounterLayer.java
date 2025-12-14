package liedge.ltxindustries.client.gui.layer;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;
import static liedge.ltxindustries.LTXIConstants.LIME_GREEN;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class AmmoCounterLayer extends LimaGuiLayer
{
    private static final ResourceLocation AMMO_COUNTER_SPRITE = RESOURCES.location("hud/ammo");
    private static final ResourceLocation ENERGY_COUNTER_SPRITE = RESOURCES.location("hud/energy_ammo");
    private static final ResourceLocation ENERGY_FILL_SPRITE = ENERGY_COUNTER_SPRITE.withSuffix("_fill");
    private static final ResourceLocation INFINITE_COUNTER_SPRITE = RESOURCES.location("hud/infinite_ammo");
    private static final int NO_CAPACITY = 0;
    private static final float NO_ENERGY = -1f;

    public static final AmmoCounterLayer AMMO_COUNTER_LAYER = new AmmoCounterLayer();

    private AmmoCounterLayer()
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
        WeaponReloadSource.Type reloadType = weaponItem.getReloadSource(heldItem).getType();
        int ammo = weaponItem.getAmmoLoaded(heldItem);

        switch (reloadType)
        {
            case ITEM -> renderCounter(graphics, AMMO_COUNTER_SPRITE, 44, 13, ammo, ammoColor(ammo), weaponItem.getAmmoCapacity(heldItem), NO_ENERGY);
            case COMMON_ENERGY -> renderCounter(graphics, ENERGY_COUNTER_SPRITE, 44, 19, ammo, ammoColor(ammo), weaponItem.getAmmoCapacity(heldItem), weaponItem.getChargePercentage(heldItem));
            case INFINITE -> renderCounter(graphics, INFINITE_COUNTER_SPRITE, 36, 13, ammo, LIME_GREEN.argb32(), NO_CAPACITY, NO_ENERGY);
        }
    }

    private void renderCounter(GuiGraphics graphics, ResourceLocation sprite, int width, int height, int ammo, int ammoColor, int capacity, float energyFill)
    {
        Font font = Minecraft.getInstance().font;
        HorizontalAlignment xAlign = LTXIClientConfig.getWeaponHorizontalAlign();
        VerticalAlignment yAlign = LTXIClientConfig.getWeaponVerticalAlign();
        int x = xAlign.getAbsoluteX(width, graphics.guiWidth(), LTXIClientConfig.WEAPON_HUD_X_OFFSET.getAsInt());
        int y = yAlign.getAbsoluteY(height, graphics.guiHeight(), LTXIClientConfig.WEAPON_HUD_Y_OFFSET.getAsInt());

        graphics.blitSprite(sprite, x, y, width, height);

        String ammoStr = Integer.toString(ammo);
        int ammoWidth = font.width(ammoStr);
        graphics.drawString(font, ammoStr, x + 12 - (ammoWidth / 2), y + 3, ammoColor, false);

        if (capacity > NO_CAPACITY)
        {
            String capStr = Integer.toString(capacity);
            int capWidth = font.width(capStr);
            graphics.drawString(font, capStr, x + 33 - (capWidth / 2), y + 3, 0x9a9a9a, false);
        }

        if (energyFill != NO_ENERGY)
        {
            TextureAtlasSprite energySprite = Minecraft.getInstance().getGuiSprites().getSprite(ENERGY_FILL_SPRITE);
            LimaGuiUtil.partialHorizontalBlit(graphics, x + 2, y + 13, 40, 4, energyFill, energySprite);
        }
    }

    private int ammoColor(int ammo)
    {
        return ammo > 0 ? LIME_GREEN.argb32() : HOSTILE_ORANGE.argb32();
    }
}