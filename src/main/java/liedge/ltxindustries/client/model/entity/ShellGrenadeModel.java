package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.entity.ShellGrenadeEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ShellGrenadeModel extends ProjectileModel<ShellGrenadeEntity>
{
	private final ModelPart body;
	private final ModelPart lights;

	public ShellGrenadeModel(EntityModelSet modelSet)
	{
		super(RenderType::entityCutoutNoCull);

		ModelPart root = modelSet.bakeLayer(LTXIModelLayers.SHELL_GRENADE);
		this.body = root.getChild("body");
		this.lights = root.getChild("lights");
	}

	@Override
	public void prepare(ShellGrenadeEntity entity, float partialTick)
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
		lights.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
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