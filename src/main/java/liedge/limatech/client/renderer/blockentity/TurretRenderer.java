package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.BakedQuadGroup;
import liedge.limatech.blockentity.BaseTurretBlockEntity;
import liedge.limatech.client.model.baked.DynamicModularItemBakedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.AABB;

public abstract class TurretRenderer<BE extends BaseTurretBlockEntity> extends LimaBlockEntityRenderer<BE>
{
    private final RenderType nonEmissiveRenderType;
    private final RenderType emissiveRenderType;
    private final BakedQuadGroup guns;
    private final BakedQuadGroup swivel;

    protected TurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        DynamicModularItemBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(getModelItem()), DynamicModularItemBakedModel.class);

        this.nonEmissiveRenderType = model.getNonEmissiveRenderType();
        this.emissiveRenderType = model.getEmissiveRenderType();
        this.guns = model.getSubmodel("guns");
        this.swivel = model.getSubmodel("swivel");
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

        swivel.putItemQuadsInBuffer(poseStack, bufferSource, nonEmissiveRenderType, emissiveRenderType, packedLight);

        poseStack.translate(0.5d, gunsYPivot(), 0.5d);
        poseStack.mulPose(Axis.XP.rotationDegrees(blockEntity.lerpXRot(partialTick)));
        poseStack.translate(-0.5d, -gunsYPivot(), -0.5d);

        guns.putItemQuadsInBuffer(poseStack, bufferSource, nonEmissiveRenderType, emissiveRenderType, packedLight);
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