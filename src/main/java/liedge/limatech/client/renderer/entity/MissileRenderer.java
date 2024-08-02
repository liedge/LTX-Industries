package liedge.limatech.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.model.entity.MissileModel;
import liedge.limatech.entity.MissileEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MissileRenderer extends EntityRenderer<MissileEntity>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("entity", "missile");
    private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);

    private final MissileModel model;

    public MissileRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx);
        this.model = new MissileModel(ctx.getModelSet());
    }

    @Override
    public void render(MissileEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        poseStack.translate(0, 0.21875d, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180f + entity.getXRot()));
        poseStack.translate(0, -1.34375d, 0);

        VertexConsumer buffer = bufferSource.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, LimaTechConstants.LIME_GREEN.rgb());

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MissileEntity entity)
    {
        return TEXTURE;
    }
}