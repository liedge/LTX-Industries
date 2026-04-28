package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.WonderlandArmorModel;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import liedge.ltxindustries.item.EnergyArmorItem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;

public class WonderlandArmorLayer extends RenderLayer<AvatarRenderState, PlayerModel>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "wonderland_armor");

    private final WonderlandArmorModel model;

    public WonderlandArmorLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, EntityModelSet entityModels)
    {
        super(renderer);
        this.model = new WonderlandArmorModel(entityModels.bakeLayer(LTXIModelLayers.WONDERLAND_ARMOR_SET));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot)
    {
        poseStack.pushPose();

        ClientHooks.copyModelProperties(getParentModel(), model);
        model.setupAnim(renderState);

        submitArmorPiece(poseStack, nodeCollector, renderState, renderState.headEquipment, EquipmentSlot.HEAD, packedLight);
        submitArmorPiece(poseStack, nodeCollector, renderState, renderState.chestEquipment, EquipmentSlot.CHEST, packedLight);
        submitArmorPiece(poseStack, nodeCollector, renderState, renderState.legsEquipment, EquipmentSlot.LEGS, packedLight);
        submitArmorPiece(poseStack, nodeCollector, renderState, renderState.feetEquipment, EquipmentSlot.FEET, packedLight);

        poseStack.popPose();
    }

    private void submitArmorPiece(PoseStack poseStack, SubmitNodeCollector nodeCollector, AvatarRenderState renderState, ItemStack stack, EquipmentSlot slot, int packedLight)
    {
        if (!(stack.getItem() instanceof EnergyArmorItem)) return;

        switch (slot)
        {
            case HEAD ->
            {
                model.submitHead(poseStack, nodeCollector, TEXTURE, packedLight);
                submitHeadEphemera(poseStack, nodeCollector);
            }
            case CHEST ->
            {
                model.submitBody(poseStack, nodeCollector, TEXTURE, packedLight);
                submitBodyEphemera(poseStack, nodeCollector, renderState);
            }
            case LEGS ->
            {
                model.submitLegs(poseStack, nodeCollector, TEXTURE, packedLight);
                submitLegsEphemera(poseStack, nodeCollector);
            }
            case FEET -> model.submitFeet(poseStack, nodeCollector, TEXTURE, packedLight);
        }
    }

    private void submitHeadEphemera(PoseStack poseStack, SubmitNodeCollector nodeCollector)
    {
        poseStack.pushPose();

        model.head.translateAndRotate(poseStack);
        poseStack.translate(0, -0.3125f, 0.375f);
        poseStack.mulPose(Axis.XP.rotationDegrees(15f));

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            LTXIRenderer.renderArcRing(pose, buffer, 0.15f, 0.02f, 0, 360, 24, LTXIConstants.LIME_GREEN);
            LTXIRenderer.renderArcRing(pose, buffer, 0.25f, 0.0325f, 0, 360, 32, LTXIConstants.LIME_GREEN);

            float spin = (Util.getMillis() % 7000L) / 7000f;
            LTXIRenderer.renderArcsRing(pose, buffer, spin * 360f, 3, 60f, 0.01875f, 0.325f, 7, LTXIConstants.LIME_GREEN);
        });

        poseStack.popPose();
    }

    private void submitBodyEphemera(PoseStack poseStack, SubmitNodeCollector nodeCollector, AvatarRenderState renderState)
    {
        float armSpin = (Util.getMillis() % 1500L) / 1500f * 360f;

        // Left arm
        poseStack.pushPose();
        model.leftArm.translateAndRotate(poseStack);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0f, 0.25f, 0.203125f);

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) -> LTXIRenderer.renderArcsRing(pose, buffer, armSpin, 7, 35f, 0.015f, 0.125f, 4, LTXIConstants.LIME_GREEN));
        poseStack.popPose();

        // Right arm
        poseStack.pushPose();
        model.rightArm.translateAndRotate(poseStack);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0f, 0.25f, -0.171875f);

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) -> LTXIRenderer.renderArcsRing(pose, buffer, armSpin, 7, 35f, 0.015f, 0.125f, 4, LTXIConstants.LIME_GREEN));
        poseStack.popPose();

        // Wings
        if (!renderState.getRenderDataOrDefault(LTXIRenderer.SHOW_WONDERLAND_WINGS, false)) return;

        float wingBreathe = (Util.getMillis() % 3000L) / 3000f;
        wingBreathe = Mth.sin(wingBreathe * Mth.PI) * 0.0625f;

        poseStack.pushPose();
        model.body.translateAndRotate(poseStack);

        // Left wing
        poseStack.pushPose();
        poseStack.translate(wingBreathe, wingBreathe, 0.25f);
        poseStack.mulPose(Axis.YN.rotationDegrees(30f));

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            renderWingPiece(pose, buffer, 0.25f, 0.025f, 0.325f, 0.35f, 0.0125f, 20, 75);
            renderWingPiece(pose, buffer, 0.35f, 0.05f, 0.5f, 0.5f, 0.025f, 300, 360);
        });

        poseStack.popPose();

        // Right wing
        poseStack.pushPose();
        poseStack.translate(-wingBreathe, wingBreathe, 0.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(30f));

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            renderWingPiece(pose, buffer, 0.25f, 0.025f, 0.325f, 0.35f, 0.0125f, 105, 160);
            renderWingPiece(pose, buffer, 0.35f, 0.05f, 0.5f, 0.5f, 0.025f, 180, 240);
        });

        poseStack.popPose();

        poseStack.popPose();
    }

    private void submitLegsEphemera(PoseStack poseStack, SubmitNodeCollector nodeCollector)
    {
        float spin = (Util.getMillis() % 3000L) / 3000f * 360f;

        // Left knee
        poseStack.pushPose();

        model.leftLeg.translateAndRotate(poseStack);
        poseStack.translate(0.0078125f, 0.2828125f, -0.1875f);

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            LTXIRenderer.renderArcRing(pose, buffer, 0.078125f, 0.009375f, 0, 360, 24, LTXIConstants.LIME_GREEN);
            LTXIRenderer.renderArcsRing(pose, buffer, -spin, 7, 35f, 0.0125f, 0.109375f, 4, LTXIConstants.LIME_GREEN);
        });

        poseStack.popPose();

        // Right knee
        poseStack.pushPose();

        model.rightLeg.translateAndRotate(poseStack);
        poseStack.translate(-0.0078125f, 0.2828125f, -0.1875f);

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            LTXIRenderer.renderArcRing(pose, buffer, 0.078125f, 0.009375f, 0, 360, 24, LTXIConstants.LIME_GREEN);
            LTXIRenderer.renderArcsRing(pose, buffer, spin, 7, 35f, 0.0125f, 0.109375f, 4, LTXIConstants.LIME_GREEN);
        });

        poseStack.popPose();
    }

    private void renderWingPiece(PoseStack.Pose pose, VertexConsumer buffer, float connectRadius, float connectWidth, float wingRadius, float wingWidth, float wingOutline, float startAngle, float endAngle)
    {
        LTXIRenderer.renderArcRing(pose, buffer, connectRadius, connectWidth, startAngle, endAngle, 8, LTXIConstants.LIME_GREEN);
        LTXIRenderer.renderArcRing(pose, buffer, wingRadius, wingOutline, startAngle, endAngle, 8, LTXIConstants.LIME_GREEN);
        LTXIRenderer.renderArcRing(pose, buffer, wingRadius + wingOutline, wingWidth - (wingOutline * 2), startAngle, endAngle, 12, LTXIConstants.LIME_GREEN, 0.5f);
        LTXIRenderer.renderArcRing(pose, buffer, wingRadius + wingWidth - wingOutline, wingOutline, startAngle, endAngle, 12, LTXIConstants.LIME_GREEN);
    }
}