package liedge.ltxindustries.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.client.renderer.entity.ProjectileRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Function;

public abstract class ProjectileModel extends Model<ProjectileRenderState>
{
    protected ProjectileModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);
    }

    @Override
    public void setupAnim(ProjectileRenderState renderState)
    {
        super.setupAnim(renderState);

        List<ModelPart> parts = allParts();
        for (ModelPart part : parts)
        {
            rotatePart(renderState, part);
        }
    }

    protected void rotatePart(ProjectileRenderState state, ModelPart part)
    {
        part.yRot = state.yRot;
        part.xRot = state.xRot;
    }

    public abstract void submitParts(ProjectileRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, Identifier texture);
}