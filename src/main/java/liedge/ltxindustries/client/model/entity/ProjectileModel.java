package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.client.renderer.entity.ProjectileRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public abstract class ProjectileModel extends EntityModel<ProjectileRenderState>
{
    private final ModelPart body;

    protected ProjectileModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);
        this.body = root.getChild("body");
    }

    @Override
    public void setupAnim(ProjectileRenderState renderState)
    {
        super.setupAnim(renderState);

        body.yRot = renderState.yRot;
        body.xRot = renderState.xRot;
    }
}