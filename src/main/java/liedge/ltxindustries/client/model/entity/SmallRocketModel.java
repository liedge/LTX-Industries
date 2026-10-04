package liedge.ltxindustries.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class SmallRocketModel extends ProjectileModel
{
    public SmallRocketModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);
    }

    public static LayerDefinition defineBaseLayer()
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

        return LayerDefinition.create(mesh, 32, 32);
    }

    public static LayerDefinition defineEmissiveLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(-1.5F, 2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
                .texOffs(0, 22).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.49F)), PartPose.offset(0f, 4.5f, 0f));

        return LayerDefinition.create(mesh, 32, 32);
    }
}