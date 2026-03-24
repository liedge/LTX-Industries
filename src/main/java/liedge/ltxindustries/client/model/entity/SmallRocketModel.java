package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.client.renderer.entity.ProjectileRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class SmallRocketModel extends ProjectileModel
{
    private final ModelPart body;
    private final ModelPart lights;
    
    public SmallRocketModel(ModelPart root)
    {
        super(root, RenderTypes::entityCutoutNoCull);
        this.body = root.getChild("body");
        this.lights = root.getChild("lights");
    }

    @Override
    public void submitParts(ProjectileRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture)
    {
        nodeCollector.submitModelPart(body, poseStack, renderType(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
        nodeCollector.submitModelPart(lights, poseStack, LimaCoreRenderTypes.entityCutoutUnlit(texture), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);
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