package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class MeshPortBlockEntity extends MeshBlockEntity
{
    public MeshPortBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.PORT_MESH_BLOCK.get(), pos, state);
    }

    // TODO: Write this properly
}