package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.WonderlandArmorModel;
import liedge.ltxindustries.item.EnergyArmorItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
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

        // Render cutout (solid) parts
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
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
}