package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.*;

public record VeinMine(BlockPredicate primary, Optional<BlockPredicate> environment, int maxBlocks, float energyActions)
{
    public static final int MAX_BLOCK_LIMIT = 256;

    public static final Codec<VeinMine> CODEC = RecordCodecBuilder.create(i -> i.group(
            BlockPredicate.CODEC.fieldOf("primary").forGetter(VeinMine::primary),
            BlockPredicate.CODEC.optionalFieldOf("environment").forGetter(VeinMine::environment),
            Codec.intRange(2, MAX_BLOCK_LIMIT).fieldOf("max_blocks").forGetter(VeinMine::maxBlocks),
            Codec.floatRange(0f, 1f).optionalFieldOf("energy_per_block", 1f).forGetter(VeinMine::energyActions))
            .apply(i, VeinMine::new));

    public static VeinMine create(BlockPredicate.Builder primary, BlockPredicate.@Nullable Builder environment, int maxBlocks, float energyActions)
    {
        return new VeinMine(primary.build(), Optional.ofNullable(environment).map(BlockPredicate.Builder::build), maxBlocks, energyActions);
    }

    public static VeinMine create(BlockPredicate.Builder primary, int maxBlocks, float energyActions)
    {
        return create(primary, null, maxBlocks, energyActions);
    }

    public List<BlockPos> apply(ServerLevel level, BlockPos originPos, BlockState originState, int usableEnergyActions)
    {
        if (!primary.matches(level, originPos)) return List.of();

        Set<BlockPos> checked = new ObjectOpenHashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        List<BlockPos> toBreak = new ObjectArrayList<>();

        toBreak.add(originPos);
        toCheck.add(originPos);
        checked.add(originPos);

        BlockPredicate environment = this.environment.orElse(null);
        boolean environmentCheck = environment == null;


        final int limit = getBlockLimit(usableEnergyActions);
        while (!toCheck.isEmpty() && toBreak.size() < limit)
        {
            BlockPos pos = toCheck.poll();
            for (BlockPos visiting : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)))
            {
                if (visiting.equals(pos) || checked.contains(visiting)) continue;

                BlockPos visitingImmutable = visiting.immutable();
                BlockState visitingState = level.getBlockState(visitingImmutable);
                checked.add(visitingImmutable);

                // Update global condition
                if (!environmentCheck && environment.matches(level, visitingImmutable))
                {
                    environmentCheck = true;
                }

                // Must match origin block and primary predicate
                if (visitingState.getBlock() != originState.getBlock() || !primary.matches(level, visitingImmutable)) continue;

                toCheck.add(visitingImmutable);
                toBreak.add(visitingImmutable);
            }
        }

        return environmentCheck ? toBreak : List.of();
    }

    private int getBlockLimit(int usableEnergyActions)
    {
        if (this.energyActions == 0f) return this.maxBlocks;

        return Math.min(this.maxBlocks, Mth.floor(usableEnergyActions / this.energyActions));
    }
}