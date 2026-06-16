package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.blockentity.BaseECABlockEntity;
import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;

public class EnergyCellArrayRenderer extends MachineBaseRenderer<BaseECABlockEntity, EnergyCellArrayRenderer.State>
{
    private static EnergyDisplayModel fillModel(float x, float z)
    {
        return EnergyDisplayModel.create(x, 2.5f, z, 4, 9, 4, Direction.Axis.Y);
    }

    private final EnergyDisplayModel[] fillModels = new EnergyDisplayModel[]
            {
                    fillModel(2, 2),
                    fillModel(10, 2),
                    fillModel(2, 10),
                    fillModel(10, 10)
            };

    private final LimaColor fillColor;

    public EnergyCellArrayRenderer(BlockEntityRendererProvider.Context context, LimaColor fillColor)
    {
        super(context);
        this.fillColor = fillColor;
    }

    @Override
    public State createRenderState()
    {
        return new State();
    }

    @Override
    protected void extractAdditional(BaseECABlockEntity blockEntity, State state, float partialTick)
    {
        state.energyFill = blockEntity.getRemoteEnergyFill();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        if (state.energyFill > 0)
        {
            for (EnergyDisplayModel model : fillModels)
            {
                model.submit(poseStack, nodeCollector, state.energyFill, fillColor.argb32(), 0.8f);
            }
        }
    }

    public static final class State extends MachineRenderState
    {
        private State() { }

        private float energyFill;
    }
}