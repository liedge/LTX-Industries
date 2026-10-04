package liedge.ltxindustries.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public class GlowstickProjectileModel extends ProjectileModel
{
	public GlowstickProjectileModel(ModelPart root)
	{
		super(root, RenderTypes::entitySolid);
	}

	public static LayerDefinition defineLayer()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1, -4, -1, 2, 8, 2, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0, 2, 0, Mth.HALF_PI, 0, 0));

		return LayerDefinition.create(mesh, 16, 16);
	}
}