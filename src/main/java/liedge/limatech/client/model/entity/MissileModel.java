package liedge.limatech.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MissileModel extends Model
{
    private final ModelPart missile;
    private final ModelPart warhead;
    
    public MissileModel(EntityModelSet set)
    {
        super(RenderType::entitySolid);
        ModelPart root = set.bakeLayer(LimaTechModelLayers.MISSILE);
        missile = root.getChild("missile");
        warhead = root.getChild("warhead");
    }
    
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, int argb32)
    {
        missile.render(poseStack, buffer, light, overlay);
        warhead.render(poseStack, buffer, light, overlay, argb32);
    }
    
    public static LayerDefinition defineLayer()
    {
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition partDef = meshDef.getRoot();
        
        PartPose pivot = PartPose.offset(0f, 21.5f, 0f);

        partDef.addOrReplaceChild("missile", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, -1.5F, -9.0F, 3.0F, 3.0F, 19.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-1.5F, -2.5F, -9.0F, 3.0F, 1.0F, 18.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-1.5F, 1.5F, -9.0F, 3.0F, 1.0F, 18.0F, CubeDeformation.NONE)
                .texOffs(26, 4).addBox(1.5F, -1.5F, -9.0F, 1.0F, 3.0F, 18.0F, CubeDeformation.NONE)
                .texOffs(26, 4).addBox(-2.5F, -1.5F, -9.0F, 1.0F, 3.0F, 18.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-2.5F, -2.5F, -11.0F, 5.0F, 5.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(6, 22).addBox(-0.5F, -3.5F, 5.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-0.5F, -4.5F, 7.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(6, 22).addBox(-0.5F, 2.5F, 5.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-0.5F, 2.5F, 7.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(8, 28).addBox(2.5F, -0.5F, 5.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(2.5F, -0.5F, 7.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(8, 28).addBox(-3.5F, -0.5F, 5.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(-4.5F, -0.5F, 7.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE), pivot);

        partDef.addOrReplaceChild("warhead", CubeListBuilder.create()
                .texOffs(0, 13).addBox(-1.5F, -1.5F, -15.0F, 3.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 7).addBox(-2.0F, -2.0F, -13.0F, 4.0F, 4.0F, 2.0F, CubeDeformation.NONE), pivot);

        return LayerDefinition.create(meshDef, 64, 48);
    }
}