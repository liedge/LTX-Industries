package liedge.ltxindustries.client;

import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.renderer.BubbleShieldRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import liedge.ltxindustries.client.renderer.LockOnRenderData;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.item.TooltipShiftHintItem;
import liedge.ltxindustries.item.weapon.RocketLauncherItem;
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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
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
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

import java.util.List;
import java.util.Objects;

import static liedge.ltxindustries.registry.game.LTXIAttachmentTypes.INPUT_EXTENSIONS;

@EventBusSubscriber(modid = LTXIndustries.MODID, value = Dist.CLIENT)
public final class LTXIClientEventHandler
{
    private static final ContextKey<LockOnRenderData> LOCK_ON_DATA = LTXIndustries.RESOURCES.contextKey("lock_on_data");

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
        BubbleShieldRenderer.INSTANCE.tickRenderer();
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
        if (player != null && !player.isSpectator() && player.input.keyPresses.shift())
        {
            ItemStack heldItem = player.getMainHandItem();
            double deltaY = event.getScrollDeltaY();
            if (heldItem.getItem() instanceof ScrollModeSwitchItem item && deltaY != 0)
            {
                TickTimer cooldown = ClientExtendedInput.of(player).getModeSwitchTimer();
                if (cooldown.getTimerState() == TickTimer.State.STOPPED)
                {
                    boolean forward = LTXIClientConfig.INVERT_MODE_SWITCH_SCROLL.getAsBoolean() ? deltaY > 0 : deltaY < 0;
                    ClientPacketDistributor.sendToServer(new ServerboundItemModeSwitchPacket(player.getInventory().getSelectedSlot(), forward));
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
    public static void registerRenderStateModifiers(final RegisterRenderStateModifiersEvent event)
    {
        // Player state
        event.registerEntityModifier(new TypeToken<AvatarRenderer<?>>() {}, (avatar, renderState) ->
        {
            boolean wings = avatar instanceof Player player && player.getAbilities().flying;
            renderState.setRenderData(LTXIRenderer.SHOW_WONDERLAND_WINGS, wings);
        });

        // Shield states
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<?,?,?>>() {}, (entity, state) ->
        {
            if (entity == Minecraft.getInstance().player && (Minecraft.getInstance().options.getCameraType().isFirstPerson()) || entity.isSpectator()) return;

            boolean isPlayer = entity instanceof Player;

            float shieldHealth = entity.getData(LTXIAttachmentTypes.BUBBLE_SHIELD_HEALTH);
            if (shieldHealth <= 0)
            {
                return;
            }
            else if (isPlayer)
            {
                float capacity = (float) LimaEntityUtil.getAttributeValueSafe(entity, LTXIAttributes.SHIELD_CAPACITY);
                if (capacity > 0 && Mth.equal(shieldHealth, capacity)) return;
            }

            float scale = isPlayer ? 1.75f : (float) LimaEntityUtil.getLargestBBDimension(entity);
            double yCenter = state.y + entity.getBoundingBox().getYsize() / 2d;
            float yRot = entity == Minecraft.getInstance().player ? -entity.getYRot() : state.yRot;

            BubbleShieldRenderer.RenderState shieldState = new BubbleShieldRenderer.RenderState(yCenter, yRot, scale, LTXIConstants.BUBBLE_SHIELD_BLUE.argb32(), state.partialTick);
            state.setRenderData(LTXIRenderer.BUBBLE_SHIELD_STATE, shieldState);
        });
    }

    @SubscribeEvent
    public static void extractLevelRenderState(final ExtractLevelRenderStateEvent event)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        LevelRenderState renderState = event.getRenderState();
        LTXIExtendedInput controls = player.getData(INPUT_EXTENSIONS);
        Camera camera = event.getCamera();
        float partialTick = event.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        LivingEntity target = controls.getFocusedTarget();
        if (target != null)
        {
            float progress = Math.min(1f, controls.lerpTargetTicks(partialTick) / (float) RocketLauncherItem.TARGET_LOCK_MIN_TICKS);
            renderState.setRenderData(LOCK_ON_DATA, LockOnRenderData.of(target, camera, progress, partialTick));
        }
    }

    @SubscribeEvent
    public static void levelRenderAfterTranslucent(final RenderLevelStageEvent.AfterTranslucentBlocks event)
    {
        LevelRenderState renderState = event.getLevelRenderState();
        MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = event.getPoseStack();

        // Render lock-on indicator
        LockOnRenderData lockOnData = renderState.getRenderData(LOCK_ON_DATA);
        if (lockOnData != null)
        {
            poseStack.pushPose();

            lockOnData.render(poseStack.last(), bufferSource.getBuffer(LTXIRenderTypes.LOCK_ON_INDICATOR));

            poseStack.popPose();
        }

        for (EntityRenderState entityState : renderState.entityRenderStates)
        {
            BubbleShieldRenderer.RenderState shieldState = entityState.getRenderData(LTXIRenderer.BUBBLE_SHIELD_STATE);
            if (shieldState == null) continue;

            poseStack.pushPose();

            Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
            poseStack.translate(entityState.x - camera.x, shieldState.yCenter() - camera.y, entityState.z - camera.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(shieldState.yRot()));
            float scale = shieldState.scale();
            poseStack.scale(scale, scale, scale);

            VertexConsumer buffer = bufferSource.getBuffer(LTXIRenderTypes.BUBBLE_SHIELD);

            BubbleShieldRenderer.INSTANCE.submit(poseStack.last(), buffer, shieldState.color(), shieldState.partialTick());

            poseStack.popPose();
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
            if (Minecraft.getInstance().hasShiftDown() && Minecraft.getInstance().level != null)
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