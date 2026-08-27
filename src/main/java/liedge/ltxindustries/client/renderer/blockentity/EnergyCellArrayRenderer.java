package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.blockentity.BaseECABlockEntity;
import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;

import java.util.List;

public class EnergyCellArrayRenderer extends MachineBaseRenderer<BaseECABlockEntity, EnergyCellArrayRenderer.State>
{
    public static List<EnergyDisplayModel> createDisplays()
    {
        List<EnergyDisplayModel> list = new ObjectArrayList<>();
        list.add(EnergyDisplayModel.create(2f, 2.5f, 2f, 4, 9, 4, Direction.Axis.Y));
        list.add(EnergyDisplayModel.create(10f, 2.5f, 2f, 4, 9, 4, Direction.Axis.Y));
        list.add(EnergyDisplayModel.create(2f, 2.5f, 10f, 4, 9, 4, Direction.Axis.Y));
        list.add(EnergyDisplayModel.create(10f, 2.5f, 10f, 4, 9, 4, Direction.Axis.Y));

        return list;
    }

    private final List<EnergyDisplayModel> fillModels = createDisplays();
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