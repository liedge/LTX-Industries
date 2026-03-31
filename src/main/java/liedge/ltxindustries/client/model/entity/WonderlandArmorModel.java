package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class WonderlandArmorModel extends HumanoidModel<AvatarRenderState>
{
	private final ModelPart headLights;
	private final ModelPart bodyLights;
	private final ModelPart leftArmLights;
	private final ModelPart rightArmLights;
	private final ModelPart leftLegLights;
	private final ModelPart rightLegLights;
	private final ModelPart leftFoot;
	private final ModelPart leftFootLights;
	private final ModelPart rightFoot;
	private final ModelPart rightFootLights;
	private final ModelPart visor;

	public WonderlandArmorModel(ModelPart root)
	{
		super(root);

		this.headLights = root.getChild("head_lights");
		this.bodyLights = root.getChild("body_lights");
		this.leftArmLights = root.getChild("left_arm_lights");
		this.rightArmLights = root.getChild("right_arm_lights");
		this.leftLegLights = root.getChild("left_leg_lights");
		this.rightLegLights = root.getChild("right_leg_lights");
		this.leftFoot = root.getChild("left_foot");
		this.leftFootLights = root.getChild("left_foot_lights");
		this.rightFoot = root.getChild("right_foot");
		this.rightFootLights = root.getChild("right_foot_lights");
		this.visor = root.getChild("visor");
	}

	public void submitHead(PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture, int packedLight)
	{
		nodeCollector.submitModelPart(head, poseStack, renderType(texture), packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(headLights, poseStack, cutoutUnlit(texture), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(visor, poseStack, RenderTypes.entityTranslucentEmissive(texture, true), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);
	}

	public void submitBody(PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture, int packedLight)
	{
		RenderType base = renderType(texture);
		nodeCollector.submitModelPart(body, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(leftArm, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(rightArm, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);

		RenderType lights = cutoutUnlit(texture);
		nodeCollector.submitModelPart(bodyLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(leftArmLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(rightArmLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
	}

	public void submitLegs(PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture, int packedLight)
	{
		RenderType base = renderType(texture);
		nodeCollector.submitModelPart(leftLeg, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(rightLeg, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);

		RenderType lights = cutoutUnlit(texture);
		nodeCollector.submitModelPart(leftLegLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(rightLegLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
	}

	public void submitFeet(PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture, int packedLight)
	{
		RenderType base = renderType(texture);
		nodeCollector.submitModelPart(leftFoot, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(rightFoot, poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, null);

		RenderType lights = cutoutUnlit(texture);
		nodeCollector.submitModelPart(leftFootLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
		nodeCollector.submitModelPart(rightFootLights, poseStack, lights, packedLight, OverlayTexture.NO_OVERLAY, null);
	}

	@Override
	public void setupAnim(AvatarRenderState state)
	{
		copyPartProperties(head, headLights);
		copyPartProperties(head, visor);

		copyPartProperties(body, bodyLights);
		copyPartProperties(leftArm, leftArmLights);
		copyPartProperties(rightArm, rightArmLights);

		copyPartProperties(leftLeg, leftLegLights);
		copyPartProperties(leftLeg, leftFoot);
		copyPartProperties(leftLeg, leftFootLights);

		copyPartProperties(rightLeg, rightLegLights);
		copyPartProperties(rightLeg, rightFoot);
		copyPartProperties(rightLeg, rightFootLights);
	}

	private void copyPartProperties(ModelPart original, ModelPart replacement)
	{
		replacement.visible = original.visible;
		replacement.x = original.x;
		replacement.y = original.y;
		replacement.z = original.z;
		replacement.xRot = original.xRot;
		replacement.yRot = original.yRot;
		replacement.zRot = original.zRot;
		replacement.xScale = original.xScale;
		replacement.yScale = original.yScale;
		replacement.zScale = original.zScale;
	}

	private RenderType cutoutUnlit(Identifier texture)
	{
		return LimaCoreRenderTypes.entityCutoutEmissive(texture);
	}

	public static LayerDefinition createArmorLayer()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		// Head non-emissive
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 29).addBox(3.5F, -5.5F, -2.0F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE)
				.texOffs(0, 29).mirror().addBox(-5.5F, -5.5F, -2.0F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE).mirror(false)
				.texOffs(32, 35).addBox(-4.5F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 37).addBox(3.5F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 18).addBox(-4.5F, -8.5F, -1.0F, 9.0F, 1.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(36, 0).addBox(-2.0F, -9.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.ZERO);
		head.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(6, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.25F, -7.5F, 1.5F, 0.0F, 0.0F, 0.6109F));
		head.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(6, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.25F, -7.5F, 1.5F, 0.0F, 0.0F, -0.6109F));

		// Hat, required and unused
		head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		// Visor
		root.addOrReplaceChild("visor", CubeListBuilder.create().texOffs(22, 40).addBox(-4.5F, -6.0F, -4.5F, 9.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.ZERO);

		// Head emissive
		PartDefinition head_lights = root.addOrReplaceChild("head_lights", CubeListBuilder.create().texOffs(0, 46).addBox(4.5F, -5.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.025F))
				.texOffs(0, 46).mirror().addBox(-5.5F, -5.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.ZERO);
		head_lights.addOrReplaceChild("leftearlight_r1", CubeListBuilder.create().texOffs(12, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.25F, -7.5F, 1.5F, 0.0F, 0.0F, 0.6109F));
		head_lights.addOrReplaceChild("rightearlight_r1", CubeListBuilder.create().texOffs(12, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.25F, -7.5F, 1.5F, 0.0F, 0.0F, -0.6109F));

		// Body
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.1F))
				.texOffs(0, 11).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.1F))
				.texOffs(40, 3).addBox(-1.5F, 9.0F, -2.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.12F)), PartPose.ZERO);
		root.addOrReplaceChild("body_lights", CubeListBuilder.create().texOffs(44, 6).addBox(-0.25F, 8.5F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.371F))
				.texOffs(40, 6).addBox(0.1F, 9.0F, -2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.13F))
				.texOffs(45, 21).addBox(-1.0F, 5.6F, -2.125F, 2.0F, 1.0F, 0.0F, CubeDeformation.NONE)
				.texOffs(45, 21).addBox(-1.0F, 5.6F, 2.125F, 2.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.ZERO);

		// Arms
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 27).mirror().addBox(-2.0F, -2.5F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false)
				.texOffs(24, 0).addBox(-1.0F, 3.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.08F))
				.texOffs(24, 5).addBox(2.75F, 3.0F, -1.5F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("left_arm_lights", CubeListBuilder.create().texOffs(10, 46).mirror().addBox(-1.825F, -2.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.21F)).mirror(false)
				.texOffs(48, 0).addBox(3.26F, 2.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.5F))
				.texOffs(43, 13).addBox(2.125F, 2.5F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(-0.48F)), PartPose.offset(5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 27).addBox(0.0F, -2.5F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(24, 0).mirror().addBox(-3.0F, 3.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.08F)).mirror(false)
				.texOffs(24, 5).mirror().addBox(-3.25F, 3.0F, -1.5F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("right_arm_lights", CubeListBuilder.create().texOffs(10, 46).mirror().addBox(0.175F, -2.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.21F)).mirror(false)
				.texOffs(48, 0).addBox(-3.76F, 2.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.5F))
				.texOffs(43, 13).addBox(-3.875F, 2.5F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(-0.48F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		// Legs
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 10).addBox(-1.9F, 4.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.065F))
				.texOffs(24, 15).addBox(-1.4F, 3.0F, -2.75F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild("left_leg_lights", CubeListBuilder.create().texOffs(32, 15).addBox(-1.9F, 2.5F, -3.25F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.48F)), PartPose.offset(1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 10).addBox(-2.1F, 4.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.08F))
				.texOffs(24, 15).addBox(-1.6F, 3.0F, -2.75F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild("right_leg_lights", CubeListBuilder.create().texOffs(32, 15).addBox(-2.1F, 2.5F, -3.25F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.48F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		// Feet
		root.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(16, 19).addBox(-1.875F, 10.0F, -3.75F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.offset(1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild("left_foot_lights", CubeListBuilder.create().texOffs(21, 47).addBox(-2.375F, 10.25F, -4.25F, 5.0F, 2.0F, 7.0F, new CubeDeformation(-0.43F)), PartPose.offset(1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(16, 19).addBox(-2.175F, 10.0F, -3.75F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild("right_foot_lights", CubeListBuilder.create().texOffs(21, 47).addBox(-2.7F, 10.25F, -4.25F, 5.0F, 2.0F, 7.0F, new CubeDeformation(-0.43F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}
}