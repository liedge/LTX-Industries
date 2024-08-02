package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.LimaSpecialItemRenderer;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.model.baked.WeaponAmmoDisplay;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.LocalWeaponInput;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import static liedge.limatech.client.gui.layer.HUDOverlaySprites.AMMO_INDICATOR;

public abstract class WeaponRenderProperties<T extends WeaponItem> extends LimaSpecialItemRenderer<T> implements IClientItemExtensions
{
    public static WeaponRenderProperties<?> fromItem(WeaponItem weaponItem)
    {
        return LimaCoreUtil.castOrThrow(WeaponRenderProperties.class, IClientItemExtensions.of(weaponItem));
    }

    protected DynamicModularBakedModel.SubModel mainSubmodel;

    @Override
    protected void onResourceManagerReload(ResourceManager manager, T item)
    {
        DynamicModularBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(item), DynamicModularBakedModel.class);
        this.mainSubmodel = model.getSubmodel("main");
        loadWeaponModelParts(item, model);
    }

    @Override
    protected void renderCustomItem(ItemStack stack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int light, int overlay)
    {
        if (isFirstPersonMainHand(displayContext))
        {
            LocalWeaponInput input = LocalWeaponInput.LOCAL_WEAPON_INPUT;
            float recoilA = input.getRecoilTimerA().lerpProgressNotPaused(partialTick);
            float recoilB = input.getRecoilTimerB().lerpProgressNotPaused(partialTick);
            renderHeldWeapon(stack, item, displayContext, poseStack, bufferSource, partialTick, light, overlay, recoilA, recoilB);
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
    public abstract HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack heldItem);

    public abstract void renderCrosshair(WeaponItem weaponItem, LocalWeaponInput weaponInput, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor);

    protected abstract void loadWeaponModelParts(T item, DynamicModularBakedModel model);

    protected abstract void renderStaticWeapon(ItemStack stack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay);

    protected abstract void renderHeldWeapon(ItemStack stack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int light, int overlay, float recoilA, float recoilB);

    public void renderHUDInfo(ItemStack stack, WeaponItem weaponItem, GuiGraphics graphics)
    {
        // By default, render ammo information
        final int x = 10;
        final int y = (graphics.guiHeight() - AMMO_INDICATOR.height()) / 2;

        AMMO_INDICATOR.singleBlit(graphics, x, y);

        int ammo = stack.getOrDefault(LimaTechDataComponents.WEAPON_AMMO, 0);
        int capacity = weaponItem.getAmmoCapacity(stack);

        final int ammoTextColor = (ammo > 0) ? LimaTechConstants.LIME_GREEN.rgb() : 16733525;

        graphics.drawString(Minecraft.getInstance().font, Integer.toString(ammo), x + 5, y + 3, ammoTextColor, false);

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        poseStack.translate(x + 31, y + 4.25f, 0);
        poseStack.scale(0.8f, 0.8f, 1f);

        graphics.drawString(Minecraft.getInstance().font, Integer.toString(capacity), 0, 0, 0x9a9a9a, false);

        poseStack.popPose();
    }

    protected void renderAmmoDisplay(T item, ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, WeaponAmmoDisplay display, LimaColor ammoColor)
    {
        float fill = LimaMathUtil.divideFloat(stack.getOrDefault(LimaTechDataComponents.WEAPON_AMMO, 0), item.getAmmoCapacity(stack));
        if (fill > 0)
        {
            VertexConsumer buffer = bufferSource.getBuffer(LimaTechRenderTypes.POSITION_COLOR_TRANSLUCENT);
            display.renderAmmoDisplay(buffer, poseStack, ammoColor, fill);
        }
    }
}