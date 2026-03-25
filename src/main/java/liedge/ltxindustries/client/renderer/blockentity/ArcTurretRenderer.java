package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.turret.ArcTurretBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ArcTurretRenderer extends TurretRenderer<ArcTurretBlockEntity>
{
    public ArcTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context, LTXIModelPartKeys.ARC_TURRET_SWIVEL, LTXIModelPartKeys.ARC_TURRET_WEAPONS);
    }

    @Override
    public void submit(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        Vec3 offset = renderState.chainOffset;
        List<EnergyBoltData> secondaryBolts = renderState.secondaryBolts;

        if (offset == null || secondaryBolts.isEmpty()) return;

        poseStack.pushPose();

        poseStack.translate(offset.x, offset.y, offset.z);

        for (EnergyBoltData bolt : secondaryBolts)
        {
            LTXIRenderer.submitEnergyBolt(poseStack, nodeCollector, RenderTypes.lightning(), bolt, LTXIConstants.ELECTRIC_GREEN, 0.85f);
        }

        poseStack.popPose();
    }

    @Override
    public void extractRenderState(ArcTurretBlockEntity blockEntity, TurretRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.primaryBolt = blockEntity.primaryBolt;

        Vec3 pos0 = blockEntity.chainOffset0;
        Vec3 pos = blockEntity.chainOffset;

        if (pos0 != null && pos != null) renderState.chainOffset = Mth.lerp(partialTick, pos0, pos);

        renderState.secondaryBolts = List.copyOf(blockEntity.boltChains);
    }

    @Override
    protected float yPivot()
    {
        return 1.71875f;
    }

    @Override
    protected void submitWeapons(TurretRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        super.submitWeapons(renderState, poseStack, nodeCollector, cameraRenderState);

        if (renderState.primaryBolt != null)
            LTXIRenderer.submitEnergyBolt(poseStack, nodeCollector, RenderTypes.lightning(), renderState.primaryBolt, LTXIConstants.ELECTRIC_GREEN, 0.85f);
    }
}