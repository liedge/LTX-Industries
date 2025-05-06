package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.blockentity.BaseESABlockEntity;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static liedge.limatech.LimaTechConstants.*;
import static liedge.limatech.block.LimaTechBlockProperties.getESASideIOProperty;

public class EnergyStorageArrayRenderer extends LimaBlockEntityRenderer<BaseESABlockEntity>
{
    private final TranslucentFillModel[] fillModels = new TranslucentFillModel[]
            {
                    TranslucentFillModel.create(0.76f, 3.01f, 0.76f, 7.74f, 12.99f, 7.74f, Direction.Axis.Y),
                    TranslucentFillModel.create(8.26f, 3.01f, 0.76f, 15.24f, 12.99f, 7.74f, Direction.Axis.Y),
                    TranslucentFillModel.create(0.76f, 3.01f, 8.26f, 7.74f, 12.99f, 15.24f, Direction.Axis.Y),
                    TranslucentFillModel.create(8.26f, 3.01f, 8.26f, 15.24f, 12.99f, 15.24f, Direction.Axis.Y)
            };

    public static final BlockColor ESA_BLOCK_COLOR = new ColorHandler();
    public static final ItemColor TIERED_ESA_COLOR = new ESAItemColor(REM_BLUE.argb32());
    public static final ItemColor INFINITE_ESA_COLOR = new ESAItemColor(CREATIVE_PINK.argb32());

    public EnergyStorageArrayRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(BaseESABlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        float fill = blockEntity.getRemoteEnergyFill();

        if (fill > 0)
        {
            VertexConsumer buffer = bufferSource.getBuffer(LimaTechRenderTypes.POSITION_COLOR_QUADS);

            for (TranslucentFillModel model : fillModels)
            {
                model.render(buffer, poseStack, blockEntity.getRemoteEnergyFillColor(), fill);
            }
        }
    }

    public static class ColorHandler implements BlockColor
    {
        private ColorHandler() {}

        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex)
        {
            if (tintIndex > 0 && tintIndex < 7)
            {
                Direction side = switch (tintIndex)
                {
                    case 1 -> Direction.NORTH;
                    case 2 -> Direction.SOUTH;
                    case 3 -> Direction.EAST;
                    case 4 -> Direction.WEST;
                    case 5 -> Direction.UP;
                    case 6 -> Direction.DOWN;
                    default -> throw new RuntimeException("Invalid tin index for side color map.");
                };

                LimaColor color = switch (state.getValue(getESASideIOProperty(side)))
                {
                    case INPUT_ONLY -> INPUT_BLUE;
                    case OUTPUT_ONLY -> OUTPUT_ORANGE;
                    default -> LimaColor.BLACK;
                };

                return color.argb32();
            }

            return 0xFFFFFFFF;
        }
    }

    private record ESAItemColor(int argb32) implements ItemColor
    {
        @Override
        public int getColor(ItemStack stack, int tintIndex)
        {
            return tintIndex == -1 ? 0xFFFFFFFF : argb32;
        }
    }
}