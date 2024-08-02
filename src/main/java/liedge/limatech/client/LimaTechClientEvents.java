package liedge.limatech.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.client.renderer.BubbleShieldRenderer;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import liedge.limatech.client.renderer.item.LimaTechItemRenderers;
import liedge.limatech.item.ScrollModeSwitchItem;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.LocalWeaponInput;
import liedge.limatech.network.packet.ServerboundItemModeSwitchPacket;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

import static liedge.limatech.LimaTechConstants.REM_BLUE;

@EventBusSubscriber(modid = LimaTech.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class LimaTechClientEvents
{
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Pre event)
    {
        LimaTechItemRenderers.tickValidRenderers();

        BubbleShieldRenderer.SHIELD_RENDERER.tickRenderer();

        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            ItemStack heldItem = player.getMainHandItem();
            WeaponItem weaponItem = LimaCoreUtil.castOrNull(WeaponItem.class, heldItem.getItem());
            LocalWeaponInput.LOCAL_WEAPON_INPUT.tickInput(player, heldItem, weaponItem);
        }

    }

    @SubscribeEvent
    public static void onClickInput(final InputEvent.InteractionKeyMappingTriggered event)
    {
        // Always cancel left clicks when holding weapon items
        ItemStack stack = LimaCoreClientUtil.getClientMainHandItem();
        if (event.isAttack() && stack.getItem() instanceof WeaponItem)
        {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void onMouseScrollInput(final InputEvent.MouseScrollingEvent event)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isSpectator() && player.input.shiftKeyDown)
        {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof ScrollModeSwitchItem)
            {
                int x = (int) Math.signum(event.getScrollDeltaX());
                int y = (int) Math.signum(event.getScrollDeltaY());
                int delta = (x == 0) ? -y : x;
                PacketDistributor.sendToServer(new ServerboundItemModeSwitchPacket(player.getInventory().selected, delta));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(final InputEvent.Key event)
    {
        // Handle reload key input
        if (LimaTechKeyMappings.RELOAD_KEY.consumeClick() && Minecraft.getInstance().player != null)
        {
            Player player = Minecraft.getInstance().player;
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.getItem() instanceof WeaponItem weaponItem)
            {
                LocalWeaponInput.LOCAL_WEAPON_INPUT.handleReloadInput(player, heldItem, weaponItem);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelStageRender(final RenderLevelStageEvent event)
    {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && Minecraft.getInstance().level != null)
        {
            ClientLevel level = Minecraft.getInstance().level;
            PoseStack poseStack = event.getPoseStack();
            VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(LimaTechRenderTypes.BUBBLE_SHIELD);
            Vec3 cam = event.getCamera().getPosition();
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);

            // TODO Rework bubble shield rendering
            // Render bubble shields
            for (Entity entity : level.entitiesForRendering())
            {
                if (entity == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;

                if (entity.hasData(LimaTechAttachmentTypes.BUBBLE_SHIELD))
                {
                    float shieldHealth = entity.getData(LimaTechAttachmentTypes.BUBBLE_SHIELD).getShieldHealth();

                    if (shieldHealth != 0)
                    {
                        poseStack.pushPose();

                        double x = Mth.lerp(partialTick, entity.xo, entity.getX()) - cam.x;
                        double y = Mth.lerp(partialTick, entity.yo, entity.getY()) - cam.y;
                        double z = Mth.lerp(partialTick, entity.zo, entity.getZ()) - cam.z;

                        AABB bb = entity.getBoundingBox();
                        float size;
                        if (entity instanceof Player)
                        {
                            size = 1.9f;
                        }
                        else
                        {
                            size = (float) Math.max(Math.max(bb.getXsize(), bb.getYsize()), bb.getZsize());
                        }

                        poseStack.translate(x, y + (bb.getYsize() / 2d), z);
                        poseStack.mulPose(Axis.YN.rotationDegrees(entity.getYRot()));
                        poseStack.scale(size, size, size);

                        BubbleShieldRenderer.SHIELD_RENDERER.renderBubbleShield(poseStack, buffer, REM_BLUE, partialTick);

                        poseStack.popPose();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderCrossHairOverlay(final RenderGuiLayerEvent.Pre event)
    {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR))
        {
            ItemStack heldItem = LimaCoreClientUtil.getClientMainHandItem();
            if (heldItem.getItem() instanceof WeaponItem)
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onGatherItemTooltipComponents(final RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof TooltipShiftHintItem item)
        {
            List<Either<FormattedText, TooltipComponent>> components = event.getTooltipElements();
            if (Screen.hasShiftDown())
            {
                item.appendTooltipHintComponents(Minecraft.getInstance().level, stack, components::add);
            }
            else
            {
                components.add(Either.left(TooltipShiftHintItem.HINT_HOVER_TOOLTIP.translate().withStyle(ChatFormatting.DARK_GRAY)));
            }
        }
    }
}