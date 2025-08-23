package liedge.ltxindustries.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
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
import net.minecraft.resources.ResourceLocation;
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

    public static PrimaryMeshBlock create(Properties properties, ResourceLocation blockMeshId, VoxelShape identityShape, boolean tickClient)
    {
        BlockMesh mesh = Objects.requireNonNull(LTXIBlockMeshes.getBlockMesh(blockMeshId));
        Int2ObjectMap<VoxelShape> map = new Int2ObjectOpenHashMap<>();

        for (Direction side : Direction.Plane.HORIZONTAL)
        {
            VoxelShape rotatedIdentity = LimaBlockUtil.rotateYClockwise(identityShape, LimaBlockUtil.rotationYFromDirection(side));

            for (MeshPosition position : mesh.getMeshPositions())
            {
                int key = shapeKey(position.pos(), side);
                Vec3i offset = mesh.computeMeshOffset(position, mesh.getPrimary(), side.getOpposite());
                VoxelShape shape = LimaBlockUtil.moveShape(rotatedIdentity, offset.getX(), offset.getY(), offset.getZ());
                map.put(key, shape);
            }
        }

        final Int2ObjectMap<VoxelShape> shapeMap = Int2ObjectMaps.unmodifiable(map);
        return new PrimaryMeshBlock(properties, mesh, (side, meshPos) -> shapeMap.getOrDefault(shapeKey(meshPos.pos(), side), Shapes.empty()), tickClient);
    }

    private final BlockMesh blockMesh;
    private final BiFunction<Direction, MeshPosition, VoxelShape> shapeFunction;
    private final boolean tickClient;

    private PrimaryMeshBlock(Properties properties, BlockMesh blockMesh, BiFunction<Direction, MeshPosition, VoxelShape> shapeFunction, boolean tickClient)
    {
        super(properties);
        this.blockMesh = blockMesh;
        this.shapeFunction = shapeFunction;
        this.tickClient = tickClient;
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
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (state.is(this) && !newState.is(this))
        {
            BlockMesh mesh = getBlockMesh();
            mesh.meshStream(pos, mesh.getPrimary(), state.getValue(HORIZONTAL_FACING).getOpposite()).filter(cursor -> !cursor.equals(pos)).forEach(cursor -> level.removeBlock(cursor, false));
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}