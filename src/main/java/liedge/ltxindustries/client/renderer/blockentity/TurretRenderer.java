package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.ltxindustries.blockentity.turret.TurretBlockEntity;
import liedge.ltxindustries.client.model.StandaloneQuads;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

public abstract class TurretRenderer<BE extends TurretBlockEntity> implements BlockEntityRenderer<BE, TurretRenderState>
{
    private final StandaloneQuads swivelModel;
    private final StandaloneQuads weaponsModel;

    TurretRenderer(BlockEntityRendererProvider.Context context, StandaloneModelKey<StandaloneQuads> swivelKey, StandaloneModelKey<StandaloneQuads> weaponsKey)
    {
        this.swivelModel = StandaloneQuads.get(swivelKey);
        this.weaponsModel = StandaloneQuads.get(weaponsKey);
    }

    protected void submitSwivel(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        swivelModel.render(poseStack, nodeCollector, renderState.lightCoords);
    }

    protected void submitWeapons(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        weaponsModel.render(poseStack, nodeCollector, renderState.lightCoords);
    }

    protected abstract float yPivot();

    @Override
    public TurretRenderState createRenderState()
    {
        return new TurretRenderState();
    }

    @Override
    public void extractRenderState(BE blockEntity, TurretRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.xRot = blockEntity.lerpXRot(partialTick);
        renderState.yRot = blockEntity.lerpYRot(partialTick);
        renderState.lookingAtTarget = blockEntity.isLookingAtTarget();
        renderState.targetDistance = blockEntity.getTargetDistance();
    }

    @Override
    public void submit(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
        poseStack.translate(-0.5f, 0, -0.5f);

        submitSwivel(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.translate(0.5f, yPivot(), 0.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.xRot));
        poseStack.translate(-0.5f, -yPivot(), -0.5f);

        submitWeapons(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 128;
    }

    @Override
    public boolean shouldRenderOffScreen()
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BE blockEntity)
    {
        return blockEntity.getTurretState().isExtendedRender() ? AABB.INFINITE : blockEntity.getBoundingBox();
    }
}