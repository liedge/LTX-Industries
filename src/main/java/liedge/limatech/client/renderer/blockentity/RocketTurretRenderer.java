package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.model.baked.LimaTechExtraBakedModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class RocketTurretRenderer extends LimaBlockEntityRenderer<RocketTurretBlockEntity>
{
    private final DynamicModularBakedModel.SubModel gun;
    private final DynamicModularBakedModel.SubModel swivel;

    public RocketTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        DynamicModularBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaTechExtraBakedModels.ROCKET_TURRET_GUN, DynamicModularBakedModel.class);
        this.gun = model.getSubmodel("gun");
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

        swivel.renderToBuffer(poseStack, bufferSource, light);

        poseStack.translate(0.5d, 1.6875d, 0.5d);
        poseStack.mulPose(Axis.XN.rotationDegrees(blockEntity.lerpXRot(partialTick)));
        poseStack.translate(-0.5d, -1.6875d, -0.5d);

        gun.renderToBuffer(poseStack, bufferSource, light);

        poseStack.popPose();

        // Render lock on triangle
        Entity currentTarget = blockEntity.getCurrentTarget();
        if (currentTarget != null)
        {
            BlockPos pos = blockEntity.getBlockPos();
            LimaTechRenderUtil.renderLockOnIndicatorOnEntity(currentTarget, poseStack, bufferSource, entityRenderer.camera, pos.getX(), pos.getY(), pos.getZ(), partialTick, blockEntity.lerpTicker(partialTick));
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
        if (blockEntity.getCurrentTarget() != null)
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