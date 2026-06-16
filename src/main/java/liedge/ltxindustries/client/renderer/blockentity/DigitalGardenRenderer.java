package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.ltxindustries.blockentity.DigitalGardenBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class DigitalGardenRenderer extends MachineBaseRenderer<DigitalGardenBlockEntity, DigitalGardenRenderer.State>
{
    public DigitalGardenRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public State createRenderState()
    {
        return new State();
    }

    @Override
    protected void extractAdditional(DigitalGardenBlockEntity blockEntity, State state, float partialTick)
    {
        ItemStack stack = blockEntity.getClientPreviewItem();
        if (stack.isEmpty()) return;

        ItemStackRenderState previewItem = new ItemStackRenderState();
        itemResolver.updateForTopItem(previewItem, stack, ItemDisplayContext.FIXED, null, null, 0);
        state.previewItem = previewItem;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        ItemStackRenderState previewItem = state.previewItem;
        if (previewItem == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.4375f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(state.facing)));
        poseStack.translate(0, 0, -0.53125f);
        poseStack.scale(0.4375f, 0.4375f, 0.4375f);

        previewItem.submit(poseStack, nodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    public static final class State extends MachineRenderState
    {
        private State() { }

        @Nullable
        private ItemStackRenderState previewItem;
    }
}