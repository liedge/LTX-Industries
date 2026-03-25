package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.blockentity.BaseECABlockEntity;
import liedge.ltxindustries.client.model.custom.EnergyFillData;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;

public class EnergyCellArrayRenderer extends MachineRenderer<BaseECABlockEntity>
{
    private static EnergyFillData fillModel(float x, float z)
    {
        return EnergyFillData.createStatic(x, 2.5f, z, 4, 9, 4, Direction.Axis.Y);
    }

    private final EnergyFillData[] fillModels = new EnergyFillData[]
            {
                    fillModel(2, 2),
                    fillModel(10, 2),
                    fillModel(2, 10),
                    fillModel(10, 10)
            };

    public EnergyCellArrayRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    void extractAdditional(BaseECABlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        renderState.energyFill = blockEntity.getRemoteEnergyFill();
        renderState.energyColor = blockEntity.getRemoteEnergyFillColor();
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        if (renderState.energyFill > 0)
        {
            for (EnergyFillData model : fillModels)
            {
                model.render(poseStack, nodeCollector, renderState.energyColor, renderState.energyFill);
            }
        }
    }
}