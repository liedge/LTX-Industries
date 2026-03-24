package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.turret.RailgunTurretBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RailgunTurretRenderer extends TurretRenderer<RailgunTurretBlockEntity>
{
    private static final float LASER_RADIUS = 0.015625f;

    public RailgunTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context, LTXIModelPartKeys.RAILGUN_TURRET_SWIVEL, LTXIModelPartKeys.RAILGUN_TURRET_WEAPONS);
    }

    @Override
    public void extractRenderState(RailgunTurretBlockEntity blockEntity, TurretRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.railgunBeamColor = blockEntity.lerpAimTicks(1f, 25) >= 1 ? LTXIConstants.LIME_GREEN : LTXIConstants.HOSTILE_ORANGE;
    }

    @Override
    protected float yPivot()
    {
        return 1.6875f;
    }

    @Override
    protected void submitWeapons(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        super.submitWeapons(renderState, poseStack, nodeCollector, cameraRenderState);

        if (renderState.lookingAtTarget)
        {
            float dist = (float) renderState.targetDistance - 0.4375f;
            if (dist > 0)
            {
                nodeCollector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, buffer) ->
                {
                    float x = 0.5f;
                    float y = 1.6875f;
                    float z = 0.0625f;

                    LTXIRenderer.submitUnlitCuboid(pose, buffer, Direction.values(), x - LASER_RADIUS, y - LASER_RADIUS, z, x + LASER_RADIUS, y + LASER_RADIUS, z - dist, renderState.railgunBeamColor, 0.8f);
                });
            }
        }
    }
}