package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.client.renderer.entity.SeekerMineRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class SeekerMineModel extends EntityModel<SeekerMineRenderState>
{
    private final ModelPart body;
    private final ModelPart wheel;
    private final ModelPart caps;

    public SeekerMineModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);

        this.body = root.getChild("body");
        this.wheel = root.getChild("wheel");
        this.caps = root.getChild("caps");
    }

    @Override
    public void setupAnim(SeekerMineRenderState state)
    {
        super.setupAnim(state);

        body.yRot = state.yRot;
        wheel.yRot = state.yRot;
        caps.yRot = state.yRot;

        body.xRot = state.wheelXRot;
        wheel.xRot = state.wheelXRot;
        caps.xRot = state.capXRot;
    }

    public static LayerDefinition defineBaseLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition wheel = root.addOrReplaceChild("wheel", CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(10, 12).addBox(-2.0F, -3.0F, 3.0F, 4.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 20.0F, 0.0F));
        wheel.addOrReplaceChild("wheel4_r1", CubeListBuilder.create().texOffs(10, 12).addBox(-2.0F, -3.0F, 3.0F, 4.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 12).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        root.addOrReplaceChild("caps", CubeListBuilder.create().texOffs(24, 0).addBox(3.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(34, 0).addBox(-4.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 20.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition defineEmissiveLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition wheel = root.addOrReplaceChild("wheel", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, -3.5F, -4.5F, 4.0F, 7.0F, 2.0F, new CubeDeformation(-0.49F))
                .texOffs(0, 19).addBox(-2.0F, -3.5F, 2.5F, 4.0F, 7.0F, 2.0F, new CubeDeformation(-0.49F)), PartPose.offset(0.0F, 20.0F, 0.0F));
        wheel.addOrReplaceChild("wheel_l4_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, -3.5F, 2.5F, 4.0F, 7.0F, 2.0F, new CubeDeformation(-0.49F))
                .texOffs(0, 19).addBox(-2.0F, -3.5F, -4.5F, 4.0F, 7.0F, 2.0F, new CubeDeformation(-0.49F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        root.addOrReplaceChild("caps", CubeListBuilder.create().texOffs(12, 19).addBox(-4.5F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(-0.49F))
                .texOffs(12, 19).addBox(3.5F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(-0.49F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }
}