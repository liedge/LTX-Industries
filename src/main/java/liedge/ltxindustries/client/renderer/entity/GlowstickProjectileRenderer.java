package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.GlowstickProjectileModel;
import liedge.ltxindustries.entity.GlowstickProjectileEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GlowstickProjectileRenderer extends EntityRenderer<GlowstickProjectileEntity>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("block", "glowstick");

    private final GlowstickProjectileModel model;

    public GlowstickProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new GlowstickProjectileModel(context.getModelSet());
    }

    @Override
    public void render(GlowstickProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        poseStack.pushPose();

        VertexConsumer buffer = bufferSource.getBuffer(model.renderType(TEXTURE));
        model.rotateModel(entity, partialTick);
        model.renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(GlowstickProjectileEntity entity)
    {
        return TEXTURE;
    }
}