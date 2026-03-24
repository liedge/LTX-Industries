package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.client.renderer.entity.ProjectileRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class GlowstickProjectileModel extends ProjectileModel
{
	private final ModelPart stick;

	public GlowstickProjectileModel(ModelPart root)
	{
		super(root, RenderTypes::entitySolid);
		this.stick = root.getChild("stick");
	}

	@Override
	public void submitParts(ProjectileRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture)
	{
		nodeCollector.submitModelPart(stick, poseStack, renderType(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
	}

	public static LayerDefinition defineLayer()
	{
		MeshDefinition meshDef = new MeshDefinition();
		PartDefinition root = meshDef.getRoot();

		root.addOrReplaceChild("stick", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1, -4, -1, 2, 8, 2, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0, 2, 0, Mth.HALF_PI, 0, 0));

		return LayerDefinition.create(meshDef, 16, 16);
	}
}