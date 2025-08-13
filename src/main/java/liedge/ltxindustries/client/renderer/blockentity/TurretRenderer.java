package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.ltxindustries.blockentity.BaseTurretBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.AABB;

public abstract class TurretRenderer<BE extends BaseTurretBlockEntity> extends LimaBlockEntityRenderer<BE>
{
    private final BakedItemLayer gunsBase;
    private final BakedItemLayer gunsEmissive;
    private final BakedItemLayer swivelBase;
    private final BakedItemLayer swivelEmissive;

    protected TurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        ItemLayerBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(getModelItem()), ItemLayerBakedModel.class);
        this.gunsBase = model.getLayer("guns");
        this.gunsEmissive = model.getLayer("guns emissive");
        this.swivelBase = model.getLayer("swivel");
        this.swivelEmissive = model.getLayer("swivel emissive");
    }

    protected abstract ItemLike getModelItem();

    protected abstract double gunsYPivot();

    protected void renderAdditionalGuns(BE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {}

    @Override
    public void render(BE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        // Render the guns
        poseStack.pushPose();

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.lerpYRot(partialTick)));
        poseStack.translate(-0.5d, 0, -0.5d);

        swivelBase.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        swivelEmissive.putQuadsInBuffer(poseStack, bufferSource, packedLight);

        poseStack.translate(0.5d, gunsYPivot(), 0.5d);
        poseStack.mulPose(Axis.XP.rotationDegrees(blockEntity.lerpXRot(partialTick)));
        poseStack.translate(-0.5d, -gunsYPivot(), -0.5d);

        gunsBase.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        gunsEmissive.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        renderAdditionalGuns(blockEntity, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 128;
    }

    @Override
    public boolean shouldRenderOffScreen(BE blockEntity)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BE blockEntity)
    {
        return !blockEntity.getTargetQueue().isEmpty() ? blockEntity.getTargetArea() : blockEntity.getDefaultRenderBox();
    }
}