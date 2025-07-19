package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.blockentity.RocketTurretBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;

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
        return 1.625d;
    }

    @Override
    public void render(RocketTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        // Render lock on indicators
        BlockPos pos = blockEntity.getBlockPos();
        float indicatorLerp = blockEntity.lerpTicker(partialTick, 34);
        for (Entity target : blockEntity.getTargetQueue())
        {
            LTXIRenderUtil.renderLockOnIndicatorOnEntity(target, poseStack, bufferSource, entityRenderer.camera, pos.getX(), pos.getY(), pos.getZ(), partialTick, indicatorLerp);
        }
    }
}