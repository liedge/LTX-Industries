package liedge.ltxindustries.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.renderer.BubbleShieldRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import liedge.ltxindustries.client.renderer.item.LTXIItemRenderers;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.item.TooltipShiftHintItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.network.packet.ServerboundItemModeSwitchPacket;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import liedge.ltxindustries.registry.game.LTXIAttributes;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;

import static liedge.ltxindustries.registry.game.LTXIAttachmentTypes.INPUT_EXTENSIONS;

@EventBusSubscriber(modid = LTXIndustries.MODID, value = Dist.CLIENT)
public final class LTXIClientEventHandler
{
    @SubscribeEvent
    public static void fovModifyEvent(final ComputeFovModifierEvent event)
    {
        if (event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().is(LTXIItems.LINEAR_FUSION_RIFLE) && event.getNewFovModifier() > 0.10f)
        {
            event.setNewFovModifier(0.10f);
        }
    }

    @SubscribeEvent
    public static void playerTurnModifyEvent(final CalculatePlayerTurnEvent event)
    {
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (player.isUsingItem() && player.getUseItem().is(LTXIItems.LINEAR_FUSION_RIFLE))
        {
            double d0 = event.getMouseSensitivity();
            event.setMouseSensitivity(d0 * 0.275d);
        }
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Pre event)
    {
        BubbleShieldRenderer.SHIELD_RENDERER.tickRenderer();

        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null)
        {
            LTXIItemRenderers.tickValidRenderers(localPlayer);
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
            double deltaY = event.getScrollDeltaY();
            if (heldItem.getItem() instanceof ScrollModeSwitchItem item && deltaY != 0)
            {
                TickTimer cooldown = ClientExtendedInput.of(player).getModeSwitchTimer();
                if (cooldown.getTimerState() == TickTimer.State.STOPPED)
                {
                    boolean forward = LTXIClientConfig.INVERT_MODE_SWITCH_SCROLL.getAsBoolean() ? deltaY > 0 : deltaY < 0;
                    PacketDistributor.sendToServer(new ServerboundItemModeSwitchPacket(player.getInventory().selected, forward));
                    player.playSound(LTXISounds.EQUIPMENT_MODE_SWITCH.get(), 1f, 1f);
                    cooldown.startTimer(item.getSwitchCooldown());
                }

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(final InputEvent.Key event)
    {
        // Handle reload key input
        LocalPlayer player = Minecraft.getInstance().player;
        if (LTXIKeyMappings.RELOAD_KEY.consumeClick() && player != null && !player.isSpectator())
        {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof WeaponItem weaponItem)
            {
                ClientExtendedInput.of(player).handleReloadInput(player, heldItem, weaponItem);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelStageRender(final RenderLevelStageEvent event)
    {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null)
        {
            ClientLevel level = Minecraft.getInstance().level;
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            Vec3 camVec = event.getCamera().getPosition();
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
            LTXIExtendedInput controls = Minecraft.getInstance().player.getData(INPUT_EXTENSIONS);

            // Render bubble shield pass
            for (Entity entity : level.entitiesForRendering())
            {
                if (entity == Minecraft.getInstance().player && (Minecraft.getInstance().options.getCameraType().isFirstPerson() || entity.isSpectator())) continue;

                boolean isPlayer = entity instanceof Player;
                float shieldHealth = entity.getData(LTXIAttachmentTypes.BUBBLE_SHIELD_HEALTH);
                if (shieldHealth <= 0)
                {
                    continue;
                }
                else if (isPlayer)
                {
                    float capacity = (float) LimaEntityUtil.getAttributeValueSafe(entity, LTXIAttributes.SHIELD_CAPACITY);
                    if (capacity > 0 && Mth.equal(shieldHealth, capacity)) continue;
                }

                poseStack.pushPose();

                float scale = isPlayer ? 1.9f : (float) LimaEntityUtil.getLargestBBDimension(entity);

                double[] pos = LTXIRenderUtil.lerpEntityCenter(entity, camVec.x, camVec.y, camVec.z, partialTick);
                poseStack.translate(pos[0], pos[1], pos[2]);
                poseStack.mulPose(Axis.YN.rotationDegrees(entity.getYRot()));
                poseStack.scale(scale, scale, scale);

                BubbleShieldRenderer.SHIELD_RENDERER.renderBubbleShield(poseStack, bufferSource.getBuffer(LTXIRenderTypes.POSITION_COLOR_TRIANGLES), LTXIConstants.BUBBLE_SHIELD_BLUE, partialTick);

                poseStack.popPose();
            }

            // Render lock-on indicator
            LivingEntity target = controls.getFocusedTarget();
            if (target != null)
            {
                float lockProgress = Math.min(1f, controls.lerpTargetTicks(partialTick) / 20f);
                LTXIRenderUtil.renderLockOnIndicatorOnEntity(target, poseStack, bufferSource,event.getCamera(), camVec.x, camVec.y, camVec.z, partialTick, lockProgress);
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
            if (Screen.hasShiftDown() && Minecraft.getInstance().level != null) // Add non-null check here, no point in running with null level for item tooltips
            {
                item.appendTooltipHintComponents(Minecraft.getInstance().level, stack, components::add);
            }
            else
            {
                components.add(Either.left(LTXILangKeys.SHIFT_HOVER_HINT.translate().withStyle(ChatFormatting.DARK_GRAY)));
            }
        }
    }
}