package liedge.ltxindustries.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.block.mesh.BlockMesh;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import liedge.ltxindustries.block.mesh.MeshPosition;
import liedge.ltxindustries.blockentity.MeshBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class PrimaryMeshBlock extends BaseMeshBlock
{
    private static int shapeKey(Vec3i pos, Direction facing)
    {
        int x = pos.getX() + 15;
        int y = pos.getY() + 15;
        int z = pos.getZ() + 15;
        return x << 12 | y << 7 | z << 2 | facing.get2DDataValue();
    }

    private static BiFunction<Direction, MeshPosition, VoxelShape> createShapeFunction(BlockMesh blockMesh, VoxelShape identityShape)
    {
        Int2ObjectMap<VoxelShape> map = new Int2ObjectOpenHashMap<>();

        for (Direction side : Direction.Plane.HORIZONTAL)
        {
            VoxelShape rotatedIdentity = LimaBlockUtil.rotateYClockwise(identityShape, LimaBlockUtil.rotationYFromDirection(side));

            for (MeshPosition position : blockMesh.getMeshPositions())
            {
                int key = shapeKey(position.pos(), side);
                Vec3i offset = blockMesh.computeMeshOffset(position, blockMesh.getPrimary(), side.getOpposite());
                VoxelShape shape = LimaBlockUtil.moveShape(rotatedIdentity, offset.getX(), offset.getY(), offset.getZ());
                map.put(key, shape);
            }
        }

        return (side, meshPos) -> map.getOrDefault(shapeKey(meshPos.pos(), side), Shapes.empty());
    }

    private final BlockMesh blockMesh;
    private final BiFunction<Direction, MeshPosition, VoxelShape> shapeFunction;
    private final boolean tickClient;

    public PrimaryMeshBlock(Properties properties, Identifier meshId, VoxelShape identityShape, boolean tickClient)
    {
        super(properties);
        this.blockMesh = Objects.requireNonNull(LTXIBlockMeshes.getBlockMesh(meshId));
        this.shapeFunction = createShapeFunction(blockMesh, identityShape);
        this.tickClient = tickClient;

        registerDefaultState(getStateDefinition().any()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    public BlockMesh getBlockMesh()
    {
        return blockMesh;
    }

    public VoxelShape getMeshBlockShape(Direction facing, MeshPosition meshPosition)
    {
        return shapeFunction.apply(facing, meshPosition);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shapeFunction.apply(state.getValue(HORIZONTAL_FACING), blockMesh.getPrimary());
    }

    @Override
    protected boolean shouldTickServer(BlockState state)
    {
        return true;
    }

    @Override
    protected boolean shouldTickClient(BlockState state)
    {
        return tickClient;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction zAxis = context.getHorizontalDirection();
        BlockMesh mesh = getBlockMesh();

        if (mesh.canPlaceMesh(level, origin, mesh.getPrimary(), zAxis))
        {
            return defaultBlockState()
                    .setValue(HORIZONTAL_FACING, zAxis.getOpposite())
                    .setValue(WATERLOGGED, level.getFluidState(origin).is(Fluids.WATER));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        BlockMesh mesh = getBlockMesh();
        Direction facing = state.getValue(HORIZONTAL_FACING);

        mesh.meshStream(pos, mesh.getPrimary(), facing.getOpposite()).filter(cursor -> !cursor.equals(pos)).forEach(cursor ->
        {
            BlockState cursorState = LTXIBlocks.MESH_BLOCK.get().defaultBlockState()
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(WATERLOGGED, level.getFluidState(cursor).is(Fluids.WATER));
            level.setBlockAndUpdate(cursor, cursorState);

            MeshBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, cursor, MeshBlockEntity.class);
            if (blockEntity != null)
            {
                blockEntity.setBlockMesh(mesh);
                blockEntity.setMeshPosition(cursor.getMeshPosition());
                blockEntity.setChanged();
            }
        });

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston)
    {
        BlockMesh mesh = getBlockMesh();
        mesh.meshStream(pos, mesh.getPrimary(), state.getValue(HORIZONTAL_FACING).getOpposite())
                .filter(cursor -> !cursor.equals(pos))
                .forEach(cursor -> {
                    MeshBlockEntity blockEntity = LimaBlockUtil.getBlockEntity(level, cursor, MeshBlockEntity.class);
                    if (blockEntity != null && mesh.equals(blockEntity.getBlockMesh())) level.removeBlock(cursor, false);
                });
    }
}