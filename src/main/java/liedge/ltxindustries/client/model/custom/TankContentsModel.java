package liedge.ltxindustries.client.model.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.ltxindustries.blockentity.PortableTankBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public final class TankContentsModel
{
    private static final Direction[] RENDER_SIDES = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public static @Nullable TankContentsModel extract(FluidStack stack, int fluidLevel)
    {
        if (stack.isEmpty() || fluidLevel <= 0) return null;

        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(stack.getFluid().defaultFluidState());
        Material.Baked material = model.stillMaterial();
        FluidTintSource tintSource = model.fluidTintSource();
        TextureAtlasSprite sprite = material.sprite();

        float[] uvs = new float[8];
        //Sides
        uvs[0] = sprite.getU(0.1875f);
        uvs[1] = sprite.getV((2 + PortableTankBlockEntity.FLUID_VISUAL_LEVELS - fluidLevel) * 0.0625f);
        uvs[2] = sprite.getU(0.8125f);
        uvs[3] = sprite.getV(0.8125f);
        // Top
        uvs[4] = sprite.getU(0.1875f);
        uvs[5] = sprite.getV(0.1875f);
        uvs[6] = sprite.getU(0.8125f);
        uvs[7] = sprite.getV(0.8125f);

        int fluidTint = tintSource != null ? tintSource.colorAsStack(stack) : -1;
        int fluidLight = stack.getFluidType().getLightLevel(stack);

        return new TankContentsModel(fluidTint, fluidLevel, fluidLight, uvs);
    }

    private final int fluidTint;
    private final int fluidLevel;
    private final int fluidLight;
    private final float[] uvs;

    private TankContentsModel(int fluidTint, int fluidLevel, int fluidLight, float[] uvs)
    {
        this.fluidTint = fluidTint;
        this.fluidLevel = fluidLevel;
        this.fluidLight = fluidLight;
        this.uvs = uvs;
    }

    public void render(PoseStack.Pose pose, VertexConsumer buffer, int contextLight)
    {
        float xz1 = 3.01f * 0.0625f;
        float xz2 = 12.99f * 0.0625f;
        float y1 = 3.01f * 0.0625f;
        float y2 = (2.99f + fluidLevel) * 0.0625f;

        for (Direction side : RENDER_SIDES)
        {
            int uvi = side.getAxis() == Direction.Axis.Y ? 4 : 0;

            int blockLight = LightCoordsUtil.block(contextLight);
            int lightCoords = fluidLight <= blockLight ? contextLight : LightCoordsUtil.withBlock(contextLight, fluidLight);

            LTXIRenderer.submitBlockFormatQuad(pose, buffer, side,
                    xz1, y1, xz1,
                    xz2, y2, xz2,
                    uvs[uvi], uvs[uvi + 1], uvs[uvi + 2], uvs[uvi + 3], fluidTint, lightCoords);
        }
    }
}