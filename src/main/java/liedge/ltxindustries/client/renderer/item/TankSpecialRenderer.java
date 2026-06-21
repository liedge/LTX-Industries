package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.ltxindustries.blockentity.PortableTankBlockEntity;
import liedge.ltxindustries.client.model.custom.TankContentsModel;
import liedge.ltxindustries.item.PortableTankItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class TankSpecialRenderer implements SpecialModelRenderer<TankContentsModel>
{
    private TankSpecialRenderer() { }

    @Override
    public void submit(@Nullable TankContentsModel argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (argument != null)
        {
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, buffer) -> argument.render(pose, buffer, lightCoords));
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output)
    {
        LimaCoreClientUtil.sizedCubeExtents(output, 3, 3, 3, 10, 11, 10);
    }

    @Override
    public @Nullable TankContentsModel extractArgument(ItemStack stack)
    {
        if (!(stack.getItem() instanceof PortableTankItem tankItem)) return null;

        SimpleFluidContent content = stack.getOrDefault(LimaCoreDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY);
        if (content.isEmpty()) return null;

        FluidStack fluidStack = content.copy();
        int fluidLevel = PortableTankBlockEntity.getFluidVisualLevel(fluidStack.getAmount(), tankItem.getFluidCapacity(stack));

        return TankContentsModel.extract(fluidStack, fluidLevel);
    }

    public static final class Unbaked implements SpecialModelRenderer.Unbaked<TankContentsModel>
    {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(INSTANCE);

        private Unbaked() { }

        @Override
        public SpecialModelRenderer<TankContentsModel> bake(BakingContext context)
        {
            return new TankSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<TankContentsModel>> type()
        {
            return CODEC;
        }
    }
}