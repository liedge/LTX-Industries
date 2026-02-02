package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public abstract class ProjectileModel<T extends Entity> extends Model
{
    protected ProjectileModel(Function<ResourceLocation, RenderType> renderType)
    {
        super(renderType);
    }

    protected void rotatePart(T entity, ModelPart part)
    {
        part.yRot = LimaCoreMath.toRad(-entity.getYRot());
        part.xRot = LimaCoreMath.toRad(entity.getXRot() - 90f);
    }

    protected void copyRotations(ModelPart origin, ModelPart destination)
    {
        destination.xRot = origin.xRot;
        destination.yRot = origin.yRot;
    }

    @Deprecated
    @Override
    public final void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {}

    public abstract void prepare(T entity, float partialTick);

    public abstract void render(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int packedLight, int color);

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int packedLight)
    {
        render(poseStack, bufferSource, texture, packedLight, -1);
    }
}