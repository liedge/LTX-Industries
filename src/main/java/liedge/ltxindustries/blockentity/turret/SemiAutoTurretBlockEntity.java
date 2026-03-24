package liedge.ltxindustries.blockentity.turret;

import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.lib.TurretTargetTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class SemiAutoTurretBlockEntity extends TurretBlockEntity
{
    protected SemiAutoTurretBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, double traceY, double horizontalSearchRadius, double verticalSearchRadius)
    {
        super(type, pos, state, traceY, horizontalSearchRadius, verticalSearchRadius);
    }

    protected abstract void attackTarget(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, Entity target);

    @Override
    protected void turretFiringTick(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, TurretTargetTracker tracker)
    {
        Entity currentTarget = getTarget();
        if (targetStillValid(currentTarget))
        {
            if (consumeUsageEnergy())
            {
                attackTarget(level, pos, state, owner, currentTarget);
                setTurretState(TurretState.COOLDOWN);
                setNextTarget(tracker);
            }
            else
            {
                purgeTargets(tracker);
                setTurretState(TurretState.INACTIVE);
            }
        }
        else
        {
            setNextTarget(tracker);
        }
    }
}