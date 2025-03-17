package liedge.limatech.blockentity.base;

import com.google.common.collect.ImmutableSet;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

import java.util.EnumSet;
import java.util.Set;

public final class SidedAccessBlockEntityType<BE extends LimaBlockEntity> extends LimaBlockEntityType<BE>
{
    public static final Set<Direction> ALL_SIDES = ImmutableSet.copyOf(EnumSet.allOf(Direction.class));

    public static <BE extends LimaBlockEntity> SidedAccessBlockEntityType<BE> create(WithTypeConstructor<BE> constructor, Holder<Block> holder, Set<IOAccess> validIOStates, Set<Direction> activeSides)
    {
        return new SidedAccessBlockEntityType<>(constructor, Set.of(holder.value()), activeSides, validIOStates);
    }

    public static <BE extends LimaBlockEntity> SidedAccessBlockEntityType<BE> create(WithTypeConstructor<BE> constructor, Holder<Block> holder, Set<IOAccess> validIOStates)
    {
        return create(constructor, holder, validIOStates, ALL_SIDES);
    }

    private final Set<Direction> activeSides;
    private final Set<IOAccess> validIOStates;

    private SidedAccessBlockEntityType(WithTypeConstructor<BE> constructor, Set<Block> validBlocks, Set<Direction> activeSides, Set<IOAccess> validIOStates)
    {
        super(constructor, validBlocks);
        this.activeSides = activeSides;
        this.validIOStates = validIOStates;
    }
}