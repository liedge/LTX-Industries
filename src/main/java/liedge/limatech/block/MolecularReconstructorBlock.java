package liedge.limatech.block;

import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MolecularReconstructorBlock extends DoubleWrenchBlock
{
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            // Base
            Block.box(0, 0, 0, 16, 8, 16),
            // Platform
            LimaBlockUtil.dimensionBox(2, 8, 2, 12, 2, 12),
            // Pillars
            LimaBlockUtil.dimensionBox(1, 8, 1, 3, 13, 3),
            LimaBlockUtil.dimensionBox(12, 8, 1, 3, 13, 3),
            LimaBlockUtil.dimensionBox(1, 8, 12, 3, 13, 3),
            LimaBlockUtil.dimensionBox(12, 8, 12, 3, 13, 3),
            // Top
            LimaBlockUtil.dimensionBox(0.5d, 21, 0.5d, 15, 4, 15),
            LimaBlockUtil.dimensionBox(2, 25, 2, 12, 2, 12));
    private static final VoxelShape UPPER_SHAPE = LimaBlockUtil.moveShape(LOWER_SHAPE, 0, -1, 0);

    public MolecularReconstructorBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected VoxelShape getUpperShape()
    {
        return UPPER_SHAPE;
    }

    @Override
    protected VoxelShape getLowerShape()
    {
        return LOWER_SHAPE;
    }
}