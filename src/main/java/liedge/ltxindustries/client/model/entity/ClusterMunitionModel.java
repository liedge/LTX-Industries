package liedge.ltxindustries.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class ClusterMunitionModel extends ProjectileModel
{
    public ClusterMunitionModel(ModelPart root)
    {
        super(root, RenderTypes::entityTranslucentEmissive);
    }

    public static LayerDefinition defineLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 8).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 22.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }
}