package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.client.renderer.entity.ProjectileRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class ShellGrenadeModel extends ProjectileModel
{
	private final ModelPart body;
	private final ModelPart lights;

	public ShellGrenadeModel(ModelPart root)
	{
		super(root, RenderTypes::entityCutout);
		this.body = root.getChild("body");
		this.lights = root.getChild("lights");
	}

	@Override
	public void submitParts(ProjectileRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture)
	{
		nodeCollector.submitModelPart(body, poseStack, renderType.apply(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(lights, poseStack, LimaCoreRenderTypes.entityCutoutUnlit(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null, renderState.color.argb32(), null);
	}

	public static LayerDefinition defineLayer()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartPose pivot = PartPose.offset(0f, 2.75f, 0f);
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, 2.0F, -1.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE), pivot);
        root.addOrReplaceChild("lights", CubeListBuilder.create()
                .texOffs(0, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 13).addBox(-2.0F, 4.25F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.49F)), pivot);

		return LayerDefinition.create(mesh, 32, 32);
    }
}