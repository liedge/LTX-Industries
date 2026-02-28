package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.WonderlandArmorModel;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import liedge.ltxindustries.item.EnergyArmorItem;
import net.minecraft.Util;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class WonderlandArmorLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "wonderland_armor");

    private final WonderlandArmorModel model;

    public WonderlandArmorLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet entityModels)
    {
        super(renderer);
        this.model = new WonderlandArmorModel(entityModels.bakeLayer(LTXIModelLayers.WONDERLAND_ARMOR_SET));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        renderArmorPiece(poseStack, bufferSource, player, EquipmentSlot.HEAD, packedLight);
        renderArmorPiece(poseStack, bufferSource, player, EquipmentSlot.CHEST, packedLight);
        renderArmorPiece(poseStack, bufferSource, player, EquipmentSlot.LEGS, packedLight);
        renderArmorPiece(poseStack, bufferSource, player, EquipmentSlot.FEET, packedLight);
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, EquipmentSlot slot, int packedLight)
    {
        ItemStack stack = player.getItemBySlot(slot);
        if (!(stack.getItem() instanceof EnergyArmorItem)) return;

        PlayerModel<AbstractClientPlayer> parent = getParentModel();
        model.copyModelProperties(parent);
        setPartVisibility(slot);

        // Render ephemera effects
        VertexConsumer buffer = bufferSource.getBuffer(LTXIRenderTypes.WONDERLAND_EPHEMERA);
        switch (slot)
        {
            case HEAD -> renderHeadEphemera(poseStack, buffer);
            case CHEST -> renderBodyEphemera(poseStack, buffer, player.getAbilities().flying);
            case LEGS -> renderLegsEphemera(poseStack, buffer);
        }

        // Render cutout (solid) parts
        buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderCutout(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        // Render lights
        buffer = bufferSource.getBuffer(LimaCoreRenderTypes.positionTexColorSolid(TEXTURE));
        model.renderLights(poseStack, buffer, -1);

        // Render visor
        buffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        model.renderVisor(poseStack, buffer, -1);
    }

    private void setPartVisibility(EquipmentSlot slot)
    {
        model.setAllVisible(false);
        switch (slot)
        {
            case HEAD:
                model.head.visible = true;
                model.headLights.visible = true;
                model.visor.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.bodyLights.visible = true;
                model.leftArm.visible = true;
                model.leftArmLights.visible = true;
                model.rightArm.visible = true;
                model.rightArmLights.visible = true;
                break;
            case LEGS:
                model.leftLeg.visible = true;
                model.leftLegLights.visible = true;
                model.rightLeg.visible = true;
                model.rightLegLights.visible = true;
                break;
            case FEET:
                model.leftFoot.visible = true;
                model.leftFootLights.visible = true;
                model.rightFoot.visible = true;
                model.rightFootLights.visible = true;
                break;
        }
    }

    private void renderHeadEphemera(PoseStack poseStack, VertexConsumer buffer)
    {
        poseStack.pushPose();

        model.head.translateAndRotate(poseStack);
        poseStack.translate(0, -0.3125f, 0.375f);
        poseStack.mulPose(Axis.XP.rotationDegrees(15f));

        LTXIRenderUtil.renderArcRing(poseStack, buffer, 0.15f, 0.02f, 0, 360, 24, LTXIConstants.LIME_GREEN);
        LTXIRenderUtil.renderArcRing(poseStack, buffer, 0.25f, 0.0325f, 0, 360, 32, LTXIConstants.LIME_GREEN);

        float spin = (Util.getMillis() % 7000L) / 7000f;
        renderSpinningRing(poseStack, buffer, spin * 360f, 3, 0.325f, 0.01875f, 60f, 7);

        poseStack.popPose();
    }

    private void renderBodyEphemera(PoseStack poseStack, VertexConsumer buffer, boolean wingsVisible)
    {
        float armSpin = (Util.getMillis() % 1500L) / 1500f * 360f;

        // Left arm
        poseStack.pushPose();
        model.leftArm.translateAndRotate(poseStack);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0f, 0.25f, 0.203125f);

        renderSpinningRing(poseStack, buffer, armSpin, 7, 0.125f, 0.015f, 35f, 4);
        poseStack.popPose();

        // Right arm
        poseStack.pushPose();
        model.rightArm.translateAndRotate(poseStack);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0f, 0.25f, -0.171875f);

        renderSpinningRing(poseStack, buffer, armSpin, 7, 0.125f, 0.015f, 35f, 4);
        poseStack.popPose();

        // Wings
        poseStack.pushPose();
        model.body.translateAndRotate(poseStack);

        if (wingsVisible)
        {
            float wingBreathe = (Util.getMillis() % 3000L) / 3000f;
            wingBreathe = Mth.sin(wingBreathe * Mth.PI) * 0.0625f;

            // Left
            poseStack.pushPose();
            poseStack.translate(wingBreathe, wingBreathe, 0.25f);
            poseStack.mulPose(Axis.YN.rotationDegrees(30f));

            renderWingPiece(poseStack, buffer, 0.25f, 0.025f, 0.325f, 0.35f, 0.0125f, 20, 75);
            renderWingPiece(poseStack, buffer, 0.35f, 0.05f, 0.5f, 0.5f, 0.025f, 300, 360);

            poseStack.popPose();

            // Right
            poseStack.pushPose();
            poseStack.translate(-wingBreathe, wingBreathe, 0.25f);
            poseStack.mulPose(Axis.YP.rotationDegrees(30f));

            renderWingPiece(poseStack, buffer, 0.25f, 0.025f, 0.325f, 0.35f, 0.0125f, 105, 160);
            renderWingPiece(poseStack, buffer, 0.35f, 0.05f, 0.5f, 0.5f, 0.025f, 180, 240);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderLegsEphemera(PoseStack poseStack, VertexConsumer buffer)
    {
        float delta = (Util.getMillis() % 3000L) / 3000f * 360f;

        // Left knee
        poseStack.pushPose();

        model.leftLeg.translateAndRotate(poseStack);
        poseStack.translate(0.0078125f, 0.2828125f, -0.1875f);

        LTXIRenderUtil.renderArcRing(poseStack, buffer, 0.078125f, 0.009375f, 0, 360, 24, LTXIConstants.LIME_GREEN);
        renderSpinningRing(poseStack, buffer, -delta, 7, 0.109375f, 0.0125f, 35f, 4);

        poseStack.popPose();

        // Right knee
        poseStack.pushPose();

        model.rightLeg.translateAndRotate(poseStack);
        poseStack.translate(-0.0078125f, 0.2828125f, -0.1875f);

        LTXIRenderUtil.renderArcRing(poseStack, buffer, 0.078125f, 0.009375f, 0, 360, 24, LTXIConstants.LIME_GREEN);
        renderSpinningRing(poseStack, buffer, delta, 7, 0.109375f, 0.0125f, 35f, 4);

        poseStack.popPose();
    }

    private void renderSpinningRing(PoseStack poseStack, VertexConsumer buffer, float delta, int arcs, float radius, float width, float arcLength, int segments)
    {
        float halfArc = arcLength / 2f;

        for (int i = 0; i < arcs; i++)
        {
            float angle = i * (360f / arcs) + delta;
            LTXIRenderUtil.renderArcRing(poseStack, buffer, radius, width, angle - halfArc, angle + halfArc, segments, LTXIConstants.LIME_GREEN);
        }
    }

    private void renderWingPiece(PoseStack poseStack, VertexConsumer buffer, float connectRadius, float connectWidth, float wingRadius, float wingWidth, float wingOutline, float startAngle, float endAngle)
    {
        LTXIRenderUtil.renderArcRing(poseStack, buffer, connectRadius, connectWidth, startAngle, endAngle, 8, LTXIConstants.LIME_GREEN);
        LTXIRenderUtil.renderArcRing(poseStack, buffer, wingRadius, wingOutline, startAngle, endAngle, 8, LTXIConstants.LIME_GREEN);
        LTXIRenderUtil.renderArcRing(poseStack, buffer, wingRadius + wingOutline, wingWidth - (wingOutline * 2), startAngle, endAngle, 12, LTXIConstants.LIME_GREEN, 0.5f);
        LTXIRenderUtil.renderArcRing(poseStack, buffer, wingRadius + wingWidth - wingOutline, wingOutline, startAngle, endAngle, 12, LTXIConstants.LIME_GREEN);
    }
}