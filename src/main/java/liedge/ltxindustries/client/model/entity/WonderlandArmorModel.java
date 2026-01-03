package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.List;

public class WonderlandArmorModel extends HumanoidModel<AbstractClientPlayer>
{
	public final ModelPart headLights;
	public final ModelPart bodyLights;
	public final ModelPart leftArmLights;
	public final ModelPart rightArmLights;
	public final ModelPart leftLegLights;
	public final ModelPart rightLegLights;
	public final ModelPart leftFoot;
	public final ModelPart leftFootLights;
	public final ModelPart rightFoot;
	public final ModelPart rightFootLights;
	public final ModelPart visor;

	private final List<ModelPart> cutoutParts;
	private final List<ModelPart> lightParts;

	public WonderlandArmorModel(ModelPart root)
	{
		super(root);

		this.hat.visible = false;

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

		this.cutoutParts = List.of(head, body, leftArm, rightArm, leftLeg, rightLeg, leftFoot, rightFoot);
		this.lightParts = List.of(headLights, bodyLights, leftArmLights, rightArmLights, leftLegLights, rightLegLights, leftFootLights, rightFootLights);
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		
		// Hat, required and unused
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		// Head non-emissive
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 29).addBox(3.5F, -5.5F, -2.0F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE)
				.texOffs(0, 29).mirror().addBox(-5.5F, -5.5F, -2.0F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE).mirror(false)
				.texOffs(32, 35).addBox(-4.5F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 37).addBox(3.5F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 18).addBox(-4.5F, -8.5F, -1.0F, 9.0F, 1.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(36, 0).addBox(-2.0F, -9.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.ZERO);
		head.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(6, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.25F, -7.5F, 1.5F, 0.0F, 0.0F, 0.6109F));
		head.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(6, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.25F, -7.5F, 1.5F, 0.0F, 0.0F, -0.6109F));

		// Visor
		root.addOrReplaceChild("visor", CubeListBuilder.create().texOffs(22, 40).addBox(-4.5F, -6.0F, -4.5F, 9.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.ZERO);

		// Head emissive
		PartDefinition head_lights = root.addOrReplaceChild("head_lights", CubeListBuilder.create().texOffs(0, 46).addBox(4.5F, -5.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.025F))
				.texOffs(0, 46).mirror().addBox(-5.5F, -5.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.ZERO);
		head_lights.addOrReplaceChild("halo_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -6.0F, 6.0F, 0.2618F, 0.0F, 0.0F));
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

	@Deprecated
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) { }

	@Override
	public void setAllVisible(boolean visible)
	{
		super.setAllVisible(visible);

		this.headLights.visible = visible;
		this.bodyLights.visible = visible;
		this.leftArmLights.visible = visible;
		this.rightArmLights.visible = visible;
		this.leftLegLights.visible = visible;
		this.rightLegLights.visible = visible;
		this.leftFoot.visible = visible;
		this.leftFootLights.visible = visible;
		this.rightFoot.visible = visible;
		this.rightFootLights.visible = visible;
		this.visor.visible = visible;
	}

	public void renderCutout(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
	{
		for (ModelPart part : cutoutParts)
		{
			part.render(poseStack, buffer, packedLight, packedOverlay, color);
		}
	}

	public void renderLights(PoseStack poseStack, VertexConsumer buffer, int color)
	{
		for (ModelPart part : lightParts)
		{
			part.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
		}
	}

	public void renderVisor(PoseStack poseStack, VertexConsumer buffer, int color)
	{
		visor.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
	}

	public void copyModelProperties(PlayerModel<AbstractClientPlayer> playerModel)
	{
		playerModel.copyPropertiesTo(this);
		headLights.copyFrom(playerModel.head);
		bodyLights.copyFrom(playerModel.body);
		leftArmLights.copyFrom(playerModel.leftArm);
		rightArmLights.copyFrom(playerModel.rightArm);
		leftLegLights.copyFrom(playerModel.leftLeg);
		rightLegLights.copyFrom(playerModel.rightLeg);
		leftFoot.copyFrom(playerModel.leftLeg);
		leftFootLights.copyFrom(playerModel.leftLeg);
		rightFoot.copyFrom(playerModel.rightLeg);
		rightFootLights.copyFrom(playerModel.rightLeg);
		visor.copyFrom(playerModel.head);
	}
}