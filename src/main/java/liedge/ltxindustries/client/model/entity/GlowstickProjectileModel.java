package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.ltxindustries.entity.GlowstickProjectileEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import static liedge.limacore.lib.math.LimaCoreMath.toRad;

public class GlowstickProjectileModel extends Model
{
	private final ModelPart stick;

	public GlowstickProjectileModel(EntityModelSet modelSet)
	{
		super(RenderType::entitySolid);

		ModelPart root = modelSet.bakeLayer(LTXIModelLayers.GLOWSTICK_PROJECTILE);
		this.stick = root.getChild("stick");
	}

	public void rotateModel(GlowstickProjectileEntity entity, float partialTick)
	{
		stick.yRot = toRad(-entity.getYRot());
		stick.xRot = toRad(entity.getXRot() - 90f);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
	{
		stick.render(poseStack, buffer, LightTexture.FULL_BRIGHT, packedOverlay);
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