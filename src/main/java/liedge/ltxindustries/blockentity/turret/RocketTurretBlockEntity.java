package liedge.ltxindustries.blockentity.turret;

import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.entity.TurretRocketEntity;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RocketTurretBlockEntity extends SemiAutoTurretBlockEntity
{
    public RocketTurretBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ROCKET_TURRET.get(), pos, state, 1.625d, 50d, 75d);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.ROCKET_TURRET_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.ROCKET_TURRET_ENERGY_PER_TARGET.getAsInt();
    }

    @Override
    protected int getMaxTargetsPerSearch()
    {
        return 4;
    }

    @Override
    protected int getSearchInterval()
    {
        return 100;
    }

    @Override
    protected int getChargingDuration()
    {
        return 40;
    }

    @Override
    protected boolean isValidDefaultTarget(Entity entity)
    {
        return entity.getType().is(LTXITags.EntityTypes.FLYING_TARGETS);
    }

    @Override
    protected void attackTarget(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, Entity target)
    {
        TurretRocketEntity rocket = new TurretRocketEntity(level, this);
        rocket.setOwner(owner);
        rocket.setPos(traceStart);
        rocket.aimTowardsEntity(target, 2.5f, 0);
        rocket.setTargetEntity(target);
        level.addFreshEntity(rocket);
        level.playSound(null, traceStart.x, traceStart.y, traceStart.z, LTXISounds.ROCKET_LAUNCHER_FIRE, SoundSource.BLOCKS, 1.5f, Mth.randomBetween(level.getRandom(), 0.75f, 0.9f));
    }
}