package liedge.ltxindustries.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import liedge.ltxindustries.block.PrimaryMeshBlock;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.BitSet;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin
{
    @WrapWithCondition(method = "renderModelFaceFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;calculateShape(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;[ILnet/minecraft/core/Direction;[FLjava/util/BitSet;)V"))
    private boolean skipShapeCheckPrimaryMesh(ModelBlockRenderer instance, BlockAndTintGetter level, BlockState state, BlockPos pos, int[] vertices, Direction direction, float[] shape, BitSet flags)
    {
        if (state.getBlock() instanceof PrimaryMeshBlock)
        {
            flags.clear(0);
            return false;
        }

        return true;
    }
}