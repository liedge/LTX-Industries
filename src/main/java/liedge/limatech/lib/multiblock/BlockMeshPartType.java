package liedge.limatech.lib.multiblock;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum BlockMeshPartType implements StringRepresentable
{
    PRIMARY("primary"),
    SECONDARY("secondary"),
    DUMMY("dummy");

    public static final LimaEnumCodec<BlockMeshPartType> CODEC = LimaEnumCodec.create(BlockMeshPartType.class);

    private final String name;

    BlockMeshPartType(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}