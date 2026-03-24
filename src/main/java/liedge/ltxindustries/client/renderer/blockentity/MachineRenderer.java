package liedge.ltxindustries.client.renderer.blockentity;

import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class MachineRenderer<BE extends LTXIMachineBlockEntity> implements BlockEntityRenderer<BE, MachineRenderState>
{
    final ItemModelResolver itemResolver;
    final ModelManager modelManager;

    protected MachineRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemResolver = context.itemModelResolver();
        this.modelManager = context.blockRenderDispatcher().getBlockModelShaper().getModelManager();
    }

    abstract void extractAdditional(BE blockEntity, MachineRenderState renderState, float partialTick);

    @Override
    public final MachineRenderState createRenderState()
    {
        return new MachineRenderState();
    }

    @Override
    public void extractRenderState(BE blockEntity, MachineRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.facing = blockEntity.getFacing();

        extractAdditional(blockEntity, renderState, partialTick);
    }
}