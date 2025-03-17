package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.BakedQuadGroup;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.DynamicModularItemBakedModel;
import liedge.limatech.registry.LimaTechBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class RocketTurretRenderer extends LimaBlockEntityRenderer<RocketTurretBlockEntity>
{
    private final RenderType nonEmissiveRenderType;
    private final RenderType emissiveRenderType;
    private final BakedQuadGroup guns;
    private final BakedQuadGroup swivel;

    public RocketTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        DynamicModularItemBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(LimaTechBlocks.ROCKET_TURRET), DynamicModularItemBakedModel.class);

        this.nonEmissiveRenderType = model.getNonEmissiveRenderType();
        this.emissiveRenderType = model.getEmissiveRenderType();
        this.guns = model.getSubmodel("guns");
        this.swivel = model.getSubmodel("swivel");
    }

    @Override
    public void render(RocketTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        // Render the turret gun
        poseStack.pushPose();

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.lerpYRot(partialTick)));
        poseStack.translate(-0.5d, 0, -0.5d);

        swivel.putItemQuadsInBuffer(poseStack, bufferSource, nonEmissiveRenderType, emissiveRenderType, light);

        poseStack.translate(0.5d, 1.625d, 0.5d);
        poseStack.mulPose(Axis.XN.rotationDegrees(blockEntity.lerpXRot(partialTick)));
        poseStack.translate(-0.5d, -1.625d, -0.5d);

        guns.putItemQuadsInBuffer(poseStack, bufferSource, nonEmissiveRenderType, emissiveRenderType, light);

        poseStack.popPose();

        // Render lock on indicators
        BlockPos pos = blockEntity.getBlockPos();
        float indicatorLerp = blockEntity.lerpIndicators(partialTick);
        for (Entity target : blockEntity.getTargetQueue())
        {
            LimaTechRenderUtil.renderLockOnIndicatorOnEntity(target, poseStack, bufferSource, entityRenderer.camera, pos.getX(), pos.getY(), pos.getZ(), partialTick, indicatorLerp);
        }
    }

    @Override
    public int getViewDistance()
    {
        return 128;
    }

    @Override
    public boolean shouldRenderOffScreen(RocketTurretBlockEntity blockEntity)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RocketTurretBlockEntity blockEntity)
    {
        if (!blockEntity.getTargetQueue().isEmpty())
        {
            return AABB.INFINITE;
        }
        else
        {
            BlockPos pos = blockEntity.getBlockPos();
            return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        }
    }
}