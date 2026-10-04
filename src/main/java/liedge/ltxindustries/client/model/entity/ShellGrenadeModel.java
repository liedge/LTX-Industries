package liedge.ltxindustries.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class ShellGrenadeModel extends ProjectileModel
{
    public ShellGrenadeModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);
    }

    public static LayerDefinition defineBaseLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 20.5F, 0.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    public static LayerDefinition defineEmissiveLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 8).addBox(-1.5F, -3.5F, -1.5F, 3.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                        .texOffs(0, 13).addBox(-2.0F, 0.75F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.49F)),
                PartPose.offset(0.0F, 20.5F, 0.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }
}