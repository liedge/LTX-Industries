package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.blockentity.PortableTankBlockEntity;
import liedge.ltxindustries.client.model.custom.TankContentsModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public class TankRenderer extends MachineBaseRenderer<PortableTankBlockEntity, TankRenderer.State>
{
    public TankRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public State createRenderState()
    {
        return new State();
    }

    @Override
    protected void extractAdditional(PortableTankBlockEntity blockEntity, State state, float partialTick)
    {
        FluidStack stack = blockEntity.clientFluid.toStack(1);
        state.fluidContents = TankContentsModel.extract(stack, blockEntity.clientFluidLevel);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        if (state.fluidContents != null)
        {
            nodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, buffer) -> state.fluidContents.render(pose, buffer, state.lightCoords));
        }
    }

    public static final class State extends MachineRenderState
    {
        @Nullable
        private TankContentsModel fluidContents;
    }
}