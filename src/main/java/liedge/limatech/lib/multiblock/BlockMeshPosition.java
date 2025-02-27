package liedge.limatech.lib.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Rotation;

public record BlockMeshPosition(int index, Vec3i pos, Rotation rotation)
{
    public static final BlockMeshPosition INVALID = new BlockMeshPosition(-1, Vec3i.ZERO, Rotation.NONE);

    public static final Codec<BlockMeshPosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(BlockMeshPosition::index),
            Vec3i.CODEC.fieldOf("pos").forGetter(BlockMeshPosition::pos),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.NONE).forGetter(BlockMeshPosition::rotation))
            .apply(instance, BlockMeshPosition::new));
}