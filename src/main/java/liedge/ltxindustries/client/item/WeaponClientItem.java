package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.gui.layer.EquipmentHUDLayer;
import liedge.ltxindustries.client.renderer.LTXIArmPoses;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.Nullable;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;
import static liedge.ltxindustries.LTXIConstants.LIME_GREEN;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class WeaponClientItem implements EquipmentHUDLayer.Renderer
{
    public static @Nullable WeaponClientItem of(WeaponItem item)
    {
        return LimaCoreObjects.tryCast(WeaponClientItem.class, IClientItemExtensions.of(item));
    }

    // Ammo counter sprites
    private static final Identifier AMMO_COUNTER_SPRITE = RESOURCES.id("hud/ammo");
    private static final Identifier ENERGY_COUNTER_SPRITE = RESOURCES.id("hud/energy_ammo");
    private static final Identifier ENERGY_FILL_SPRITE = ENERGY_COUNTER_SPRITE.withSuffix("_fill");
    private static final Identifier INFINITE_COUNTER_SPRITE = RESOURCES.id("hud/infinite_ammo");
    private static final int NO_CAPACITY = 0;
    private static final float NO_ENERGY = -1f;

    // Crosshair sprites
    static final Identifier HOLLOW_DOT = RESOURCES.id("crosshair/hollow_dot");
    static final Identifier CIRCLE_BRACKET = RESOURCES.id("crosshair/circle_bracket");
    static final Identifier ANGLE_BRACKET = RESOURCES.id("crosshair/angle_bracket");
    static final Identifier AOE_HORIZONTAL = RESOURCES.id("crosshair/aoe_h");
    static final Identifier AOE_VERTICAL = RESOURCES.id("crosshair/aoe_v");
    static final Identifier HEAVY_PISTOL_CROSSHAIR = RESOURCES.id("crosshair/heavy_pistol");

    // Class def
    private final int crosshairWidth;
    private final int crosshairHeight;

    WeaponClientItem(int crosshairWidth, int crosshairHeight)
    {
        this.crosshairWidth = crosshairWidth;
        this.crosshairHeight = crosshairHeight;
    }

    public void extractCrosshairs(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem,
                                  ClientExtendedInput controls, int screenWidth, int screenHeight, LimaColor color, float partialTick)
    {
        int centerX = (screenWidth - crosshairWidth) / 2;
        int centerY = (screenHeight - crosshairHeight) / 2;

        extractCrosshairSprites(graphics, pipeline, player, weaponItem, controls, centerX, centerY, color, partialTick);
    }

    protected abstract void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem,
                                                    ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick);

    public void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientExtendedInput controls) { }

    public void onMainHandTick(ItemStack stack, WeaponItem weaponItem, ClientExtendedInput controls) { }

    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack heldItem)
    {
        if (hand == InteractionHand.MAIN_HAND && heldItem.getItem() instanceof WeaponItem weaponItem)
        {
            ItemStack offhand = entity.getOffhandItem();

            if (offhand.isEmpty())
            {
                return LTXIArmPoses.TWO_HANDED_WEAPON.getValue();
            }
            else if (entity.isUsingItem() && entity.getUsedItemHand() == InteractionHand.OFF_HAND && offhand.has(DataComponents.BLOCKS_ATTACKS))
            {
                return LTXIArmPoses.WEAPON_SHIELD_POSE.getValue();
            }
            else if (weaponItem.isOneHanded(heldItem))
            {
                return LTXIArmPoses.ONE_HANDED_WEAPON.getValue();
            }
        }

        return HumanoidModel.ArmPose.EMPTY;
    }

    //#region HUD rendering

    @Override
    public void renderHUDLayer(GuiGraphicsExtractor graphics, LocalPlayer player, ItemStack heldItem, HorizontalAlignment xAlign, VerticalAlignment yAlign, int xOffset, int yOffset, float partialTick)
    {
        if (!(heldItem.getItem() instanceof WeaponItem weaponItem)) return;

        WeaponReloadSource.Type reloadType = weaponItem.getReloadSource(heldItem).getType();
        int ammo = weaponItem.getAmmoLoaded(heldItem);

        switch (reloadType)
        {
            case ITEM -> renderAmmoCounter(graphics, AMMO_COUNTER_SPRITE, xAlign, yAlign, xOffset, yOffset, 44, 13, ammo, ammoColor(ammo), weaponItem.getAmmoCapacity(heldItem), NO_ENERGY);
            case COMMON_ENERGY -> renderAmmoCounter(graphics, ENERGY_COUNTER_SPRITE, xAlign, yAlign, xOffset, yOffset, 44, 19, ammo, ammoColor(ammo), weaponItem.getAmmoCapacity(heldItem), weaponItem.getChargePercentage(heldItem));
            case INFINITE -> renderAmmoCounter(graphics, INFINITE_COUNTER_SPRITE, xAlign, yAlign, xOffset, yOffset, 36, 13, ammo, LIME_GREEN.argb32(), NO_CAPACITY, NO_ENERGY);
        }
    }

    private void renderAmmoCounter(GuiGraphicsExtractor graphics, Identifier sprite, HorizontalAlignment xAlign, VerticalAlignment yAlign, int xOffset, int yOffset,
                                   int width, int height, int ammo, int ammoColor, int capacity, float energyFill)
    {
        Font font = Minecraft.getInstance().font;
        int x = xAlign.getAbsoluteX(width, graphics.guiWidth(), xOffset);
        int y = yAlign.getAbsoluteY(height, graphics.guiHeight(), yOffset);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);

        String ammoStr = Integer.toString(ammo);
        int ammoWidth = font.width(ammoStr);
        graphics.text(font, ammoStr, x + 12 - (ammoWidth / 2), y + 3, ammoColor, false);

        if (capacity > NO_CAPACITY)
        {
            String capStr = Integer.toString(capacity);
            int capWidth = font.width(capStr);
            graphics.text(font, capStr, x + 33 - (capWidth / 2), y + 3, 0xff9a9a9a, false);
        }

        if (energyFill != NO_ENERGY)
        {
            TextureAtlasSprite energySprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(ENERGY_FILL_SPRITE);
            LimaGuiUtil.partialHorizontalBlit(graphics, RenderPipelines.GUI_TEXTURED, energySprite, x + 2, y + 13, 40, 4, energyFill, -1);
        }
    }

    private int ammoColor(int ammo)
    {
        return ammo > 0 ? LIME_GREEN.argb32() : HOSTILE_ORANGE.argb32();
    }

    //#endregion

    //#region Crosshair render helpers

    protected float triggerCurve(ClientExtendedInput controls, WeaponItem weaponItem, float threshold, float partialTick)
    {
        return LTXIRenderer.linearThresholdCurve(controls.lerpTriggerTimer(weaponItem, partialTick), threshold);
    }

    protected void blitSprite(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier spriteId, float x, float y, int width, int height, LimaColor color)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spriteId);
        LimaGuiUtil.floatBlit(graphics, pipeline, sprite, x, y, width, height, color.argb32());
    }

    protected void blitSpriteMirrorU(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier spriteId, float x, float y, int width, int height, LimaColor color)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spriteId);
        LimaGuiUtil.floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + height, sprite.getU1(), sprite.getU0(), sprite.getV0(), sprite.getV1(), color.argb32());
    }

    protected void blitSpriteMirrorV(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier spriteId, float x, float y, int width, int height, LimaColor color)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spriteId);
        LimaGuiUtil.floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + height, sprite.getU0(), sprite.getU1(), sprite.getV1(), sprite.getV0(), color.argb32());
    }

    //#endregion
}