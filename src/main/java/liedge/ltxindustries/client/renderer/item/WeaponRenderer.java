package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.LimaSpecialItemRenderer;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.client.renderer.LTXIArmPoses;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class WeaponRenderer<T extends WeaponItem> extends LimaSpecialItemRenderer<T> implements IClientItemExtensions
{
    static final ResourceLocation HOLLOW_DOT = RESOURCES.location("crosshair/hollow_dot");
    static final ResourceLocation CIRCLE_BRACKET = RESOURCES.location("crosshair/circle_bracket");
    static final ResourceLocation ANGLE_BRACKET = RESOURCES.location("crosshair/angle_bracket");
    static final ResourceLocation AOE_HORIZONTAL = RESOURCES.location("crosshair/aoe_h");
    static final ResourceLocation AOE_VERTICAL = RESOURCES.location("crosshair/aoe_v");
    static final ResourceLocation HEAVY_PISTOL_CROSSHAIR = RESOURCES.location("crosshair/heavy_pistol");

    public static WeaponRenderer<?> fromItem(WeaponItem weaponItem)
    {
        return LimaCoreUtil.castOrThrow(WeaponRenderer.class, IClientItemExtensions.of(weaponItem));
    }

    protected BakedItemLayer rootBaseLayer;
    protected BakedItemLayer rootEmissiveLayer;
    private GuiSpriteManager sprites;

    @Override
    protected void onResourceManagerReload(ResourceManager manager, T item)
    {
        ItemLayerBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(item), ItemLayerBakedModel.class);
        this.rootBaseLayer = model.getLayer("root");
        this.rootEmissiveLayer = model.getLayer("root emissive");
        this.sprites = Minecraft.getInstance().getGuiSprites();
        loadWeaponModelParts(item, model);
    }

    @Override
    protected void renderCustomItem(ItemStack stack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int light, int overlay)
    {
        if (isFirstPersonMainHand(displayContext) && Minecraft.getInstance().player != null)
        {
            ClientWeaponControls controls = ClientWeaponControls.of(Minecraft.getInstance().player);
            renderWeaponFirstPerson(stack, item, displayContext, poseStack, bufferSource, light, overlay, partialTick, controls);
        }
        else
        {
            renderStaticWeapon(stack, item, displayContext, poseStack, bufferSource, light, overlay);
        }
    }

    @Override
    public final BlockEntityWithoutLevelRenderer getCustomRenderer()
    {
        return this;
    }

    @Override
    public @Nullable HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack heldItem)
    {
        if (hand == InteractionHand.MAIN_HAND && heldItem.getItem() instanceof WeaponItem weaponItem) // Should always be the case, but we'll avoid castOrThrow()
        {
            ItemStack offHandStack = entity.getOffhandItem();

            if (offHandStack.isEmpty())
            {
                return LTXIArmPoses.TWO_HANDED_WEAPON.getValue();
            }
            else if (entity.isUsingItem() && entity.getUsedItemHand() == InteractionHand.OFF_HAND && offHandStack.canPerformAction(ItemAbilities.SHIELD_BLOCK))
            {
                return LTXIArmPoses.WEAPON_SHIELD_POSE.getValue();
            }
            else if (weaponItem.isOneHanded(heldItem))
            {
                return LTXIArmPoses.ONE_HANDED_WEAPON.getValue();
            }
        }

        return HumanoidModel.ArmPose.EMPTY; // Neutral pose, weapon at rest (i.e. holding weapon in offhand or holding an offhand item with a two-handed weapon)
    }

    public abstract void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor);

    public abstract void onWeaponFired(ItemStack stack, WeaponItem weaponItem, ClientWeaponControls controls);

    protected abstract void loadWeaponModelParts(T item, ItemLayerBakedModel model);

    protected abstract void renderStaticWeapon(ItemStack stack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay);

    protected abstract void renderWeaponFirstPerson(ItemStack stack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, ClientWeaponControls controls);

    protected void renderStaticMagazineFill(T item, ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, TranslucentFillModel fillModel, LimaColor color)
    {
        float fill = Math.min(LimaMathUtil.divideFloat(item.getAmmoLoaded(stack), item.getAmmoCapacity(stack)), 1f);
        if (fill > 0)
        {
            VertexConsumer buffer = bufferSource.getBuffer(LTXIRenderTypes.POSITION_COLOR_QUADS);
            fillModel.renderRotated(buffer, poseStack, color, fill);
        }
    }

    protected void renderAnimatedMagazineFill(T item, ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, TranslucentFillModel fillModel, LimaColor color, float partialTick, ClientWeaponControls controls)
    {
        if (!controls.getReloadTimer().isRunningClient())
        {
            renderStaticMagazineFill(item, stack, poseStack, bufferSource, fillModel, color);
        }
        else
        {
            float mul = controls.getReloadTimer().lerpProgressNotPaused(partialTick);

            VertexConsumer buffer = bufferSource.getBuffer(LTXIRenderTypes.POSITION_COLOR_QUADS);
            fillModel.renderRotated(buffer, poseStack, color, mul);
        }
    }

    protected void blitSprite(GuiGraphics graphics, float x, float y, int width, int height, LimaColor crosshairColor, ResourceLocation spriteLocation)
    {
        LimaGuiUtil.directColorBlit(graphics, x, y, width, height, crosshairColor.red(), crosshairColor.green(), crosshairColor.blue(), 1f, sprites.getSprite(spriteLocation));
    }

    protected void blitMirroredUSprite(GuiGraphics graphics, float x, float y, int width, int height, LimaColor crosshairColor, ResourceLocation spriteLocation)
    {
        TextureAtlasSprite sprite = sprites.getSprite(spriteLocation);
        LimaGuiUtil.directColorBlit(graphics, sprite.atlasLocation(), x, y, x + width, y + height, 0, sprite.getU1(), sprite.getU0(), sprite.getV0(), sprite.getV1(), crosshairColor.red(), crosshairColor.green(), crosshairColor.blue(), 1f);
    }

    protected void blitMirroredVSprite(GuiGraphics graphics, float x, float y, int width, int height, LimaColor crosshairColor, ResourceLocation spriteLocation)
    {
        TextureAtlasSprite sprite = sprites.getSprite(spriteLocation);
        LimaGuiUtil.directColorBlit(graphics, sprite.atlasLocation(), x, y, x + width, y + height, 0, sprite.getU0(), sprite.getU1(), sprite.getV1(), sprite.getV0(), crosshairColor.red(), crosshairColor.green(), crosshairColor.blue(), 1f);
    }
}