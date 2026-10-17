package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.client.renderer.entity.BombletRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class BombletModel extends EntityModel<BombletRenderer.State>
{
    private final ModelPart body;

    public BombletModel(ModelPart root)
    {
        super(root, RenderTypes::entityTranslucent);
        this.body = root.getChild("body");
    }

    @Override
    public void setupAnim(BombletRenderer.State state)
    {
        super.setupAnim(state);

        body.xRot = state.rotation;
        body.yRot = state.rotation;
    }

    public static LayerDefinition defineLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 8).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 22.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }
}