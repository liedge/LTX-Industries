package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.blockentity.turret.RocketTurretBlockEntity;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import liedge.ltxindustries.client.renderer.LockOnRenderData;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class RocketTurretRenderer extends TurretRenderer<RocketTurretBlockEntity>
{
    private final EntityRenderDispatcher entityRenderer;

    public RocketTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context, LTXIModelPartKeys.ROCKET_TURRET_SWIVEL, LTXIModelPartKeys.ROCKET_TURRET_WEAPONS);
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    protected float yPivot()
    {
        return 1.59375f;
    }

    @Override
    public void extractRenderState(RocketTurretBlockEntity blockEntity, TurretRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        Camera camera = entityRenderer.camera;
        Entity target = blockEntity.getClientTarget();

        if (camera != null)
        {
            BlockPos pos = renderState.blockPos;
            float progress = blockEntity.lerpAimTicks(partialTick, 34);

            List<LockOnRenderData> rocketTargets = new ObjectArrayList<>();

            if (target != null) rocketTargets.add(LockOnRenderData.of(target, pos.getX(), pos.getY(), pos.getZ(), camera, progress, partialTick));

            for (Entity entity : blockEntity.getTargetQueue())
            {
                rocketTargets.add(LockOnRenderData.of(entity, pos.getX(), pos.getY(), pos.getZ(), camera, progress, partialTick));
            }

            renderState.rocketTargets = rocketTargets;
        }
    }

    @Override
    public void submit(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        List<LockOnRenderData> targets = renderState.rocketTargets;
        for (LockOnRenderData target : targets)
        {
            nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.LOCK_ON_INDICATOR, target);
        }
    }
}