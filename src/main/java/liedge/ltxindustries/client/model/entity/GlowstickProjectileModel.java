package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.entity.GlowstickProjectileEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GlowstickProjectileModel extends ProjectileModel<GlowstickProjectileEntity>
{
	private final ModelPart stick;

	public GlowstickProjectileModel(EntityModelSet modelSet)
	{
		super(RenderType::entitySolid);

		ModelPart root = modelSet.bakeLayer(LTXIModelLayers.GLOWSTICK_PROJECTILE);
		this.stick = root.getChild("stick");
	}

	@Override
	public void prepare(GlowstickProjectileEntity entity, float partialTick)
	{
		rotatePart(entity, stick);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int packedLight, int color)
	{
		stick.render(poseStack, bufferSource.getBuffer(renderType(texture)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
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