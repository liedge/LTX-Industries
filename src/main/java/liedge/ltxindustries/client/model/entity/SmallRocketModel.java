package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.entity.BaseRocketEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SmallRocketModel<T extends BaseRocketEntity> extends ProjectileModel<T>
{
    private final ModelPart body;
    private final ModelPart lights;
    
    public SmallRocketModel(EntityModelSet modelSet)
    {
        super(RenderType::entityCutoutNoCull);
        
        ModelPart root = modelSet.bakeLayer(LTXIModelLayers.SMALL_ROCKET);
        this.body = root.getChild("body");
        this.lights = root.getChild("lights");
    }

    @Override
    public void prepare(T entity, float partialTick)
    {
        rotatePart(entity, body);
        copyRotations(body, lights);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int packedLight, int color)
    {
        VertexConsumer buffer = bufferSource.getBuffer(renderType(texture));
        body.render(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

        buffer = bufferSource.getBuffer(LimaCoreRenderTypes.positionTexColorSolid(texture));
        lights.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
    }

    public static LayerDefinition defineLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 16.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(12, 0).addBox(1.5F, -6.0F, 0.0F, 3.0F, 3.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(12, 3).addBox(-4.5F, -6.0F, 0.0F, 3.0F, 3.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(12, 0).addBox(0.0F, -6.0F, 1.5F, 0.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(12, -3).addBox(0.0F, -6.0F, -4.5F, 0.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(12, 6).addBox(1.5F, 5.0F, 0.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(12, 10).addBox(-4.5F, 5.0F, 0.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(12, 7).addBox(0.0F, 5.0F, 1.5F, 0.0F, 4.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(12, 3).addBox(0.0F, 5.0F, -4.5F, 0.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0f, 4.5f, 0f));

        root.addOrReplaceChild("lights", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(-1.5F, 2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
                .texOffs(0, 22).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.49F)), PartPose.offset(0f, 4.5f, 0f));

        return LayerDefinition.create(mesh, 32, 32);
    }
}