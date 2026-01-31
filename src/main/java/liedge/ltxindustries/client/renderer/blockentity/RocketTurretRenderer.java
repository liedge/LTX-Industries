package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.blockentity.turret.RocketTurretBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class RocketTurretRenderer extends TurretRenderer<RocketTurretBlockEntity>
{
    public RocketTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected ItemLike getModelItem()
    {
        return LTXIBlocks.ROCKET_TURRET;
    }

    @Override
    protected double gunsYPivot()
    {
        return 1.59375d;
    }

    @Override
    public void render(RocketTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        // Render lock on indicators
        BlockPos pos = blockEntity.getBlockPos();
        float indicatorLerp = blockEntity.lerpAimTicks(partialTick, 34);

        drawLockIndicator(blockEntity.getClientTarget(), poseStack, bufferSource, pos, partialTick, indicatorLerp);
        for (Entity entity : blockEntity.getTargetQueue())
        {
            drawLockIndicator(entity, poseStack, bufferSource, pos, partialTick, indicatorLerp);
        }
    }

    private void drawLockIndicator(@Nullable Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, BlockPos pos, float partialTick, float targetProgress)
    {
        if (entity != null)
        {
            LTXIRenderUtil.renderLockOnIndicatorOnEntity(entity, poseStack, bufferSource, entityRenderer.camera, pos.getX(), pos.getY(), pos.getZ(), partialTick, targetProgress);
        }
    }
}