package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class MachineBaseRenderer<BE extends MachineBaseBlockEntity, S extends MachineRenderState> implements BlockEntityRenderer<BE, S>
{
    protected final ItemModelResolver itemResolver;
    protected final ModelManager modelManager;

    protected MachineBaseRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemResolver = context.itemModelResolver();
        this.modelManager = Minecraft.getInstance().getModelManager();
    }

    @Override
    public abstract S createRenderState();

    protected abstract void extractAdditional(BE blockEntity, S state, float partialTick);

    @Override
    public abstract void submit(S state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera);

    @Override
    public void extractRenderState(BE blockEntity, S state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getFacing();
        extractAdditional(blockEntity, state, partialTicks);
    }
}