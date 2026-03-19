package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.SmallRocketModel;
import liedge.ltxindustries.entity.BaseRocketEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RocketRenderer<T extends BaseRocketEntity> extends EntityRenderer<T>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "small_rocket");

    private final SmallRocketModel<T> model;

    public RocketRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new SmallRocketModel<>(context.getModelSet());
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        poseStack.pushPose();

        model.prepare(entity, partialTick);
        model.render(poseStack, bufferSource, TEXTURE, light);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return TEXTURE;
    }
}