package liedge.ltxindustries.client.renderer.blockentity;

import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jspecify.annotations.Nullable;

public abstract class SimpleMachineRenderer<BE extends MachineBaseBlockEntity> extends MachineBaseRenderer<BE, SimpleMachineRenderer.State>
{
    protected SimpleMachineRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public State createRenderState()
    {
        return new State();
    }

    public static final class State extends MachineRenderState
    {
        private State() { }

        float machineSpin;

        @Nullable
        EnergyBoltData machineBolt;
    }
}