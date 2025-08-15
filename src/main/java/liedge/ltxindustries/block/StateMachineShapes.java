package liedge.ltxindustries.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static liedge.limacore.util.LimaBlockUtil.dimensionBox;

public final class StateMachineShapes
{
    private StateMachineShapes() {}

    public static final VoxelShape COOKING_MACHINE_SHAPE = Shapes.or(
            // Bottom frame base
            Block.box(0, 0, 0, 16, 4, 16),
            // Vertical frame pieces
            Block.box(0, 0, 0, 1, 16, 1),
            Block.box(15, 0, 0, 16, 16, 1),
            Block.box(0, 0, 15, 1, 16, 16),
            Block.box(15, 0, 15, 16, 16, 16),
            // Top frame pieces
            Block.box(0, 15, 0, 16, 16, 1),
            Block.box(0, 15, 15, 16, 16, 16),
            Block.box(0, 15, 0, 1, 16, 16),
            Block.box(15, 15, 0, 16, 16, 16),
            // Center box
            dimensionBox(0.5d, 3.5d, 0.5d, 15, 12, 15));

    public static final VoxelShape ELECTROCENTRIFUGE_SHAPE = Shapes.or(
            // Chassis
            dimensionBox(0, 0, 0, 16, 8, 16),
            // Frame top
            dimensionBox(0, 8, 0, 16, 2, 1),
            dimensionBox(0, 8, 15, 16, 2, 1),
            dimensionBox(0, 8, 1, 1, 2, 14),
            dimensionBox(15, 8, 1, 1, 2, 14),
            // Tubes
            dimensionBox(2, 8, 2, 12, 8, 12));

    public static final VoxelShape MIXER_SHAPE = Shapes.or(
            dimensionBox(0.5d, 0, 0.5d, 15, 5, 15),
            dimensionBox(1.5d, 5, 1.5d, 13, 1, 13),
            dimensionBox(4, 0.5d, 0, 8, 5, 1),
            dimensionBox(2, 6, 2, 12, 7, 12),
            dimensionBox(1.5d, 13, 1.5d, 13, 2, 13),
            dimensionBox(3, 15, 3, 10, 1, 10));

    public static final VoxelShape CHEM_LAB_SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 5, 16),
            dimensionBox(6, 5, 0, 10, 10, 16),
            dimensionBox(1, 5, 1, 4, 9, 4),
            dimensionBox(1, 5, 6, 4, 9, 4),
            dimensionBox(1, 5, 11, 4, 9, 4));
}