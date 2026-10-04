package liedge.ltxindustries.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class WonderlandArmorModel<S extends HumanoidRenderState> extends HumanoidModel<S>
{
	public WonderlandArmorModel(ModelPart root, Function<Identifier, RenderType> renderType)
	{
		super(root, renderType);
	}

	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event, ArmorModelSet<ModelLayerLocation> locations, Supplier<MeshDefinition> meshFactory)
	{
		MeshDefinition head = meshFactory.get();
		head.getRoot().retainPartsAndChildren(Set.of("head"));
		event.registerLayerDefinition(locations.head(), () -> LayerDefinition.create(head, 64, 64));

		MeshDefinition body = meshFactory.get();
		body.getRoot().retainExactParts(Set.of("body", "left_arm", "right_arm"));
		event.registerLayerDefinition(locations.chest(), () -> LayerDefinition.create(body, 64, 64));

		MeshDefinition legs = meshFactory.get();
		legs.getRoot().retainExactParts(Set.of("left_leg", "right_leg"));
		event.registerLayerDefinition(locations.legs(), () -> LayerDefinition.create(legs, 64, 64));

		MeshDefinition feet = meshFactory.get();
		feet.getRoot().retainExactParts(Set.of("left_foot", "right_foot"));
		event.registerLayerDefinition(locations.feet(), () -> LayerDefinition.create(feet, 64, 64));
	}

	public static LayerDefinition defineVisorLayer()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 40).addBox(-4.5F, -6.0F, -4.5F, 9.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.ZERO);
		head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		return LayerDefinition.create(mesh, 64, 64);
	}

	public static MeshDefinition createBaseMesh()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 29).addBox(3.5F, -5.5F, -2.0F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE)
				.texOffs(0, 29).mirror().addBox(-5.5F, -5.5F, -2.0F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE).mirror(false)
				.texOffs(32, 35).addBox(-4.5F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 37).addBox(3.5F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 18).addBox(-4.5F, -8.5F, -1.0F, 9.0F, 1.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(36, 0).addBox(-2.0F, -9.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.ZERO);
		head.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(6, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.25F, -7.5F, 1.5F, 0.0F, 0.0F, 0.6109F));
		head.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(6, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.25F, -7.5F, 1.5F, 0.0F, 0.0F, -0.6109F));
		head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.1F))
				.texOffs(0, 11).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.1F))
				.texOffs(40, 3).addBox(-1.5F, 9.0F, -2.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.12F)), PartPose.ZERO);

		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 27).mirror().addBox(-2.0F, -2.5F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false)
				.texOffs(24, 0).addBox(-1.0F, 3.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.08F))
				.texOffs(24, 5).addBox(2.75F, 3.0F, -1.5F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));

		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 27).addBox(0.0F, -2.5F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(24, 0).mirror().addBox(-3.0F, 3.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.08F)).mirror(false)
				.texOffs(24, 5).mirror().addBox(-3.25F, 3.0F, -1.5F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 10).addBox(-1.9F, 4.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.065F))
				.texOffs(24, 15).addBox(-1.4F, 3.0F, -2.75F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F, 0.0F));
		leftLeg.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(16, 19).addBox(-1.875F, 10.0F, -3.75F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.ZERO);

		PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 10).addBox(-2.1F, 4.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.08F))
				.texOffs(24, 15).addBox(-1.6F, 3.0F, -2.75F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F, 0.0F));
		rightLeg.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(16, 19).addBox(-2.175F, 10.0F, -3.75F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.ZERO);

		return mesh;
	}

	public static MeshDefinition createEmissiveMesh()
	{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 46).addBox(4.5F, -5.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.025F))
				.texOffs(0, 46).mirror().addBox(-5.5F, -5.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.ZERO);
		head.addOrReplaceChild("leftearlight_r1", CubeListBuilder.create().texOffs(12, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.25F, -7.5F, 1.5F, 0.0F, 0.0F, 0.6109F));
		head.addOrReplaceChild("rightearlight_r1", CubeListBuilder.create().texOffs(12, 37).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.25F, -7.5F, 1.5F, 0.0F, 0.0F, -0.6109F));
		head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(44, 6).addBox(-0.25F, 8.5F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.371F))
				.texOffs(40, 6).addBox(0.1F, 9.0F, -2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.13F))
				.texOffs(45, 21).addBox(-1.0F, 5.6F, -2.125F, 2.0F, 1.0F, 0.0F, CubeDeformation.NONE)
				.texOffs(45, 21).addBox(-1.0F, 5.6F, 2.125F, 2.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.ZERO);

		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(10, 46).mirror().addBox(-1.825F, -2.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.21F)).mirror(false)
				.texOffs(48, 0).addBox(3.26F, 2.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.5F))
				.texOffs(43, 13).addBox(2.125F, 2.5F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(-0.48F)), PartPose.offset(5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(10, 46).mirror().addBox(0.175F, -2.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.21F)).mirror(false)
				.texOffs(48, 0).addBox(-3.76F, 2.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.5F))
				.texOffs(43, 13).addBox(-3.875F, 2.5F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(-0.48F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 15).addBox(-1.9F, 2.5F, -3.25F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.48F)), PartPose.offset(1.9F, 12.0F, 0.0F));
		leftLeg.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(21, 47).addBox(-2.375F, 10.25F, -4.25F, 5.0F, 2.0F, 7.0F, new CubeDeformation(-0.43F)), PartPose.ZERO);

		PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 15).addBox(-2.1F, 2.5F, -3.25F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.48F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
		rightLeg.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(21, 47).addBox(-2.7F, 10.25F, -4.25F, 5.0F, 2.0F, 7.0F, new CubeDeformation(-0.43F)), PartPose.ZERO);

		return mesh;
	}
}