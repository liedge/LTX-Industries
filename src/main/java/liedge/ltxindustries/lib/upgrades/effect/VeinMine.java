package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record VeinMine(BlockPredicate target, Optional<BlockPredicate> condition, int maxBlocks, boolean needsSameBlock)
{
    public static final int MAX_BLOCK_LIMIT = 256;

    public static final Codec<VeinMine> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("target").forGetter(VeinMine::target),
            BlockPredicate.CODEC.optionalFieldOf("condition").forGetter(VeinMine::condition),
            Codec.intRange(2, MAX_BLOCK_LIMIT).fieldOf("max_blocks").forGetter(VeinMine::maxBlocks),
            Codec.BOOL.optionalFieldOf("needs_same_block", true).forGetter(VeinMine::needsSameBlock))
            .apply(instance, VeinMine::new));

    public static VeinMine create(BlockPredicate.Builder target, @Nullable BlockPredicate.Builder condition, int maxBlocks, boolean needsSameBlock)
    {
        return new VeinMine(target.build(), Optional.ofNullable(condition).map(BlockPredicate.Builder::build), maxBlocks, needsSameBlock);
    }

    public List<BlockPos> apply(ServerLevel level, BlockPos originPos, BlockState originState)
    {
        if (!target.matches(level, originPos)) return List.of();

        Set<BlockPos> checked = new ObjectOpenHashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        List<BlockPos> toBreak = new ObjectArrayList<>();

        toBreak.add(originPos);
        toCheck.add(originPos);
        checked.add(originPos);

        BlockPredicate condition = this.condition.orElse(null);
        boolean conditionMet = condition == null;

        while (!toCheck.isEmpty() && toBreak.size() < maxBlocks)
        {
            BlockPos pos = toCheck.poll();
            for (BlockPos visiting : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)))
            {
                if (visiting.equals(pos) || checked.contains(visiting)) continue;

                BlockPos visitingImmutable = visiting.immutable();
                BlockState visitingState = level.getBlockState(visitingImmutable);
                checked.add(visitingImmutable);

                // Update global condition
                if (!conditionMet && condition.matches(level, visitingImmutable))
                {
                    conditionMet = true;
                }

                // Check target predicate and block matching requirement
                if (!target.matches(level, visitingImmutable)) continue;
                if (needsSameBlock && visitingState.getBlock() != originState.getBlock()) continue;

                toCheck.add(visitingImmutable);
                toBreak.add(visitingImmutable);
            }
        }

        return conditionMet ? toBreak : List.of();
    }
}