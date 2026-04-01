package liedge.ltxindustries.blockentity.turret;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import liedge.ltxindustries.entity.damage.TurretDamageSource;
import liedge.ltxindustries.lib.TurretTargetTracker;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcTurretBlockEntity extends TurretBlockEntity
{
    public @Nullable EnergyBoltData primaryBolt;
    public @Nullable Vec3 chainOffset;
    public @Nullable Vec3 chainOffset0;
    public final List<EnergyBoltData> boltChains = new ObjectArrayList<>();

    public ArcTurretBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ARC_TURRET.get(), pos, state, 1.71875d, 30, 20);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.ARC_TURRET_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.ARC_TURRET_ENERGY_USAGE.getAsInt();
    }

    @Override
    protected int getMaxTargetsPerSearch()
    {
        return 16;
    }

    @Override
    protected int getSearchInterval()
    {
        return 30;
    }

    @Override
    protected int getChargingDuration()
    {
        return 8;
    }

    @Override
    protected int getCooldownDuration()
    {
        return 6;
    }

    @Override
    protected boolean isValidDefaultTarget(ServerLevel level, Entity targetEntity)
    {
        MobHostility hostility = LimaEntityUtil.getEntityHostility(level, targetEntity, getOwner());
        return LimaCoreObjects.greaterThanOrEquals(hostility, MobHostility.NEUTRAL_ENEMY);
    }

    @Override
    protected void turretFiringTick(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, TurretTargetTracker tracker)
    {
        Entity currentTarget = getTarget();
        if (targetStillValid(currentTarget))
        {
            if (consumeUsageEnergy())
            {
                TurretDamageSource source = TurretDamageSource.create(level, LTXIDamageTypes.ARC_TURRET, this, null, owner, traceStart);
                float damage = (float) LTXIMachinesConfig.ARC_TURRET_DAMAGE.getAsDouble();

                if (!currentTarget.hurtServer(level, source, damage))
                {
                    setNextTarget(tracker);
                    setTurretState(TurretState.COOLDOWN);
                }
                else
                {
                    for (Entity chained : getTargetQueue())
                    {
                        if (targetStillValid(chained)) chained.hurtServer(level, source, damage);
                    }
                }
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
            setTurretState(TurretState.COOLDOWN);
        }
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        super.tickClient(level, pos, state);

        boltChains.clear();
        if (getTurretState() == TurretState.FIRING)
        {
            RandomSource random = level.getRandom();
            Entity currentTarget = getClientTarget();
            if (targetStillValid(currentTarget))
            {
                primaryBolt = EnergyBoltData.create(traceStart, traceStart.add(0, 0, -getTargetDistance() + 0.875f), 0.015625f, 0.375f, random);

                Vec3 chainStart = currentTarget.getBoundingBox().getCenter();
                this.chainOffset0 = chainOffset;
                this.chainOffset = chainStart.subtract(pos.getX(), pos.getY(), pos.getZ());
                if (chainOffset0 == null) chainOffset0 = chainOffset;

                for (Entity chained : getTargetQueue())
                {
                    if (targetStillValid(chained))
                    {
                        Vec3 end = chained.getBoundingBox().getCenter();

                        EnergyBoltData bolt = EnergyBoltData.create(chainStart, end, 0.015625f, 0.25f, random);
                        boltChains.add(bolt);
                    }
                }
            }
            else
            {
                primaryBolt = null;
                chainOffset0 = null;
                chainOffset = null;
            }
        }
        else
        {
            primaryBolt = null;
            chainOffset0 = null;
            chainOffset = null;
        }
    }
}