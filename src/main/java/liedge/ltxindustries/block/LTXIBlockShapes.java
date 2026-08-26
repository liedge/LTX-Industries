package liedge.ltxindustries.block;

import liedge.limacore.util.LimaShapesUtil;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class LTXIBlockShapes
{
    private LTXIBlockShapes() {}

    //#region Single block shapes
    public static final VoxelShape RAW_ORE_CLUSTER = LimaShapesUtil.shapeBuilder()
            .box(1, 0, 1, 14, 2, 14)
            .box(2, 2, 2, 12, 14, 12)
            .build();

    public static final VoxelShape GLOWSTICK = LimaShapesUtil.sizedBox(7, 0, 7, 2, 8, 2);

    public static final VoxelShape UPGRADE_STATION = LimaShapesUtil.shapeBuilder()
            // Center
            .box(1, 0, 1, 14, 12, 14)
            // Feet
            .box(0, 0, 0, 2, 4, 5)
            .box(14, 0, 0, 2, 4, 5)
            .box(0, 0, 11, 5, 4, 5)
            .box(11, 0, 11, 5, 4, 5)
            // Top edges
            .box(0, 12, 0, 16, 1, 16)
            .box(0, 12, 0, 1, 4, 15)
            .box(15, 12, 0, 1, 4, 15)
            .box(0, 12, 15, 16, 4, 1)
            .build();

    public static final VoxelShape ENERGY_CELL_ARRAY = LimaShapesUtil.shapeBuilder()
            .box(0, 0, 0, 16, 3, 16)
            .box(0, 13, 0, 16, 3, 16)
            .box(1, 2.5d, 1, 14, 11, 14)
            .build();

    public static final VoxelShape PORTABLE_TANK = LimaShapesUtil.shapeBuilder()
            .box(3, 0, 3, 10, 16, 10)
            .box(5, 12, 2, 6, 4, 1)
            .build();

    public static final VoxelShape COOKING_MACHINE = LimaShapesUtil.shapeBuilder()
            // Bottom frame base
            .absBox(0, 0, 0, 16, 4, 16)
            // Vertical frame pieces
            .absBox(0, 0, 0, 1, 16, 1)
            .absBox(15, 0, 0, 16, 16, 1)
            .absBox(0, 0, 15, 1, 16, 16)
            .absBox(15, 0, 15, 16, 16, 16)
            // Top frame pieces
            .absBox(0, 15, 0, 16, 16, 1)
            .absBox(0, 15, 15, 16, 16, 16)
            .absBox(0, 15, 0, 1, 16, 16)
            .absBox(15, 15, 0, 16, 16, 16)
            // Center box
            .box(0.5d, 3.5d, 0.5d, 15, 12, 15)
            .build();

    public static final VoxelShape GRINDER = LimaShapesUtil.shapeBuilder()
            .box(0, 0, 1, 16, 3, 14)
            .box(1, 3, 2, 14, 3, 12)
            .box(0, 6, 1, 16, 8, 1)
            .box(0, 6, 14, 16, 8, 1)
            .box(15, 6, 2, 1, 8, 12)
            .box(0, 6, 2, 1, 8, 12)
            .box(4, 9, 0, 8, 5, 1)
            .build();

    public static final VoxelShape ARC_FURNACE = LimaShapesUtil.shapeBuilder()
            .box(2, 0, 1, 12, 3, 14)
            .box(3, 3, 2, 10, 8, 10)
            .box(2, 5, 6, 12, 4, 7)
            .box(2, 11, 1, 12, 2, 12)
            .box(3, 13, 2, 10, 1, 10)
            .box(4, 3, 12, 8, 10, 3)
            .box(6, 14, 5, 4, 2, 4)
            .build();

    public static final VoxelShape MATERIAL_PRESS = LimaShapesUtil.shapeBuilder()
            .box(1, 0, 1, 14, 3, 14)
            .box(2, 3, 2, 12, 1, 12)
            .box(0, 0, 5, 2, 16, 6)
            .box(14, 0, 5, 2, 16, 6)
            .box(2, 13, 2, 12, 3, 12)
            .box(5, 12, 1, 6, 4, 1)
            .build();

    public static final VoxelShape HYDROSIEVE = LimaShapesUtil.shapeBuilder()
            .box(1, 0, 1, 14, 3, 14)
            .box(2, 3, 2, 12, 3, 12)
            .box(1, 6, 1, 14, 9, 1)
            .box(1, 6, 14, 14, 9, 1)
            .box(1, 6, 2, 1, 9, 12)
            .box(14, 6, 2, 1, 9, 12)
            .build();

    public static final VoxelShape ELECTROCENTRIFUGE = LimaShapesUtil.shapeBuilder()
            // Chassis
            .box(0, 0, 0, 4, 2, 4)
            .box(0, 0, 12, 4, 2, 4)
            .box(12, 0, 0, 4, 2, 4)
            .box(12, 0, 12, 4, 2, 4)
            .box(0, 2, 0, 16, 6, 16)
            // Frame top
            .box(0, 8, 0, 16, 2, 1)
            .box(0, 8, 15, 16, 2, 1)
            .box(0, 8, 1, 1, 2, 14)
            .box(15, 8, 1, 1, 2, 14)
            // Tubes
            .box(2, 8, 2, 12, 8, 12)
            .build();

    public static final VoxelShape MIXER = LimaShapesUtil.shapeBuilder()
            .box(0, 0, 0, 16, 4, 16)
            .box(1, 4, 1, 14, 1, 14)
            .box(1.5d, 5, 1.5d, 13, 8, 13)
            .box(1, 13, 1, 14, 2, 14)
            .box(2, 15, 2, 12, 1, 12)
            .build();

    public static final VoxelShape VOLTAIC_INJECTOR = LimaShapesUtil.shapeBuilder()
            .box(1, 0, 1, 14, 4, 14)
            .box(2, 4, 2, 12, 2, 12)
            .box(14, 4, 5, 1, 11, 6)
            .box(1, 4, 5, 1, 11, 6)
            .box(1, 15, 5, 14, 1, 6)
            .box(5, 12, 4, 6, 4, 1)
            .build();

    public static final VoxelShape CHEM_LAB = LimaShapesUtil.shapeBuilder()
            // Frame
            .box(0, 0, 0, 16, 4, 16)
            .box(2, 4, 7, 12, 9, 9)
            .box(1, 13, 1, 14, 3, 15)
            .box(5, 12, 0, 6, 4, 1)
            // Tubes
            .box(1.5d, 4, 2, 4, 9, 4)
            .box(6, 4, 2, 4, 9, 4)
            .box(10.5d, 4, 2, 4, 9, 4)
            .build();

    public static final VoxelShape ASSEMBLER = LimaShapesUtil.sizedBox(0, 0, 1, 16, 15, 14);

    public static final VoxelShape AUTO_FABRICATOR = LimaShapesUtil.shapeBuilder()
            // Feet & base
            .box(0, 0, 0, 4, 2, 4)
            .box(12, 0, 0, 4, 2, 4)
            .box(0, 0, 12, 4, 2, 4)
            .box(12, 0, 12, 4, 2, 4)
            .box(0, 2, 0, 16, 2, 16)
            // Vertical frame pieces
            .box(14, 4, 0, 2, 10, 2)
            .box(14, 4, 14, 2, 10, 2)
            .box(0, 4, 14, 2, 10, 2)
            .box(0, 4, 0, 2, 10, 2)
            // Top frame pieces
            .box(0, 14, 0, 16, 2, 2)
            .box(0, 14, 14, 16, 2, 2)
            .box(14, 14, 2, 2, 2, 12)
            .box(0, 14, 2, 2, 2, 12)
            // Inner cube
            .box(0.5d, 3.5d, 0.5d, 15, 12, 15)
            .build();

    public static final VoxelShape ATMOSPHERIC_SCRUBBER = LimaShapesUtil.shapeBuilder()
            .box(1, 0, 2, 14, 3, 12)
            .box(9, 3, 3, 6, 10, 10)
            .box(1, 3, 3, 8, 4, 10)
            .box(1, 3, 4, 8, 12, 8)
            .box(2, 3, 2, 6, 4, 1)
            .build();

    public static final VoxelShape GEO_SYNTHESIZER = LimaShapesUtil.shapeBuilder()
            .box(0, 0, 1, 16, 2, 14)
            .box(0.5d, 2, 1.5d, 15, 6, 13)
            .box(0, 8, 1, 16, 2, 14)
            .box(4, 5, 0.5d, 8, 11, 15)
            .build();

    public static final VoxelShape PORTABLE_GENERATOR = LimaShapesUtil.shapeBuilder()
            .box(0, 0, 1, 16, 3, 14)
            .box(1, 3, 1, 14, 4, 14)
            .box(0, 3, 2, 2, 12, 12)
            .box(4, 3, 2, 8, 12, 12)
            .box(14, 3, 2, 2, 12, 12)
            .box(2, 3, 3, 12, 11, 10)
            .build();

    public static final VoxelShape SOLAR_PANEL = LimaShapesUtil.shapeBuilder()
            .box(5, 0, 5, 6, 2, 6)
            .box(6, 2, 6, 4, 3, 4)
            .box(7, 5, 7, 2, 6, 2)
            .box(6, 11, 6, 4, 2, 4)
            .box(1, 12.5d, 0.5d, 14, 3, 15)
            .build();

    public static final VoxelShape REPAIR_STATION = LimaShapesUtil.shapeBuilder()
            .box(2, 0, 2, 12, 3, 12)
            .box(4, 3, 4, 8, 6, 8)
            .box(0, 9, 1, 16, 4, 14)
            .box(1, 13, 2, 14, 1, 12)
            .box(0, 13, 6, 2, 2, 4)
            .box(14, 13, 6, 2, 2, 4)
            .box(6, 13, 13, 4, 2, 2)
            .build();
    //#endregion

    //#region Multi-block/mesh shapes
    public static final VoxelShape FABRICATOR = LimaShapesUtil.shapeBuilder()
            .box(-16, 0, 0, 32, 2, 16)
            .box(-15, 2, 1, 30, 11, 14)
            .box(-16, 13, 0, 32, 2, 16)
            .box(-1, 15, 2, 12, 2, 12)
            .box(11, 15, 7, 2, 12, 2)
            .box(-3, 15, 7, 2, 12, 2)
            .box(-3, 27, 7, 16, 2, 2)
            .box(-14, 15, 4.25d, 10, 8.25d, 6.5d)
            .build();

    public static final VoxelShape GENERAL_TURRET = LimaShapesUtil.shapeBuilder()
            // Base
            .absBox(0, 0, 0, 16, 3, 16)
            .box(1, 3, 1, 14, 12, 14)
            .box(0, 6, 15, 16, 10, 1)
            .box(15, 6, 1, 1, 10, 14)
            .box(0, 6, 1, 1, 10, 14)
            .box(12, 6, 0, 4, 10, 1)
            .box(0, 6, 0, 4, 10, 1)
            .box(4, 7.5, 0, 8, 6, 1)
            // Swivel & gun
            .absBox(4, 15, 4, 12, 17, 12)
            .absBox(5, 17, 5, 11, 25, 11)
            .build();

    public static final VoxelShape DIGITAL_GARDEN = LimaShapesUtil.shapeBuilder()
            .box(1, 0, 1, 14, 4, 14)
            .box(2, 5, 2, 12, 3, 12)
            .box(4, 4, 1, 8, 8, 1)
            .box(1, 9, 1, 14, 3, 14)
            .box(1.5d, 12, 1.5d, 13, 16, 13)
            .box(1, 28, 1, 14, 3, 14)
            .box(2, 31, 2, 12, 1, 12)
            .build();
    //#endregion
}